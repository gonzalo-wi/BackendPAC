package com.el_jumillano.pac.audit.infrastructure;

import com.el_jumillano.pac.audit.domain.AuditLog;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuditLogMapper {

    AuditLog toDomain(AuditLogJpaEntity entity);

    AuditLogJpaEntity toEntity(AuditLog domain);
}
