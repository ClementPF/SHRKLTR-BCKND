package calc.service;

import calc.DTO.*;
import calc.config.Application;
import calc.entity.*;
import calc.repository.GameRepository;
import calc.repository.SportRepository;
import calc.repository.TournamentRepository;
import calc.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import java.util.*;

import static org.assertj.core.api.Assertions.*;

/**
 * Created by clementperez on 03/06/18.
 */

/*
@SpringBootTest(classes = {Application.class})
//@PropertySource(value = {"classpath:application.properties", "${api.config.location}"}, ignoreResourceNotFound = true)
@TestExecutionListeners(inheritListeners = false, listeners = {
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class
})
@RunWith(SpringRunner.class)
public class RivalryServiceTest {

    @Autowired
    private RivalryStatsService rivalryStatsService;
    @Autowired
    private UserService userService;
    @Autowired
    private TournamentService tournamentService;
    @Autowired
    private GameService gameService;
    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SportRepository sportRepository;

    @Autowired
    private TournamentRepository tournamentRepository;

    User user1;
    User user2;
    Sport sport1;
    Tournament tournament1;

   // @Before
    public void setUp() {
        User john = new User(UUID.randomUUID().toString());
        john.setUserId(11L);
        john.setExternalId(UUID.randomUUID().toString());

        User alex = new User(UUID.randomUUID().toString());
        alex.setUserId(12L);
        alex.setExternalId(UUID.randomUUID().toString());

        user1 = userRepository.save(john);
        user2 = userRepository.save(alex);

        Sport pingpong = new Sport(UUID.randomUUID().toString());
        sport1 = sportRepository.save(pingpong);

        Tournament pingpongTournament = new Tournament(UUID.randomUUID().toString(),sport1,user1);
        tournament1 = tournamentRepository.save(pingpongTournament);
    }



    //@Test
    public void whenValidGame_thenRivalryStatsShouldBeFOund() {

        User john = userRepository.findByUserName(user1.getUserName());
        User alex = userRepository.findByUserName(user2.getUserName());
        double value = 10.10;

        Tournament pingpongTournament = tournamentRepository.findByName(tournament1.getName());

        Game game = new Game(pingpongTournament);
        game.setGameId(10L);

        Outcome winnerOutcome = new Outcome(value, Outcome.Result.WIN,game,john);
        Outcome looserOutcome = new Outcome(value, Outcome.Result.LOSS,game,alex);

        List<Outcome> outcomeDTOs = Arrays.asList(winnerOutcome,looserOutcome);

        Game g = new Game(pingpongTournament,outcomeDTOs);

        rivalryStatsService.recalculateAfterOutcome(g.getOutcomes().get(0),g.getOutcomes().get(1));
        rivalryStatsService.recalculateAfterOutcome(g.getOutcomes().get(1),g.getOutcomes().get(0));

        RivalryStatsDTO rivalryStats = rivalryStatsService.findByUserAndRivalAndTournament(john.getUserId(),alex.getUserId(),pingpongTournament.getTournamentId());

        assertThat(rivalryStats.getScore()).isEqualTo(value);
    }

    //@Test
    public void whenValidGames_thenRivalryStatsShouldBeValid() {

        User john = userRepository.findByUserName(user1.getUserName());
        User alex = userRepository.findByUserName(user2.getUserName());
        double value = 10.10;

        int rdm = new Random().nextInt(10);

        Tournament pingpongTournament = tournamentRepository.findByName(tournament1.getName());

        Game game = new Game(pingpongTournament);
        game.setGameId(10L);

        Outcome winnerOutcome = new Outcome(value, Outcome.Result.WIN,game,john);
        Outcome looserOutcome = new Outcome(-value, Outcome.Result.LOSS,game,alex);

        List<Outcome> outcomeDTOs = Arrays.asList(winnerOutcome,looserOutcome);

        Game g = new Game(pingpongTournament,outcomeDTOs);

        for(int i = 0; i < rdm; i++){
            rivalryStatsService.recalculateAfterOutcome(g.getOutcomes().get(0),g.getOutcomes().get(1));
            rivalryStatsService.recalculateAfterOutcome(g.getOutcomes().get(1),g.getOutcomes().get(0));
        }

        RivalryStatsDTO rivalryStats = rivalryStatsService.findByUserAndRivalAndTournament(john.getUserId(),alex.getUserId(),pingpongTournament.getTournamentId());

        assertThat(rivalryStats.getScore()).isEqualTo(value*rdm);
        assertThat(rivalryStats.getGameCount()).isEqualTo(rdm);
        assertThat(rivalryStats.getWinCount()).isEqualTo(rdm);
        assertThat(rivalryStats.getLonguestWinStreak()).isEqualTo(rdm);
    }
}
*/