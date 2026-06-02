package com.el_jumillano.pac.deposits.infrastructure;

import com.el_jumillano.pac.deposits.domain.ItemType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "checks")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "deposit_id", nullable = false)
    private DepositJpaEntity deposit;

    @Column(name = "account_number")
    private Long accountNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "item_type", nullable = false, length = 20)
    @Builder.Default
    private ItemType itemType = ItemType.CHECK;

    @Column(length = 50)
    @Builder.Default
    private String concepto = "CHE";

    @Column(length = 255)
    private String bank;

    @Column(length = 50)
    @Builder.Default
    private String branch = "001";

    @Column(length = 100)
    @Builder.Default
    private String locality = "1234";

    @Column(name = "check_number", length = 100)
    private String checkNumber;

    @Column(name = "account_code")
    @Builder.Default
    private Integer accountCode = 1234;

    @Column(length = 255)
    @Builder.Default
    private String holder = "";

    @Column(name = "payment_date", length = 50)
    private String paymentDate;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;
}
