package com.el_jumillano.pac.integrations.minibank;

import com.el_jumillano.pac.cashiers.infrastructure.CashierJpaRepository;
import com.el_jumillano.pac.deposits.domain.Deposit;
import com.el_jumillano.pac.plants.application.PlantResolverService;
import com.el_jumillano.pac.plants.infrastructure.PlantJpaRepository;
import com.el_jumillano.pac.shared.exception.EntityNotFoundException;
import com.el_jumillano.pac.shared.exception.IntegrationUnavailableException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Adaptador que implementa el puerto MinibankClient usando MinibankXmlClient + parser + mapper.
 */
@Component
@RequiredArgsConstructor
public class MinibankClientAdapter implements MinibankClient {

    /** Formato de fecha que espera Minibank: MM/dd/yyyy */
    private static final DateTimeFormatter MINIBANK_DATE_FMT = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    private final MinibankXmlClient xmlClient;
    private final MinibankXmlParser parser;
    private final MinibankDepositInfraMapper mapper;
    private final CashierJpaRepository cashierRepo;
    private final PlantResolverService plantResolver;
    private final PlantJpaRepository plantRepo;
    private final MinibankProperties minibankProperties;

    @Override
    @CircuitBreaker(name = "minibank", fallbackMethod = "fallback")
    @Retry(name = "minibank")
    public List<Deposit> getDepositsByDate(LocalDate date, Integer cashierId) {
        var cashierEntity = cashierRepo.findByExternalCashierNumber(cashierId)
                .orElseThrow(() -> new EntityNotFoundException("Cashier", cashierId));
        var plantCode = plantResolver.resolveByExternalCashierNumber(cashierId);
        var plant = plantRepo.findByCode(plantCode)
                .orElseThrow(() -> new EntityNotFoundException("Plant", plantCode));

        String stIdentifier = minibankProperties.getCashierIdentifiers().get(cashierId);
        if (stIdentifier == null) {
            throw new EntityNotFoundException("MinibankCashierIdentifier", cashierId);
        }

        String rawXml = xmlClient.getDepositsXml(stIdentifier, date.format(MINIBANK_DATE_FMT));

        return parser.parse(rawXml).stream()
                .map(xml -> mapper.toDomain(xml, plant.getId(), cashierEntity.getId()))
                .toList();
    }

    public List<Deposit> fallback(LocalDate date, Integer cashierId, Throwable t) {
        throw new IntegrationUnavailableException("Minibank", t);
    }
}
