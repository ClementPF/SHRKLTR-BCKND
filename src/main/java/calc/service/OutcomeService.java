package calc.service;

import calc.DTO.OutcomeDTO;
import calc.entity.Outcome;
import calc.repository.GameRepository;
import calc.repository.OutcomeRepository;
import calc.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by clementperez on 9/20/16.
 */
@Service
public class OutcomeService {

    @Autowired
    private OutcomeRepository outcomeRepository;
    @Autowired
    private GameService gameService;
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;

    public List<OutcomeDTO> findByUserId(Long userId) {
        return outcomeRepository.findByUserId(userId).stream()
                .map(o -> convertToDto(o)).collect(Collectors.toList());
    }

    public List<OutcomeDTO> findByGameId(Long gameId) {
        return outcomeRepository.findByGameId(gameId).stream()
                .map(o -> convertToDto(o)).collect(Collectors.toList());

    }

    protected Outcome convertToEntity(OutcomeDTO outcomeDto) throws ParseException {
       // Outcome outcome = modelMapper.map(outcomeDto, Outcome.class);
       // modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE);

        Outcome outcome = null;

        if(outcomeDto.getGameId() != null) {
            outcome = new Outcome(
                    outcomeDto.getScoreValue(),
                    outcomeDto.getResult(),
                    gameRepository.findOne(outcomeDto.getGameId()),
                    userRepository.findByUserName(outcomeDto.getUserName()));
        }

        return outcome;
    }

    protected OutcomeDTO convertToDto(Outcome outcome) {
        // OutcomeDTO outcomeDTO = modelMapper.map(outcome, OutcomeDTO.class);
        // modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE);

        OutcomeDTO outcomeDTO = new OutcomeDTO();

        outcomeDTO.setOutcomeId(outcome.getOutcomeId());
        outcomeDTO.setScoreValue(outcome.getScoreValue());
        outcomeDTO.setResult(outcome.getResults());
        outcomeDTO.setGameId(outcome.getGame().getGameId());
        outcomeDTO.setUserName(outcome.getUser().getUserName());

        return outcomeDTO;
    }
}

