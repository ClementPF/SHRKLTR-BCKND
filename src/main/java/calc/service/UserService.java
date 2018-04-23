package calc.service;

import calc.DTO.FacebookUserInfoDTO;
import calc.DTO.StatsDTO;
import calc.DTO.TournamentDTO;
import calc.DTO.UserDTO;
import calc.entity.Stats;
import calc.entity.Tournament;
import calc.entity.User;
import calc.repository.OutcomeRepository;
import calc.repository.StatsRepository;
import calc.repository.TournamentRepository;
import calc.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by clementperez on 9/20/16.
 */
@Service
public class UserService{

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

        FacebookUserInfoDTO userInfo = (FacebookUserInfoDTO) request.getAttribute("user_info");
        return findByExternalId(userInfo.getId());
    }

    public List<UserDTO> findByLastName(String lastName){
        return userRepository.findByLastName(lastName).stream()
                .map(u -> convertToDto(u)).collect(Collectors.toList());
    }
    
    public UserDTO findByUserName(String username){
        return convertToDto(userRepository.findByUserName(username));
    }


    public UserDTO findByExternalId(long id){
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

        List<Stats> stats = statsRepository.findByTournament(tournamentRepository.findByName(tournamentName));
        for(Stats s : stats){
            p.add(s.getUser());
        }

        return p.stream()
                .map(u -> convertToDto(u)).collect(Collectors.toList());
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
}
