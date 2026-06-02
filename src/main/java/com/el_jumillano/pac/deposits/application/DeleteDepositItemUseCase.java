package com.el_jumillano.pac.deposits.application;

import com.el_jumillano.pac.deposits.infrastructure.DepositItemRepositoryAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteDepositItemUseCase {

    private final DepositItemRepositoryAdapter itemRepository;

    public void deleteCheck(Long checkId) {
        itemRepository.deleteCheck(checkId);
    }

    public void deleteWithholding(Long withholdingId) {
        itemRepository.deleteWithholding(withholdingId);
    }
}
