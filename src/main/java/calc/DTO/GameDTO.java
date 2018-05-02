package calc.DTO;

import calc.entity.Outcome;
import calc.entity.Tournament;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by clementperez on 10/2/16.
 */
public class GameDTO {

    private Long gameId;
    private Date date;
    private String tournamentName;
    private String tournamentDisplayName;
    private List<OutcomeDTO> outcomes;

    public GameDTO() {
        super();
    }

    public List<OutcomeDTO> getOutcomes() {
        return outcomes;
    }

    public void setOutcomes(List<OutcomeDTO> outcomes) {
        this.outcomes = outcomes;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTournamentName() {
        return tournamentName;
    }

    public void setTournamentName(String tournamentName) {
        this.tournamentName = tournamentName;
    }

    public String getTournamentDisplayName() { return tournamentDisplayName; }

    public void setTournamentDisplayName(String tournamentDisplayName) { this.tournamentDisplayName = tournamentDisplayName;}

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }
}
