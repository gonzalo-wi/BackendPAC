package com.el_jumillano.pac.cashiers.infrastructure;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cashiers")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CashierJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_cashier_number", nullable = false, unique = true)
    private Integer externalCashierNumber;

    @Column(name = "plant_id", nullable = false)
    private Long plantId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private boolean active;
}
