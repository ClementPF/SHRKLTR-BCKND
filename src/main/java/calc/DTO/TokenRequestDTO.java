package calc.DTO;

/**
 *
 * @author danny
 */
public class TokenRequestDTO {
    private String providerAccessToken;
    private String tokenProvider;

    public String getProviderAccessToken() {
        return providerAccessToken;
    }

    public void setProviderAccessToken(String providerAccessToken) {
        this.providerAccessToken = providerAccessToken;
    }

    public String getTokenProvider() {
        return tokenProvider;
    }

    public void setTokenProvider(String tokenProvider) {
        this.tokenProvider = tokenProvider;
    }
}
