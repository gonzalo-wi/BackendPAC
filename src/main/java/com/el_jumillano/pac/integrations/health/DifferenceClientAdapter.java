package com.el_jumillano.pac.integrations.health;

import com.el_jumillano.pac.differences.domain.DifferenceRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Implementación stub del puerto DifferenceClient.
 * Loguea la diferencia y deja el campo notified=false.
 * El endpoint externo real se configurará en una iteración futura.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DifferenceClientAdapter implements DifferenceClient {

    @Override
    public void notifyDifference(DifferenceRecord difference) {
        // TODO: integrar con el servicio externo de diferencias del legajo
        log.warn("[DifferenceClient] Diferencia pendiente de notificación: reconciliationId={}, " +
                        "routeNumber={}, amount={}, type={}",
                difference.getReconciliationId(),
                difference.getRouteNumber(),
                difference.getAmount(),
                difference.getType());
    }
}
