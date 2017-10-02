package calc.repository;

import calc.entity.Game;
import calc.entity.User;
import calc.entity.Stats;
import calc.entity.Tournament;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface StatsRepository extends CrudRepository<Stats, Long> {
    List<Stats> findByTournament(Tournament tournament);
    List<Stats> findByTournamentOrderByScoreDesc(Tournament tournament);
    List<Stats> findByUser(User user);
    List<Stats> findByUserId(Long userId);
    public Stats findByUserAndTournament(@Param("userId") Long userId,@Param("tournamentName") String tournamentId);
}