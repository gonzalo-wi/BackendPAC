package com.el_jumillano.pac.cashiers.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cashier {

    private Long id;
    private Integer externalCashierNumber;
    private Long plantId;
    private String name;
    private boolean active;
}
