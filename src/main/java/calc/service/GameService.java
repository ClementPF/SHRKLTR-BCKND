package calc.service;

import calc.DTO.*;
import calc.ELO.EloRating;
import calc.entity.Game;
import calc.entity.Outcome;
import calc.repository.GameRepository;
import calc.repository.StatsRepository;
import calc.repository.TournamentRepository;
import calc.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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

        if(outcomes.size() != 2 ||
                (outcomes.get(0).getResult().equals(Outcome.Result.WIN) && outcomes.get(1).getResult().equals(Outcome.Result.WIN)) ||
                (outcomes.get(0).getResult().equals(Outcome.Result.LOSS) && outcomes.get(1).getResult().equals(Outcome.Result.LOSS))){
            throw new AssertionError();
        }

        // this method init both users even when there is a tie
        String winner = outcomes.get(0).getResult().equals(Outcome.Result.WIN) ? outcomes.get(0).getUserName() : outcomes.get(1).getUserName();
        String looser = outcomes.get(0).getResult().equals(Outcome.Result.WIN) ? outcomes.get(1).getUserName() : outcomes.get(0).getUserName();
        Boolean isTie = outcomes.get(0).getResult().equals(Outcome.Result.TIE);

        UserDTO w = userService.findByUserName(winner);
        UserDTO l = userService.findByUserName(looser);

        return addGame(tournament,w,l, isTie);
    }

    public GameDTO addGame(TournamentDTO tournament, UserDTO winner, UserDTO looser, boolean isTie) {

        StatsDTO winnerStats = statsService.findByUserAndTournamentCreateIfNone(winner,tournament);
        StatsDTO loserStats = statsService.findByUserAndTournamentCreateIfNone(looser,tournament);

        double pointValue = EloRating.calculatePointValue(winnerStats.getScore(),loserStats.getScore(),isTie ? "=" : "+");

        try {
            return addGame(tournament,winner,looser, pointValue, isTie);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    protected GameDTO addGame(TournamentDTO tournament, UserDTO winner, UserDTO looser, double pointValue, boolean isTie) throws ParseException {

        Game game = new Game(tournamentService.convertToEntity(tournament));
        List<Outcome> outcomes = new ArrayList<>(Arrays.asList(
                new Outcome(pointValue, isTie ? Outcome.Result.TIE : Outcome.Result.WIN, game, userService.convertToEntity(winner)),
                new Outcome(-pointValue, isTie ? Outcome.Result.TIE : Outcome.Result.LOSS, game, userService.convertToEntity(looser)))
        );
        game.setOutcomes(outcomes);
        Game m = gameRepository.save(game);

        for (Outcome outcome : outcomes) {
            statsService.recalculateAfterOutcome(outcome);
        }

        return convertToDto(m);
    }

    public List<GameDTO> findByTournament(TournamentDTO tournament){
        return findByTournamentName(tournament.getName());
    }

    public List<GameDTO> findByTournamentName(String tournamentName){
        return gameRepository.findByTournamentName(tournamentName).stream()
                .map(m -> convertToDto(m)).collect(Collectors.toList());
    }


    public List<GameDTO> findByUserByTournament(Long userId, String tournamentName){
        return gameRepository.findByUserIdByTournamentName(userId, tournamentName).stream()
                .map(m -> convertToDto(m)).collect(Collectors.toList());
    }


    public List<GameDTO> findByUser(Long userId) {
        return gameRepository.findByUserId(userId).stream()
                .map(m -> convertToDto(m)).collect(Collectors.toList());
    }

    public GameDTO save(GameDTO game){
        try {
            return convertToDto(gameRepository.save(convertToEntity(game)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
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

        if (game.getGameId() != null)
            gameDTO.setOutcomes(game.getOutcomes().stream().map(o -> outcomeService.convertToDto(o) ).collect(Collectors.toList()));

        return gameDTO;
    }

}

