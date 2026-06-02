package com.el_jumillano.pac.deposits.infrastructure;

import com.el_jumillano.pac.deposits.domain.Check;
import com.el_jumillano.pac.deposits.domain.Withholding;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DepositItemMapper {

    // ──────────────────────────────────────────
    // Check
    // ──────────────────────────────────────────

    @Mapping(source = "deposit.id", target = "depositId")
    Check toDomain(CheckJpaEntity entity);

    @Mapping(target = "deposit", expression = "java(depositProxy(domain.getDepositId()))")
    CheckJpaEntity toEntity(Check domain);

    // ──────────────────────────────────────────
    // Withholding
    // ──────────────────────────────────────────

    @Mapping(source = "deposit.id", target = "depositId")
    Withholding toDomain(WithholdingJpaEntity entity);

    @Mapping(target = "deposit", expression = "java(depositProxy(domain.getDepositId()))")
    WithholdingJpaEntity toEntity(Withholding domain);

    // ──────────────────────────────────────────
    // Helper: minimal proxy carrying only the id
    // ──────────────────────────────────────────
    default DepositJpaEntity depositProxy(Long depositId) {
        DepositJpaEntity proxy = new DepositJpaEntity();
        proxy.setId(depositId);
        return proxy;
    }
}
