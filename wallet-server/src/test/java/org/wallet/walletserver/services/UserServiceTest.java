package org.wallet.walletserver.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.wallet.walletdata.model.User;
import org.wallet.walletdata.repositories.UserRepository;
import org.wallet.walletserver.services.exceptions.UserNotFoundException;
import org.wallet.walletserver.services.jpa.UserService;
import org.wallet.walletserver.services.jpa.UserServiceImpl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    public void setup() {
        User user = new User("testUser");
        user.setId(1L);
        Optional<User> userAnswer = Optional.of(user);
        when(userRepository.findById(1L)).thenReturn(userAnswer);
        when(userRepository.findById(2L)).thenThrow(new UserNotFoundException("User 2L is not found"));
    }

    @Test
    public void whenFindUserById1_thenReturnUser() {
        UserService userService = new UserServiceImpl(userRepository);

        User foundUser = userService.getUser(1L);

        assertEquals(foundUser.getName(), "testUser");
    }

    @Test
    public void whenFindUserById2_thenThrowException() {
        UserService userService = new UserServiceImpl(userRepository);
        assertThrows(UserNotFoundException.class, () -> userService.getUser(2L));
    }
}
