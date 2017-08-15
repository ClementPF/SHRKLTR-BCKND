package calc.controller;

import calc.entity.Game;
import calc.service.GameService;
import org.springframework.data.repository.CrudRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class MatchController {

    private GameService matchService;

}
