package org.wallet.walletdata.model;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class Balance {
    private Map<Currency, BigDecimal> balance = new HashMap<>();

    public Map<Currency, BigDecimal> getBalance() {
        return balance;
    }

    public void setBalance(Map<Currency, BigDecimal> balance) {
        this.balance = balance;
    }
}
