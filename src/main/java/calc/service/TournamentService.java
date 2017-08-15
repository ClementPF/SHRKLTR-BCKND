package calc.service;

import calc.DTO.GameDTO;
import calc.DTO.TournamentDTO;
import calc.entity.Game;
import calc.entity.User;
import calc.entity.Sport;
import calc.entity.Tournament;
import calc.repository.StatsRepository;
import calc.repository.TournamentRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import javax.persistence.*;
import java.text.ParseException;
import java.util.List;
import java.util.Set;

/**
 * Created by clementperez on 9/25/16.
 */
@Service
public class TournamentService {

    @Autowired
    private TournamentRepository tournamentRepository;
    @Autowired
    private SportService sportService;
    @Autowired
    private UserService userService;
    @Autowired
    private GameService matchService;
    @Autowired
    private ModelMapper modelMapper;

    List<Tournament> findBySport(Sport sport){
        return tournamentRepository.findBySport(sport);
    }

    Tournament findByName(String name){
        return tournamentRepository.findByName(name);
    }

    public Game addMatchForTournament(String tournamentName, Game match) {

        //TODO validate data
        //TODO looser send game
        //TODO calculate point value
        //TODO might make more sense to be in POST /match ??

        Tournament tournament =  tournamentRepository.findByName(tournamentName);

        return matchService.addGame(tournament, match.getOutcomes());
    }

    public Tournament convertToEntity(TournamentDTO tournamentDto) throws ParseException {

        Tournament tournament = modelMapper.map(tournamentDto, Tournament.class);

        tournament.setTournamentId(tournamentDto.getTournamentId());
        tournament.setName(tournamentDto.getName());
        tournament.setDisplayName(tournamentDto.getDisplayName());
        tournament.setIsOver(tournamentDto.getIsOver());
        tournament.setSport(sportService.convertToEntity(tournamentDto.getSport()));
        tournament.setMatchs(matchService.findByTournamentName(tournamentDto.getName()));
        tournament.setOwner(userService.convertToEntity(tournamentDto.getOwner()));

        return tournament;
    }

    public TournamentDTO convertToDto(Tournament tournament) {
        TournamentDTO tournamentDTO = modelMapper.map(tournament, TournamentDTO.class);

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
