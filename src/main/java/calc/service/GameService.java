package calc.service;

import calc.DTO.GameDTO;
import calc.ELO.EloRating;
import calc.entity.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;
import calc.repository.GameRepository;

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
    private OutcomeService outcomeService;
    @Autowired
    private StatsService statsService;
    @Autowired
    private ModelMapper modelMapper;

    public Game findOne(Long gameId){
        return gameRepository.findOne(gameId);
    }


    public Game addGame(Tournament tournament, List<Outcome> outcomes) {

        if(outcomes.size() != 2 ||
                (outcomes.get(0).getResults().equals(Outcome.Result.WIN) && outcomes.get(1).getResults().equals(Outcome.Result.WIN)) ||
                (outcomes.get(0).getResults().equals(Outcome.Result.LOSS) && outcomes.get(1).getResults().equals(Outcome.Result.LOSS))){
            throw new AssertionError();
        }

        // this method init both users even when there is a tie
        User winner = outcomes.get(0).getResults().equals(Outcome.Result.WIN) ? outcomes.get(0).getUser() : outcomes.get(1).getUser();
        User looser = outcomes.get(0).getResults().equals(Outcome.Result.WIN) ? outcomes.get(1).getUser() : outcomes.get(0).getUser();
        Boolean isTie = outcomes.get(0).getResults().equals(Outcome.Result.TIE);

        return addGame(tournament,winner,looser, isTie);
    }

    public Game addGame(Tournament tournament, User winner, User looser, boolean isTie) {

        Stats winnerStats = statsService.findByUserAndTournamentCreateIfNone(winner,tournament);
        Stats loserStats = statsService.findByUserAndTournamentCreateIfNone(looser,tournament);

        double pointValue = EloRating.calculatePointValue(winnerStats.getScore(),loserStats.getScore(),isTie ? "=" : "+");

        return addGame(tournament,winner,looser, pointValue, isTie);
    }

    protected Game addGame(Tournament tournament, User winner, User looser, double pointValue, boolean isTie) {

        Game game = new Game(tournament);
        List<Outcome> outcomes = new ArrayList<>(Arrays.asList(
                new Outcome(pointValue, isTie ? Outcome.Result.TIE : Outcome.Result.WIN, game, winner),
                new Outcome(-pointValue, isTie ? Outcome.Result.TIE : Outcome.Result.LOSS, game, looser))
        );
        game.setOutcomes(outcomes);
        Game m = this.save(game);

        for (Outcome outcome : outcomes) {
            statsService.recalculateAfterOutcome(outcome);
        }

        return m;
    }

    List<Game> findByTournament(Tournament tournament){
        return gameRepository.findByTournament(tournament);
    }

    List<Game> findByTournamentName(String tournamentName){
        return gameRepository.findByTournamentName(tournamentName);
    }


    public List<Game> findByUserByTournament(Long userId, String tournamentName){
        return gameRepository.findByUserIdByTournamentName(userId, tournamentName);
    }


    public List<Game> findByUser(Long userId) {
        return gameRepository.findByUserId(userId);
    }

    public Iterable<Game> findAll() {
        return gameRepository.findAll();
    }

    public Game save(Game game){
        return gameRepository.save(game);
    }

    public Game convertToEntity(GameDTO gameDto) throws ParseException {
        Game game = modelMapper.map(gameDto, Game.class);

        game.setGameId(gameDto.getGameId());
        game.setDate(gameDto.getDate());
        game.setTournament(tournamentService.findByName(gameDto.getTournamentName()));

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

    public GameDTO convertToDto(Game game) {
        GameDTO gameDTO = modelMapper.map(game, GameDTO.class);

        gameDTO.setGameId(game.getGameId());
        gameDTO.setDate(game.getDate());
        gameDTO.setTournamentName(game.getTournament().getName());

        if (game.getGameId() != null)
            gameDTO.setOutcomes(game.getOutcomes().stream().map(o -> outcomeService.convertToDto(o) ).collect(Collectors.toList()));

        return gameDTO;
    }

}

