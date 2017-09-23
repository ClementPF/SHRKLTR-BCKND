package calc.controller;

import calc.DTO.SportDTO;
import calc.entity.User;
import calc.entity.Sport;
import calc.repository.UserRepository;
import calc.repository.SportRepository;
import calc.security.Secured;
import calc.service.SportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by clementperez on 9/22/16.
*/
@RestController
public class SportController {

    @Autowired
    private SportService sportService;

    @RequestMapping(value = "/sports", method = RequestMethod.GET)
    @Secured
    public List<SportDTO> sports() {
        return sportService.findAll();
    }
}
