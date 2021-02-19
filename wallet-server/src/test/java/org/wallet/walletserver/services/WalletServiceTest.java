package org.wallet.walletserver.services;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.wallet.walletdata.model.Currency;
import org.wallet.walletdata.model.User;
import org.wallet.walletdata.model.Wallet;
import org.wallet.walletdata.repositories.WalletRepository;
import org.wallet.walletserver.services.jpa.UserService;
import org.wallet.walletserver.services.jpa.WalletService;
import org.wallet.walletserver.services.jpa.WalletServiceImpl;

import java.util.Optional;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WalletServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private WalletRepository walletRepository;

    @Test
    public void whenFindWallet_thenReturnExistingWallet() {
        User user = new User("testUser");
        user.setId(1L);
        Wallet walletEur = new Wallet(user, Currency.EUR);
        walletEur.setName("EUR wallet");
        Optional<Wallet> walletAnswerEur = Optional.of(walletEur);
        when(userService.getUser(1L)).thenReturn(user);
        when(walletRepository.findByUserAndCurrency(user, Currency.EUR)).thenReturn(walletAnswerEur);
        WalletService walletService = new WalletServiceImpl(walletRepository, userService);

        Wallet foundWallet = walletService.findWallet(1L, Currency.EUR);

        assertEquals("EUR wallet", foundWallet.getName());
        assertEquals(Currency.EUR, foundWallet.getCurrency());
    }

    @Test
    public void whenFindWallet_thenCreatingWallet() {
        User user = new User("testUser");
        user.setId(1L);
        Wallet walletUsd = new Wallet(user, Currency.USD);
        walletUsd.setName("USD wallet");
        when(userService.getUser(1L)).thenReturn(user);
        when(walletRepository.findByUserAndCurrency(user, Currency.USD)).thenReturn(Optional.empty());
        // when(walletRepository.findByUserAndCurrency(user, Currency.USD)).thenReturn(walletAnswerUsd);
        when(walletRepository.save(walletUsd)).thenReturn(walletUsd);
        WalletService walletService = new WalletServiceImpl(walletRepository, userService);
        Wallet foundWallet = walletService.findWallet(1L, Currency.USD);

        assertEquals("USD wallet", foundWallet.getName());
        assertEquals(Currency.USD, foundWallet.getCurrency());
    }

}
