package com.el_jumillano.pac.deposits.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Bank check linked to a deposit.
 * Its amount is accumulated in manualChecksTotal during reconciliation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Check {

    private Long id;

    /** FK → Deposit.id */
    private Long depositId;

    /** Internal account number */
    private Long accountNumber;

    @Builder.Default
    private ItemType itemType = ItemType.CHECK;

    /** Concept code — e.g. CHE, CHC, ECH. See ConceptoCatalog. */
    @Builder.Default
    private String concepto = "CHE";

    private String bank;

    @Builder.Default
    private String branch = "001";

    @Builder.Default
    private String locality = "1234";

    /** External check number */
    private String checkNumber;

    @Builder.Default
    private Integer accountCode = 1234;

    @Builder.Default
    private String holder = "";

    /** Payment date (may include full timestamp) */
    private String paymentDate;

    private BigDecimal amount;
}
