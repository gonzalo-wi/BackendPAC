package com.el_jumillano.pac.integrations.health;

import java.time.LocalDateTime;

public record IntegrationStatusResponse(
        IntegrationProvider provider,
        Long plantId,
        Long cashierId,
        IntegrationHealthStatus status,
        LocalDateTime lastSuccessAt,
        LocalDateTime lastErrorAt,
        String lastErrorMessage,
        LocalDateTime updatedAt
) {}
