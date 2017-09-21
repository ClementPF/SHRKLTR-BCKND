package calc.controller;

import calc.DTO.MatchDTO;
import calc.DTO.StatsDTO;
import calc.DTO.UserDTO;
import calc.service.MatchService;
import calc.service.StatsService;
import calc.service.TournamentService;
import calc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private TournamentService tournamentService;
    @Autowired
    private StatsService statsService;
    @Autowired
    private MatchService matchService;

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public List<UserDTO> users() {

        return userService.findAll();
    }

    @RequestMapping(value = "/user/{userId}", method = RequestMethod.GET)
    public UserDTO getUser(@PathVariable(value="userId") Long userId) {
        return userService.findOne(userId);
    }

    //TODO probably need to send a bad request or something
    @RequestMapping(value = "/user", method = RequestMethod.POST)
    public UserDTO createUser(@RequestBody UserDTO user) {
        return userService.save(user);
    }

    @RequestMapping(value = "/user/{userId}", method = RequestMethod.PUT)
    public UserDTO updateUser(@PathVariable(value="userId") Long userId, @RequestBody UserDTO user) {
        UserDTO p = userService.findOne(userId);
        p.setFirstName(user.getFirstName());
        p.setLastName(user.getLastName());
        p.setUserName(user.getUserName());

        return userService.save(p);
    }

    @RequestMapping(value = "/user/{userId}/stats", method = RequestMethod.GET)
    public List<StatsDTO> userStats(@PathVariable(value="userId") Long userId) {
        return statsService.findByUser(userService.findOne(userId));
    }

    @RequestMapping(value = "/user/{userId}/stats2", method = RequestMethod.GET)
    public StatsDTO userStatsForTournament(@PathVariable(value="userId") Long userId, @RequestParam(value="tournamentName", defaultValue="") String tournamentName) {
        return statsService.findByUserAndTournament(userId, tournamentName);
    }
/*
    @RequestMapping(value = "/user/{userId}/matchs", method = RequestMethod.GET)
    public List<Match> userMacths(@PathVariable(value="userId") Long userId) {
        return repoMatchs.findByUser(userService.findOne(userId));
    }*/

    @RequestMapping(value = "/user/{userId}/matchs", method = RequestMethod.GET)
    public List<MatchDTO> userMatchsForTournament(@PathVariable(value="userId") Long userId, @RequestParam(value="tournamentName", required = false) String tournamentName) {
        List<MatchDTO> m = new ArrayList<>();
        if(tournamentName != null){
            m = matchService.findByUserByTournament(userId,tournamentName);
        }else
            m = matchService.findByUser(userId);

        return m;
    }
}
