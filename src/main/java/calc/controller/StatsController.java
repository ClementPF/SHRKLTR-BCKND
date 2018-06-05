package calc.controller;

import calc.DTO.RivalryStatsDTO;
import calc.DTO.StatsDTO;
import calc.entity.Game;
import calc.entity.RivalryStats;
import calc.entity.Stats;
import calc.repository.GameRepository;
import calc.repository.RivalryStatsRepository;
import calc.repository.StatsRepository;
import calc.security.Secured;
import calc.service.RivalryStatsService;
import calc.service.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;


@RestController
@Secured
public class StatsController {

    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private StatsService statsService;
    @Autowired
    private RivalryStatsService rivalryStatsService;

    @RequestMapping(value ="/stats", method = RequestMethod.GET)
    public StatsDTO stats(@RequestParam(value="userName") String username,@RequestParam(value="tournamentName") String tournamentName) {
        return statsService.findByUserNameAndTournament(username, tournamentName);
    }

    @RequestMapping(value ="/rivalry", method = RequestMethod.GET)
    public RivalryStatsDTO rivalryStats(@RequestParam(value="userName") String username,@RequestParam(value="rivalName") String rivalname,@RequestParam(value="tournamentName") String tournamentName) {
        return rivalryStatsService.findByUserNameAndRivalNameAndTournament(username, rivalname, tournamentName);
    }
}