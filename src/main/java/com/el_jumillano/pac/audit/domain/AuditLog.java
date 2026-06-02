package com.el_jumillano.pac.audit.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {

    private Long id;
    private String entityType;
    private String entityId;
    private AuditAction action;
    private String oldValue;
    private String newValue;
    private String userId;
    private String reason;
    private LocalDateTime createdAt;
}
