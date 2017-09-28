package calc.service;

import calc.DTO.GameDTO;
import calc.DTO.OutcomeDTO;
import calc.DTO.SportDTO;
import calc.DTO.TournamentDTO;
import calc.entity.Tournament;
import calc.repository.GameRepository;
import calc.repository.TournamentRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private UserService userService;
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

    public List<TournamentDTO> findBySportId(Long sportId){
        return tournamentRepository.findBySportId(sportId).stream().map(t -> convertToDto(t)).collect(Collectors.toList());
    }

    public TournamentDTO save(TournamentDTO tournament){
        try {
            return convertToDto(tournamentRepository.save(convertToEntity(tournament)));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
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

    public GameDTO addGameForTournament(String tournamentName, GameDTO game) {


        //TODO validate data
        //TODO looser send game
        //TODO calculate point value
        //TODO might make more sense to be in POST /game ??

        TournamentDTO tournament =  tournamentService.findByName(tournamentName);
        List<OutcomeDTO> outcomes = game.getOutcomes();


        return gameService.addGame(tournament, outcomes);

    }

    protected Tournament convertToEntity(TournamentDTO tournamentDto) throws ParseException {

        Tournament tournament = modelMapper.map(tournamentDto, Tournament.class);
/*
        tournament.setTournamentId(tournamentDto.getTournamentId());
        tournament.setName(tournamentDto.getName());
        tournament.setDisplayName(tournamentDto.getDisplayName());
        tournament.setIsOver(tournamentDto.getIsOver());
        tournament.setSport(sportService.convertToEntity(tournamentDto.getSport()));
        tournament.setGames(gameRepository.findByTournamentName(tournamentDto.getName()));
        tournament.setOwner(userService.convertToEntity(tournamentDto.getOwner()));
*/
        return tournament;
    }

    protected TournamentDTO convertToDto(Tournament tournament) {
        TournamentDTO tournamentDTO = modelMapper.map(tournament, TournamentDTO.class);
/*
        tournamentDTO.setTournamentId(tournament.getTournamentId());
        tournamentDTO.setName(tournament.getName());
        tournamentDTO.setDisplayName(tournament.getDisplayName());
        tournamentDTO.setIsOver(tournament.getIsOver());
        tournamentDTO.setSport(sportService.convertToDto(tournament.getSport()));

        if (tournament.getTournamentId() != null) {
            tournamentDTO.setOwner(
                    userService.convertToDto(tournament.getOwner())
            );
        }*/
        return tournamentDTO;
    }
}
