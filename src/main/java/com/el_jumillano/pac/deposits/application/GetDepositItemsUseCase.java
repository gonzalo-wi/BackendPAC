package com.el_jumillano.pac.deposits.application;

import com.el_jumillano.pac.deposits.domain.Check;
import com.el_jumillano.pac.deposits.domain.Withholding;
import com.el_jumillano.pac.deposits.infrastructure.DepositItemRepositoryAdapter;
import com.el_jumillano.pac.deposits.infrastructure.DepositRepositoryAdapter;
import com.el_jumillano.pac.shared.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetDepositItemsUseCase {

    private final DepositRepositoryAdapter depositRepository;
    private final DepositItemRepositoryAdapter itemRepository;

    public List<Check> getChecks(Long depositId) {
        depositRepository.findById(depositId)
                .orElseThrow(() -> new EntityNotFoundException("Deposit", depositId));
        return itemRepository.findChecksByDepositId(depositId);
    }

    public List<Withholding> getWithholdings(Long depositId) {
        depositRepository.findById(depositId)
                .orElseThrow(() -> new EntityNotFoundException("Deposit", depositId));
        return itemRepository.findWithholdingsByDepositId(depositId);
    }
}
