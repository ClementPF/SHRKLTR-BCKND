package calc.service;

import calc.Application;
import calc.DTO.RivalryStatsDTO;
import calc.entity.*;
import calc.repository.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by clementperez on 03/06/18.
 */


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
    private StatsRepository statsRepository;

    @Autowired
    private TournamentRepository tournamentRepository;

    UserServiceTest userServiceTest;
    GameServiceTest gameServiceTest;
    StatsServiceTest statsServiceTest;

    User user1;
    User user2;
    Sport sport1;
    Tournament tournament1;

    @Before
    public void setUp() {

        userServiceTest = new UserServiceTest();
        gameServiceTest = new GameServiceTest();
        statsServiceTest = new StatsServiceTest();

        user1 = userRepository.save(userServiceTest.makeRandomUser());
        user2 = userRepository.save(userServiceTest.makeRandomUser());

        Sport pingpong = new Sport(UUID.randomUUID().toString());
        sport1 = sportRepository.save(pingpong);

        Tournament pingpongTournament = new Tournament(UUID.randomUUID().toString(),sport1,user1);
        tournament1 = tournamentRepository.save(pingpongTournament);
    }



    @Test
    public void whenValidGame_thenRivalryStatsShouldBeFound() {

        Game g = gameServiceTest.makeRandomGame(user1,user2,tournament1);

        gameRepository.save(g);

        Stats s1 = statsServiceTest.makeRandomStats(user1,tournament1);
        Stats s2 = statsServiceTest.makeRandomStats(user2,tournament1);

        RivalryStats rs = rivalryStatsService.recalculateAfterOutcome(s1,g.getOutcomes().get(0),g.getOutcomes().get(1));
        rivalryStatsService.recalculateAfterOutcome(s2,g.getOutcomes().get(1),g.getOutcomes().get(0));

        assertThat(rs.getScore()).isEqualTo(g.getOutcomes().get(0).getScoreValue());
    }

    @Test
    public void whenValidGames_thenRivalryStatsShouldBeValid() {
        double totalScore = 0;

        int rdm = new Random().nextInt(100);

        Stats s1 = statsServiceTest.makeRandomStats(user1,tournament1);
        Stats s2 = statsServiceTest.makeRandomStats(user2,tournament1);
        statsRepository.save(s1);
        statsRepository.save(s2);

        RivalryStats rs1 = null;
        RivalryStats rs2 = null;

        for(int i = 0; i < rdm; i++){
            Boolean b = new Random().nextBoolean();
            Game g = gameServiceTest.makeRandomGame(b ? user1 : user2, !b ? user1 : user2,tournament1);
            gameRepository.save(g);
            totalScore = totalScore + g.getOutcomes().get(b?1:0).getScoreValue();
            rs1 = rivalryStatsService.recalculateAfterOutcome(s1,g.getOutcomes().get(b?1:0),g.getOutcomes().get(!b?1:0));
            rivalryStatsService.save(rs1);
            rs2 = rivalryStatsService.recalculateAfterOutcome(s2,g.getOutcomes().get(b?0:1),g.getOutcomes().get(b?0:1));
            rivalryStatsService.save(rs2);
        }

        assertThat(rs1.getScore()).isEqualTo(totalScore);
        assertThat(rs1.getGameCount()).isEqualTo(rdm);
        assertThat(rs1.getScore()).isEqualTo(-rs2.getScore());
    }

    public RivalryStats makeRandomRivalryStats(User user, User rival, Tournament tournament, Stats stats){
        RivalryStats rivalryStats = new RivalryStats(user, rival,tournament,stats);

        rivalryStats.setScore(new Random().nextDouble()*20);
        rivalryStats.setBestScore(Math.max(new Random().nextDouble(), rivalryStats.getScore()));
        rivalryStats.setWorstScore(Math.min(new Random().nextDouble(), rivalryStats.getScore()));

        rivalryStats.setLoseCount(Math.abs(new Random().nextInt()));
        rivalryStats.setTieCount(Math.abs(new Random().nextInt()));
        rivalryStats.setWinCount(Math.abs(new Random().nextInt()));
        rivalryStats.setGameCount(rivalryStats.getLoseCount() + rivalryStats.getWinCount() + rivalryStats.getLoseCount());

        rivalryStats.setWinStreak(new Random().nextInt(rivalryStats.getWinCount()));
        rivalryStats.setLoseStreak(new Random().nextInt(rivalryStats.getLoseCount()));
        rivalryStats.setTieStreak(new Random().nextInt(rivalryStats.getTieCount()));

        rivalryStats.setLonguestWinStreak(new Random().nextInt(rivalryStats.getWinCount()));
        rivalryStats.setLonguestLoseStreak(new Random().nextInt(rivalryStats.getLoseCount()));
        rivalryStats.setLonguestTieStreak(new Random().nextInt(rivalryStats.getTieCount()));

        return rivalryStats;
    }
}