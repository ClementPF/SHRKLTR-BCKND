package calc.service;

import calc.DTO.*;
import calc.ELO.EloRating;
import calc.entity.Game;
import calc.entity.Outcome;
import calc.entity.Tournament;
import calc.entity.User;
import calc.exception.APIException;
import calc.repository.GameRepository;
import calc.repository.StatsRepository;
import calc.repository.TournamentRepository;
import calc.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by clementperez on 9/20/16.
 */
@Service
public class GameService {

    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private TournamentService tournamentService;
    @Autowired
    private TournamentRepository tournamentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OutcomeService outcomeService;;
    @Autowired
    private UserService userService;
    @Autowired
    private StatsService statsService;
    @Autowired
    private StatsRepository statsRepository;
    @Autowired
    private ModelMapper modelMapper;

    public GameDTO findOne(Long gameId){
        return convertToDto(gameRepository.findOne(gameId));
    }

    public GameDTO addGame(TournamentDTO tournament, List<OutcomeDTO> outcomes) {

        System.out.print("outcome username : " +outcomes.get(0).getUserName() + " " + outcomes.get(1).getUserName() + "\n");

        int winnerOutcomeIndex = outcomes.get(0).isWin() ? 0 : 1;
        int looserOutcomeIndex = outcomes.get(1).isWin() ? 0 : 1;
        Boolean isTie = outcomes.get(0).isTie();

        UserDTO w = userService.findByUserName(outcomes.get(winnerOutcomeIndex).getUserName());
        UserDTO l = userService.findByUserName(outcomes.get(looserOutcomeIndex).getUserName());
        UserDTO u = userService.whoIsLoggedIn();

        StatsDTO winnerStats = statsService.findByUserNameAndTournament(w.getUsername(),tournament.getName());
        StatsDTO loserStats = statsService.findByUserNameAndTournament(l.getUsername(),tournament.getName());

        double pointValue = EloRating.calculatePointValue(
                winnerStats == null ? 1000 : winnerStats.getScore(),
                loserStats == null ? 1000 : loserStats.getScore(),
                isTie ? "=" : "+");

        outcomes.get(winnerOutcomeIndex).setScoreValue(pointValue);
        outcomes.get(looserOutcomeIndex).setScoreValue(-pointValue);

        GameDTO game = new GameDTO(tournament,outcomes);

        return save(game);
    }

    public List<GameDTO> findByTournament(TournamentDTO tournament){
        if(tournamentService.findByName(tournament.getName()) == null){
            throw new APIException(Tournament.class,tournament.getName(),HttpStatus.NOT_FOUND);
        }

        return findByTournamentName(tournament.getName());
    }

    public List<GameDTO> findByTournamentName(String tournamentName){
        if(tournamentService.findByName(tournamentName) == null){
            throw new APIException(Tournament.class,tournamentName,HttpStatus.NOT_FOUND);
        }
        return gameRepository.findByTournamentName(tournamentName).stream()
                .map(m -> convertToDto(m)).collect(Collectors.toList());
    }

    public List<GameDTO> findByUserByTournament(Long userId, String tournamentName){
        if(tournamentService.findByName(tournamentName) == null){
            throw new APIException(Tournament.class,tournamentName,HttpStatus.NOT_FOUND);
        }
        if(userService.findOne(userId) == null){
            throw new APIException(User.class,userId+"",HttpStatus.NOT_FOUND);
        }
        return gameRepository.findByUserIdByTournamentName(userId, tournamentName).stream()
                .map(m -> convertToDto(m)).collect(Collectors.toList());
    }

    public List<GameDTO> findByUserByTournament(String username, String tournamentName){
        if(tournamentService.findByName(tournamentName) == null){
            throw new APIException(Tournament.class,tournamentName,HttpStatus.NOT_FOUND);
        }
        if(userService.findByUserName(username) == null){
            throw new APIException(User.class,username+"",HttpStatus.NOT_FOUND);
        }
        return gameRepository.findByUserNameByTournamentName(username, tournamentName).stream()
                .map(m -> convertToDto(m)).collect(Collectors.toList());
    }

    public List<GameDTO> findByUser(Long userId) {
        if(userService.findOne(userId) == null){
            throw new APIException(User.class,userId+"",HttpStatus.NOT_FOUND);
        }
        return gameRepository.findByUserId(userId).stream()
                .map(m -> convertToDto(m)).collect(Collectors.toList());
    }

    public List<GameDTO> findByUser(String username) {
        if(userService.findByUserName(username) == null){
            throw new APIException(User.class,username+"",HttpStatus.NOT_FOUND);
        }
        return gameRepository.findByUserName(username).stream()
                .map(m -> convertToDto(m)).collect(Collectors.toList());
    }

    public GameDTO save(GameDTO game){

        int winnerOutcomeIndex = game.getOutcomes().get(0).isWin() ? 0 : 1;
        int looserOutcomeIndex = game.getOutcomes().get(1).isWin() ? 0 : 1;
        Boolean isTie = game.getOutcomes().get(0).isTie();

        UserDTO w = userService.findByUserName(game.getOutcomes().get(winnerOutcomeIndex).getUserName());
        UserDTO l = userService.findByUserName(game.getOutcomes().get(looserOutcomeIndex).getUserName());
        UserDTO u = userService.whoIsLoggedIn();

        TournamentDTO tournament = tournamentService.findByName(game.getTournamentName());

        if(game.getOutcomes().size() != 2){
            throw new APIException(this.getClass(), game.getTournamentName() + " Only two outcomes accepted " + game.getOutcomes().size() + " were provided", HttpStatus.BAD_REQUEST);
        }else if(winnerOutcomeIndex == looserOutcomeIndex){
            throw new APIException(this.getClass(), tournament.getName() + " Outcomes invalid - incompatible results", HttpStatus.BAD_REQUEST);
        }else if(l.getUserId() == u.getUserId()){
            throw new APIException(this.getClass(), tournament.getName() + " Only the looser can enter a game", HttpStatus.UNAUTHORIZED);
        }else if(w.getUserId() == l.getUserId()){
            throw new APIException(this.getClass(), tournament.getName() + " Same username for both outcomes", HttpStatus.BAD_REQUEST);
        }else if(isTie){
            throw new APIException(this.getClass(), tournament.getName() + " Tie is not supported", HttpStatus.BAD_REQUEST);
        }

        Game g = null;

        try {
            g = convertToEntity(game);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        for (Outcome outcome : g.getOutcomes()) {
            statsService.recalculateAfterOutcome(outcome);
        }

        return convertToDto(gameRepository.save(g));
    }

    protected Game convertToEntity(GameDTO gameDto) throws ParseException {
        Game game = modelMapper.map(gameDto, Game.class);

        game.setGameId(gameDto.getGameId());
        game.setDate(gameDto.getDate());
        game.setTournament(tournamentRepository.findByName(gameDto.getTournamentName()));

        List<Outcome> outcomeSet = gameDto.getOutcomes().stream()
                .map(o -> {
                    try {
                        return outcomeService.convertToEntity(o);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    return null;
                }).collect(Collectors.toList());
        game.setOutcomes(outcomeSet);

        return game;
    }

    protected GameDTO convertToDto(Game game) {
        GameDTO gameDTO = modelMapper.map(game, GameDTO.class);

        gameDTO.setGameId(game.getGameId());
        gameDTO.setDate(game.getDate());
        gameDTO.setTournamentName(game.getTournament().getName());
        gameDTO.setTournamentDisplayName(game.getTournament().getDisplayName());

        if (game.getGameId() != null)
            gameDTO.setOutcomes(game.getOutcomes().stream().map(o -> outcomeService.convertToDto(o) ).collect(Collectors.toList()));

        return gameDTO;
    }

}

