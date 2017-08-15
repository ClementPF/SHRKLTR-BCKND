package calc.service;

import calc.DTO.UserDTO;
import calc.DTO.StatsDTO;
import calc.entity.Outcome;
import calc.entity.User;
import calc.entity.Stats;
import calc.entity.Tournament;
import calc.repository.UserRepository;
import calc.repository.StatsRepository;
import calc.repository.TournamentRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.text.ParseException;
import java.util.List;
import java.util.Set;


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

    public List<Stats> findByTournament(Tournament tournament){
        return statsRepository.findByTournament(tournament);
    }

    public List<Stats> findByUser(User user){
        return statsRepository.findByUser(user);
    }

    public Stats findByUserAndTournament(Long userId, String tournamentName){
        return statsRepository.findByUserAndTournament(userId, tournamentName);
    }

    public Stats findByUserAndTournamentCreateIfNone(User user, Tournament tournament){

        for(Stats s : user.getStats()){
            if(s.getTournament().equals(tournament))
                return s;
        }

        Stats stats = statsRepository.findByUserAndTournament(user.getUserId(), tournament.getName());

        if(stats == null) {
            stats = new Stats(user, tournament);
            statsRepository.save(stats);
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

    public Stats convertToEntity(StatsDTO statsDto) throws ParseException {

        Stats stats = modelMapper.map(statsDto, Stats.class);
/*
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
        stats.setWorstScore(statsDto.getWorstScore());*/
        if(statsDto.getStatsId() != null) {
            stats.setTournament(statsRepository.findOne(statsDto.getStatsId()).getTournament());
            stats.setUser(statsRepository.findOne(statsDto.getStatsId()).getUser());
        }

        return stats;
    }

    public StatsDTO convertToDto(Stats stats) {

        StatsDTO statsDTO = modelMapper.map(stats, StatsDTO.class);
/*
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
        statsDTO.setWorstScore(stats.getWorstScore());*/

        return statsDTO;
    }
}
