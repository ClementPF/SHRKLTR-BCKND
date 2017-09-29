package calc.controller;

import calc.DTO.FacebookUserInfoDTO;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import calc.DTO.GameDTO;
import calc.DTO.UserDTO;
import calc.DTO.StatsDTO;
import calc.entity.Game;
import calc.entity.User;
import calc.entity.Stats;
import calc.repository.UserRepository;
import calc.security.Secured;
import calc.service.GameService;
import calc.service.UserService;
import calc.service.StatsService;
import calc.service.TournamentService;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

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

    @RequestMapping(value = "/user/{userId}", method = RequestMethod.GET)
    public UserDTO getUser(@PathVariable(value="userId") Long userId) {
        return userService.findOne(userId);
    }

    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public UserDTO getCurrentUser() {
        FacebookUserInfoDTO userInfo = (FacebookUserInfoDTO)request.getAttribute("user_info");
        return userService.findByExternalId(userInfo.getId());
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
    @RequestMapping(value = "/user/{userId}/games", method = RequestMethod.GET)
    public List<Game> userMacths(@PathVariable(value="userId") Long userId) {
        return repoGames.findByUser(userService.findOne(userId));
    }*/

    @RequestMapping(value = "/user/{userId}/games", method = RequestMethod.GET)
    public List<GameDTO> userGamesForTournament(@PathVariable(value="userId") Long userId, @RequestParam(value="tournamentName", required = false) String tournamentName) {
        List<GameDTO> m = new ArrayList<>();

        if(tournamentName != null){
            m = gameService.findByUserByTournament(userId,tournamentName);
        }else
            m = gameService.findByUser(userId);

        return m;
    }
}
