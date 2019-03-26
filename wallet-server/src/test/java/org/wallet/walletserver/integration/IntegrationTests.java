package org.wallet.walletserver.integration;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.wallet.walletdata.model.*;
import org.wallet.walletdata.repositories.TransactionRepository;
import org.wallet.walletdata.repositories.UserRepository;
import org.wallet.walletdata.repositories.WalletRepository;
import org.wallet.walletserver.services.jpa.TransactionService;
import org.wallet.walletserver.services.jpa.UserService;
import org.wallet.walletserver.services.jpa.WalletService;
import org.wallet.walletserver.services.exceptions.InsufficientFundsException;

import java.math.BigDecimal;

import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
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

    @Before
    public void setup() {
        User user = new User("testUser");
        user.setId(1L);

        Wallet testWallet = new Wallet();
        testWallet.setName("USD Wallet");
        testWallet.setCurrency(Currency.USD);

        userRepository.save(user);
        walletRepository.save(testWallet);

    }

    @Test(expected = InsufficientFundsException.class)
    public void whenTest1WithdrawFromEmptyWallet_thenThrowException() {
        transactionService.withdrawTransaction(1L, BigDecimal.valueOf(200L), Currency.USD);
    }

    @Test
    public void whenTest2DepositToUSDWallet_thenReturnSuccess() {
        transactionService.depositTransaction(1L, BigDecimal.valueOf(100L), Currency.USD);
    }

    @Test
    public void whenTest3GetBallance_thenReturn100USD() {
        Balance balance = transactionService.getBalance(1L);
        Integer balanceCOmpare = balance.getBalance().get(Currency.USD).compareTo(BigDecimal.valueOf(100L));
        BigDecimal balanceUSD = balance.getBalance().get(Currency.USD);
        assertTrue(balance.getBalance().get(Currency.USD).compareTo(BigDecimal.valueOf(100L)) == 0);

    }

    @Test(expected = InsufficientFundsException.class)
    public void whenTest4WithdrawFromUSD100Wallet_thenThrowException() {
        transactionService.withdrawTransaction(1L, BigDecimal.valueOf(200L), Currency.USD);
    }

    @Test
    public void whenTest5DepositAnother100ToEURWallet_thenReturnSuccess() {
        transactionService.depositTransaction(1L, BigDecimal.valueOf(100L), Currency.EUR);
    }

    @Test
    public void whenTest6GetBallance_thenReturn100USD100EUR() {
        Balance balance = transactionService.getBalance(1L);
        assertTrue(balance.getBalance().get(Currency.USD).compareTo(BigDecimal.valueOf(100L)) == 0);
        assertTrue(balance.getBalance().get(Currency.EUR).compareTo(BigDecimal.valueOf(100L)) == 0);
    }

    @Test(expected = InsufficientFundsException.class)
    public void whenTest7WithdrawFromUSD100WalletAgain_thenThrowException() {
        transactionService.withdrawTransaction(1L, BigDecimal.valueOf(200L), Currency.USD);
    }

    @Test
    public void whenTest8DepositAnother100ToUSDWallet_thenReturnSuccess() {
        transactionService.depositTransaction(1L, BigDecimal.valueOf(100L), Currency.USD);
    }

    @Test
    public void whenTest90GetBallance_thenReturn200USD100EUR() {
        Balance balance = transactionService.getBalance(1L);
        assertTrue(balance.getBalance().get(Currency.USD).compareTo(BigDecimal.valueOf(200L)) == 0);
        assertTrue(balance.getBalance().get(Currency.EUR).compareTo(BigDecimal.valueOf(100L)) == 0);
    }

    @Test
    public void whenTest91WithdrawFromUSD200WalletAgain_thenReturnSuccess() {
        transactionService.withdrawTransaction(1L, BigDecimal.valueOf(200L), Currency.USD);
    }

    @Test
    public void whenTest92GetBallance_thenReturn0USD100EUR() {
        Balance balance = transactionService.getBalance(1L);
        assertTrue(balance.getBalance().get(Currency.USD).compareTo(BigDecimal.ZERO) == 0);
        assertTrue(balance.getBalance().get(Currency.EUR).compareTo(BigDecimal.valueOf(100L)) == 0);
    }

    @Test(expected = InsufficientFundsException.class)
    public void whenTest93WithdrawFromUSD200WalletAgain_thenThrowException() {
        transactionService.withdrawTransaction(1L, BigDecimal.valueOf(200L), Currency.USD);
    }


}
