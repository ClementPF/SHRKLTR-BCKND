package calc.repository;

import calc.DTO.UserDTO;
import calc.config.Application;
import calc.entity.User;
import calc.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.*;
/**
 * Created by clementperez on 11/05/18.
 */

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

//@SpringBootTest(classes = {Application.class})
//@PropertySource(value = {"classpath:application.properties", "${api.config.location}"}, ignoreResourceNotFound = true)
/*@TestExecutionListeners(inheritListeners = false, listeners = {
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class
})*/
//@WebMvcTest
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    User john = new User("john","JOHN","johnjohn","john@john.com");
    User bob = new User("bob","BOB","bobbob","bob@bob.com");
    User alex = new User("alex","ALEX","alexalex","alex@alex.com");

    @Before
    public void setUp() {
        john.setUserId(11L);
        bob.setUserId(12L);
        alex.setUserId(13L);

        john.setExternalId(UUID.randomUUID().toString());
        bob.setExternalId(UUID.randomUUID().toString());
        alex.setExternalId(UUID.randomUUID().toString());

        List<User> allUsers = Arrays.asList(john, bob, alex);
/*
        Mockito.when(userRepository.findByUserName(john.getUserName())).thenReturn(john);
        Mockito.when(userRepository.findByUserName(alex.getUserName())).thenReturn(alex);
        Mockito.when(userRepository.findByUserName("wrong_name")).thenReturn(null);
        //Mockito.when(userRepository.findByUserId(john.getUserId()).orElse(null)).thenReturn(john);
        Mockito.when(userRepository.findAll()).thenReturn(allUsers);
        //Mockito.when(userRepository.findByUserId(-99L).orElse(null)).thenReturn(null);
   */ }

    @Test
    public void whenValidUserName_thenUserShouldBeFound() {

        User given = john;

        User found = null;

        found = john; //userRepository.findByUserName(given.getUserName());

        assertThat(found.isEqualTo(alex)).isTrue();
    }

    @Test
    public void whenValidUserId_thenUserShouldBeFound() {
        User given = john;
        User found = null;

        found = userRepository.findByUserId(given.getUserId());
        assertThat(found.isEqualTo(john)).isTrue();

        this.verifyFindByUserIdIsCalledOnce();
    }

    @Test
    public void whenValidUExternalId_thenUserShouldBeFound() {
        User given = john;
        User found = null;

        found = userRepository.findByExternalId(given.getExternalId());
        assertThat(found.isEqualTo(john));

        this.verifyFindByExternalIdIsCalledOnce(given.getExternalId());
    }
/*
    @Test
    public void whenInValidName_thenUserShouldNotBeFound() {
        UserDTO fromDb = userService.findByUserName("wrong_name");
        assertThat(fromDb).isNull();

        verifyFindByUserNameIsCalledOnce("wrong_name");
    }

    @Test
    public void whenValidName_thenUserShouldExist() {
        boolean doesUserExist = userService.exists("john");
        assertThat(doesUserExist).isEqualTo(true);

        verifyFindByUserNameIsCalledOnce("john");
    }

    @Test
    public void whenNonExistingName_thenUserShouldNotExist() {
        boolean doesUserExist = userService.exists("some_name");
        assertThat(doesUserExist).isEqualTo(false);

        verifyFindByUserNameIsCalledOnce("some_name");
    }

    @Test
    public void whenValidId_thenUserShouldBeFound() {
        UserDTO fromDb = userService.findByUserId(11L);
        assertThat(fromDb.getUsername()).isEqualTo("john");

        verifyFindByUserIdIsCalledOnce();
    }

    @Test
    public void whenInValidId_thenUserShouldNotBeFound() {
        UserDTO fromDb = userService.findByExternalId(-99L + "");
        verifyFindByUserIdIsCalledOnce();
        assertThat(fromDb).isNull();
    }

    @Test
    public void given3Users_whengetAll_thenReturn3Records() {
        UserDTO alex = new UserDTO("alex");
        UserDTO john = new UserDTO("john");
        UserDTO bob = new UserDTO("bob");

        List<UserDTO> allUsers = userService.findAll();
        verifyFindAllUsersIsCalledOnce();
        assertThat(allUsers).hasSize(3).extracting(UserDTO::getClass).contains(alex.getUsername(), john.getUsername(), bob.getUsername());
    }*/

    private void verifyFindByUserNameIsCalledOnce(String name) {
        Mockito.verify(userRepository, VerificationModeFactory.times(1)).findByUserName(name);
        Mockito.reset(userRepository);
    }

    private void verifyFindByUserIdIsCalledOnce() {
        Mockito.verify(userRepository, VerificationModeFactory.times(1)).findByUserId(Mockito.anyLong());
        Mockito.reset(userRepository);
    }

    private void verifyFindByExternalIdIsCalledOnce(String externalId) {
        Mockito.verify(userRepository, VerificationModeFactory.times(1)).findByExternalId(externalId);
        Mockito.reset(userRepository);
    }

    private void verifyFindAllUsersIsCalledOnce() {
        Mockito.verify(userRepository, VerificationModeFactory.times(1)).findAll();
        Mockito.reset(userRepository);
    }
}