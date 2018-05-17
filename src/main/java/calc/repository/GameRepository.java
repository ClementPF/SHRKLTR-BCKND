package calc.repository;

import calc.entity.Game;
import calc.entity.User;
import calc.entity.Stats;
import calc.entity.Tournament;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Set;

@Repository
public interface GameRepository extends CrudRepository<Game, Long> {
    List<Game> findByTournament(Tournament tournament);
    public List<Game> findByUserIdByTournamentName(@Param("userId") Long userId, @Param("tournamentName") String tournamentId);
    public List<Game> findByUserNameByTournamentName(@Param("userName") String username, @Param("tournamentName") String tournamentId);
    List<Game> findByTournamentName(String tournamentName);
    List<Game> findByUserId(Long userId);
    List<Game> findByUserName(String username);
}