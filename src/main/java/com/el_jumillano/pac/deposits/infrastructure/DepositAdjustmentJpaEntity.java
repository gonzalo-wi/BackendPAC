package com.el_jumillano.pac.deposits.infrastructure;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "deposit_adjustments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepositAdjustmentJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "original_deposit_id", nullable = false)
    private Long originalDepositId;

    @Column(name = "from_route_number", nullable = false)
    private Integer fromRouteNumber;

    @Column(name = "to_route_number", nullable = false)
    private Integer toRouteNumber;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @Column(name = "created_by", nullable = false)
    private String createdBy;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
