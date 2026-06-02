package com.el_jumillano.pac.audit.infrastructure;

import com.el_jumillano.pac.audit.domain.AuditLog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class AuditLogRepositoryAdapter {

    private final AuditLogJpaRepository jpaRepository;
    private final AuditLogMapper mapper;

    public AuditLog save(AuditLog log) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(log)));
    }

    public List<AuditLog> findByEntity(String entityType, String entityId) {
        return jpaRepository.findByEntityTypeAndEntityIdOrderByCreatedAtDesc(entityType, entityId)
                .stream().map(mapper::toDomain).toList();
    }

    public List<AuditLog> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }
}
