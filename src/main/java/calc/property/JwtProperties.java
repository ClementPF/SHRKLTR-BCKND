package calc.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 *
 * @author danny
 */
@Component
@ConfigurationProperties(prefix = "calc.jwt")
public class JwtProperties {
    private String secret;
    private String iss;
    private int tokenExpirationMinutes;
    private int tokenRefreshExpirationMinutes;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getIss() {
        return iss;
    }

    public void setIss(String iss) {
        this.iss = iss;
    }

    public int getTokenExpirationMinutes() {
        return tokenExpirationMinutes;
    }

    public void setTokenExpirationMinutes(int tokenExpirationMinutes) {
        this.tokenExpirationMinutes = tokenExpirationMinutes;
    }

    public int getTokenRefreshExpirationMinutes() {
        return tokenRefreshExpirationMinutes;
    }

    public void setTokenRefreshExpirationMinutes(int tokenRefreshExpirationMinutes) {
        this.tokenRefreshExpirationMinutes = tokenRefreshExpirationMinutes;
    }
}
