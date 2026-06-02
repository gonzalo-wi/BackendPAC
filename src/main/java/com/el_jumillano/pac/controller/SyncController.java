package com.el_jumillano.pac.controller;
import com.el_jumillano.pac.deposits.application.SyncMinibankDepositsUseCase;
import com.el_jumillano.pac.expected.application.SyncAguasExpectedUseCase;
import com.el_jumillano.pac.shared.dto.SyncAguasRequest;
import com.el_jumillano.pac.shared.dto.SyncMinibankRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@Tag(name = "Sync")
@RestController
@RequestMapping("/api/sync")
@RequiredArgsConstructor
public class SyncController {

    private final SyncMinibankDepositsUseCase syncMinibank;
    private final SyncAguasExpectedUseCase    syncAguas;

    @PostMapping("/minibank")
    public ResponseEntity<Map<String, Object>> syncMinibank(@Valid @RequestBody SyncMinibankRequest req) {
        var deposits = syncMinibank.execute(req.date(), req.cashierId());
        return ResponseEntity.ok(Map.of("synced", deposits.size(), "date", req.date()));
    }

    @PostMapping("/aguas")
    public ResponseEntity<Map<String, Object>> syncAguas(@Valid @RequestBody SyncAguasRequest req) {
        var expected = syncAguas.execute(req.date(), req.routeNumber());
        return ResponseEntity.ok(Map.of(
                "routeNumber", expected.getRouteNumber(),
                "date", expected.getDate(),
                "version", expected.getVersion()));
    }
}
