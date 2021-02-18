package org.wallet.walletserver.services.jpa;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.wallet.walletdata.model.*;
import org.wallet.walletserver.services.exceptions.InsufficientFundsException;
import org.wallet.walletdata.repositories.TransactionRepository;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserService userService;
    private final WalletService walletService;

    @Override
    @Transactional
    public void depositTransaction(Long userId, BigDecimal amount, Currency currency) {
        ArrayList<Transaction> transactions = new ArrayList<>();
        User currentUser = userService.getUser(userId);
        Wallet userWallet = walletService.findWallet(userId, currency);

        Transaction newTransaction = Transaction.builder()
                .description("deposit " + amount.toString() + "for user " + userId)
                .currency(currency)
                .wallet(userWallet)
                .user(currentUser)
                .amount(amount)
                .startBalance(BigDecimal.ZERO)
                .lastTransaction(true)
                .build();

        transactionRepository
                .findByUserAndCurrencyAndWalletAndLastTransaction(currentUser, currency, userWallet, true)
                .ifPresent(t -> {
                    newTransaction.setStartBalance(t.getEndBalance());
                    t.setLastTransaction(false);
                    transactions.add(t);
                });

        newTransaction.setEndBalance(newTransaction.getStartBalance().add(newTransaction.getAmount()));
        transactions.add(newTransaction);

        transactionRepository.saveAll(transactions);
    }

    @Override
    public void withdrawTransaction(Long userId, BigDecimal amount, Currency currency) {
        ArrayList<Transaction> transactions = new ArrayList<>();
        User currentUser = userService.getUser(userId);
        Wallet userWallet = walletService.findWallet(userId, currency);
        Transaction lastTransaction = transactionRepository
                .findByUserAndCurrencyAndWalletAndLastTransaction(currentUser, currency, userWallet, true)
                .orElseThrow(() -> new InsufficientFundsException(currency.name() + " wallet is not found"));

        if (lastTransaction.getEndBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds");
        }

        Transaction transaction = Transaction.builder()
                .description("withdraw " + amount.toString() + "for user " + userId)
                .currency(currency)
                .wallet(userWallet)
                .user(currentUser)
                .amount(amount)
                .startBalance(lastTransaction.getEndBalance())
                .lastTransaction(true)
                .endBalance(lastTransaction.getEndBalance().subtract(amount))
                .build();

        lastTransaction.setLastTransaction(false);
        transactions.add(lastTransaction);
        transactions.add(transaction);

        transactionRepository.saveAll(transactions);
    }

    @Override
    public Balance getBalance(Long userId) {
        Balance balance = new Balance();
        User user = userService.getUser(userId);
        transactionRepository.findByUserAndLastTransaction(user, true)
                .forEach(transaction ->
                        balance.getBalance().put(transaction.getCurrency(), transaction.getEndBalance()));

        return balance;
    }
}
