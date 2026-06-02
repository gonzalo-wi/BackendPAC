package com.el_jumillano.pac.differences.infrastructure;

import com.el_jumillano.pac.differences.domain.DifferenceType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "difference_records")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DifferenceRecordJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reconciliation_id", nullable = false)
    private Long reconciliationId;

    @Column(name = "route_number", nullable = false)
    private Integer routeNumber;

    @Column(name = "plant_id", nullable = false)
    private Long plantId;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "employee_id")
    private String employeeId;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DifferenceType type;

    @Column(name = "external_reference")
    private String externalReference;

    @Column(nullable = false)
    private boolean notified;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
