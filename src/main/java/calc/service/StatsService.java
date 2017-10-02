package calc.service;

import calc.DTO.StatsDTO;
import calc.DTO.TournamentDTO;
import calc.DTO.UserDTO;
import calc.entity.Outcome;
import calc.entity.Stats;
import calc.entity.Tournament;
import calc.entity.User;
import calc.repository.StatsRepository;
import calc.repository.TournamentRepository;
import calc.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.jws.soap.SOAPBinding;
import java.text.ParseException;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Created by clementperez on 9/23/16.
 */
@Service
public class StatsService {

    @Autowired
    private StatsRepository statsRepository;
    @Autowired
    private TournamentRepository tournamentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;

    public List<StatsDTO> findByTournament(TournamentDTO tournament){
        return statsRepository.findByTournamentOrderByScoreDesc(tournamentRepository.findOne(tournament.getTournamentId())).stream()
                .map(s -> convertToDto(s)).collect(Collectors.toList());
    }

    public List<StatsDTO> findByUser(UserDTO user){
        return statsRepository.findByUser(userRepository.findOne(user.getUserId())).stream()
                .map(s -> convertToDto(s)).collect(Collectors.toList());
    }

    public StatsDTO save(StatsDTO stats){
        try {
            return convertToDto(statsRepository.save(convertToEntity(stats)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public StatsDTO findByUserAndTournament(Long userId, String tournamentName){

        Stats s = statsRepository.findByUserAndTournament(userId, tournamentName);
        return s == null ? null : convertToDto(s);
    }

    public StatsDTO findByUserAndTournamentCreateIfNone(UserDTO user, TournamentDTO tournament){

        StatsDTO stats = findByUserAndTournament(user.getUserId(),tournament.getName());

        User u = userRepository.findOne(user.getUserId());
        Tournament t = tournamentRepository.findOne(tournament.getTournamentId());
        if(stats == null) {
            Stats s = new Stats(u,t);
            statsRepository.save(s);
            return convertToDto(s);
        }

        return stats;
    }

    public void recalculateAfterOutcome(Outcome outcome){
        Stats stats = statsRepository.findByUserAndTournament(outcome.getUser().getUserId(), outcome.getGame().getTournament().getName());

        if(stats == null){
            stats = new Stats(outcome.getUser(),outcome.getGame().getTournament());
        }

        stats.setScore(stats.getScore() + outcome.getScoreValue());

        switch (outcome.getResults()){
            case WIN:
                stats.addWin();
                break;
            case LOSS:
                stats.addLose();
                break;
            case TIE:
                stats.addTie();
                break;
            default:
                break;
        }
        statsRepository.save(stats);
    }

    protected Stats convertToEntity(StatsDTO statsDto) throws ParseException {

        Stats stats = modelMapper.map(statsDto, Stats.class);

        stats.setStatsId(statsDto.getStatsId());
        stats.setScore(statsDto.getScore());
        stats.setGameCount(statsDto.getGameCount());
        stats.setWinCount(statsDto.getWinCount());
        stats.setLoseCount(statsDto.getLoseCount());
        stats.setTieCount(statsDto.getTieCount());
        stats.setWinStreak(statsDto.getWinStreak());
        stats.setLoseStreak(statsDto.getLoseStreak());
        stats.setTieStreak(statsDto.getTieStreak());
        stats.setLonguestWinStreak(statsDto.getLonguestWinStreak());
        stats.setLonguestLoseStreak(statsDto.getLonguestLoseStreak());
        stats.setLonguestTieStreak(statsDto.getLonguestTieStreak());
        stats.setBestScore(statsDto.getBestScore());
        stats.setWorstScore(statsDto.getWorstScore());
        if(statsDto.getStatsId() != null) {
            stats.setTournament(statsRepository.findOne(statsDto.getStatsId()).getTournament());
            stats.setUser(statsRepository.findOne(statsDto.getStatsId()).getUser());
        }

        return stats;
    }

    protected StatsDTO convertToDto(Stats stats) {

        StatsDTO statsDTO = modelMapper.map(stats, StatsDTO.class);

        statsDTO.setStatsId(stats.getStatsId());
        statsDTO.setScore(stats.getScore());
        statsDTO.setGameCount(stats.getGameCount());
        statsDTO.setWinCount(stats.getWinCount());
        statsDTO.setLoseCount(stats.getLoseCount());
        statsDTO.setTieCount(stats.getTieCount());
        statsDTO.setWinStreak(stats.getWinStreak());
        statsDTO.setLoseStreak(stats.getLoseStreak());
        statsDTO.setTieStreak(stats.getTieStreak());
        statsDTO.setLonguestWinStreak(stats.getLonguestWinStreak());
        statsDTO.setLonguestLoseStreak(stats.getLonguestLoseStreak());
        statsDTO.setLonguestTieStreak(stats.getLonguestTieStreak());
        statsDTO.setBestScore(stats.getBestScore());
        statsDTO.setWorstScore(stats.getWorstScore());
        statsDTO.setUsername(stats.getUser().getUserName());

        return statsDTO;
    }
}
