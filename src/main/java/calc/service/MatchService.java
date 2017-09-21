package calc.service;

import calc.DTO.*;
import calc.ELO.EloRating;
import calc.entity.Match;
import calc.entity.Outcome;
import calc.repository.MatchRepository;
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
public class MatchService {

    @Autowired
    private MatchRepository matchRepository;
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

    public MatchDTO findOne(Long matchId){
        return convertToDto(matchRepository.findOne(matchId));
    }

    public MatchDTO addMatch(TournamentDTO tournament, List<OutcomeDTO> outcomes) {

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

        return addMatch(tournament,w,l, isTie);
    }

    public MatchDTO addMatch(TournamentDTO tournament, UserDTO winner, UserDTO looser, boolean isTie) {

        StatsDTO winnerStats = statsService.findByUserAndTournamentCreateIfNone(winner,tournament);
        StatsDTO loserStats = statsService.findByUserAndTournamentCreateIfNone(looser,tournament);

        double pointValue = EloRating.calculatePointValue(winnerStats.getScore(),loserStats.getScore(),isTie ? "=" : "+");

        try {
            return addMatch(tournament,winner,looser, pointValue, isTie);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    protected MatchDTO addMatch(TournamentDTO tournament, UserDTO winner, UserDTO looser, double pointValue, boolean isTie) throws ParseException {

        Match match = new Match(tournamentService.convertToEntity(tournament));
        List<Outcome> outcomes = new ArrayList<>(Arrays.asList(
                new Outcome(pointValue, isTie ? Outcome.Result.TIE : Outcome.Result.WIN, match, userService.convertToEntity(winner)),
                new Outcome(-pointValue, isTie ? Outcome.Result.TIE : Outcome.Result.LOSS, match, userService.convertToEntity(looser)))
        );
        match.setOutcomes(outcomes);
        Match m = matchRepository.save(match);

        for (Outcome outcome : outcomes) {
            statsService.recalculateAfterOutcome(outcome);
        }

        return convertToDto(m);
    }

    public List<MatchDTO> findByTournament(TournamentDTO tournament){
        return findByTournamentName(tournament.getName());
    }

    public List<MatchDTO> findByTournamentName(String tournamentName){
        return matchRepository.findByTournamentName(tournamentName).stream()
                .map(m -> convertToDto(m)).collect(Collectors.toList());
    }


    public List<MatchDTO> findByUserByTournament(Long userId, String tournamentName){
        return matchRepository.findByUserIdByTournamentName(userId, tournamentName).stream()
                .map(m -> convertToDto(m)).collect(Collectors.toList());
    }


    public List<MatchDTO> findByUser(Long userId) {
        return matchRepository.findByUserId(userId).stream()
                .map(m -> convertToDto(m)).collect(Collectors.toList());
    }

    public MatchDTO save(MatchDTO match){
        try {
            return convertToDto(matchRepository.save(convertToEntity(match)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected Match convertToEntity(MatchDTO matchDto) throws ParseException {
        Match match = modelMapper.map(matchDto, Match.class);

        match.setMatchId(matchDto.getMatchId());
        match.setDate(matchDto.getDate());
        match.setTournament(tournamentRepository.findByName(matchDto.getTournamentName()));

        List<Outcome> outcomeSet = matchDto.getOutcomes().stream()
                .map(o -> {
                    try {
                        return outcomeService.convertToEntity(o);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    return null;
                }).collect(Collectors.toList());
        match.setOutcomes(outcomeSet);

        return match;
    }

    protected MatchDTO convertToDto(Match match) {
        MatchDTO matchDTO = modelMapper.map(match, MatchDTO.class);

        matchDTO.setMatchId(match.getMatchId());
        matchDTO.setDate(match.getDate());
        matchDTO.setTournamentName(match.getTournament().getName());

        if (match.getMatchId() != null)
            matchDTO.setOutcomes(match.getOutcomes().stream().map(o -> outcomeService.convertToDto(o) ).collect(Collectors.toList()));

        return matchDTO;
    }

}

