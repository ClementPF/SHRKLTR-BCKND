package calc.service;

import calc.DTO.RivalryStatsDTO;
import calc.DTO.TournamentDTO;
import calc.DTO.UserDTO;
import calc.entity.*;
import calc.repository.RivalryStatsRepository;
import calc.repository.TournamentRepository;
import calc.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Created by clementperez on 9/23/16.
 */
@Service
public class RivalryStatsService {

    @Autowired
    private RivalryStatsRepository rivalryStatsRepository;
    @Autowired
    private TournamentRepository tournamentRepository;
    @Autowired
    private TournamentService tournamentService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private ModelMapper modelMapper;

    public List<RivalryStatsDTO> findByTournament(TournamentDTO tournament){
        return rivalryStatsRepository.findByTournament(tournamentRepository.findOne(tournament.getTournamentId())).stream()
                .map(s -> convertToDto(s)).collect(Collectors.toList());
    }

    public List<RivalryStatsDTO> findByUser(UserDTO user){
        return rivalryStatsRepository.findByUser(userRepository.findOne(user.getUserId())).stream()
                .map(s -> convertToDto(s)).collect(Collectors.toList());
    }

    public List<RivalryStatsDTO> findByUsername(String username){
        return rivalryStatsRepository.findByUser(userRepository.findByUserName(username)).stream()
                .map(s -> convertToDto(s)).collect(Collectors.toList());
    }

    public List<RivalryStatsDTO> findByRival(UserDTO user){
        return rivalryStatsRepository.findByRival(userRepository.findOne(user.getUserId())).stream()
                .map(s -> convertToDto(s)).collect(Collectors.toList());
    }

    public RivalryStatsDTO save(RivalryStatsDTO stats){
        try {
            return convertToDto(rivalryStatsRepository.save(convertToEntity(stats)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<RivalryStatsDTO> findByUserAndTournament(Long userId, Long tournamentId){

        List<RivalryStats> rs = rivalryStatsRepository.findByUserUserIdAndTournamentTournamentId(userId, tournamentId);

        return rs.stream()
                .map(s -> convertToDto(s)).collect(Collectors.toList());
    }

    public List<RivalryStatsDTO> findByRivalAndTournament(Long userId, String tournamentName){

        List<RivalryStats> rs = rivalryStatsRepository.findByRivalUserIdAndTournamentTournamentId(userId, tournamentName);

        return rs.stream()
                .map(s -> convertToDto(s)).collect(Collectors.toList());
    }

    public List<RivalryStatsDTO> findByUserNameAndTournament(String username, String tournamentName){

        List<RivalryStats> rs = rivalryStatsRepository.findByUserUserNameAndTournamentName(username, tournamentName);
        return rs.stream()
                .map(s -> convertToDto(s)).collect(Collectors.toList());
    }

    public List<RivalryStatsDTO> findByRivalUserNameAndTournament(String username, String tournamentName){

        List<RivalryStats> rs = rivalryStatsRepository.findByRivalUserNameAndTournamentName(username, tournamentName);
        return rs.stream()
                .map(s -> convertToDto(s)).collect(Collectors.toList());
    }

    public RivalryStatsDTO findByUserAndRivalAndTournament(Long userId,Long rivalId, Long tournamentId){

        RivalryStats stats = rivalryStatsRepository.findByUserUserIdAndRivalUserIdAndTournamentTournamentId(userId, rivalId, tournamentId);

        return stats == null ? null : convertToDto(stats);
    }

    public RivalryStatsDTO findByUserNameAndRivalNameAndTournament(String username,String rivalName, String tournamentName){

        RivalryStats stats = rivalryStatsRepository.findByUserUserNameAndRivalUserNameAndTournamentName(username, rivalName, tournamentName);

        return stats == null ? null : convertToDto(stats);
    }

    public RivalryStatsDTO findByUserAndRivalAndTournamentCreateIfNone(UserDTO user,UserDTO rival, TournamentDTO tournament){

        RivalryStatsDTO stats = findByUserAndRivalAndTournament(user.getUserId(),rival.getUserId(),tournament.getTournamentId());

        User u = userRepository.findOne(user.getUserId());
        User r = userRepository.findOne(rival.getUserId());
        Tournament t = tournamentRepository.findOne(tournament.getTournamentId());

        if(stats == null) {
            RivalryStats s = new RivalryStats(u,r,t);
            rivalryStatsRepository.save(s);
            return convertToDto(s);
        }

        return stats;
    }

    public void recalculateAfterOutcome(Outcome outcome1, Outcome outcome2){
        RivalryStats stats = rivalryStatsRepository.findByUserUserIdAndRivalUserIdAndTournamentTournamentId(
                outcome1.getUser().getUserId(),
                outcome2.getUser().getUserId(),
                outcome1.getGame().getTournament().getTournamentId());

        if(stats == null){
            stats = new RivalryStats(outcome1.getUser(),outcome2.getUser(),outcome1.getGame().getTournament());
        }

        stats.setScore(stats.getScore() + outcome1.getScoreValue());

        switch (outcome1.getResults()){
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
        rivalryStatsRepository.save(stats);
    }

    public RivalryStats convertToEntity(RivalryStatsDTO rivalryStats) throws ParseException {

        //modelMapper.getConfiguration().setAmbiguityIgnored(true);
        RivalryStats stats = new RivalryStats(); //modelMapper.map(RivalryStatsDTO, RivalryStats.class);

        stats.setRivalryStatsId(rivalryStats.getRivalryStatsId());
        stats.setScore(rivalryStats.getScore());
        stats.setGameCount(rivalryStats.getGameCount());
        stats.setWinCount(rivalryStats.getWinCount());
        stats.setLoseCount(rivalryStats.getLoseCount());
        stats.setTieCount(rivalryStats.getTieCount());
        stats.setWinStreak(rivalryStats.getWinStreak());
        stats.setLoseStreak(rivalryStats.getLoseStreak());
        stats.setTieStreak(rivalryStats.getTieStreak());
        stats.setLonguestWinStreak(rivalryStats.getLonguestWinStreak());
        stats.setLonguestLoseStreak(rivalryStats.getLonguestLoseStreak());
        stats.setLonguestTieStreak(rivalryStats.getLonguestTieStreak());
        stats.setBestScore(rivalryStats.getBestScore());
        stats.setWorstScore(rivalryStats.getWorstScore());
        if(rivalryStats.getRivalryStatsId() != null) {
            stats.setTournament(rivalryStatsRepository.findOne(rivalryStats.getRivalryStatsId()).getTournament());
            stats.setUser(rivalryStatsRepository.findOne(rivalryStats.getRivalryStatsId()).getUser());
            stats.setRival(rivalryStatsRepository.findOne(rivalryStats.getRivalryStatsId()).getRival());
        }

        return stats;
    }

    public RivalryStatsDTO convertToDto(RivalryStats stats) {

        RivalryStatsDTO rivalryStatsDTO = new RivalryStatsDTO(); //modelMapper.map(stats, RivalryStatsDTO.class);

        rivalryStatsDTO.setRivalryStatsId(stats.getRivalryStatsId());
        rivalryStatsDTO.setScore(stats.getScore());
        rivalryStatsDTO.setGameCount(stats.getGameCount());
        rivalryStatsDTO.setWinCount(stats.getWinCount());
        rivalryStatsDTO.setLoseCount(stats.getLoseCount());
        rivalryStatsDTO.setTieCount(stats.getTieCount());
        rivalryStatsDTO.setWinStreak(stats.getWinStreak());
        rivalryStatsDTO.setLoseStreak(stats.getLoseStreak());
        rivalryStatsDTO.setTieStreak(stats.getTieStreak());
        rivalryStatsDTO.setLonguestWinStreak(stats.getLonguestWinStreak());
        rivalryStatsDTO.setLonguestLoseStreak(stats.getLonguestLoseStreak());
        rivalryStatsDTO.setLonguestTieStreak(stats.getLonguestTieStreak());
        rivalryStatsDTO.setBestScore(stats.getBestScore());
        rivalryStatsDTO.setWorstScore(stats.getWorstScore());
        rivalryStatsDTO.setUser(userService.convertToDto(stats.getUser()));
        rivalryStatsDTO.setRival(userService.convertToDto(stats.getRival()));
        rivalryStatsDTO.setTournament(tournamentService.convertToDto(stats.getTournament()));

        return rivalryStatsDTO;
    }
}