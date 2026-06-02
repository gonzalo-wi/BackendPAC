package com.el_jumillano.pac.cashiers.infrastructure;

import com.el_jumillano.pac.cashiers.domain.Cashier;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CashierMapper {

    Cashier toDomain(CashierJpaEntity entity);

    CashierJpaEntity toEntity(Cashier domain);
}
