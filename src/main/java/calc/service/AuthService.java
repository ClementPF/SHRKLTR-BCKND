package calc.service;

import calc.DTO.FacebookErrorDTO;
import calc.DTO.FacebookUserInfoDTO;
import calc.DTO.TokenDTO;
import calc.DTO.TokenRequestDTO;
import calc.entity.User;
import calc.exception.APIException;
import calc.property.FacebookProperties;
import calc.property.JwtProperties;
import calc.repository.UserRepository;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.text.Normalizer;
import java.util.Date;

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
    private JWTVerifier jwtVerifier;
    @Autowired
    private FacebookProperties facebookProperties;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private Algorithm algorithm;
    @Autowired
    private ObjectMapper objectMapper;
    
    private long tokenRefreshExpirationMillis; //1y
    private long tokenExpirationMillis;
    private RestTemplate restTemplate;

    @PostConstruct
    private void setUp() {
        tokenExpirationMillis = jwtProperties.getTokenExpirationMinutes() * 60 * 1000;
        tokenRefreshExpirationMillis = jwtProperties.getTokenRefreshExpirationMinutes() * 60 * 1000; // 1y
        restTemplate = new RestTemplate();
    }
    
    public TokenDTO getToken(TokenRequestDTO tokenRequest) {
        String requestURL = facebookProperties.getGraphApiUri() + "/me?fields=" + 
            facebookProperties.getUserFields() + "&access_token=" + tokenRequest.getFbAccessToken();

        logger.debug("get TOKEN :" + tokenRequest.getFbAccessToken() );

        try {
            logger.debug("Making graph API request for user info");
            ResponseEntity<FacebookUserInfoDTO> response = restTemplate.getForEntity(requestURL, FacebookUserInfoDTO.class);
            logger.debug("Received 200 from graph API");

            FacebookUserInfoDTO userInfo = response.getBody();
            User user = userRepository.findByExternalId(userInfo.getId());
            if (user == null) {
                createUserFromExternalProvider(userInfo);
            }

            return new TokenDTO(createToken(response.getBody()),createTokenRefresh(response.getBody()));
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

    public TokenDTO refreshToken(TokenDTO tokenRefresh){

        try {
            DecodedJWT jwt = jwtVerifier.verify(tokenRefresh.getRefreshToken());

            Claim uid = jwt.getClaim("uid");
            Claim name = jwt.getClaim("name");
            Claim email = jwt.getClaim("email");
            Claim roles = jwt.getClaim("scopes");

            logger.debug("JWToken refresh verified for uid: {}", uid.asLong());

            if(roles.asString().equals("REFRESH_TOKEN")){
                FacebookUserInfoDTO userInfo = new FacebookUserInfoDTO(uid.asLong(), name.asString(), email.asString());
                return createTokenPair(userInfo);
            }else{
                throw new APIException(HttpStatus.UNAUTHORIZED, "trying to refresh a token which is not a refresh token");
            }
        } catch (JWTVerificationException ve) {
            logger.warn("JWToken refresh verification failed: {}", ve.getMessage());
        }
        return null;
    }

    private TokenDTO createTokenPair(FacebookUserInfoDTO userInfo) {
        // check if user exists and create a new user if needed

        String accessToken = createToken(userInfo);
        String refreshToken = createTokenRefresh(userInfo);

        return new TokenDTO(accessToken,refreshToken);
    }

    private String createToken(FacebookUserInfoDTO userInfo) {
        // check if user exists and create a new user if needed
        
        Date now = new Date();

        System.out.println(jwtProperties.getTokenExpirationMinutes());
        System.out.println(tokenExpirationMillis);
        System.out.println(new Date(now.getTime() + tokenExpirationMillis));

        String jwt = JWT.create()
            .withIssuer(jwtProperties.getIss())
            .withIssuedAt(now)
            .withExpiresAt(new Date(now.getTime() + tokenExpirationMillis))
            .withClaim("uid", userInfo.getId())
            .withClaim("name", userInfo.getName())
            .withClaim("email", userInfo.getEmail())
            .sign(algorithm);

        return jwt;
    }

    private String createTokenRefresh(FacebookUserInfoDTO userInfo) {

        Date now = new Date();

        System.out.println(jwtProperties.getTokenRefreshExpirationMinutes());
        System.out.println(tokenRefreshExpirationMillis);
        System.out.println(new Date(now.getTime() + tokenRefreshExpirationMillis));

        String jwt = JWT.create()
                .withIssuer(jwtProperties.getIss())
                .withIssuedAt(now)
                .withExpiresAt(new Date(now.getTime() + tokenRefreshExpirationMillis))
                .withClaim("uid", userInfo.getId())
                .withClaim("scopes","REFRESH_TOKEN")
                .withClaim("name", userInfo.getName())
                .withClaim("email", userInfo.getEmail())
                .sign(algorithm);

        logger.debug("Refresh Token Created: " + jwt);

        return jwt;
    }

    private User createUserFromExternalProvider(FacebookUserInfoDTO userInfo){

        String username = userInfo.getName().toLowerCase().replace(' ', '-');
        username = Normalizer.normalize(username, Normalizer.Form.NFD);
        username = username.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");

        User check = userRepository.findByUserName(username);
        int suffix = 1;
        int max = 999;
        while(check != null && suffix < max + 1){
            check = userRepository.findByUserName(username + "-" + suffix);
            if(check == null){
                username = username + '-' + suffix;
            }
        }

        if(suffix == max){
            System.out.print("username reached 999 for " + username);
        }

        User user = new User(username);
        int indexOfLastSpace = userInfo.getName().lastIndexOf(" ");
        if (indexOfLastSpace > 0) {
            user.setEmail(userInfo.getEmail());
            user.setLast(userInfo.getName().substring(indexOfLastSpace + 1));
            user.setFirst(userInfo.getName().substring(0, indexOfLastSpace));
            user.setExternalIdProvider("facebook");
            user.setExternalId(userInfo.getId());
        } else {
            user.setFirst(userInfo.getName());
            user.setEmail(userInfo.getEmail());
        }

        return userRepository.save(user);
    }

}
