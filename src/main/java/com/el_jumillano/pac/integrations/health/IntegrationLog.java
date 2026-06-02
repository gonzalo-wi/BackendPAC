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
public class IntegrationLog {

    private Long id;
    private IntegrationProvider provider;
    private String endpoint;
    private Long plantId;
    private Long cashierId;
    private IntegrationHealthStatus status;
    private Integer httpStatus;
    private String errorMessage;
    private Long responseTimeMs;
    private String requestPayload;
    private String responsePayload;
    private LocalDateTime createdAt;
}
