package calc.service;

import calc.DTO.*;
import calc.entity.*;
import calc.exception.APIException;
import calc.repository.GameRepository;
import calc.repository.SportRepository;
import calc.repository.TournamentRepository;
import calc.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
//@PropertySource(value = {"classpath:application.properties", "${api.config.location}"}, ignoreResourceNotFound = true)
@TestExecutionListeners(inheritListeners = false, listeners = {
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class
})
@RunWith(SpringRunner.class)
public class GameServiceTest {


    @Autowired
    private GameService gameService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TournamentService tournamentService;

    @Autowired
    private SportRepository sportRepository;
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private TournamentRepository tournamentRepository;

    UserServiceTest userServiceTest;
    TournamentServiceTest tournamentServiceTest;

    List<GameDTO> allGames ;
    User u1;
    User u2;

    @Before
    public void setUp() {
        userServiceTest = new UserServiceTest();
        tournamentServiceTest = new TournamentServiceTest();

        SportDTO sportDTO = new SportDTO();
        UserDTO userDTO = userServiceTest.makeRandomUserDTO();
        TournamentDTO tournamentDTO = tournamentServiceTest.makeRandomTournamentDTO(sportDTO,userDTO);

        User u = userServiceTest.makeRandomUser();
        u1 = userRepository.save(userServiceTest.makeRandomUser());
        u2 = userRepository.save(userServiceTest.makeRandomUser());
        UserDTO u2 = userServiceTest.makeRandomUserDTO();

        //allGames.add(gameService.save(game));
    }

    //@Test
    public void findOne(){
        GameDTO gTest = allGames.get(new Random().nextInt(allGames.size() - 1));
        GameDTO gFetch = gameService.findOne(gTest.getGameId());
        assertThat(gTest).isEqualToComparingFieldByField(gFetch);
    }

    @Test
    public void addGame() {

    }

    @Test
    public void findByTournament(){

    }

    @Test
    public void findByTournamentName(){

        Sport sport = sportRepository.save(new SportServiceTest().makeRandomSport());
        Tournament tournament = tournamentRepository.save(tournamentServiceTest.makeRandomTournament(sport,u1));

        int size = 30;
        for(int i = 0; i < size; i++){
            gameRepository.save(makeRandomGame(u1,u2,tournament));
        }


        List<Game> games3 = gameRepository.findByOutcomesUserUserId(u1.getUserId(), null);
        assertThat(games3).isNotNull();
        List<Game> games = gameRepository.findByTournamentName(tournament.getName(),null);
//        List<GameDTO> games2 = gameService.findByTournamentName(tournament.getName());

        assertThat(games).isNotNull();
        // assertThat(games2.size()).isEqualTo(30);
        assertThat(games.size()).isEqualTo(30);
        assertThat(games).hasSize(30);
    }

    @Test
    public void findByOutcomeUserUserIdByTournamentTournamentId(){
        Sport sport = sportRepository.save(new SportServiceTest().makeRandomSport());
        User u = userRepository.save(userServiceTest.makeRandomUser());
        Tournament tournament = tournamentRepository.save(tournamentServiceTest.makeRandomTournament(sport,u));

        int size = 5;
        for(int i = 0; i < size; i++){
            gameRepository.save(makeRandomGame(u,u2,tournament));
        }

        List<Game> games = gameRepository.findByOutcomesUserUserIdAndTournamentTournamentId(u.getUserId(), tournament.getTournamentId(), null);
        assertThat(games).isNotNull();
        assertThat(games).hasSize(size);
    }


    @Test
    public void findByOutcomeUserUserIdByTournamentTournamentId2(){
        Sport sport = sportRepository.save(new SportServiceTest().makeRandomSport());
        User u = userRepository.save(userServiceTest.makeRandomUser());
        Tournament tournament = tournamentRepository.save(tournamentServiceTest.makeRandomTournament(sport,u));


        int halfSize = 15;
        int size = halfSize*2;
        for(int i = 0; i < size; i++){
            gameRepository.save(makeRandomGame(u,u2,tournament));
        }

        List<Game> games = gameRepository.findByOutcomesUserUserIdAndTournamentTournamentId(u.getUserId(), tournament.getTournamentId(), new PageRequest(0, halfSize));
        assertThat(games).isNotNull();
        assertThat(games).hasSize(halfSize);
        games.addAll(gameRepository.findByOutcomesUserUserIdAndTournamentTournamentId(u.getUserId(), tournament.getTournamentId(), new PageRequest(1, halfSize)));
        assertThat(games).isNotNull();
        assertThat(games).hasSize(size);
    }

    @Test
    public void perf(){
        Sport sport = sportRepository.save(new SportServiceTest().makeRandomSport());
        User u = userRepository.save(userServiceTest.makeRandomUser());
        Tournament tournament = tournamentRepository.save(tournamentServiceTest.makeRandomTournament(sport,u));


        int halfSize = 5000;
        int size = halfSize*2;
        for(int i = 0; i < size; i++){
            gameRepository.save(makeRandomGame(u,u2,tournament));
        }

        long time = System.currentTimeMillis();

        Pageable p = new PageRequest(0,100);
        gameRepository.findByOutcomesUserUserIdAndTournamentTournamentId(u.getUserId(), tournament.getTournamentId(), p);

        time = System.currentTimeMillis() - time;
        System.out.print("TIME FOR a page " + time + "\n");

        time = System.currentTimeMillis();
        List<Game> games= gameRepository.findByOutcomesUserUserIdAndTournamentTournamentId(u.getUserId(), tournament.getTournamentId(), null);

        time = System.currentTimeMillis() - time;
        System.out.print("TIME FOR a full fetch " + time + " ms for " + games.size() +  "\n");

        time = System.currentTimeMillis();
        Integer gameCount = gameRepository.countByTournament(tournament);

        time = System.currentTimeMillis() - time;
        System.out.print("TIME TO count all " + time + " ms for " + gameCount + "\n");
    }

    @Test
    public void findByUserUsernameByTournamentTournamentName(){
        Sport sport = sportRepository.save(new SportServiceTest().makeRandomSport());
        User u = userRepository.save(userServiceTest.makeRandomUser());
        Tournament tournament = tournamentRepository.save(tournamentServiceTest.makeRandomTournament(sport,u));

        int size = 5;
        for(int i = 0; i < size; i++){
            gameRepository.save(makeRandomGame(u,u2,tournament));
        }

        List<Game> games = gameRepository.findByOutcomesUserUserNameAndTournamentName(u.getUserName(), tournament.getName(), null);
        assertThat(games).isNotNull();
        assertThat(games).hasSize(size);
    }


    @Test
    public void findByOutcomeUser() {
        Sport sport = sportRepository.save(new SportServiceTest().makeRandomSport());
        User u = userRepository.save(userServiceTest.makeRandomUser());
        Tournament tournament = tournamentRepository.save(tournamentServiceTest.makeRandomTournament(sport,u));

        int size = 5;
        for(int i = 0; i < size; i++){
            gameRepository.save(makeRandomGame(u,u2,tournament));
        }

        List<Game> games = gameRepository.findByOutcomesUser(u, null);
        assertThat(games).isNotNull();
        assertThat(games).hasSize(size);
    }

    @Test
    public void findByOutcomeUserUserName() {
        Sport sport = sportRepository.save(new SportServiceTest().makeRandomSport());
        User u = userRepository.save(userServiceTest.makeRandomUser());
        Tournament tournament = tournamentRepository.save(tournamentServiceTest.makeRandomTournament(sport,u));

        int size = 5;
        for(int i = 0; i < size; i++){
            gameRepository.save(makeRandomGame(u,u2,tournament));
        }

        List<Game> games = gameRepository.findByOutcomesUserUserName(u.getUserName(), null);
        assertThat(games).isNotNull();
        assertThat(games).hasSize(size);
    }

    @Test
    public void save(){
    }

    @Test
    public void validateGame_whenLooserLoggedIn() {

        UserDTO winner = userServiceTest.makeRandomUserDTO();
        UserDTO looser = userServiceTest.makeRandomUserDTO();

        TournamentDTO tournamentDTO = tournamentServiceTest.makeRandomTournamentDTO(new SportDTO("sport"),userServiceTest.makeRandomUserDTO());
        GameDTO gameDTO = makeRandomGameDTO(winner,looser,tournamentDTO);
        gameService.validateGame(winner,looser,looser,tournamentDTO,gameDTO);
    }

    @Test(expected = APIException.class)
    public void validateGame_whenWinnerLoggedIn() {

        UserDTO winner = userServiceTest.makeRandomUserDTO();
        UserDTO looser = userServiceTest.makeRandomUserDTO();

        TournamentDTO tournamentDTO = tournamentServiceTest.makeRandomTournamentDTO(new SportDTO("sport"),userServiceTest.makeRandomUserDTO());
        GameDTO gameDTO = makeRandomGameDTO(winner,looser,tournamentDTO);

        gameService.validateGame(winner,looser,winner,tournamentDTO,gameDTO);
        //assertThat(false).isTrue();
    }

    @Test(expected = APIException.class)
    public void validateGame_whenOneOutcome() {

        UserDTO winner = userServiceTest.makeRandomUserDTO();
        UserDTO looser = userServiceTest.makeRandomUserDTO();

        TournamentDTO tournamentDTO = tournamentServiceTest.makeRandomTournamentDTO(new SportDTO("sport"),userServiceTest.makeRandomUserDTO());
        GameDTO gameDTO = makeRandomGameDTO(winner,looser,tournamentDTO);

        gameDTO.setOutcomes(gameDTO.getOutcomes().subList(0,0));

        gameService.validateGame(winner,looser,winner,tournamentDTO,gameDTO);
    }


    @Test(expected = APIException.class)
    public void validateGame_whenInvalidOutcome() {

        UserDTO winner = userServiceTest.makeRandomUserDTO();
        UserDTO looser = userServiceTest.makeRandomUserDTO();

        TournamentDTO tournamentDTO = tournamentServiceTest.makeRandomTournamentDTO(new SportDTO("sport"),userServiceTest.makeRandomUserDTO());
        GameDTO gameDTO = makeRandomGameDTO(winner,looser,tournamentDTO);

        gameDTO.getOutcomes().get(0).setResult(Outcome.Result.WIN);
        gameDTO.getOutcomes().get(1).setResult(Outcome.Result.WIN);

        gameService.validateGame(winner, looser, looser, tournamentDTO, gameDTO);
    }

    @Test
    public void sendPushNotificationPostGame(){
    }

    @Test
    public void convertToEntity() {
    }

    @Test
    public void convertToDto() {
    }

    public GameDTO makeRandomGameDTO(UserDTO winnerDTO, UserDTO looserDTO, TournamentDTO tournamentDTO) {
        double score = new Random().nextDouble()*10;

        ArrayList<OutcomeDTO> outcomes = new ArrayList<OutcomeDTO>();
        outcomes.add(new OutcomeDTO(winnerDTO, Outcome.Result.WIN,score));
        outcomes.add(new OutcomeDTO(looserDTO, Outcome.Result.LOSS,-score));

        GameDTO game = new GameDTO(tournamentDTO,outcomes);
        return game;
    }

    public Game makeRandomGame(User winner, User looser, Tournament tournament) {
        double score = new Random().nextDouble()*10;
        Game g = new Game(tournament);
        g.setGameId(new Random().nextLong());

        ArrayList<Outcome> outcomes = new ArrayList<Outcome>();
        outcomes.add(new Outcome(score, Outcome.Result.WIN,g,winner));
        outcomes.add(new Outcome(-score, Outcome.Result.LOSS,g,looser));

        g.setOutcomes(outcomes);

        return g;
    }
}
