package calc.service;

import calc.DTO.GameDTO;
import calc.DTO.OutcomeDTO;
import calc.ELO.EloRating;
import calc.entity.*;
import calc.repository.OutcomeRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import calc.repository.GameRepository;

/**
 * Created by clementperez on 9/20/16.
 */
@Service
public class OutcomeService {

    @Autowired
    private OutcomeRepository outcomeRepository;
    @Autowired
    private GameService matchService;
    @Autowired
    private UserService userService;
    @Autowired
    private ModelMapper modelMapper;

    public List<Outcome> findByUserId(Long userId) {
        return outcomeRepository.findByUserId(userId);
    }

    public List<Outcome> findByGameId(Long gameId) {
        return outcomeRepository.findByGameId(gameId);
    }

    public Outcome convertToEntity(OutcomeDTO outcomeDto) throws ParseException {
        Outcome outcome = modelMapper.map(outcomeDto, Outcome.class);

        outcome.setOutcomeId(outcomeDto.getOutcomeId());
        outcome.setScoreValue(outcomeDto.getScoreValue());
        outcome.setResults(outcomeDto.getResult());
        if(outcomeDto.getGameId() != null) {
            outcome.setGame(matchService.findOne(outcomeDto.getGameId()));
        }
        outcome.setUser(userService.findByUserName(outcomeDto.getUserName()));

        return outcome;
    }

    public OutcomeDTO convertToDto(Outcome outcome) {
        OutcomeDTO outcomeDTO = modelMapper.map(outcome, OutcomeDTO.class);

        outcomeDTO.setOutcomeId(outcome.getOutcomeId());
        outcomeDTO.setScoreValue(outcome.getScoreValue());
        outcomeDTO.setResult(outcome.getResults());
        outcomeDTO.setGameId(outcome.getGame().getGameId());
        outcomeDTO.setUserName(outcome.getUser().getUserName());

        return outcomeDTO;
    }
}

