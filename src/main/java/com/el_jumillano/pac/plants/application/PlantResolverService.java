package com.el_jumillano.pac.plants.application;

import com.el_jumillano.pac.plants.domain.PlantCode;
import org.springframework.stereotype.Service;

/**
 * Servicio de dominio centralizado que resuelve a qué planta pertenece
 * un cajero Minibank o un número de reparto de Aguas.
 *
 * Reglas de cajeros:
 *   Cajero 1 → CIUDADELA
 *   Cajero 2 → CIUDADELA
 *   Cajero 3 → LA_PLATA
 *   Cajero 4 → LOMAS_DE_ZAMORA
 *
 * Reglas de repartos:
 *   1–200   → CIUDADELA
 *   230–300 → LA_PLATA
 *   400–500 → LOMAS_DE_ZAMORA
 */
@Service
public class PlantResolverService {

    public PlantCode resolveByExternalCashierNumber(Integer externalCashierNumber) {
        return switch (externalCashierNumber) {
            case 1, 2 -> PlantCode.CIUDADELA;
            case 3    -> PlantCode.LA_PLATA;
            case 4    -> PlantCode.LOMAS_DE_ZAMORA;
            default   -> throw new IllegalArgumentException(
                    "Cajero externo sin planta asignada: " + externalCashierNumber);
        };
    }

    public PlantCode resolveByRouteNumber(Integer routeNumber) {
        if (routeNumber >= 1 && routeNumber <= 200) {
            return PlantCode.CIUDADELA;
        }
        if (routeNumber >= 230 && routeNumber <= 300) {
            return PlantCode.LA_PLATA;
        }
        if (routeNumber >= 400 && routeNumber <= 500) {
            return PlantCode.LOMAS_DE_ZAMORA;
        }
        throw new IllegalArgumentException(
                "Número de reparto sin planta asignada: " + routeNumber);
    }
}
