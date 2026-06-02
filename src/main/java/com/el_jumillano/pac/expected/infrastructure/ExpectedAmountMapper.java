package com.el_jumillano.pac.expected.infrastructure;

import com.el_jumillano.pac.expected.domain.ExpectedAmount;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ExpectedAmountMapper {

    ExpectedAmount toDomain(ExpectedAmountJpaEntity entity);

    ExpectedAmountJpaEntity toEntity(ExpectedAmount domain);
}
