package com.el_jumillano.pac.integrations.health;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "integration_logs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IntegrationLogJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IntegrationProvider provider;

    @Column(nullable = false)
    private String endpoint;

    @Column(name = "plant_id")
    private Long plantId;

    @Column(name = "cashier_id")
    private Long cashierId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IntegrationHealthStatus status;

    @Column(name = "http_status")
    private Integer httpStatus;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "response_time_ms")
    private Long responseTimeMs;

    @Column(name = "request_payload", columnDefinition = "TEXT")
    private String requestPayload;

    @Column(name = "response_payload", columnDefinition = "TEXT")
    private String responsePayload;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
