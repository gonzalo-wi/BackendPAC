package com.el_jumillano.pac.controller;

import com.el_jumillano.pac.deposits.application.CheckResponse;
import com.el_jumillano.pac.deposits.application.CreateCheckRequest;
import com.el_jumillano.pac.deposits.application.CreateCheckUseCase;
import com.el_jumillano.pac.deposits.application.CreateWithholdingRequest;
import com.el_jumillano.pac.deposits.application.CreateWithholdingUseCase;
import com.el_jumillano.pac.deposits.application.DeleteDepositItemUseCase;
import com.el_jumillano.pac.deposits.application.GetDepositItemsUseCase;
import com.el_jumillano.pac.deposits.application.WithholdingResponse;
import com.el_jumillano.pac.deposits.domain.Check;
import com.el_jumillano.pac.deposits.domain.ConceptoCatalog;
import com.el_jumillano.pac.deposits.domain.ConceptoCode;
import com.el_jumillano.pac.deposits.domain.Withholding;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Deposit Items")
@RestController
@RequestMapping("/api/deposits")
@RequiredArgsConstructor
public class DepositItemController {

    private final CreateCheckUseCase createCheckUseCase;
    private final CreateWithholdingUseCase createWithholdingUseCase;
    private final GetDepositItemsUseCase getItemsUseCase;
    private final DeleteDepositItemUseCase deleteItemUseCase;

    @GetMapping("/conceptos/checks")
    public List<ConceptoCode> conceptosCheques() {
        return ConceptoCatalog.CHEQUES;
    }

    @GetMapping("/conceptos/withholdings")
    public List<ConceptoCode> conceptosRetenciones() {
        return ConceptoCatalog.RETENCIONES;
    }

    @GetMapping("/{depositId}/checks")
    public List<CheckResponse> listChecks(@PathVariable Long depositId) {
        return getItemsUseCase.getChecks(depositId).stream().map(this::toCheckResponse).toList();
    }

    @PostMapping("/{depositId}/checks")
    public ResponseEntity<CheckResponse> createCheck(
            @PathVariable Long depositId,
            @Valid @RequestBody CreateCheckRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toCheckResponse(createCheckUseCase.execute(depositId, request)));
    }

    @DeleteMapping("/{depositId}/checks/{checkId}")
    public ResponseEntity<Void> deleteCheck(
            @PathVariable Long depositId,
            @PathVariable Long checkId) {
        deleteItemUseCase.deleteCheck(checkId);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/{depositId}/withholdings")
    public List<WithholdingResponse> listWithholdings(@PathVariable Long depositId) {
        return getItemsUseCase.getWithholdings(depositId).stream().map(this::toWithholdingResponse).toList();
    }

    @PostMapping("/{depositId}/withholdings")
    public ResponseEntity<WithholdingResponse> createWithholding(
            @PathVariable Long depositId,
            @Valid @RequestBody CreateWithholdingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toWithholdingResponse(createWithholdingUseCase.execute(depositId, request)));
    }

    @DeleteMapping("/{depositId}/withholdings/{withholdingId}")
    public ResponseEntity<Void> deleteWithholding(
            @PathVariable Long depositId,
            @PathVariable Long withholdingId) {
        deleteItemUseCase.deleteWithholding(withholdingId);
        return ResponseEntity.noContent().build();
    }

   
    private CheckResponse toCheckResponse(Check c) {
        return new CheckResponse(
                c.getId(), c.getDepositId(), c.getConcepto(),
                c.getBank(), c.getBranch(), c.getLocality(),
                c.getCheckNumber(), c.getAccountNumber(), c.getAccountCode(),
                c.getHolder(), c.getPaymentDate(), c.getAmount());
    }

    private WithholdingResponse toWithholdingResponse(Withholding w) {
        return new WithholdingResponse(
                w.getId(), w.getDepositId(), w.getConcepto(),
                w.getWithholdingNumber(), w.getAccountNumber(),
                w.getPaymentDate(), w.getType(), w.getAmount());
    }
}
