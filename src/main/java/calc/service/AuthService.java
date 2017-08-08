package calc.service;

import calc.DTO.FacebookErrorDTO;
import calc.DTO.FacebookUserInfoDTO;
import calc.DTO.TokenDTO;
import calc.DTO.TokenRequestDTO;
import calc.entity.User;
import calc.exception.APIException;
import calc.property.FacebookProperties;
import calc.property.JwtProperties;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Date;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author danny
 */
@Service
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    
    @Autowired
    private JwtProperties jwtProperties;
    @Autowired
    private FacebookProperties facebookProperties;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private Algorithm algorithm;
    @Autowired
    private ObjectMapper objectMapper;
    
    private long tokenExpirationMillis;
    private RestTemplate restTemplate;

    @PostConstruct
    private void setUp() {
        tokenExpirationMillis = jwtProperties.getTokenExpirationMinutes() * 60 * 1000;
        restTemplate = new RestTemplate();
    }
    
    public TokenDTO getToken(TokenRequestDTO tokenRequest) {
        String requestURL = facebookProperties.getGraphApiUri() + "/me?fields=" + 
            facebookProperties.getUserFields() + "&access_token=" + tokenRequest.getFbAccessToken();
        
        try {
            logger.debug("Making graph API request for user info");
            ResponseEntity<FacebookUserInfoDTO> response = restTemplate.getForEntity(requestURL, FacebookUserInfoDTO.class);
            logger.debug("Received 200 from graph API");
            return createToken(response.getBody());
        } catch (HttpStatusCodeException hsce) {
            // Received HTTP status != 200 from Graph API
            logger.warn("Received bad HTTP status code from Graph API. Exception message: {}", hsce.getMessage());
            try {
                // attempt to deserialize error body
                FacebookErrorDTO error = objectMapper.readValue(hsce.getResponseBodyAsString(), FacebookErrorDTO.class);
                throw new APIException(hsce.getStatusCode(), error.getError().getMessage(), hsce);
            } catch (IOException ioe) {
                logger.error("Failed to deserialize Graph API error response", ioe);
                throw new APIException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to reach Facebook Graph API");
            }
        } catch (ResourceAccessException rae) {
            // IO error
            logger.error("Failed to reach Facebook Graph API", rae);
            throw new APIException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to reach Facebook Graph API");
        }
    }
    
    private TokenDTO createToken(FacebookUserInfoDTO userInfo) {
        // check if user exists and create a new user if needed
        User user = userService.findByUserName(String.valueOf(userInfo.getId()));
        if (user == null) {
            userService.save(new User(String.valueOf(userInfo.getId()), userInfo.getEmail()));
        }
        
        Date now = new Date();

        String jwt = JWT.create()
            .withIssuer(jwtProperties.getIss())
            .withIssuedAt(now)
            .withExpiresAt(new Date(now.getTime() + tokenExpirationMillis))
            .withClaim("uid", userInfo.getId())
            .withClaim("name", userInfo.getName())
            .withClaim("email", userInfo.getEmail())
            .sign(algorithm);

        return new TokenDTO(jwt);
    }
}
