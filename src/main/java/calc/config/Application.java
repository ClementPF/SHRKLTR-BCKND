package calc.config;

import calc.entity.*;
import calc.property.JwtProperties;
import calc.repository.UserRepository;
import calc.security.JwtTokenInterceptor;
import calc.service.GameService;
import calc.service.UserService;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import java.io.UnsupportedEncodingException;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.CrudRepository;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.annotation.PostConstruct;
import java.util.*;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@SpringBootApplication
@ComponentScan(basePackages = {
    "calc.controller", 
    "calc.repository", 
    "calc.entity", 
    "calc.rest", 
    "calc.service", 
    "calc.property",
    "calc.security",
    "calc.exception"
})
@EnableJpaRepositories(basePackages = {"calc.repository"})
@EnableSwagger2
@EntityScan(basePackages = {"calc.entity"})
public class Application extends WebMvcConfigurerAdapter {

    public static void main(String[] args) { SpringApplication.run(Application.class, args);}

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    @Autowired
    private CrudRepository<Sport,Long> repoSport;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CrudRepository<Tournament,Long> repoTournament;
    @Autowired
    private CrudRepository<Game,Long> repoMatch;
    @Autowired
    private CrudRepository<Outcome,Long> repoOutcome;
    @Autowired
    private UserService userService;
    @Autowired
    private GameService matchService;
    @Autowired
    private JwtProperties jwtProperties;
    @Autowired
    private JwtTokenInterceptor jwtTokenInterceptor;

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
    
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
    }

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build();
    }
    
    @Bean
    public Algorithm algorithm() throws UnsupportedEncodingException {
        return Algorithm.HMAC256(jwtProperties.getSecret());
    }
    
    @Bean
    public JWTVerifier jwtVerifier() throws UnsupportedEncodingException {
        return JWT.require(algorithm())
            .withIssuer(jwtProperties.getIss())
            .build();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtTokenInterceptor);
    }

//    @PostConstruct
    public void initDB(){
        long count = repoSport.count();
        
        List<Sport> sports = Arrays.asList(
                 new Sport("Pool"),
                 new Sport("Ping Pong"),
                 new Sport("Fussball"),
                 new Sport("Darts"));

        for(Sport sport : sports){
            repoSport.save(sport);
        }
        
        List<User> users = Arrays.asList(
                new User("AAAAA"),
                new User("BBBBB"),
                new User("CCCCC"),
                new User("DDDDD"),
                new User("EEEEE"),
                new User("FFFFF"),
                new User("GGGGG"),
                new User("HHHHH"));

        for(User user : users){
            userService.save(user);
        }

        List<Tournament> tournaments = Arrays.asList(
                new Tournament("Tournament1", sports.get(0), users.get(0)),
                new Tournament("Tournament2", sports.get(0), users.get(1)),
                new Tournament("Tournament3", sports.get(1), users.get(2)),
                new Tournament("Tournament4", sports.get(1), users.get(3)),
                new Tournament("Tournament5", sports.get(2), users.get(4)),
                new Tournament("Tournament6", sports.get(3), users.get(1)),
                new Tournament("Tournament7", sports.get(3), users.get(2)),
                new Tournament("Tournament8", sports.get(2), users.get(3)));

        for(Tournament tournament : tournaments){
            repoTournament.save(tournament);

            for(User user : users){
                List<User> opponents = new ArrayList<User>(users);;
                opponents.remove(user);
                for(int i = 0;i<2;i++){
                    Random rdm = new Random();
                    User opponent = opponents.get(rdm.nextInt(opponents.size() - 1));

                    int result = rdm.nextInt(1);
                    matchService.addGame(tournament, result == 0 ? user : opponent, result != 0 ? user : opponent, false);
                }
            }
        }
    }
}
