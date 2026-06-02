package com.el_jumillano.pac.deposits.application;

import com.el_jumillano.pac.audit.application.AuditService;
import com.el_jumillano.pac.audit.domain.AuditAction;
import com.el_jumillano.pac.deposits.domain.Deposit;
import com.el_jumillano.pac.deposits.domain.DepositAdjustment;
import com.el_jumillano.pac.deposits.infrastructure.DepositRepositoryAdapter;
import com.el_jumillano.pac.shared.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AdjustDepositUseCase {

    private final DepositRepositoryAdapter depositRepository;
    private final AuditService auditService;

    @Transactional
    public DepositAdjustment execute(Long depositId, DepositAdjustmentRequest request, String userId) {
        Deposit original = depositRepository.findById(depositId)
                .orElseThrow(() -> new EntityNotFoundException("Deposit", depositId));

        DepositAdjustment adjustment = DepositAdjustment.builder()
                .originalDepositId(depositId)
                .fromRouteNumber(original.getRouteNumber())
                .toRouteNumber(request.toRouteNumber())
                .amount(request.amount())
                .reason(request.reason())
                .createdBy(userId)
                .createdAt(LocalDateTime.now())
                .build();

        DepositAdjustment saved = depositRepository.saveAdjustment(adjustment);

        auditService.log(AuditAction.DEPOSIT_ADJUSTED, "Deposit",
                String.valueOf(depositId),
                "reparto=" + original.getRouteNumber() + " monto=" + original.getAmount(),
                "toReparto=" + request.toRouteNumber() + " monto=" + request.amount(),
                userId);

        return saved;
    }
}
