package calc.repository;

import calc.entity.Stats;
import calc.entity.Tournament;
import calc.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StatsRepository extends CrudRepository<Stats, Long> {
    List<Stats> findByTournament(Tournament tournament);
    List<Stats> findByTournamentOrderByScoreDesc(Tournament tournament);
    List<Stats> findByUser(User user);
    List<Stats> findByUserId(Long userId);
    public Stats findByUserIdAndTournament(@Param("userId") Long userId,@Param("tournamentName") String tournamentId);
    public Stats findByUsernameAndTournament(@Param("username") String username,@Param("tournamentName") String tournamentId);
}