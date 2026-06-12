package com.el_jumillano.pac.controller;
import com.el_jumillano.pac.integrations.health.GetIntegrationStatusUseCase;
import com.el_jumillano.pac.integrations.health.IntegrationStatus;
import com.el_jumillano.pac.integrations.health.IntegrationStatusResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@Tag(name = "Integrations")
@RestController
@RequestMapping("/api/integrations")
@RequiredArgsConstructor
public class IntegrationController {

    private final GetIntegrationStatusUseCase useCase;

    @GetMapping("/status")
    public List<IntegrationStatusResponse> status() {
        return useCase.execute().stream()
                .map(this::toResponse)
                .toList();
    }

    private IntegrationStatusResponse toResponse(IntegrationStatus s) {
        return new IntegrationStatusResponse(
                s.getProvider(), s.getPlantId(), s.getCashierId(),
                s.getStatus(), s.getLastSuccessAt(), s.getLastErrorAt(),
                s.getLastErrorMessage(), s.getUpdatedAt());
    }
}
