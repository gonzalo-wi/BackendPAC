package com.el_jumillano.pac.integrations.health;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class IntegrationStatusRepositoryAdapter {

    private final IntegrationStatusJpaRepository jpaRepository;
    private final IntegrationStatusMapper mapper;
    private final IntegrationLogJpaRepository logJpaRepository;
    private final IntegrationLogMapper logMapper;

    public IntegrationStatus saveStatus(IntegrationStatus status) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(status)));
    }

    public IntegrationStatus upsertStatus(IntegrationStatus status) {
        var existing = jpaRepository.findByProviderAndPlantIdAndCashierId(
                status.getProvider(), status.getPlantId(), status.getCashierId());
        if (existing.isPresent()) {
            var entity = existing.get();
            entity.setStatus(status.getStatus());
            // Solo actualizar el timestamp que corresponde — no borrar el historial del otro
            if (status.getLastSuccessAt() != null) {
                entity.setLastSuccessAt(status.getLastSuccessAt());
            }
            if (status.getLastErrorAt() != null) {
                entity.setLastErrorAt(status.getLastErrorAt());
                entity.setLastErrorMessage(status.getLastErrorMessage());
            }
            // Forzar dirty para que @UpdateTimestamp dispare siempre
            entity.setUpdatedAt(LocalDateTime.now());
            return mapper.toDomain(jpaRepository.save(entity));
        }
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(status)));
    }

    public List<IntegrationStatus> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    public IntegrationLog saveLog(IntegrationLog log) {
        return logMapper.toDomain(logJpaRepository.save(logMapper.toEntity(log)));
    }
}
