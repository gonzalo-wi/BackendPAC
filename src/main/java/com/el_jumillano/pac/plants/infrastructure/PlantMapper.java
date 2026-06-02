package com.el_jumillano.pac.plants.infrastructure;

import com.el_jumillano.pac.plants.domain.Plant;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PlantMapper {

    Plant toDomain(PlantJpaEntity entity);

    PlantJpaEntity toEntity(Plant domain);
}
