package org.wallet.walletserver.integration;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.wallet.walletdata.model.*;
import org.wallet.walletdata.repositories.TransactionRepository;
import org.wallet.walletdata.repositories.UserRepository;
import org.wallet.walletdata.repositories.WalletRepository;
import org.wallet.walletserver.services.jpa.TransactionService;
import org.wallet.walletserver.services.jpa.UserService;
import org.wallet.walletserver.services.jpa.WalletService;
import org.wallet.walletserver.services.exceptions.InsufficientFundsException;

import java.math.BigDecimal;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class IntegrationTests {

    @Autowired
    private UserService userService;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private WalletService walletService;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private WalletRepository walletRepository;
    @Autowired
    private TransactionRepository transactionRepository;

    @Test
    public void integrationTest() {
        User user = new User("testUser");
        user.setId(1L);

        Wallet testWallet = new Wallet();
        testWallet.setName("USD Wallet");
        testWallet.setCurrency(Currency.USD);

        userRepository.save(user);
        walletRepository.save(testWallet);
        assertThrows(InsufficientFundsException.class, () -> transactionService.withdrawTransaction(1L, BigDecimal.valueOf(200L), Currency.USD));

        transactionService.depositTransaction(1L, BigDecimal.valueOf(100L), Currency.USD);
        //whenTest3GetBallance_thenReturn100USD
        Balance balance = transactionService.getBalance(1L);
        Integer balanceCOmpare = balance.getBalance().get(Currency.USD).compareTo(BigDecimal.valueOf(100L));
        BigDecimal balanceUSD = balance.getBalance().get(Currency.USD);
        assertEquals(balance.getBalance().get(Currency.USD).compareTo(BigDecimal.valueOf(100L)), 0);
        //whenTest4WithdrawFromUSD100Wallet_thenThrowException
        assertThrows(InsufficientFundsException.class, () -> transactionService.withdrawTransaction(1L, BigDecimal.valueOf(200L), Currency.USD));

        transactionService.depositTransaction(1L, BigDecimal.valueOf(100L), Currency.EUR);
        //whenTest6GetBallance_thenReturn100USD100EUR
        balance = transactionService.getBalance(1L);
        assertTrue(balance.getBalance().get(Currency.USD).compareTo(BigDecimal.valueOf(100L)) == 0);
        assertTrue(balance.getBalance().get(Currency.EUR).compareTo(BigDecimal.valueOf(100L)) == 0);
        //whenTest7WithdrawFromUSD100WalletAgain_thenThrowException
        assertThrows(InsufficientFundsException.class, () -> transactionService.withdrawTransaction(1L, BigDecimal.valueOf(200L), Currency.USD));

        transactionService.depositTransaction(1L, BigDecimal.valueOf(100L), Currency.USD);

        //whenTest90GetBallance_thenReturn200USD100EUR
        balance = transactionService.getBalance(1L);
        assertTrue(balance.getBalance().get(Currency.USD).compareTo(BigDecimal.valueOf(200L)) == 0);
        assertTrue(balance.getBalance().get(Currency.EUR).compareTo(BigDecimal.valueOf(100L)) == 0);

        transactionService.withdrawTransaction(1L, BigDecimal.valueOf(200L), Currency.USD);
        //whenTest92GetBallance_thenReturn0USD100EUR
        balance = transactionService.getBalance(1L);
        assertTrue(balance.getBalance().get(Currency.USD).compareTo(BigDecimal.ZERO) == 0);
        assertTrue(balance.getBalance().get(Currency.EUR).compareTo(BigDecimal.valueOf(100L)) == 0);
        //whenTest93WithdrawFromUSD200WalletAgain_thenThrowException
        assertThrows(InsufficientFundsException.class, () -> transactionService.withdrawTransaction(1L, BigDecimal.valueOf(200L), Currency.USD));
    }


}
