package org.wallet.walletdata.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Data
public class Balance {
    private Map<Currency, BigDecimal> balance = new HashMap<>();

}
