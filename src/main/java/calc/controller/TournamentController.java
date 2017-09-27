package calc.controller;

import calc.DTO.GameDTO;
import calc.DTO.StatsDTO;
import calc.DTO.TournamentDTO;
import calc.DTO.UserDTO;
import calc.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Description;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by clementperez on 9/22/16.
 */

@RestController
public class TournamentController {

    @Autowired
    private TournamentService tournamentService;
    @Autowired
    private StatsService statsService;
    @Autowired
    private GameService gameService;
    @Autowired
    private SportService sportService;
    @Autowired
    private UserService userService;


    @RequestMapping(value = "/tournaments", method = RequestMethod.GET)
    public List<TournamentDTO> tournamentsForSport(@RequestParam(value="sport", defaultValue="") String name) {

        List<TournamentDTO> tournamentSet = tournamentService.findBySport(sportService.findByName(name));

        return tournamentSet;
    }

    /**
     * Let the owner of a Tournament to delete it.
     * @param name 
     */
    @RequestMapping(value = "/tournament/{tournamentName}", method = RequestMethod.DELETE)
    @Description("Let the owner of a Tournament to delete it.")
    public void deleteTournament(@PathVariable(value="tournamentName") String name) {
        // need to check if tournament is deleted by owner

        tournamentService.delete(tournamentService.findByName(name));
    }

    /**
     * Create a Tournament
     *
     * @param tournament
     */
    @RequestMapping(value = "/tournament", method = RequestMethod.POST)
    public void createTournament(TournamentDTO tournament) {
        UserDTO owner = userService.whoIsLoggedIn();
        tournament.setOwner(owner);
        tournamentService.save(tournament);
    }

    @RequestMapping(value = "/tournament/{tournamentName}", method = RequestMethod.PUT)
    public void updateTournament(@PathVariable(value="tournamentName") String name, TournamentDTO tournament) {
        UserDTO owner = userService.whoIsLoggedIn();
        tournamentService.save(tournament);
    }

    @RequestMapping(value = "/tournament/{tournamentName}", method = RequestMethod.GET)
    public Map tournamentNamed(@PathVariable(value="tournamentName") String name) {
        TournamentDTO tournament =  tournamentService.findByName(name);
//        List<StatsDTO> stats = statsService.findByTournament(tournament);

        HashMap map = new HashMap<>();
        map.put("tournament",tournament);
 //       map.put("stats",stats);
        return map;
    }

    @RequestMapping(value = "/tournament/{tournamentName}/games", method = RequestMethod.GET)
    public List<GameDTO> gamesForTournament(@PathVariable(value="tournamentName") String name) {
        List<GameDTO> games = gameService.findByTournamentName(name);
        return games;
    }

    @RequestMapping(value = "/tournament/{tournamentName}/games", method = RequestMethod.POST)
    public GameDTO addGameForTournament(@PathVariable(value="tournamentName") String name, @RequestBody GameDTO game) {

        //TODO validate data
        //TODO looser send game
        //TODO calculate point value
        //TODO might make more sense to be in POST /game ??


        return tournamentService.addGameForTournament(name,game);
    }

    @RequestMapping(value = "/tournament/{tournamentName}/stats", method = RequestMethod.GET)
    public List<StatsDTO> statsForTournament(@PathVariable(value="tournamentName") String name) {
        TournamentDTO tournament =  tournamentService.findByName(name);
        List<StatsDTO> stats = statsService.findByTournament(tournament);
        return stats;
    }

    @RequestMapping(value = "/tournament/{tournamentName}/users", method = RequestMethod.GET)
    public List<UserDTO> usersForTournament(@PathVariable(value="tournamentName") String name) {

        return userService.findUsersInTournamentNamed(name);
    }
}
