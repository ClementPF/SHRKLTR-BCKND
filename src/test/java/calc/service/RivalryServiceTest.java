package calc.service;

import calc.Application;
import calc.DTO.RivalryStatsDTO;
import calc.entity.*;
import calc.exception.APIException;
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

import java.text.ParseException;
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
    private StatsService statsService;

    @Autowired
    private TournamentRepository tournamentRepository;

    UserServiceTest userServiceTest;
    GameServiceTest gameServiceTest;
    StatsServiceTest statsServiceTest;

    User user1;
    User user2;
    Stats stats1;
    Stats stats2;
    Sport sport1;
    Tournament tournament1;
    RivalryStats rivalryStats1;
    RivalryStats rivalryStats2;
    RivalryStatsDTO rivalryStatsDTO1;
    RivalryStatsDTO rivalryStatsDTO2;

    @Before
    @Test  public void setUp() {

        userServiceTest = new UserServiceTest();
        gameServiceTest = new GameServiceTest();
        statsServiceTest = new StatsServiceTest();

        user1 = userRepository.save(userServiceTest.makeRandomUser());
        user2 = userRepository.save(userServiceTest.makeRandomUser());

        Sport pingpong = new Sport(UUID.randomUUID().toString());
        sport1 = sportRepository.save(pingpong);

        Tournament pingpongTournament = new Tournament(UUID.randomUUID().toString(),sport1,user1);
        tournament1 = tournamentRepository.save(pingpongTournament);

        stats1 = statsRepository.save(statsServiceTest.makeRandomStats(user1,tournament1));
        stats2 = statsRepository.save(statsServiceTest.makeRandomStats(user2,tournament1));


        rivalryStats1 = rivalryStatsService.save(makeRandomRivalryStats(user1,user2,stats1));
        rivalryStats2 = rivalryStatsService.save(makeRandomRivalryStats(user2,user1,stats2));

        rivalryStatsDTO1 = rivalryStatsService.convertToDto(rivalryStats1);
        rivalryStatsDTO2 = rivalryStatsService.convertToDto(rivalryStats2);
    }

    @Test
    public void findByTournament(){

        List<RivalryStatsDTO> fetched = rivalryStatsService.findByTournament(tournamentService.convertToDto(tournament1));

        assertThat(fetched.get(0)).isEqualToComparingFieldByFieldRecursively(rivalryStatsDTO1);
        assertThat(fetched.get(1)).isEqualToComparingFieldByFieldRecursively(rivalryStatsDTO2);
    }

    //@Test(expected = APIException.class)
    public void findByTournament_notFound(){

        Tournament tournament_NotExist = new Tournament(UUID.randomUUID().toString(),sport1,user1);
        tournament_NotExist.setTournamentId(123L);

        List<RivalryStatsDTO> fetched = rivalryStatsService.findByTournament(tournamentService.convertToDto(tournament_NotExist));
    }

    @Test
    public void findByUser(){
        List<RivalryStatsDTO> fetched1 = rivalryStatsService.findByUser(userService.convertToDto(user1));
        List<RivalryStatsDTO> fetched2 = rivalryStatsService.findByUser(userService.convertToDto(user2));

        assertThat(fetched1.get(0)).isEqualToComparingFieldByFieldRecursively(rivalryStatsDTO1);
        assertThat(fetched2.get(0)).isEqualToComparingFieldByFieldRecursively(rivalryStatsDTO2);
    }

    //@Test(expected = APIException.class)
    public void findByUser_notFound(){
        List<RivalryStatsDTO> fetched1 = rivalryStatsService.findByUser(userServiceTest.makeRandomUserDTO());
    }

    @Test
    public void findByUsername(){
        List<RivalryStatsDTO> fetched1 = rivalryStatsService.findByUsername(user1.getUserName());
        List<RivalryStatsDTO> fetched2 = rivalryStatsService.findByUsername(user2.getUserName());

        assertThat(fetched1.get(0)).isEqualToComparingFieldByFieldRecursively(rivalryStatsDTO1);
        assertThat(fetched2.get(0)).isEqualToComparingFieldByFieldRecursively(rivalryStatsDTO2);
    }

    //@Test(expected = APIException.class)
    public void findByUsername_notFound(){
        List<RivalryStatsDTO> fetched1 = rivalryStatsService.findByUsername(userServiceTest.makeRandomUserDTO().getUsername());
    }

    @Test
    public void findByRival(){
        List<RivalryStatsDTO> fetched1 = rivalryStatsService.findByRival(userService.convertToDto(user2));
        List<RivalryStatsDTO> fetched2 = rivalryStatsService.findByRival(userService.convertToDto(user1));

        assertThat(fetched1.get(0)).isEqualToComparingFieldByFieldRecursively(rivalryStatsDTO1);
        assertThat(fetched2.get(0)).isEqualToComparingFieldByFieldRecursively(rivalryStatsDTO2);
    }

    //@Test(expected = APIException.class)
    public void findByRival_notFound(){
        List<RivalryStatsDTO> fetched1 = rivalryStatsService.findByRival(userServiceTest.makeRandomUserDTO());
    }

    @Test
    public void save(){

    }

    @Test
    public void findByUserAndTournament(){
        List<RivalryStatsDTO> fetched1 = rivalryStatsService.findByUserAndTournament(user1.getUserId(), tournament1.getTournamentId());
        List<RivalryStatsDTO> fetched2 = rivalryStatsService.findByUserAndTournament(user2.getUserId(), tournament1.getTournamentId());

        assertThat(fetched1.get(0)).isEqualToComparingFieldByFieldRecursively(rivalryStatsDTO1);
        assertThat(fetched2.get(0)).isEqualToComparingFieldByFieldRecursively(rivalryStatsDTO2);
    }

    //@Test(expected = APIException.class)
    public void findByUserAndTournament_UserNotFound(){
        List<RivalryStatsDTO> fetched1 = rivalryStatsService.findByUserAndTournament(212L,tournament1.getTournamentId());
    }

    //@Test(expected = APIException.class)
    public void findByUserAndTournament_TournamentNotFound(){
        List<RivalryStatsDTO> fetched1 = rivalryStatsService.findByUserAndTournament(user1.getUserId(),2345L);
    }

    @Test  public void findByRivalAndTournament(){
        List<RivalryStatsDTO> fetched1 = rivalryStatsService.findByRivalAndTournament(user2.getUserId(), tournament1.getTournamentId());
        List<RivalryStatsDTO> fetched2 = rivalryStatsService.findByRivalAndTournament(user1.getUserId(), tournament1.getTournamentId());

        assertThat(fetched1.get(0)).isEqualToComparingFieldByFieldRecursively(rivalryStatsDTO1);
        assertThat(fetched2.get(0)).isEqualToComparingFieldByFieldRecursively(rivalryStatsDTO2);
    }

    //@Test(expected = APIException.class)
    public void findByRivalAndTournament_RivalNotFound(){
        List<RivalryStatsDTO> fetched1 = rivalryStatsService.findByRivalAndTournament(212L, tournament1.getTournamentId());
    }

    //@Test(expected = APIException.class)
    public void findByRivalAndTournament_TournamentNotFound(){
        List<RivalryStatsDTO> fetched1 = rivalryStatsService.findByRivalAndTournament(user1.getUserId(), 2345L);

    }

    @Test
    public void findByUserNameAndTournament(){
        List<RivalryStatsDTO> fetched1 = rivalryStatsService.findByUserNameAndTournament(user1.getUserName(), tournament1.getName());
        List<RivalryStatsDTO> fetched2 = rivalryStatsService.findByUserNameAndTournament(user2.getUserName(), tournament1.getName());

        assertThat(fetched1.get(0)).isEqualToComparingFieldByFieldRecursively(rivalryStatsDTO1);
        assertThat(fetched2.get(0)).isEqualToComparingFieldByFieldRecursively(rivalryStatsDTO2);
    }

    //@Test(expected = APIException.class)
    public void findByUserNameAndTournament_usernameNotFound(){
        List<RivalryStatsDTO> fetched1 = rivalryStatsService.findByUserNameAndTournament("plop",tournament1.getName());
    }

    //@Test(expected = APIException.class)
    public void findByUserNameAndTournament_tournamentNotFound(){
        List<RivalryStatsDTO> fetched1 = rivalryStatsService.findByUserNameAndTournament(user1.getUserName(),"plop");
    }

    @Test
    public void findByRivalUserNameAndTournament(){
        List<RivalryStatsDTO> fetched1 = rivalryStatsService.findByRivalUserNameAndTournament(user2.getUserName(), tournament1.getName());
        List<RivalryStatsDTO> fetched2 = rivalryStatsService.findByRivalUserNameAndTournament(user1.getUserName(), tournament1.getName());

        assertThat(fetched1.get(0)).isEqualToComparingFieldByFieldRecursively(rivalryStatsDTO1);
        assertThat(fetched2.get(0)).isEqualToComparingFieldByFieldRecursively(rivalryStatsDTO2);
    }

    //@Test(expected = APIException.class)
    public void findByRivalUserNameAndTournament_usernameNotFound(){
        List<RivalryStatsDTO> fetched1 = rivalryStatsService.findByRivalUserNameAndTournament("plop", tournament1.getName());
    }

    //@Test(expected = APIException.class)
    public void findByRivalUserNameAndTournament_tournamentNotFound(){
        List<RivalryStatsDTO> fetched1 = rivalryStatsService.findByRivalUserNameAndTournament(user1.getUserName(), "plop");
    }

    @Test
    public void findByUserAndRivalAndTournament(){
        RivalryStatsDTO fetched1 = rivalryStatsService.findByUserAndRivalAndTournament(user1.getUserId(),user2.getUserId(),tournament1.getTournamentId());
        RivalryStatsDTO fetched2 = rivalryStatsService.findByUserAndRivalAndTournament(user2.getUserId(),user1.getUserId(),tournament1.getTournamentId());

        assertThat(fetched1).isEqualToComparingFieldByFieldRecursively(rivalryStatsDTO1);
        assertThat(fetched2).isEqualToComparingFieldByFieldRecursively(rivalryStatsDTO2);
    }

    //@Test(expected = APIException.class)
    public void findByUserAndRivalAndTournament_usernameNotFound(){
        RivalryStatsDTO fetched1 = rivalryStatsService.findByUserAndRivalAndTournament(125487L, user2.getUserId(), tournament1.getTournamentId());
    }

    //@Test(expected = APIException.class)
    public void findByUserAndRivalAndTournament_rivalnameNotFound(){
        RivalryStatsDTO fetched1 = rivalryStatsService.findByUserAndRivalAndTournament(user1.getUserId(),125487L,tournament1.getTournamentId());
    }

    //@Test(expected = APIException.class)
    public void findByUserAndRivalAndTournament_tournamentNotFound(){
        RivalryStatsDTO fetched1 = rivalryStatsService.findByUserAndRivalAndTournament(user1.getUserId(), user2.getUserId(), 654L);
    }

    @Test
    public void findByUserAndRivalAndTournamentCreateIfNone(){
        Tournament tournament = tournamentRepository.save(new Tournament(UUID.randomUUID().toString(),sport1,user1));
        RivalryStatsDTO fetched1 = rivalryStatsService.findByUserAndRivalAndTournament(user1.getUserId(),user2.getUserId(),tournament.getTournamentId());

        Stats stats = statsRepository.save(statsServiceTest.makeRandomStats(user1,tournament));

        assertThat(fetched1).isNull();

        rivalryStatsService.findByStatsAndRivalCreateIfNone(statsService.convertToDto(stats), userService.convertToDto(user2));

        fetched1 = rivalryStatsService.findByUserAndRivalAndTournament(user1.getUserId(),user2.getUserId(),tournament.getTournamentId());

        assertThat(fetched1).isNotNull();
    }

    @Test
    public void convertToEntity() {
        Tournament tournament = tournamentRepository.save(new Tournament(UUID.randomUUID().toString(),sport1,user1));
        Stats stats = statsRepository.save(statsServiceTest.makeRandomStats(user1,tournament));
        RivalryStatsDTO fetched1 = rivalryStatsService.findByStatsAndRivalCreateIfNone(statsService.convertToDto(stats), userService.convertToDto(user2));
        RivalryStatsDTO converted = null;
        try {
            converted = rivalryStatsService.convertToDto(rivalryStatsService.convertToEntity(fetched1));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        assertThat(fetched1.getRivalryStatsId()).isEqualTo(converted.getRivalryStatsId());
        assertThat(fetched1.getUser().getUserId()).isEqualTo(converted.getUser().getUserId());
        assertThat(fetched1.getRival().getUserId()).isEqualTo(converted.getRival().getUserId());
        assertThat(fetched1.getTournament().getTournamentId()).isEqualTo(converted.getTournament().getTournamentId());
    }

    @Test
    public void whenValidGame_thenRivalryStatsShouldBeFound() {

        Tournament tournament = tournamentRepository.save(new Tournament(UUID.randomUUID().toString(),sport1,user1));
        Game g = gameServiceTest.makeRandomGame(user1,user2,tournament);

        gameRepository.save(g);

        Stats s1 = statsServiceTest.makeRandomStats(user1,tournament);
        Stats s2 = statsServiceTest.makeRandomStats(user2,tournament);

        RivalryStats rs = rivalryStatsService.recalculateAfterOutcome(s1,g.getOutcomes().get(0),g.getOutcomes().get(1));
        rivalryStatsService.recalculateAfterOutcome(s2,g.getOutcomes().get(1),g.getOutcomes().get(0));

        assertThat(rs.getScore()).isEqualTo(g.getOutcomes().get(0).getScoreValue());
    }

    @Test
    public void whenValidGames_thenRivalryStatsShouldBeValid() {
        double totalScore = 0;

        Tournament tournament = tournamentRepository.save(new Tournament(UUID.randomUUID().toString(),sport1,user1));

        int rdm = new Random().nextInt(100);

        Stats s1 = statsServiceTest.makeRandomStats(user1,tournament);
        Stats s2 = statsServiceTest.makeRandomStats(user2,tournament);
        statsRepository.save(s1);
        statsRepository.save(s2);

        RivalryStats rs1 = null;
        RivalryStats rs2 = null;

        for(int i = 0; i < rdm; i++){
            Boolean b = new Random().nextBoolean();
            Game g = gameServiceTest.makeRandomGame(b ? user1 : user2, !b ? user1 : user2,tournament);
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

    public RivalryStats makeRandomRivalryStats(User user, User rival, Stats stats){
        RivalryStats rivalryStats = new RivalryStats(user, rival ,stats);

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