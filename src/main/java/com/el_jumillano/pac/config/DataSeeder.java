package com.el_jumillano.pac.config;

import com.el_jumillano.pac.cashiers.infrastructure.CashierJpaEntity;
import com.el_jumillano.pac.cashiers.infrastructure.CashierJpaRepository;
import com.el_jumillano.pac.plants.domain.PlantCode;
import com.el_jumillano.pac.plants.infrastructure.PlantJpaEntity;
import com.el_jumillano.pac.plants.infrastructure.PlantJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Siembra los registros maestros (plantas y cajeros) al arrancar la aplicación.
 * Es idempotente: solo inserta si el registro no existe.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder {

    private final PlantJpaRepository plantRepo;
    private final CashierJpaRepository cashierRepo;

    @EventListener(ApplicationReadyEvent.class)
    public void seed() {
        seedPlants();
        seedCashiers();
    }

    private void seedPlants() {
        for (PlantCode code : PlantCode.values()) {
            if (plantRepo.findByCode(code).isEmpty()) {
                plantRepo.save(PlantJpaEntity.builder()
                        .name(readableName(code))
                        .code(code)
                        .build());
                log.info("[DataSeeder] Planta creada: {}", code);
            }
        }
    }

    private void seedCashiers() {
        record CashierSeed(int externalNumber, PlantCode plantCode, String name) {}

        var seeds = List.of(
                new CashierSeed(1, PlantCode.CIUDADELA,      "Ciudadela 1"),
                new CashierSeed(2, PlantCode.CIUDADELA,      "Ciudadela 2"),
                new CashierSeed(3, PlantCode.LA_PLATA,       "La Plata"),
                new CashierSeed(4, PlantCode.LOMAS_DE_ZAMORA, "Lomas de Zamora")
        );

        for (var seed : seeds) {
            if (cashierRepo.findByExternalCashierNumber(seed.externalNumber()).isEmpty()) {
                var plant = plantRepo.findByCode(seed.plantCode())
                        .orElseThrow(() -> new IllegalStateException(
                                "Planta no encontrada durante seed: " + seed.plantCode()));
                cashierRepo.save(CashierJpaEntity.builder()
                        .externalCashierNumber(seed.externalNumber())
                        .plantId(plant.getId())
                        .name(seed.name())
                        .active(true)
                        .build());
                log.info("[DataSeeder] Cajero creado: externo={} planta={}", seed.externalNumber(), seed.plantCode());
            }
        }
    }

    private String readableName(PlantCode code) {
        return switch (code) {
            case CIUDADELA       -> "Ciudadela";
            case LA_PLATA        -> "La Plata";
            case LOMAS_DE_ZAMORA -> "Lomas de Zamora";
        };
    }
}
