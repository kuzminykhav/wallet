package org.wallet.walletdata.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.wallet.walletdata.model.Currency;
import org.wallet.walletdata.model.User;
import org.wallet.walletdata.model.Wallet;

import java.util.Optional;

@Repository
public interface WalletRepository extends CrudRepository<Wallet, Long> {

    Optional<Wallet> findByUserAndCurrency(User user, Currency currency);

    Iterable<Wallet> findByUser(User user);
}
