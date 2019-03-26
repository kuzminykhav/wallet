package org.wallet.walletdata.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.wallet.walletdata.model.Currency;
import org.wallet.walletdata.model.Transaction;
import org.wallet.walletdata.model.User;
import org.wallet.walletdata.model.Wallet;

import java.util.Optional;

@Repository
public interface TransactionRepository extends CrudRepository<Transaction, Long> {

    Optional<Transaction> findByUserAndCurrencyAndWalletAndLastTransaction(User user, Currency currency, Wallet wallet, Boolean lastTransaction);

    Iterable<Transaction> findByUserAndLastTransaction(User user, Boolean lastTransaction);

}
