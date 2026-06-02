package com.el_jumillano.pac.audit.application;

import com.el_jumillano.pac.audit.domain.AuditAction;

import java.time.LocalDateTime;

public record AuditLogResponse(
        Long id,
        String entityType,
        String entityId,
        AuditAction action,
        String oldValue,
        String newValue,
        String userId,
        String reason,
        LocalDateTime createdAt
) {}
