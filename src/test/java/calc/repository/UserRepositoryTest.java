package calc.repository;

import calc.DTO.UserDTO;
import calc.config.Application;
import calc.entity.Game;
import calc.entity.Outcome;
import calc.entity.Stats;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Iterator;
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
//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = {Application.class})
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@Transactional
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    User john = new User("john","JOHN","johnjohn","john@john.com");
    User bob = new User("bob","BOB","bobbob","bob@bob.com");
    User alex = new User("alex","ALEX","alexalex","alex@alex.com");

    @Before
    public void setUp() {

        john.setExternalId(UUID.randomUUID().toString());
        bob.setExternalId(UUID.randomUUID().toString());
        alex.setExternalId(UUID.randomUUID().toString());

        john.setStats(Arrays.asList(new Stats(john, null)));
        bob.setStats(Arrays.asList(new Stats(bob,null)));
        alex.setStats(Arrays.asList(new Stats(alex,null)));
/*
        john.setOutcomes(Arrays.asList(new Outcome()));
        bob.setOutcomes(Arrays.asList(new Outcome()));
        alex.setOutcomes(Arrays.asList(new Outcome( 10L, Outcome.Result.WIN, new Game()), alex));

        List<User> allUsers = Arrays.asList(john, bob, alex);
 */
        john = userRepository.save(john);
        bob = userRepository.save(bob);
        alex = userRepository.save(alex);

/*        Mockito.when(userRepository.findByUserName(john.getUserName())).thenReturn(john);
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

        found = userRepository.findByUserName(given.getUserName());
        assertThat(found.equals(given)).isTrue();
    }

    @Test
    public void whenValidUserId_thenUserShouldBeFound() {
        User given = john;
        User found = null;

        found = userRepository.findByUserId(given.getUserId());
        assertThat(found.equals(john)).isTrue();
    }

    @Test
    public void whenValidUExternalId_thenUserShouldBeFound() {
        User given = john;
        User found = null;

        found = userRepository.findByExternalId(given.getExternalId());
        assertThat(found.equals(john));
    }

    @Test
    public void whenInvalidUserName_thenUserShouldNotBeFound() {
        User found = null;

        found = userRepository.findByUserName("notanexistingusername");
        assertThat(found).isNull();
    }

    @Test
    public void whenInvalidUserId_thenUserShouldNotBeFound() {
        User given = john;
        User found = null;

        found = userRepository.findByUserId(given.getUserId());
        assertThat(found.equals(john)).isTrue();
    }

    @Test
    public void whenInvalidUExternalId_thenUserShouldNotBeFound() {
        User found = null;

        found = userRepository.findByExternalId(99L + "");
        assertThat(found).isNull();
    }

    @Test
    public void given3Users_whengetAll_thenReturn3Records() {
        Iterable<User> allUsers = userRepository.findAll();
        assertThat(allUsers).hasSize(3).extracting(User::getUserName).contains(alex.getUserName(), john.getUserName(), bob.getUserName());
    }

    /*
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
    }*/
}