package com.el_jumillano.pac.integrations.health;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IntegrationStatus {

    private Long id;
    private IntegrationProvider provider;
    private Long plantId;
    private Long cashierId;
    private IntegrationHealthStatus status;
    private LocalDateTime lastSuccessAt;
    private LocalDateTime lastErrorAt;
    private String lastErrorMessage;
    private LocalDateTime updatedAt;
}
