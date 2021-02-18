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
        User user = userService.getUser(userId);
        Wallet wallet = walletService.findWallet(userId, currency);

        Transaction transaction = new Transaction();
        transaction.setDescription("deposit " + amount.toString() + "for user " + userId);
        transaction.setCurrency(currency);
        transaction.setWallet(wallet);
        transaction.setUser(user);
        transaction.setAmount(amount);

        Optional<Transaction> lastTransaction = transactionRepository.findByUserAndCurrencyAndWalletAndLastTransaction(user, currency, wallet, true);
        if (lastTransaction.isPresent()) {
            transaction.setStartBalance(lastTransaction.get().getEndBalance());
            lastTransaction.get().setLastTransaction(false);
            transactions.add(lastTransaction.get());
        } else {
            transaction.setStartBalance(BigDecimal.ZERO);
        }
        transaction.setEndBalance(transaction.getStartBalance().add(transaction.getAmount()));
        transaction.setLastTransaction(true);
        transactions.add(transaction);

        transactionRepository.saveAll(transactions);
    }

    @Override
    public void withdrawTransaction(Long userId, BigDecimal amount, Currency currency) {
        User user = userService.getUser(userId);
        Wallet wallet = walletService.findWallet(userId, currency);
        Optional<Transaction> lastTransaction = transactionRepository.findByUserAndCurrencyAndWalletAndLastTransaction(user, currency, wallet, true);
        ArrayList<Transaction> transactions = new ArrayList<>();
        if (lastTransaction.isPresent()) {
            if (lastTransaction.get().getEndBalance().compareTo(amount) < 0) {
                throw new InsufficientFundsException("Insufficient funds");
            }
        } else {
            throw new InsufficientFundsException(currency.name() + " wallet is not found");
        }
        Transaction transaction = new Transaction();
        transaction.setDescription("withdraw " + amount.toString() + "for user " + userId);
        transaction.setCurrency(currency);
        transaction.setWallet(wallet);
        transaction.setUser(user);
        transaction.setAmount(amount);
        transaction.setStartBalance(lastTransaction.get().getEndBalance());
        lastTransaction.get().setLastTransaction(false);
        transactions.add(lastTransaction.get());
        transaction.setEndBalance(transaction.getStartBalance().subtract(amount));
        transaction.setLastTransaction(true);
        transactions.add(transaction);

        transactionRepository.saveAll(transactions);
    }

    @Override
    public Balance getBalance(Long userId) {
        Balance balance = new Balance();
        User user = userService.getUser(userId);
        transactionRepository.findByUserAndLastTransaction(user, true).forEach(transaction ->
                balance.getBalance().put(transaction.getCurrency(), transaction.getEndBalance()));

        return balance;
    }
}
