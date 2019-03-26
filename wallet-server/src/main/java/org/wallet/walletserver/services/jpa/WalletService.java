package org.wallet.walletserver.services.jpa;

import org.wallet.walletdata.model.Currency;
import org.wallet.walletdata.model.Wallet;

import java.util.List;

public interface WalletService {

    Wallet findWallet(Long userId, Currency currency);

    List<Wallet> findAllUserWallets(Long userId);
}
