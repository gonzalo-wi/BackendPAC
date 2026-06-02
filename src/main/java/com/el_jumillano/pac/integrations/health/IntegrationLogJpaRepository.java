package com.el_jumillano.pac.integrations.health;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IntegrationLogJpaRepository extends JpaRepository<IntegrationLogJpaEntity, Long> {

    List<IntegrationLogJpaEntity> findByProviderAndPlantIdOrderByCreatedAtDesc(
            IntegrationProvider provider, Long plantId);
}
