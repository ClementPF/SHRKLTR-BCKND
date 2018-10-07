package calc.service;

import calc.DTO.*;
import calc.entity.Game;
import calc.entity.Outcome;
import calc.entity.Tournament;
import calc.entity.User;
import calc.exception.APIException;
import calc.repository.GameRepository;
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
    private GameRepository gameRepository;

    UserServiceTest userServiceTest;
    TournamentServiceTest tournamentServiceTest;

    List<GameDTO> allGames ;

    @Before
    public void setUp() {
        userServiceTest = new UserServiceTest();
        tournamentServiceTest = new TournamentServiceTest();

        SportDTO sportDTO = new SportDTO();
        UserDTO userDTO = userServiceTest.makeRandomUserDTO();
        TournamentDTO tournamentDTO = tournamentServiceTest.makeRandomTournamentDTO(sportDTO,userDTO);

        //User u = userServiceTest.makeRandomUser();
        //UserDTO u1 = userRepository.save(u);
        UserDTO u1 = userServiceTest.makeRandomUserDTO();
        UserDTO u2 = userServiceTest.makeRandomUserDTO();

        double score = new Random().nextDouble();
        OutcomeDTO o1 = new OutcomeDTO(u1, Outcome.Result.WIN,score);
        OutcomeDTO o2 = new OutcomeDTO(u2, Outcome.Result.LOSS,-score);

        ArrayList<OutcomeDTO> outcomes = new ArrayList<OutcomeDTO>();
        outcomes.add(o1);
        outcomes.add(o2);

        GameDTO game = new GameDTO(tournamentDTO,outcomes);

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
        
    }

    @Test
    public void findByUserByTournament(){
        
    }

    @Test
    public void findByUser() {
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
