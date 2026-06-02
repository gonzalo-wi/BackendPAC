package com.el_jumillano.pac.deposits.infrastructure;

import com.el_jumillano.pac.deposits.domain.ItemType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "withholdingsdeposit")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WithholdingJpaEntity {

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
    private ItemType itemType = ItemType.WITHHOLDING;

    @Column(length = 50)
    @Builder.Default
    private String concepto = "RIB";

    @Column(name = "withholding_number", length = 100)
    private String withholdingNumber;

    @Column(name = "payment_date", length = 50)
    private String paymentDate;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "withholding_type", length = 50)
    private String type;
}
