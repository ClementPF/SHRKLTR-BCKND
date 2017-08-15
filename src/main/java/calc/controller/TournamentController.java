package calc.controller;

import calc.DTO.GameDTO;
import calc.DTO.UserDTO;
import calc.DTO.StatsDTO;
import calc.DTO.TournamentDTO;
import calc.entity.*;
import calc.repository.*;
import calc.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Description;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by clementperez on 9/22/16.
 */

@RestController
public class TournamentController {

    @Autowired
    private TournamentRepository tournamentRepository;
    @Autowired
    private TournamentService tournamentService;
    @Autowired
    private StatsService statsService;
    @Autowired
    private GameService matchService;
    @Autowired
    private SportService sportService;
    @Autowired
    private UserService userService;


    @RequestMapping(value = "/tournaments", method = RequestMethod.GET)
    public List<TournamentDTO> tournamentsForSport(@RequestParam(value="sport", defaultValue="") String name) {

        List<Tournament> tournamentSet = tournamentRepository.findBySport(sportService.findByName(name));

        return tournamentSet.stream().map(t -> tournamentService.convertToDto(t)).collect(Collectors.toList());
    }

    /**
     * Let the owner of a Tournament to delete it.
     * @param name 
     */
    @RequestMapping(value = "/tournament/{tournamentName}", method = RequestMethod.DELETE)
    @Description("Let the owner of a Tournament to delete it.")
    public void deleteTournament(@PathVariable(value="tournamentName") String name) {
        // need to check if tournament is deleted by owner

        tournamentRepository.delete(tournamentRepository.findByName(name));
    }

    /**
     * Create a Tournament
     *
     * @param tournament
     */
    @RequestMapping(value = "/tournament", method = RequestMethod.POST)
    public void createTournament(TournamentDTO tournament) {
        User owner = userService.whoIsLoggedIn();
        try {
            tournamentRepository.save(tournamentService.convertToEntity(tournament));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/tournament/{tournamentName}", method = RequestMethod.PUT)
    public void updateTournament(@PathVariable(value="tournamentName") String name, TournamentDTO tournament) {
        User owner = userService.whoIsLoggedIn();
        try {
            tournamentRepository.save(tournamentService.convertToEntity(tournament));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/tournament/{tournamentName}", method = RequestMethod.GET)
    public Map tournamentNamed(@PathVariable(value="tournamentName") String name) {
        Tournament tournament =  tournamentRepository.findByName(name);
        List<StatsDTO> stats = statsService.findByTournament(tournament).stream()
                .map(s -> statsService.convertToDto(s)).collect(Collectors.toList());

        HashMap map = new HashMap<>();
        map.put("tournament",tournament);
        map.put("stats",stats);
        return map;
    }

    @RequestMapping(value = "/tournament/{tournamentName}/matchs", method = RequestMethod.GET)
    public List<GameDTO> matchesForTournament(@PathVariable(value="tournamentName") String name) {
        Tournament tournament =  tournamentRepository.findByName(name);
        List<Game> matchs = tournament.getMatchs();
        return matchs.stream()
                .map(m -> matchService.convertToDto(m)).collect(Collectors.toList());
    }

    @RequestMapping(value = "/tournament/{tournamentName}/matchs", method = RequestMethod.POST)
    public GameDTO addMatchForTournament(@PathVariable(value="tournamentName") String name, @RequestBody GameDTO match) {

        //TODO validate data
        //TODO looser send game
        //TODO calculate point value
        //TODO might make more sense to be in POST /match ??

        Game m = null;
        try {
            m = matchService.convertToEntity(match);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return matchService.convertToDto(tournamentService.addMatchForTournament(name,m));
    }

    @RequestMapping(value = "/tournament/{tournamentName}/stats", method = RequestMethod.GET)
    public List<StatsDTO> statsForTournament(@PathVariable(value="tournamentName") String name) {
        Tournament tournament =  tournamentRepository.findByName(name);
        List<Stats> stats = tournament.getStats();
        return stats.stream()
                .map(s -> statsService.convertToDto(s)).collect(Collectors.toList());
    }

    @RequestMapping(value = "/tournament/{tournamentName}/users", method = RequestMethod.GET)
    public List<UserDTO> usersForTournament(@PathVariable(value="tournamentName") String name) {
        Tournament tournament =  tournamentRepository.findByName(name);

        return userService.findUsersInTournament(tournament).stream()
                .map(users -> userService.convertToDto(users,tournament)).collect(Collectors.toList());
    }
}
