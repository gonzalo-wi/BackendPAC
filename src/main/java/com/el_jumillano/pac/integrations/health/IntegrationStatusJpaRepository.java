package com.el_jumillano.pac.integrations.health;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IntegrationStatusJpaRepository extends JpaRepository<IntegrationStatusJpaEntity, Long> {

    List<IntegrationStatusJpaEntity> findAll();

    Optional<IntegrationStatusJpaEntity> findByProviderAndPlantIdAndCashierId(
            IntegrationProvider provider, Long plantId, Long cashierId);
}
