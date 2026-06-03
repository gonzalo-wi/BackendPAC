package com.el_jumillano.pac.reconciliation.infrastructure;

import com.el_jumillano.pac.differences.domain.DifferenceType;
import com.el_jumillano.pac.reconciliation.domain.ReconciliationStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "reconciliations",
    uniqueConstraints = @UniqueConstraint(columnNames = {"plant_id", "route_number", "date"}),
    indexes = {
        // Consulta principal: listar todos los repartos de una planta en una fecha
        @Index(name = "idx_reconciliations_plant_date", columnList = "plant_id, date"),
        // Scheduler findByStatusIn: busca todos los PENDING/READY en curso
        @Index(name = "idx_reconciliations_status", columnList = "status")
    }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReconciliationJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "route_number", nullable = false)
    private Integer routeNumber;

    @Column(name = "plant_id", nullable = false)
    private Long plantId;

    @Column(nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReconciliationStatus status;

    @Column(name = "minibank_cash_total", precision = 19, scale = 2)
    private BigDecimal minibankCashTotal;

    @Column(name = "manual_checks_total", precision = 19, scale = 2)
    private BigDecimal manualChecksTotal;

    @Column(name = "manual_withholdings_total", precision = 19, scale = 2)
    private BigDecimal manualWithholdingsTotal;

    @Column(name = "total_received", precision = 19, scale = 2)
    private BigDecimal totalReceived;

    @Column(name = "aguas_expected_cash", precision = 19, scale = 2)
    private BigDecimal aguasExpectedCash;

    @Column(name = "aguas_expected_checks", precision = 19, scale = 2)
    private BigDecimal aguasExpectedChecks;

    @Column(name = "aguas_expected_withholdings", precision = 19, scale = 2)
    private BigDecimal aguasExpectedWithholdings;

    @Column(name = "aguas_expected_total", precision = 19, scale = 2)
    private BigDecimal aguasExpectedTotal;

    @Column(name = "difference_amount", precision = 19, scale = 2)
    private BigDecimal differenceAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "difference_type")
    private DifferenceType differenceType;

    @Column(name = "processed_by")
    private String processedBy;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    @Column(name = "aguas_sent_amount", precision = 19, scale = 2)
    private BigDecimal aguasSentAmount;

    @Column(name = "aguas_response", columnDefinition = "TEXT")
    private String aguasResponse;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
