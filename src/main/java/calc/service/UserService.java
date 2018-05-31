package calc.service;

import calc.DTO.*;
import calc.entity.Stats;
import calc.entity.Tournament;
import calc.entity.User;
import calc.exception.APIException;
import calc.repository.OutcomeRepository;
import calc.repository.StatsRepository;
import calc.repository.TournamentRepository;
import calc.repository.UserRepository;
import org.apache.http.*;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by clementperez on 9/20/16.
 */
@Service
public class UserService{

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private StatsRepository statsRepository;
    @Autowired
    private StatsService statsService;
    @Autowired
    private OutcomeService outcomeService;
    @Autowired
    private OutcomeRepository outcomeRepository;
    @Autowired
    private TournamentRepository tournamentRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Resource
    private HttpServletRequest request;
    
/*
    private List<User> userFromTournament(Tournament tournament){

    }*/

    public List<UserDTO> findAll(){
        List<User> copy = new ArrayList<>();

        for (User user : userRepository.findAll()) {
            copy.add(user);
        }
        return copy.stream()
                .map(u -> convertToDto(u)).collect(Collectors.toList());
    }

    public UserDTO findOne(Long id){
        return convertToDto(userRepository.findOne(id));
    }

    public UserDTO save(UserDTO user){
        try {
            return convertToDto(userRepository.save(convertToEntity(user)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public UserDTO whoIsLoggedIn(){

        ProviderUserInfoDTO userInfo = (ProviderUserInfoDTO) request.getAttribute("user_info");
        return findByExternalId(userInfo.getId());
    }

    private User whoIsLoggedInEntity(){
        ProviderUserInfoDTO userInfo = (ProviderUserInfoDTO) request.getAttribute("user_info");
        return userRepository.findByExternalId(userInfo.getId());
    }

    public List<UserDTO> findByLastName(String lastName){
        return userRepository.findByLastName(lastName).stream()
                .map(u -> convertToDto(u)).collect(Collectors.toList());
    }
    
    public UserDTO findByUserName(String username){
        return convertToDto(userRepository.findByUserName(username));
    }

    public UserDTO findByUserId(long id){
        return convertToDto(userRepository.findByUserId(id));
    }

    public UserDTO findByExternalId(String id){
        return convertToDto(userRepository.findByExternalId(id));
    }

    public List<UserDTO> findUsersInTournament(TournamentDTO tournament){
        List<User> p = new ArrayList<>();

        List<Stats> stats = statsRepository.findByTournament(tournamentRepository.findOne(tournament.getTournamentId()));
        for(Stats s : stats){
            p.add(s.getUser());
        }

        return p.stream()
                .map(u -> convertToDto(u)).collect(Collectors.toList());
    }

    public List<UserDTO> findUsersInTournamentNamed(String tournamentName){
        List<User> p = new ArrayList<>();
        Tournament t = tournamentRepository.findByName(tournamentName);
        List<Stats> stats = statsRepository.findByTournament(t);
        for(Stats s : stats){
            p.add(s.getUser());
        }

        return p.stream()
                .map(u -> convertToDto(u, t)).collect(Collectors.toList());
    }


    public User convertToEntity(UserDTO userDto) throws ParseException {
       // User user = modelMapper.map(userDto, User.class);

        User user = new User(userDto.getUsername());
        user.setUserId(userDto.getUserId());
        user.setFirst(userDto.getFirstName());
        user.setLast(userDto.getLastName());
        user.setFirst(userDto.getFirstName());

        if (userDto.getUserId() != null) {
            User u = userRepository.findOne(userDto.getUserId());
            user.setExternalId(u.getExternalId());
            user.setExternalIdProvider(u.getExternalIdProvider());
            user.setStats(statsRepository.findByUserId(userDto.getUserId()));
            user.setOutcomes(outcomeRepository.findByUserId(userDto.getUserId()));
        }
        return user;
    }

    protected UserDTO convertToDto(User user, Tournament tournament) {
        //UserDTO userDTO = modelMapper.map(user, UserDTO.class);
        UserDTO userDTO = new UserDTO();

        userDTO.setUserId(user.getUserId());
        userDTO.setFirstName(user.getFirst());
        userDTO.setLastName(user.getLast());
        userDTO.setFirstName(user.getFirst());
        userDTO.setUsername(user.getUserName());

        if (user.getUserId() != null)
            userDTO.setStats(new ArrayList<StatsDTO>(Arrays.asList(statsService.convertToDto(user.getStats(tournament)))));

        return userDTO;
    }

    protected UserDTO convertToDto(User user) {
    //UserDTO userDTO = modelMapper.map(user, UserDTO.class);
        UserDTO userDTO = new UserDTO();

        userDTO.setUserId(user.getUserId());
        userDTO.setFirstName(user.getFirst());
        userDTO.setLastName(user.getLast());
        userDTO.setFirstName(user.getFirst());
        userDTO.setUsername(user.getUserName());

        return userDTO;
    }

    public boolean exists(String username) {
        return findByUserName(username) != null;
    }


    public ResponseEntity registerForPushNotification(PushTokenDTO token) {

        User u = whoIsLoggedInEntity();
        u.setPushId(token.getValue());

        userRepository.save(u);

        return new ResponseEntity(HttpStatus.OK);
    }

    public void pushNotificationForUser(UserDTO user, String title ,String message, Object obj){

        User u = userRepository.findByUserId(user.getUserId());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> map= new HashMap<>();

        map.put("to", u.getPushId());
        //map.add("sound", "");
        map.put("title", title);
        map.put("body", message);
        map.put("data",  "{\"message\" : \"" + message + "\"}");

        HttpEntity<Map<String, String>> request = new HttpEntity<>(map, headers);

        try {
            logger.debug("Making Expo push API request");
            ResponseEntity<String> response = new RestTemplate().postForEntity("https://exp.host/--/api/v2/push/send", request, String.class);

            if(response.getStatusCode() == HttpStatus.OK && response.getStatusCode() == HttpStatus.BAD_REQUEST){
                logger.warn("Pushing the notification failed : {}", response.getBody());
            }
        } catch (HttpStatusCodeException hsce) {

            logger.warn("Received bad HTTP status code from EXPO push API. Exception message: {}", hsce.getMessage());
            //throw new APIException(hsce.getStatusCode(), hsce.getCause().getMessage(), hsce);
        }



    }
}
