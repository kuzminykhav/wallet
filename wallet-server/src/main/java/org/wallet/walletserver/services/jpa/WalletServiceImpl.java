package org.wallet.walletserver.services.jpa;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.wallet.walletdata.model.*;
import org.wallet.walletdata.repositories.WalletRepository;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final UserService userService;


    @Override
    public Wallet findWallet(Long userId, Currency currency) {
        User user = userService.getUser(userId);
        return walletRepository.findByUserAndCurrency(user, currency).orElseGet(() ->walletRepository.save(new Wallet(user, currency)));
    }

    @Override
    public List<Wallet> findAllUserWallets(Long userId) {
        User user = userService.getUser(userId);
        ArrayList<Wallet> wallets = new ArrayList<>();
        walletRepository.findByUser(user).forEach(wallets::add);
        return wallets;
    }
}
