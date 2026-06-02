package com.el_jumillano.pac.deposits.infrastructure;

import com.el_jumillano.pac.deposits.domain.Deposit;
import com.el_jumillano.pac.deposits.domain.DepositAdjustment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DepositMapper {

    Deposit toDomain(DepositJpaEntity entity);

    DepositJpaEntity toEntity(Deposit domain);

    DepositAdjustment toAdjustmentDomain(DepositAdjustmentJpaEntity entity);

    DepositAdjustmentJpaEntity toAdjustmentEntity(DepositAdjustment domain);
}
