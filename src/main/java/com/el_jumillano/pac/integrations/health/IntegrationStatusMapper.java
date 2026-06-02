package com.el_jumillano.pac.integrations.health;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IntegrationStatusMapper {

    IntegrationStatus toDomain(IntegrationStatusJpaEntity entity);

    IntegrationStatusJpaEntity toEntity(IntegrationStatus domain);
}
