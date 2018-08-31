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

import java.util.*;


@RestController
@Secured
public class StatsController {

    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private StatsService statsService;
    @Autowired
    private StatsRepository statsRepository;
    @Autowired
    private RivalryStatsRepository rivalryStatsRepository;
    @Autowired
    private RivalryStatsService rivalryStatsService;

    @RequestMapping(value ="/stats", method = RequestMethod.GET)
    public StatsDTO stats(@RequestParam(value="userName") String username,@RequestParam(value="tournamentName") String tournamentName) {
        return statsService.findByUserNameAndTournament(username, tournamentName);
    }

    @RequestMapping(value ="/rivalries", method = RequestMethod.GET)
    public List<RivalryStatsDTO> rivalryStatsForUserAndTournament(@RequestParam(value="userName") String username, @RequestParam(value="tournamentName") String tournamentName) {
        return rivalryStatsService.findByUserNameAndTournament(username, tournamentName);
    }

    @RequestMapping(value ="/rivalry", method = RequestMethod.GET)
    public RivalryStatsDTO rivalryStatsForUserAndRivalAndTournament(@RequestParam(value="userName") String username,@RequestParam(value="rivalName") String rivalname,@RequestParam(value="tournamentName") String tournamentName) {
        return rivalryStatsService.findByUserNameAndRivalNameAndTournament(username, rivalname, tournamentName);
    }

/*
    @RequestMapping(value ="/upgraderivalry", method = RequestMethod.GET)
    public void rivalryStatsUpdate(@RequestParam(value="userName") String username,@RequestParam(value="rivalName") String rivalname,@RequestParam(value="tournamentName") String tournamentName) {

        Iterator<Stats> it = statsRepository.findAll().iterator();

        for (Stats s : statsRepository.findAll()) {
            List<RivalryStats> rss = rivalryStatsRepository.findByUserUserIdAndTournamentTournamentId(s.getUser().getUserId(),s.getTournament().getTournamentId());
            if(rss.isEmpty()) continue;
            RivalryStats bestRs = rss.stream()
                    .max(Comparator.comparing(RivalryStats::getScore))
                    .orElseThrow(NoSuchElementException::new);

            RivalryStats worstRs = rss.stream()
                    .min(Comparator.comparing(RivalryStats::getScore))
                    .orElseThrow(NoSuchElementException::new);

            System.out.print(bestRs.getUser().getUserName() + " " + bestRs.getScore() + " " + bestRs.getRival().getUserName());
            System.out.print(worstRs.getUser().getUserName() + " " + worstRs.getScore() + " " + worstRs.getRival().getUserName());

            if(bestRs.getRivalryStatsId() == 120){
                System.out.print(bestRs.getTournament().getName() + " " +bestRs.getUser().getUserName());
            }if(worstRs.getRivalryStatsId() == 120){
                System.out.print(worstRs.getTournament().getName() + " " +worstRs.getUser().getUserName());
            }
            if(bestRs.getScore() > 0)
                s.setBestRivalry(bestRs);
            else{
                s.setBestRivalry(null);
            }
            if(worstRs.getScore() < 0)
                s.setWorstRivalry(worstRs);
            else{
                s.setWorstRivalry(null);
            }
            statsRepository.save(s);
        }
    }*/
}