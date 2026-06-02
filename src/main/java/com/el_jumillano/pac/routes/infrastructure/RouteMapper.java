package com.el_jumillano.pac.routes.infrastructure;

import com.el_jumillano.pac.routes.domain.Route;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RouteMapper {

    Route toDomain(RouteJpaEntity entity);

    RouteJpaEntity toEntity(Route domain);
}
