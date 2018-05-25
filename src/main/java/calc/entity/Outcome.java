package calc.entity;

import javax.persistence.*;
import java.lang.reflect.Field;

/**
 * Created by clementperez on 9/13/16.
 */

@Entity

@NamedQueries({
        @NamedQuery(name = "Outcome.findByUserId", query = "SELECT o FROM Outcome o WHERE o.user.userId = ?1"),
        @NamedQuery(name = "Outcome.findByGameId", query = "SELECT o FROM Outcome o WHERE o.game.gameId = ?1")
})
public class Outcome {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long outcomeId;
    @Column(nullable = false)
    private double scoreValue;
    @Column(nullable = false)
    private Result result;
    @ManyToOne
    @JoinColumn(name = "gameId")
    private Game game;
    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    public Outcome() {}

    public Outcome(double scoreValue, Result results, Game game, User user) {
        this.scoreValue = scoreValue;
        this.result = results;
        this.game = game;
        this.user = user;
    }

    public Long getOutcomeId() {
        return outcomeId;
    }

    public void setOutcomeId(Long outcomeId) {
        this.outcomeId = outcomeId;
    }

    public double getScoreValue() {
        return scoreValue;
    }

    public void setScoreValue(double scoreValue) {
        this.scoreValue = scoreValue;
    }

    public Result getResults() {
        return result;
    }

    public void setResults(Result results) {
        this.result = results;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public enum Result {
        WIN(1),
        TIE(2),
        LOSS(3);

         Result(int i) {
        }
    };

    public boolean equals(Outcome o){
        boolean b = true;

        for(Field f : this.getClass().getDeclaredFields()){
            try {
                try {
                    b = b && f.get(this).equals(f.get(o));
                }catch(NullPointerException npe){
                    b = b && f.get(this) == f.get(o);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                b = false;
            }
        }

        if(b) {
            System.out.print(this.getOutcomeId() + " is " + o.getOutcomeId());
        }
        return b;
    }
}
