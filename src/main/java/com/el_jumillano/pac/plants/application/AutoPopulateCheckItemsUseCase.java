package com.el_jumillano.pac.plants.application;

import com.el_jumillano.pac.deposits.domain.Check;
import com.el_jumillano.pac.deposits.domain.Withholding;
import com.el_jumillano.pac.deposits.infrastructure.DepositItemRepositoryAdapter;
import com.el_jumillano.pac.deposits.infrastructure.DepositRepositoryAdapter;
import com.el_jumillano.pac.integrations.aguas.AguasDetailJsonClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Pre-crea cheques y retenciones desde Aguas (getcobranzadetalle) para repartos
 * que tienen ítems esperados pero que el cajero aún no cargó manualmente.
 * Solo actúa si no hay ítems registrados para ese reparto/fecha (idempotente).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AutoPopulateCheckItemsUseCase {

    private final AguasDetailJsonClient aguasDetailClient;
    private final DepositRepositoryAdapter depositRepository;
    private final DepositItemRepositoryAdapter depositItemRepository;

    public void execute(Integer routeNumber, Long plantId, LocalDate date) {
        BigDecimal existingChecks = depositItemRepository.sumChecksByRouteAndPlantAndDate(routeNumber, plantId, date);
        BigDecimal existingWithh  = depositItemRepository.sumWithholdingsByRouteAndPlantAndDate(routeNumber, plantId, date);
        if (existingChecks.compareTo(BigDecimal.ZERO) > 0 || existingWithh.compareTo(BigDecimal.ZERO) > 0) {
            return;
        }

        var deposits = depositRepository.findByRouteAndPlantAndDate(routeNumber, plantId, date);
        if (deposits.isEmpty()) return;
        Long depositId = deposits.get(0).getId();

        var detail = aguasDetailClient.getRouteDetail(routeNumber, date.toString(), true);
        if (!detail.isSuccess() || detail.getResultado() == null) return;

        detail.getResultado().forEach(clientPayment -> {
            if (clientPayment.getDetalle() == null) return;
            Long nrocta = parseNrocta(clientPayment.getNrocta());

            clientPayment.getDetalle().forEach(item -> {
                if (item.getMonto() == null || item.getMonto().compareTo(BigDecimal.ZERO) <= 0) return;

                if ("Cheque".equalsIgnoreCase(item.getTipoValor())) {
                    depositItemRepository.saveCheck(Check.builder()
                            .depositId(depositId)
                            .concepto("CHE")
                            .accountNumber(nrocta)
                            .amount(item.getMonto())
                            .build());
                } else if ("Retencion".equalsIgnoreCase(item.getTipoValor())
                        || "Retención".equalsIgnoreCase(item.getTipoValor())) {
                    depositItemRepository.saveWithholding(Withholding.builder()
                            .depositId(depositId)
                            .concepto("RIB")
                            .accountNumber(nrocta)
                            .amount(item.getMonto())
                            .build());
                }
            });
        });
    }

    private Long parseNrocta(String nrocta) {
        if (nrocta == null || nrocta.isBlank()) return null;
        try {
            long val = Long.parseLong(nrocta.trim());
            return val > 0 ? val : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
