package com.el_jumillano.pac.deposits.application;

import com.el_jumillano.pac.deposits.domain.Check;
import com.el_jumillano.pac.deposits.domain.ConceptoCatalog;
import com.el_jumillano.pac.deposits.infrastructure.DepositItemRepositoryAdapter;
import com.el_jumillano.pac.deposits.infrastructure.DepositRepositoryAdapter;
import com.el_jumillano.pac.shared.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateCheckUseCase {

    private final DepositRepositoryAdapter depositRepository;
    private final DepositItemRepositoryAdapter itemRepository;

    public Check execute(Long depositId, CreateCheckRequest request) {
        depositRepository.findById(depositId)
                .orElseThrow(() -> new EntityNotFoundException("Deposit", depositId));
        if (!ConceptoCatalog.isValidCheque(request.concepto())) {
            throw new IllegalArgumentException("Concepto inválido para cheque: " + request.concepto());
        }
        return itemRepository.saveCheck(Check.builder()
                .depositId(depositId)
                .concepto(request.concepto())
                .bank(request.bank())
                .branch(request.branch() != null ? request.branch() : "001")
                .locality(request.locality() != null ? request.locality() : "1234")
                .checkNumber(request.checkNumber())
                .accountNumber(request.accountNumber())
                .accountCode(request.accountCode() != null ? request.accountCode() : 1234)
                .holder(request.holder() != null ? request.holder() : "")
                .paymentDate(request.paymentDate())
                .amount(request.amount())
                .build());
    }
}
