package calc.DTO;

import calc.entity.Stats;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by clementperez on 10/2/16.
 */
public class UserDTO {

    private Long userId;
    private String firstName;
    private String lastName;
    private String userName;
    private List<StatsDTO> stats;

    public UserDTO() {
        super();
    }

    public UserDTO(String userName) {
        this.userName = userName;
        this.stats = new ArrayList<StatsDTO>();
    }

    public List<StatsDTO> getStats() {
        return stats;
    }

    public void setStats(List<StatsDTO> stats) {
        this.stats = stats;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
