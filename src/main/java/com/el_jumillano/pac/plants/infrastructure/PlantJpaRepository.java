package com.el_jumillano.pac.plants.infrastructure;

import com.el_jumillano.pac.plants.domain.PlantCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlantJpaRepository extends JpaRepository<PlantJpaEntity, Long> {

    Optional<PlantJpaEntity> findByCode(PlantCode code);
}
