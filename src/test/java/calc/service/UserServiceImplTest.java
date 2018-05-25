package calc.service;

import calc.DTO.UserDTO;
import calc.config.Application;
import calc.entity.User;
import calc.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by clementperez on 25/05/18.
 */

@SpringBootTest(classes = Application.class)
@RunWith(SpringRunner.class)
public class UserServiceImplTest {

    @TestConfiguration
    static class EmployeeServiceImplTestContextConfiguration {

        @Bean
        public UserService userService() {
            return new UserService();
        }
    }

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @Before
    public void setUp() {
        User alex = new User("alex");

        Mockito.when(userRepository.findByUserName(alex.getUserName()))
                .thenReturn(alex);
    }

    @Test
    public void whenValidName_thenEmployeeShouldBeFound() {
        String name = "alex";
        UserDTO found = userService.findByUserName(name);

        assertThat(found.getUsername())
                .isEqualTo(name);
    }
}