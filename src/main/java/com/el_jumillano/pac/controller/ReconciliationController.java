package com.el_jumillano.pac.controller;

import com.el_jumillano.pac.deposits.application.GetReconciliationDepositsUseCase;
import com.el_jumillano.pac.deposits.domain.Deposit;
import com.el_jumillano.pac.integrations.aguas.GetRouteDetailUseCase;
import com.el_jumillano.pac.integrations.aguas.RouteDetailResponse;
import com.el_jumillano.pac.reconciliation.application.CloseReconciliationUseCase;
import com.el_jumillano.pac.reconciliation.application.CreateReconciliationRequest;
import com.el_jumillano.pac.reconciliation.application.CreateReconciliationUseCase;
import com.el_jumillano.pac.reconciliation.application.GetReconciliationsUseCase;
import com.el_jumillano.pac.reconciliation.application.LoadManualValuesUseCase;
import com.el_jumillano.pac.reconciliation.application.ManualValuesRequest;
import com.el_jumillano.pac.reconciliation.application.ProcessReconciliationUseCase;
import com.el_jumillano.pac.reconciliation.application.ReconciliationResponse;
import com.el_jumillano.pac.reconciliation.application.ReconciliationResponseMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDate;
import java.util.List;

@Tag(name = "Reconciliations")
@RestController
@RequestMapping("/api/reconciliations")
@RequiredArgsConstructor
public class ReconciliationController {

    private final CreateReconciliationUseCase      createUseCase;
    private final GetReconciliationsUseCase        getUseCase;
    private final LoadManualValuesUseCase          manualValuesUseCase;
    private final ProcessReconciliationUseCase     processUseCase;
    private final CloseReconciliationUseCase       closeUseCase;
    private final GetRouteDetailUseCase            routeDetailUseCase;
    private final GetReconciliationDepositsUseCase depositsUseCase;
    private final ReconciliationResponseMapper     reconciliationMapper;

    @PostMapping
    public ResponseEntity<ReconciliationResponse> create(
            @Valid @RequestBody CreateReconciliationRequest request,
            @RequestHeader(value = "X-User-Id", defaultValue = "system") String userId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reconciliationMapper.toResponse(createUseCase.execute(request.routeNumber(), request.date(), userId)));
    }


    @GetMapping
    public List<ReconciliationResponse> list(
            @RequestParam Long plantId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return getUseCase.getByPlantAndDate(plantId, date)
                .stream().map(reconciliationMapper::toResponse).toList();
    }


    @GetMapping("/{id}")
    public ReconciliationResponse getById(@PathVariable Long id) {
        return reconciliationMapper.toResponse(getUseCase.getById(id));
    }


    @PostMapping("/{id}/manual-values")
    public ReconciliationResponse loadManualValues(
            @PathVariable Long id,
            @Valid @RequestBody ManualValuesRequest request,
            @RequestHeader(value = "X-User-Id", defaultValue = "system") String userId) {
        return reconciliationMapper.toResponse(manualValuesUseCase.execute(id, request, userId));
    }


    @PostMapping("/{id}/process")
    public ResponseEntity<ReconciliationResponse> process(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", defaultValue = "system") String userId) {
        return ResponseEntity.ok(reconciliationMapper.toResponse(processUseCase.execute(id, userId)));
    }


    @GetMapping("/{id}/deposits")
    public List<Deposit> deposits(@PathVariable Long id) {
        return depositsUseCase.execute(id);
    }


    @GetMapping("/{id}/aguas-detail")
    public RouteDetailResponse aguasDetail(@PathVariable Long id) {
        return routeDetailUseCase.execute(id);
    }

    
    @PostMapping("/{id}/close")
    public ResponseEntity<ReconciliationResponse> close(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", defaultValue = "system") String userId) {
        return ResponseEntity.ok(reconciliationMapper.toResponse(closeUseCase.execute(id, userId)));
    }
}
