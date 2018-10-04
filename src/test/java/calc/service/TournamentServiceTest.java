package calc.service;

import calc.DTO.SportDTO;
import calc.DTO.TournamentDTO;
import calc.DTO.UserDTO;
import calc.entity.Sport;
import calc.entity.Tournament;
import calc.entity.User;

import java.util.Random;
import java.util.UUID;

/**
 * Created by clementperez on 31/08/18.
 */
public class TournamentServiceTest {

    public TournamentDTO makeRandomTournamentDTO(SportDTO sport, UserDTO owner){

        TournamentDTO t = new TournamentDTO(UUID.randomUUID().toString(), sport, owner);

        t.setTournamentId(new Random().nextLong());
        t.setName(UUID.randomUUID().toString());
        t.setIsOver(new Random().nextBoolean());

        return t;
    }

    public Tournament makeRandomTournament(Sport sport, User owner){

        Tournament t = new Tournament(UUID.randomUUID().toString(),sport, owner);

        t.setTournamentId(new Random().nextLong());
        t.setName(UUID.randomUUID().toString());
        t.setIsOver(new Random().nextBoolean());

        return t;
    }
}
