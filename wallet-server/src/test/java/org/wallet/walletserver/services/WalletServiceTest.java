package org.wallet.walletserver.services;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;
import org.wallet.walletdata.model.Currency;
import org.wallet.walletdata.model.User;
import org.wallet.walletdata.model.Wallet;
import org.wallet.walletdata.repositories.WalletRepository;
import org.wallet.walletserver.services.jpa.UserService;
import org.wallet.walletserver.services.jpa.WalletService;
import org.wallet.walletserver.services.jpa.WalletServiceImpl;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class WalletServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private WalletRepository walletRepository;

    @Before
    public void setup() {
        User user = new User("testUser");
        user.setId(1L);

        Wallet walletEur = new Wallet(user, Currency.EUR);
        walletEur.setName("EUR wallet");
        Optional<Wallet> walletAnswerEur = Optional.of(walletEur);

        Wallet walletUsd = new Wallet(user, Currency.USD);
        walletUsd.setName("USD wallet");

        when(userService.getUser(1L)).thenReturn(user);
        when(walletRepository.findByUserAndCurrency(user,Currency.EUR)).thenReturn(walletAnswerEur);
        when(walletRepository.save(any())).thenReturn(walletUsd);
    }

    @Test
    public void whenFindWallet_thenReturnExistingWallet() {
        WalletService walletService = new WalletServiceImpl(walletRepository,userService);

        Wallet foundWallet = walletService.findWallet(1L,Currency.EUR);

        assertEquals(foundWallet.getName(),"EUR wallet");
        assertEquals(foundWallet.getCurrency(),Currency.EUR);
    }

    @Test
    public void whenFindWallet_thenCreatingWallet() {
        WalletService walletService = new WalletServiceImpl(walletRepository,userService);
        Wallet foundWallet = walletService.findWallet(1L,Currency.USD);

        assertEquals(foundWallet.getName(),"USD wallet");
        assertEquals(foundWallet.getCurrency(),Currency.USD);
    }

}
