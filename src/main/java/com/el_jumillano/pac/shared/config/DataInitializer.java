package com.el_jumillano.pac.shared.config;

import com.el_jumillano.pac.cashiers.infrastructure.CashierJpaEntity;
import com.el_jumillano.pac.cashiers.infrastructure.CashierJpaRepository;
import com.el_jumillano.pac.plants.domain.PlantCode;
import com.el_jumillano.pac.plants.infrastructure.PlantJpaEntity;
import com.el_jumillano.pac.plants.infrastructure.PlantJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final PlantJpaRepository plantRepo;
    private final CashierJpaRepository cashierRepo;

    @Bean
    ApplicationRunner seedReferenceData() {
        return args -> {
            seedPlants();
            seedCashiers();
        };
    }

    private void seedPlants() {
        Map<PlantCode, String> definitions = Map.of(
                PlantCode.CIUDADELA,       "Ciudadela",
                PlantCode.LA_PLATA,        "La Plata",
                PlantCode.LOMAS_DE_ZAMORA, "Lomas de Zamora"
        );
        definitions.forEach((code, name) -> {
            if (plantRepo.findByCode(code).isEmpty()) {
                plantRepo.save(PlantJpaEntity.builder().code(code).name(name).build());
                log.info("[DataInit] Planta creada: {}", code);
            }
        });
    }

    private void seedCashiers() {
        record CashierDef(int number, PlantCode plant, String name) {}

        List<CashierDef> definitions = List.of(
                new CashierDef(1, PlantCode.CIUDADELA,       "Cajero Ciudadela 1"),
                new CashierDef(2, PlantCode.CIUDADELA,       "Cajero Ciudadela 2"),
                new CashierDef(3, PlantCode.LA_PLATA,        "Cajero La Plata"),
                new CashierDef(4, PlantCode.LOMAS_DE_ZAMORA, "Cajero Lomas de Zamora")
        );

        definitions.forEach(def -> {
            if (cashierRepo.findByExternalCashierNumber(def.number()).isEmpty()) {
                var plant = plantRepo.findByCode(def.plant())
                        .orElseThrow(() -> new IllegalStateException(
                                "Planta no inicializada antes que los cajeros: " + def.plant()));
                cashierRepo.save(CashierJpaEntity.builder()
                        .externalCashierNumber(def.number())
                        .plantId(plant.getId())
                        .name(def.name())
                        .active(true)
                        .build());
                log.info("[DataInit] Cajero creado: {} (numero {})", def.name(), def.number());
            }
        });
    }
}
