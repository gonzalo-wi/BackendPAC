package com.el_jumillano.pac.controller;

import com.el_jumillano.pac.audit.application.AuditLogResponse;
import com.el_jumillano.pac.audit.application.AuditService;
import com.el_jumillano.pac.audit.domain.AuditLog;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Audit")
@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
public class AuditController {

    private final AuditService auditService;

    @GetMapping
    public List<AuditLogResponse> list(
            @RequestParam(required = false) String entityType,
            @RequestParam(required = false) String entityId) {
        List<AuditLog> logs;
        if (entityType != null && entityId != null) {
            logs = auditService.findByEntity(entityType, entityId);
        } else {
            logs = auditService.findAll();
        }
        return logs.stream().map(this::toResponse).toList();
    }

    private AuditLogResponse toResponse(AuditLog l) {
        return new AuditLogResponse(
                l.getId(), l.getEntityType(), l.getEntityId(), l.getAction(),
                l.getOldValue(), l.getNewValue(), l.getUserId(), l.getReason(), l.getCreatedAt());
    }
}
