package org.wallet.walletserver.services;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;
import org.wallet.walletdata.model.User;
import org.wallet.walletdata.repositories.UserRepository;
import org.wallet.walletserver.services.exceptions.UserNotFoundException;
import org.wallet.walletserver.services.jpa.UserService;
import org.wallet.walletserver.services.jpa.UserServiceImpl;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Before
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

        assertEquals(foundUser.getName(),"testUser");
    }

    @Test(expected=UserNotFoundException.class)
    public void whenFindUserById2_thenThrowException() {
        UserService userService = new UserServiceImpl(userRepository);
        userService.getUser(2L);
    }
}
