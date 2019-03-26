package org.wallet.walletserver.services.jpa;

import org.springframework.stereotype.Service;
import org.wallet.walletserver.services.exceptions.UserNotFoundException;
import org.wallet.walletdata.model.User;
import org.wallet.walletdata.repositories.UserRepository;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User getUser(Long id) {
        return userRepository.findById(id).orElse(userRepository.save(new User(id,"testuser "+id.toString())));
    }
}
