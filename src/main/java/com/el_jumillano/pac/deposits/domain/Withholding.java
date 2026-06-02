package com.el_jumillano.pac.deposits.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Bank withholding (RIB) linked to a deposit.
 * Its amount is accumulated in manualWithholdingsTotal during reconciliation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Withholding {

    private Long id;

    /** FK → Deposit.id */
    private Long depositId;

    /** Internal account number */
    private Long accountNumber;

    @Builder.Default
    private ItemType itemType = ItemType.WITHHOLDING;

    /** Concept code — e.g. RIB, RGA, RCI. See ConceptoCatalog. */
    @Builder.Default
    private String concepto = "RIB";

    /** External withholding number */
    private String withholdingNumber;

    /** Withholding date (may include full timestamp) */
    private String paymentDate;

    private BigDecimal amount;

    /** Descriptive withholding type (e.g. "IIBB", "GANANCIAS") */
    private String type;
}
