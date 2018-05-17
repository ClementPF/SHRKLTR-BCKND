package calc.controller;

import calc.DTO.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import calc.security.Secured;
import calc.service.GameService;
import calc.service.UserService;
import calc.service.StatsService;
import calc.service.TournamentService;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Secured
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private TournamentService tournamentService;
    @Autowired
    private StatsService statsService;
    @Autowired
    private GameService gameService;
    
    @Resource
    private HttpServletRequest request;

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public List<UserDTO> users() {

        return userService.findAll();
    }

    @RequestMapping(value = "/user/{userName}", method = RequestMethod.GET)
    public UserDTO getUser(@PathVariable(value="userName") String username) {
        return userService.findByUserName(username);
    }

    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public UserDTO getCurrentUser() {
        ProviderUserInfoDTO userInfo = (ProviderUserInfoDTO)request.getAttribute("user_info");
        return userService.findByExternalId(userInfo.getId());
    }
    
    //TODO probably need to send a bad request or something
    @RequestMapping(value = "/user", method = RequestMethod.POST)
    public UserDTO createUser(@RequestBody UserDTO user) {
        return userService.save(user);
    }

    @RequestMapping(value = "/user/{userName}", method = RequestMethod.PUT)
    public UserDTO updateUser(@PathVariable(value="userName") String username, @RequestBody UserDTO user) {
        UserDTO p = userService.findByUserName(username);
        p.setFirstName(user.getFirstName());
        p.setLastName(user.getLastName());
        p.setUsername(user.getUsername());

        return userService.save(p);
    }

    @RequestMapping(value = "/user/{userName}/stats", method = RequestMethod.GET)
    public List<StatsDTO> userStats(@PathVariable(value="userName") String username) {
        List<StatsDTO> statsDTOs = statsService.findByUser(userService.findByUserName(username));

        return statsService.findByUser(userService.findByUserName(username));
    }

    @RequestMapping(value = "/user/{userName}/stats2", method = RequestMethod.GET)
    public StatsDTO userStatsForTournament(@PathVariable(value="userName") String username, @RequestParam(value="tournamentName", defaultValue="") String tournamentName) {
        return statsService.findByUserNameAndTournament(username, tournamentName);
    }


    @RequestMapping(value = "/user/{userName}/tournaments", method = RequestMethod.GET)
    public List<TournamentDTO> userTournaments(@PathVariable(value="userName") String username) {
        return tournamentService.findByUserName(username);
    }
/*
    @RequestMapping(value = "/user/{userId}/games", method = RequestMethod.GET)
    public List<Game> userMacths(@PathVariable(value="userId") Long userId) {
        return repoGames.findByUser(userService.findOne(userId));
    }*/

    @RequestMapping(value = "/user/{userName}/games", method = RequestMethod.GET)
    public List<GameDTO> userGamesForTournament(@PathVariable(value="userName") String username, @RequestParam(value="tournamentName", required = false) String tournamentName) {
        List<GameDTO> m = new ArrayList<>();

        if(tournamentName != null){
            m = gameService.findByUserByTournament(username,tournamentName);
        }else
            m = gameService.findByUser(username);

        m = m.stream().sorted(Comparator.comparing(GameDTO::getDate).reversed()).collect(Collectors.toList());

        return m;
    }
}
