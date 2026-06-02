package com.el_jumillano.pac.reconciliation.infrastructure;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "manual_values",
    uniqueConstraints = @UniqueConstraint(columnNames = {"route_number", "date", "plant_id"})
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ManualValuesJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "route_number", nullable = false)
    private Integer routeNumber;

    @Column(name = "plant_id", nullable = false)
    private Long plantId;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "checks_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal checksAmount;

    @Column(name = "withholdings_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal withholdingsAmount;

    @Column(name = "entered_by", nullable = false)
    private String enteredBy;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
