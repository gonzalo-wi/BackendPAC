package com.el_jumillano.pac.controller;

import com.el_jumillano.pac.deposits.application.AdjustDepositUseCase;
import com.el_jumillano.pac.deposits.application.DepositAdjustmentRequest;
import com.el_jumillano.pac.deposits.domain.DepositAdjustment;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Deposits")
@RestController
@RequestMapping("/api/deposits")
@RequiredArgsConstructor
public class DepositController {

    private final AdjustDepositUseCase adjustUseCase;

    @PostMapping("/{id}/adjustments")
    public ResponseEntity<DepositAdjustment> adjust(
            @PathVariable Long id,
            @Valid @RequestBody DepositAdjustmentRequest request,
            @RequestHeader(value = "X-User-Id", defaultValue = "system") String userId) {
        return ResponseEntity.ok(adjustUseCase.execute(id, request, userId));
    }
}
