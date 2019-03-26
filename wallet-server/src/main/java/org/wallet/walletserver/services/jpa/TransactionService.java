package org.wallet.walletserver.services.jpa;

import org.wallet.walletdata.model.Balance;
import org.wallet.walletdata.model.Currency;

import java.math.BigDecimal;

public interface TransactionService {

    void depositTransaction(Long userId, BigDecimal amount, Currency currency);

    void withdrawTransaction(Long userId, BigDecimal amount, Currency currency);

    Balance getBalance(Long userId);
}
