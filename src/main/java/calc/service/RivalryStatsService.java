package calc.service;

import calc.DTO.RivalryStatsDTO;
import calc.DTO.TournamentDTO;
import calc.DTO.UserDTO;
import calc.entity.*;
import calc.repository.RivalryStatsRepository;
import calc.repository.StatsRepository;
import calc.repository.TournamentRepository;
import calc.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
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
    private StatsService statsService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private StatsRepository statsRepository;
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

        RivalryStatsDTO rivalryStats = findByUserAndRivalAndTournament(user.getUserId(),rival.getUserId(),tournament.getTournamentId());

        User u = userRepository.findOne(user.getUserId());
        User r = userRepository.findOne(rival.getUserId());
        Tournament t = tournamentRepository.findOne(tournament.getTournamentId());
        Stats s = statsRepository.findByUserUserIdAndTournamentTournamentId(user.getUserId(),tournament.getTournamentId());

        if(rivalryStats == null) {
            RivalryStats rs = new RivalryStats(u,r,t,s);
            rivalryStatsRepository.save(rs);
            return convertToDto(rs);
        }

        return rivalryStats;
    }

    public void recalculateAfterOutcome(Outcome outcome1, Outcome outcome2){
        RivalryStats rivalryStats = rivalryStatsRepository.findByUserUserIdAndRivalUserIdAndTournamentTournamentId(
                outcome1.getUser().getUserId(),
                outcome2.getUser().getUserId(),
                outcome1.getGame().getTournament().getTournamentId());

        if(rivalryStats == null){
            rivalryStats = new RivalryStats(outcome1.getUser(),outcome2.getUser(),outcome1.getGame().getTournament(), outcome1.getUser().getStats(outcome1.getGame().getTournament()));
        }

        rivalryStats.setScore(rivalryStats.getScore() + outcome1.getScoreValue());

        // recalculate worst and best rivalry
        double score = rivalryStats.getScore();
        Stats s = rivalryStats.getStats();
        RivalryStats bestRs = s.getBestRivalry();
        RivalryStats worstRs = s.getWorstRivalry();

        if(bestRs == null // can be null if no games were won
                || ( score > 0 && score > bestRs.getScore())){
            s.setBestRivalry(rivalryStats);
        }else if(bestRs.getRivalryStatsId() == rivalryStats.getRivalryStatsId()){
            // need to find next bestRivalry
            List<RivalryStats> rss = rivalryStatsRepository.findByUserUserIdAndTournamentTournamentId(s.getUser().getUserId(),s.getTournament().getTournamentId());

            RivalryStats newBestRs = rss.stream()
                    .max(Comparator.comparing(RivalryStats::getScore))
                    .filter(rs -> rs.getScore() > 0)
                    .orElse(null);

            s.setBestRivalry(newBestRs);
            statsRepository.save(s);
        }

        if(worstRs == null // can be null if no games were won
                || ( score < 0 && score < worstRs.getScore())){
            s.setWorstRivalry(rivalryStats);
        }else if(s.getWorstRivalry().getRivalryStatsId() == rivalryStats.getRivalryStatsId()){
            // need to find next newWorstRivalry
            List<RivalryStats> rss = rivalryStatsRepository.findByUserUserIdAndTournamentTournamentId(s.getUser().getUserId(),s.getTournament().getTournamentId());

            RivalryStats newWorstRs = rss.stream()
                    .min(Comparator.comparing(RivalryStats::getScore))
                    .filter(rs -> rs.getScore() < 0)
                    .orElse(null);

            s.setBestRivalry(newWorstRs);
            statsRepository.save(s);
        }


        switch (outcome1.getResults()){
            case WIN:
                rivalryStats.addWin();
                break;
            case LOSS:
                rivalryStats.addLose();
                break;
            case TIE:
                rivalryStats.addTie();
                break;
            default:
                break;
        }
        rivalryStatsRepository.save(rivalryStats);
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
            stats.setStats(statsRepository.findByUserUserIdAndTournamentTournamentId(rivalryStats.getUser().getUserId(), rivalryStats.getTournament().getTournamentId()));
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
        //rivalryStatsDTO.setStats(statsService.convertToDto(stats.getStats()));

        return rivalryStatsDTO;
    }
}
