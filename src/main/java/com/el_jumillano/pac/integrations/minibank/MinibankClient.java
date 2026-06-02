package com.el_jumillano.pac.integrations.minibank;

import com.el_jumillano.pac.deposits.domain.Deposit;

import java.time.LocalDate;
import java.util.List;

public interface MinibankClient {

    List<Deposit> getDepositsByDate(LocalDate date, Integer cashierId);
}
