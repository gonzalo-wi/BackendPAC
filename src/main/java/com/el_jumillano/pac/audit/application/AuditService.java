package com.el_jumillano.pac.audit.application;

import com.el_jumillano.pac.audit.domain.AuditAction;
import com.el_jumillano.pac.audit.domain.AuditLog;
import com.el_jumillano.pac.audit.infrastructure.AuditLogRepositoryAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepositoryAdapter repository;

    public void log(AuditAction action, String entityType, String entityId,
                    String oldValue, String newValue) {
        log(action, entityType, entityId, oldValue, newValue, null);
    }

    public void log(AuditAction action, String entityType, String entityId,
                    String oldValue, String newValue, String userId) {
        repository.save(AuditLog.builder()
                .action(action)
                .entityType(entityType)
                .entityId(entityId)
                .oldValue(oldValue)
                .newValue(newValue)
                .userId(userId)
                .createdAt(LocalDateTime.now())
                .build());
    }

    public List<AuditLog> findAll() {
        return repository.findAll();
    }

    public List<AuditLog> findByEntity(String entityType, String entityId) {
        return repository.findByEntity(entityType, entityId);
    }
}
