package com.el_jumillano.pac.deposits.infrastructure;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(
    name = "deposits",
    uniqueConstraints = @UniqueConstraint(columnNames = {"external_deposit_id", "cashier_id", "deposit_date"})
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepositJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_deposit_id", nullable = false)
    private String externalDepositId;

    @Column(name = "route_number", nullable = false)
    private Integer routeNumber;

    @Column(name = "plant_id", nullable = false)
    private Long plantId;

    @Column(name = "cashier_id", nullable = false)
    private Long cashierId;

    @Column(name = "deposit_date", nullable = false)
    private LocalDate depositDate;

    @Column(name = "deposit_time")
    private LocalTime depositTime;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "raw_payload", columnDefinition = "TEXT")
    private String rawPayload;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
