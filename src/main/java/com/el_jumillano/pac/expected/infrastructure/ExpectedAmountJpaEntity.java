package com.el_jumillano.pac.expected.infrastructure;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "expected_amounts",
    uniqueConstraints = @UniqueConstraint(columnNames = {"route_number", "date", "plant_id", "version"})
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpectedAmountJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "route_number", nullable = false)
    private Integer routeNumber;

    @Column(name = "plant_id", nullable = false)
    private Long plantId;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "expected_cash", nullable = false, precision = 19, scale = 2)
    private BigDecimal expectedCash;

    @Column(name = "expected_checks", nullable = false, precision = 19, scale = 2)
    private BigDecimal expectedChecks;

    @Column(name = "expected_withholdings", nullable = false, precision = 19, scale = 2)
    private BigDecimal expectedWithholdings;

    @Column(name = "expected_total", nullable = false, precision = 19, scale = 2)
    private BigDecimal expectedTotal;

    @Column(nullable = false)
    private Integer version;

    @Column(nullable = false)
    private boolean current;

    @Column(name = "fetched_at", nullable = false)
    private LocalDateTime fetchedAt;
}
