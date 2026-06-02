package com.el_jumillano.pac.audit.infrastructure;

import com.el_jumillano.pac.audit.domain.AuditAction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditLogJpaRepository extends JpaRepository<AuditLogJpaEntity, Long> {

    List<AuditLogJpaEntity> findByEntityTypeAndEntityIdOrderByCreatedAtDesc(
            String entityType, String entityId);

    List<AuditLogJpaEntity> findByActionOrderByCreatedAtDesc(AuditAction action);
}
