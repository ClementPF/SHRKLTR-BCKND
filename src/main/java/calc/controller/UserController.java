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
    private GameService matchService;
    
    @Resource
    private HttpServletRequest request;

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public List<UserDTO> users() {

        return userService.findAll().stream()
                .map(users -> userService.convertToDto(users)).collect(Collectors.toList());
    }

    @RequestMapping(value = "/user/{userId}", method = RequestMethod.GET)
    public UserDTO getUser(@PathVariable(value="userId") Long userId) {
        User p =  userService.findOne(userId);
        return userService.convertToDto(p);
    }

    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public UserDTO getCurrentUser() {
        FacebookUserInfoDTO userInfo = (FacebookUserInfoDTO)request.getAttribute("user_info");
        return userService.convertToDto(userService.findByUserName(String.valueOf(userInfo.getId())));
    }
    
    //TODO probably need to send a bad request or something
    @RequestMapping(value = "/user", method = RequestMethod.POST)
    public UserDTO createUser(@RequestBody UserDTO user) {
        try {
            User p = userService.convertToEntity(user);
            return userService.convertToDto(userService.save(p));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping(value = "/user/{userId}", method = RequestMethod.PUT)
    public UserDTO updateUser(@PathVariable(value="userId") Long userId, @RequestBody UserDTO user) {
        User p = userService.findOne(userId);
        p.setFirstName(user.getFirstName());
        p.setLastName(user.getLastName());
        p.setUserName(user.getUserName());

        return userService.convertToDto(userService.save(p));
    }

    @RequestMapping(value = "/user/{userId}/stats", method = RequestMethod.GET)
    public List<StatsDTO> userStats(@PathVariable(value="userId") Long userId) {
        List<Stats> s = statsService.findByUser(userService.findOne(userId));
        return s.stream()
                .map(stats -> statsService.convertToDto(stats)).collect(Collectors.toList());
    }

    @RequestMapping(value = "/user/{userId}/stats2", method = RequestMethod.GET)
    public StatsDTO userStatsForTournament(@PathVariable(value="userId") Long userId, @RequestParam(value="tournamentName", defaultValue="") String tournamentName) {
        Stats s = statsService.findByUserAndTournament(userId, tournamentName);
        return statsService.convertToDto(s);
    }
/*
    @RequestMapping(value = "/user/{userId}/matchs", method = RequestMethod.GET)
    public List<Match> userMacths(@PathVariable(value="userId") Long userId) {
        return repoMatchs.findByUser(userService.findOne(userId));
    }*/

    @RequestMapping(value = "/user/{userId}/matchs", method = RequestMethod.GET)
    public List<GameDTO> userMatchsForTournament(@PathVariable(value="userId") Long userId, @RequestParam(value="tournamentName", required = false) String tournamentName) {
        List<Game> m = new ArrayList<>();
        if(tournamentName != null){
            m = matchService.findByUserByTournament(userId,tournamentName);
        }else
            m = matchService.findByUser(userId);

        return m.stream()
                .map(matchs -> matchService.convertToDto(matchs)).collect(Collectors.toList());
    }
}
