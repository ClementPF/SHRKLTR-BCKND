package calc.service;

import calc.DTO.OutcomeDTO;
import calc.entity.Outcome;
import calc.repository.MatchRepository;
import calc.repository.OutcomeRepository;
import calc.repository.UserRepository;
import org.modelmapper.ModelMapper;
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
    private MatchService matchService;
    @Autowired
    private MatchRepository matchRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;

    public List<OutcomeDTO> findByUserId(Long userId) {
        return outcomeRepository.findByUserId(userId).stream()
                .map(o -> convertToDto(o)).collect(Collectors.toList());
    }

    public List<OutcomeDTO> findByMatchId(Long matchId) {
        return outcomeRepository.findByMatchId(matchId).stream()
                .map(o -> convertToDto(o)).collect(Collectors.toList());
    }

    protected Outcome convertToEntity(OutcomeDTO outcomeDto) throws ParseException {
        Outcome outcome = modelMapper.map(outcomeDto, Outcome.class);

        outcome.setOutcomeId(outcomeDto.getOutcomeId());
        outcome.setScoreValue(outcomeDto.getScoreValue());
        outcome.setResults(outcomeDto.getResult());
        if(outcomeDto.getMatchId() != null) {
            outcome.setMatch(matchRepository.findOne(outcomeDto.getMatchId()));
        }
        outcome.setUser(userRepository.findByUserName(outcomeDto.getUserName()));

        return outcome;
    }

    protected OutcomeDTO convertToDto(Outcome outcome) {
        OutcomeDTO outcomeDTO = modelMapper.map(outcome, OutcomeDTO.class);

        outcomeDTO.setOutcomeId(outcome.getOutcomeId());
        outcomeDTO.setScoreValue(outcome.getScoreValue());
        outcomeDTO.setResult(outcome.getResults());
        outcomeDTO.setMatchId(outcome.getMatch().getMatchId());
        outcomeDTO.setUserName(outcome.getUser().getUserName());

        return outcomeDTO;
    }
}

