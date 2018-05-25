package calc.service;

import calc.DTO.UserDTO;
import calc.config.Application;
import calc.entity.Stats;
import calc.entity.User;
import calc.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.verification.VerificationModeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by clementperez on 25/05/18.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@Transactional
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockitoAnnotations.Mock
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
*/
        List<User> allUsers = Arrays.asList(john, bob, alex);

        userRepository.save(john);

        Mockito.when(userRepository.findByUserName(john.getUserName())).thenReturn(john);
        Mockito.when(userRepository.findByUserName(alex.getUserName())).thenReturn(alex);
        Mockito.when(userRepository.findByUserName(bob.getUserName())).thenReturn(bob);

        Mockito.when(userRepository.findByUserName("wrong_name")).thenReturn(null);

        Mockito.when(userRepository.findByUserId(john.getUserId())).thenReturn(john);
        Mockito.when(userRepository.findByUserId(alex.getUserId())).thenReturn(alex);
        Mockito.when(userRepository.findByUserId(bob.getUserId())).thenReturn(bob);

        Mockito.when(userRepository.findByExternalId(john.getExternalIdProvider())).thenReturn(john);
        Mockito.when(userRepository.findByExternalId(alex.getExternalIdProvider())).thenReturn(alex);
        Mockito.when(userRepository.findByExternalId(bob.getExternalIdProvider())).thenReturn(bob);

        //Mockito.when(userRepository.findByUserId(john.getUserId()).orElse(null)).thenReturn(john);
        Mockito.when(userRepository.findAll()).thenReturn(allUsers);
        //Mockito.when(userRepository.findByUserId(-99L).orElse(null)).thenReturn(null);
    }

    @Test
    public void whenValidUserName_thenUserShouldBeFound() {
        User given = john;
        UserDTO found = null;

        found = userService.findByUserName(given.getUserName());
        assertThat(found.getUsername().equals(given.getUserName())).isTrue();

        verifyFindByUserNameIsCalledOnce(given.getUserName());
    }

    @Test
    public void whenValidUserId_thenUserShouldBeFound() {
        User given = john;
        UserDTO found = null;

        found = userService.findByUserId(given.getUserId());
        assertThat(found.getUserId().equals(given.getUserId())).isTrue();

        verifyFindByUserIdIsCalledOnce();
    }

    @Test
    public void whenValidUExternalId_thenUserShouldBeFound() {
        User given = john;
        UserDTO found = null;

        found = userService.findByUserName(given.getExternalId());
        assertThat(found.getUsername().equals(given.getExternalId())).isTrue();

        verifyFindByExternalIdIsCalledOnce(given.getExternalId());
    }

    @Test
    public void given3Users_whengetAll_thenReturn3Records() {
        List<UserDTO> allUsers = userService.findAll();

        assertThat(allUsers).hasSize(3).extracting(UserDTO::getUsername).contains(alex.getUserName(), john.getUserName(), bob.getUserName());
        verifyFindAllUsersIsCalledOnce();
    }

    @Test
    public void giverUser_thenReturnConvertedUser() {

        User given = john;
        UserDTO converted = userService.convertToDto(given);

        assertThat(converted.getUserId()).isEqualTo(given.getUserId());
        assertThat(converted.getUsername()).isEqualTo(given.getUserName());
        assertThat(converted.getFirstName()).isEqualTo(given.getFirst());
        assertThat(converted.getLastName()).isEqualTo(given.getLast());
        //assertThat(converted.getStats()).isEqualTo(given.getStats());
    }

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
