package com.el_jumillano.pac.integrations.aguas;

import com.el_jumillano.pac.expected.domain.ExpectedAmount;
import com.el_jumillano.pac.expected.infrastructure.ExpectedAmountJpaRepository;
import com.el_jumillano.pac.plants.application.PlantResolverService;
import com.el_jumillano.pac.plants.infrastructure.PlantJpaRepository;
import com.el_jumillano.pac.reconciliation.domain.CloseRouteResult;
import com.el_jumillano.pac.shared.exception.EntityNotFoundException;
import com.el_jumillano.pac.shared.exception.IntegrationUnavailableException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AguasClientAdapter implements AguasClient {

    /** Formato de fecha que espera Aguas: dd/MM/yyyy */
    private static final DateTimeFormatter AGUAS_DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final AguasExpectedJsonClient expectedClient;
    private final AguasCloseXmlClient closeClient;
    private final AguasExpectedInfraMapper expectedMapper;
    private final AguasCloseXmlBuilder closeXmlBuilder;
    private final AguasCloseResponseParser closeResponseParser;
    private final PlantResolverService plantResolver;
    private final PlantJpaRepository plantRepo;
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
                                                          BigDecimal expectedAmount) {
        String xml = closeXmlBuilder.build(date, routeNumber, expectedAmount);
        String response = closeClient.closeRoute(xml);
        return closeResponseParser.parse(response);
    }

    public ExpectedAmount fallbackExpected(LocalDate date, Integer routeNumber, Throwable t) {
        throw new IntegrationUnavailableException("Aguas", t);
    }

    public List<ExpectedAmount> fallbackAllExpected(LocalDate date, Throwable t) {
        throw new IntegrationUnavailableException("Aguas", t);
    }

    public CloseRouteResult fallbackClose(LocalDate date, Integer routeNumber,
                                          BigDecimal expectedAmount, Throwable t) {
        throw new IntegrationUnavailableException("Aguas", t);
    }
}
