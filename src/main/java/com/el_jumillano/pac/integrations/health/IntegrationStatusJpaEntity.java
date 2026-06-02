package com.el_jumillano.pac.integrations.health;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "integration_status",
    uniqueConstraints = @UniqueConstraint(columnNames = {"provider", "plant_id", "cashier_id"})
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IntegrationStatusJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IntegrationProvider provider;

    @Column(name = "plant_id")
    private Long plantId;

    @Column(name = "cashier_id")
    private Long cashierId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IntegrationHealthStatus status;

    @Column(name = "last_success_at")
    private LocalDateTime lastSuccessAt;

    @Column(name = "last_error_at")
    private LocalDateTime lastErrorAt;

    @Column(name = "last_error_message", columnDefinition = "TEXT")
    private String lastErrorMessage;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
