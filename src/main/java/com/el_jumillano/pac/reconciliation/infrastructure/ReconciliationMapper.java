package com.el_jumillano.pac.reconciliation.infrastructure;

import com.el_jumillano.pac.reconciliation.domain.ManualValues;
import com.el_jumillano.pac.reconciliation.domain.Reconciliation;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReconciliationMapper {

    Reconciliation toDomain(ReconciliationJpaEntity entity);

    ReconciliationJpaEntity toEntity(Reconciliation domain);

    ManualValues toManualValuesDomain(ManualValuesJpaEntity entity);

    ManualValuesJpaEntity toManualValuesEntity(ManualValues domain);
}
