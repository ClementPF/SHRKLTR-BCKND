package calc.controller;

import calc.DTO.TokenDTO;
import calc.DTO.TokenRequestDTO;
import calc.property.JwtProperties;
import calc.service.AuthService;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import java.util.Date;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author danny
 */
@RestController
public class AuthController {
    @Autowired
    private Algorithm algorithm;
    
    @Autowired
    private AuthService authService;
    
    /**
     * API endpoint to exchange Facebook access tokens for a JWToken
     * @param tokenRequest a JSON object containing the Facebook access token in the 'fb_access_token' field
     * @return JWToken that can be used to access secured APIs
     */
    @RequestMapping(value = "/auth/token", method = RequestMethod.POST)
    public TokenDTO getToken(@RequestBody TokenRequestDTO tokenRequest) {
        return authService.getToken(tokenRequest);
    }

    /**
     * API endpoint to refresh previously created long living JWToken
     * @param tokenRefresh a JSON object containing a JWToken
     * @return JWToken that can be used to access secured APIs
     */
    @RequestMapping(value = "/auth/refresh", method = RequestMethod.POST)
    public TokenDTO refreshToken(@RequestBody TokenDTO tokenRefresh) {
        return authService.refreshToken(tokenRefresh);
    }
}
