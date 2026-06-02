package com.el_jumillano.pac.reconciliation.application;

import com.el_jumillano.pac.reconciliation.domain.Reconciliation;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReconciliationResponseMapper {

    ReconciliationResponse toResponse(Reconciliation reconciliation);
}
