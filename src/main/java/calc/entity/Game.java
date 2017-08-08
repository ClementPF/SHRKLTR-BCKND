package calc.entity;

import javax.persistence.*;
import java.util.*;

/**
 * Created by clementperez on 9/13/16.
 */

@Entity
@NamedQueries({
        @NamedQuery(name = "Game.findByTournamentName", query = "SELECT m FROM Game m WHERE m.tournament.name = ?1"),
        @NamedQuery(name = "Game.findByUserId",
                query = "SELECT m FROM Game m " +
                        "INNER JOIN m.outcomes o " +
                        "WHERE o.user.userId = ?1"),
        @NamedQuery(name = "Game.findByUserIdByTournamentName",
                query = "SELECT m FROM Game m " +
                        "INNER JOIN m.outcomes o " +
                        "WHERE o.user.userId = ?1 AND m.tournament.name=?2" )
})
public class Game {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long gameId;
    private Date date;

    @ManyToOne
    @JoinColumn(name = "tournamentId")
    private Tournament tournament;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL)
    private List<Outcome> outcomes;

    protected Game() {}

    public Game(Date date, Tournament tournament) {
        this.date = date;
        this.tournament = tournament;
    }

    public Game(Tournament tournament) {
        this.date = new Date();
        this.tournament = tournament;
    }

    public Game(Tournament tournament, List<Outcome> outcomes) {
        this.date = new Date();
        this.tournament = tournament;
        this.outcomes = outcomes;
    }

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Tournament getTournament() {
        return tournament;
    }

    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
    }

    public List<Outcome> getOutcomes() {
        return outcomes;
    }

    public void setOutcomes(List<Outcome> outcomes) {
        this.outcomes = outcomes;
        for(Outcome o : outcomes){
            o.setGame(this);
        }
    }
}