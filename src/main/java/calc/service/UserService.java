package calc.service;

import calc.DTO.UserDTO;
import calc.entity.*;
import calc.repository.OutcomeRepository;
import calc.repository.UserRepository;
import calc.repository.StatsRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.CascadeType;
import javax.persistence.OneToMany;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private ModelMapper modelMapper;
    
/*
    private List<User> userFromTournament(Tournament tournament){

    }*/

    public List<User> findAll(){
        List<User> copy = new ArrayList<>();

        for (User user : userRepository.findAll()) {
            copy.add(user);
        }
        return copy;
    }

    public User findOne(Long id){
        return userRepository.findOne(id);
    }

    public User save(User user){
        return userRepository.save(user);
    }

    public User whoIsLoggedIn(){
        User user = null; //find the logged in user
        return user;
    }

    public List<User> findByLastName(String lastName){
        return userRepository.findByLastName(lastName);
    }

    public User findByUserName(String userName){
        return userRepository.findByUserName(userName);
    }

    public List<User> findUsersInTournament(Tournament tournament){
        List<User> p = new ArrayList<>();

        List<Stats> stats = tournament.getStats();
        for(Stats s : stats){
            p.add(s.getUser());
        }

        return p;
    }


    public User convertToEntity(UserDTO userDto) throws ParseException {
        User user = modelMapper.map(userDto, User.class);

        user.setUserId(userDto.getUserId());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setFirstName(userDto.getFirstName());
        user.setUserName(userDto.getUserName());

        if (userDto.getUserId() != null) {
            user.setStats(statsRepository.findByUserId(userDto.getUserId()));
            user.setOutcomes(outcomeService.findByUserId(userDto.getUserId()));
        }
        return user;
    }

    public UserDTO convertToDto(User user, Tournament tournament) {
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);

        userDTO.setUserId(user.getUserId());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setUserName(user.getUserName());

        if (user.getUserId() != null)
            userDTO.setStats(statsService.convertToDto(user.getStats(tournament)));

        return userDTO;
    }

    public UserDTO convertToDto(User user) {
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);

        userDTO.setUserId(user.getUserId());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setUserName(user.getUserName());

        return userDTO;
    }
}