package calc.service;

import calc.DTO.*;
import calc.entity.Outcome;
import calc.entity.Sport;
import calc.entity.Tournament;
import calc.entity.User;
import calc.repository.GameRepository;
import calc.repository.SportRepository;
import calc.repository.TournamentRepository;
import calc.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.security.auth.callback.TextOutputCallback;
import java.text.ParseException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by clementperez on 9/25/16.
 */
@Service
public class TournamentService {

    @Autowired
    private TournamentRepository tournamentRepository;
    @Autowired
    private TournamentService tournamentService;
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private SportService sportService;
    @Autowired
    private SportRepository sportRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GameService gameService;
    @Autowired
    private ModelMapper modelMapper;

    public List<TournamentDTO> findBySport(SportDTO sport){
        try {
            return tournamentRepository.findBySport(sportService.convertToEntity(sport)).stream().map(t -> convertToDto(t)).collect(Collectors.toList());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<TournamentDTO> findAll(){
       return tournamentRepository.findAll().stream().map(t -> convertToDto(t)).collect(Collectors.toList());
    }

    public List<TournamentDTO> findBySportId(Long sportId){
        return tournamentRepository.findBySportId(sportId).stream().map(t -> convertToDto(t)).collect(Collectors.toList());
    }

    public List<TournamentDTO> findByUserName(String username) {
        return tournamentRepository.findByUserName(username).stream().map(t -> convertToDto(t)).collect(Collectors.toList());
    }

    public TournamentDTO createTournament(TournamentDTO tournament) {

        UserDTO owner = userService.whoIsLoggedIn();
        tournament.setOwner(owner);

        SportDTO s = tournament.getSport();
        String sportName = s.getName();
        Sport sport = sportRepository.findByName(s.getName());

        if(sport == null) {
            sport = new Sport();
            sport.setName(sportName);
            sportRepository.save(sport);
        }


        // tournament already exist

        // sport already exist

        // sport doesn't exist

        // tournament doesn't exist

        return save(tournament);
    }

    public TournamentDTO save(TournamentDTO tournament){

            Sport sport = sportRepository.findByName(tournament.getSport().getName());
            User owner = userRepository.findByUserName(tournament.getOwner().getUsername());
            Tournament t = new Tournament(tournament.getDisplayName(),sport, owner);
            return convertToDto(tournamentRepository.save(t));
    }

    public void delete(TournamentDTO tournament){
        try {
            tournamentRepository.delete(convertToEntity(tournament));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public TournamentDTO findByName(String name){
        Tournament t = tournamentRepository.findByName(name);
        return t == null ? null : convertToDto(t);
    }

    public GameDTO addGameForTournament(String tournamentName, GameDTO game){


        //TODO validate data
        //TODO looser send game
        //TODO calculate point value
        //TODO might make more sense to be in POST /game ??

        TournamentDTO tournament =  tournamentService.findByName(tournamentName);
        List<OutcomeDTO> outcomes = game.getOutcomes();

        return gameService.addGame(tournament, outcomes);

    }

    protected Tournament convertToEntity(TournamentDTO tournamentDto) throws ParseException {

       // Tournament tournament = modelMapper.map(tournamentDto, Tournament.class);

        Tournament tournament = new Tournament();
        tournament.setTournamentId(tournamentDto.getTournamentId());
        tournament.setName(tournamentDto.getName());
        tournament.setDisplayName(tournamentDto.getDisplayName());
        tournament.setIsOver(tournamentDto.getIsOver());
        tournament.setSport(sportRepository.findByName(tournamentDto.getSport().getName()));
        tournament.setGames(gameRepository.findByTournamentName(tournamentDto.getName()));
        tournament.setOwner(userService.convertToEntity(tournamentDto.getOwner()));

        return tournament;
    }

    protected TournamentDTO convertToDto(Tournament tournament) {
       // TournamentDTO tournamentDTO = modelMapper.map(tournament, TournamentDTO.class);

        TournamentDTO tournamentDTO = new TournamentDTO();

        tournamentDTO.setTournamentId(tournament.getTournamentId());
        tournamentDTO.setName(tournament.getName());
        tournamentDTO.setDisplayName(tournament.getDisplayName());
        tournamentDTO.setIsOver(tournament.getIsOver());
        tournamentDTO.setSport(sportService.convertToDto(tournament.getSport()));

        if (tournament.getTournamentId() != null) {
            tournamentDTO.setOwner(
                    userService.convertToDto(tournament.getOwner())
            );
        }
        return tournamentDTO;
    }
}
