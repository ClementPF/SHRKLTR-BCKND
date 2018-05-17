package calc.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 *
 * @author danny
 */
@Component
@ConfigurationProperties(prefix = "calc.google")
public class GoogleProperties {

    private String iosClientId;
    private String androidClientId;

    public String getIosClientId() {
        return iosClientId;
    }

    public void setIosClientId(String iosClientId) {
        this.iosClientId = iosClientId;
    }

    public String getAndroidClientId() {
        return androidClientId;
    }

    public void setAndroidClientId(String androidClientId) {
        this.androidClientId = androidClientId;
    }
}
