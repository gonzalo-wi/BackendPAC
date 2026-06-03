package com.el_jumillano.pac.integrations.aguas;

import com.el_jumillano.pac.expected.domain.ExpectedAmount;
import com.el_jumillano.pac.expected.infrastructure.ExpectedAmountJpaRepository;
import com.el_jumillano.pac.plants.application.PlantResolverService;
import com.el_jumillano.pac.plants.infrastructure.PlantJpaRepository;
import com.el_jumillano.pac.reconciliation.domain.CloseRouteResult;
import com.el_jumillano.pac.shared.exception.EntityNotFoundException;
import com.el_jumillano.pac.shared.exception.IntegrationUnavailableException;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AguasClientAdapter implements AguasClient {

    /** Formato de fecha que espera Aguas: dd/MM/yyyy */
    private static final DateTimeFormatter AGUAS_DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final int AJUSTAR_ENVASES = 0;

    private final AguasExpectedJsonClient    expectedClient;
    private final AguasCloseHttpClient       closeClient;
    private final AguasExpectedInfraMapper   expectedMapper;
    private final AguasCloseResponseParser   closeResponseParser;
    private final PlantResolverService       plantResolver;
    private final PlantJpaRepository         plantRepo;
    private final ExpectedAmountJpaRepository expectedRepo;

    @Override
    @CircuitBreaker(name = "aguas", fallbackMethod = "fallbackExpected")
    @Retry(name = "aguas")
    public ExpectedAmount getExpectedByRoute(LocalDate date, Integer routeNumber) {
        var plantCode = plantResolver.resolveByRouteNumber(routeNumber);
        var plant = plantRepo.findByCode(plantCode)
                .orElseThrow(() -> new EntityNotFoundException("Plant", plantCode));

        List<AguasExpectedDto> results = expectedClient.getExpected(
                routeNumber, date.format(AGUAS_DATE_FMT));

        AguasExpectedDto dto = results.stream()
                .filter(d -> routeNumber.equals(d.getIdReparto()))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("ExpectedAmount en Aguas", routeNumber));

        int nextVersion = expectedRepo.findMaxVersion(routeNumber, plant.getId(), date) + 1;
        return expectedMapper.toDomain(dto, date, plant.getId(), nextVersion);
    }

    @Override
    @CircuitBreaker(name = "aguas", fallbackMethod = "fallbackAllExpected")
    @Retry(name = "aguas")
    public List<ExpectedAmount> getAllExpectedByDate(LocalDate date) {
        List<AguasExpectedDto> results = expectedClient.getExpected(0, date.format(AGUAS_DATE_FMT));
        return results.stream().map(dto -> {
            var plantCode = plantResolver.resolveByRouteNumber(dto.getIdReparto());
            var plant = plantRepo.findByCode(plantCode)
                    .orElseThrow(() -> new EntityNotFoundException("Plant", plantCode));
            int nextVersion = expectedRepo.findMaxVersion(dto.getIdReparto(), plant.getId(), date) + 1;
            return expectedMapper.toDomain(dto, date, plant.getId(), nextVersion);
        }).toList();
    }

    @Override
    @CircuitBreaker(name = "aguas", fallbackMethod = "fallbackClose")
    @Retry(name = "aguas")
    public CloseRouteResult closeRouteWithExpectedAmount(LocalDate date, Integer routeNumber,
                                                          BigDecimal expectedCash,
                                                          String checksJson,
                                                          String withholdingsJson,
                                                          String userId) {
        String rawResponse;
        try {
            rawResponse = closeClient.closeRoute(
                    routeNumber,
                    date.format(AGUAS_DATE_FMT),
                    AJUSTAR_ENVASES,
                    toPlain(expectedCash),
                    withholdingsJson,
                    checksJson,
                    userId != null ? userId : "PAC"
            );
        } catch (FeignException e) {
            // HTTP 411: IIS procesa la request correctamente pero exige Content-Length.
            // El cierre ya quedó registrado en Aguas — se trata como éxito.
            if (e.status() == 411) {
                log.info("Reparto {} cerrado en Aguas (HTTP 411 ignorado)", routeNumber);
                return closeResponseParser.parseSuccess("HTTP 411 - cierre procesado");
            }
            log.error("Aguas rechazó el cierre del reparto {}: HTTP {} - {}",
                    routeNumber, e.status(), e.getMessage());
            return closeResponseParser.parseError("HTTP " + e.status() + ": " + e.getMessage());
        }
        return closeResponseParser.parseSuccess(rawResponse);
    }

    // ── Fallbacks ────────────────────────────────────────────────────────────

    public ExpectedAmount fallbackExpected(LocalDate date, Integer routeNumber, Throwable t) {
        throw new IntegrationUnavailableException("Aguas", t);
    }

    public List<ExpectedAmount> fallbackAllExpected(LocalDate date, Throwable t) {
        throw new IntegrationUnavailableException("Aguas", t);
    }

    public CloseRouteResult fallbackClose(LocalDate date, Integer routeNumber,
                                          BigDecimal expectedCash, String checksJson,
                                          String withholdingsJson, String userId, Throwable t) {
        throw new IntegrationUnavailableException("Aguas", t);
    }

    private static String toPlain(BigDecimal value) {
        return value != null ? value.toPlainString() : "0";
    }
}
