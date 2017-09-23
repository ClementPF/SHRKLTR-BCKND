package calc.DTO;

/**
 *
 * @author danny
 */
public class TokenDTO {
    private String accessToken;

    public TokenDTO(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
