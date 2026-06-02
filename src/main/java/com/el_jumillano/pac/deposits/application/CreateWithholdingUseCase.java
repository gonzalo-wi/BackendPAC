package com.el_jumillano.pac.deposits.application;

import com.el_jumillano.pac.deposits.domain.ConceptoCatalog;
import com.el_jumillano.pac.deposits.domain.Withholding;
import com.el_jumillano.pac.deposits.infrastructure.DepositItemRepositoryAdapter;
import com.el_jumillano.pac.deposits.infrastructure.DepositRepositoryAdapter;
import com.el_jumillano.pac.shared.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateWithholdingUseCase {

    private final DepositRepositoryAdapter depositRepository;
    private final DepositItemRepositoryAdapter itemRepository;

    public Withholding execute(Long depositId, CreateWithholdingRequest request) {
        depositRepository.findById(depositId)
                .orElseThrow(() -> new EntityNotFoundException("Deposit", depositId));

        if (!ConceptoCatalog.isValidRetencion(request.concepto())) {
            throw new IllegalArgumentException("Concepto inválido para retención: " + request.concepto());
        }

        return itemRepository.saveWithholding(Withholding.builder()
                .depositId(depositId)
                .concepto(request.concepto())
                .withholdingNumber(request.withholdingNumber())
                .accountNumber(request.accountNumber())
                .paymentDate(request.paymentDate())
                .type(request.type())
                .amount(request.amount())
                .build());
    }
}
