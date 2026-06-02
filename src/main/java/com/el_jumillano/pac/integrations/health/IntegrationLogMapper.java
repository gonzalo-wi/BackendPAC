package com.el_jumillano.pac.integrations.health;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IntegrationLogMapper {

    IntegrationLog toDomain(IntegrationLogJpaEntity entity);

    IntegrationLogJpaEntity toEntity(IntegrationLog domain);
}
