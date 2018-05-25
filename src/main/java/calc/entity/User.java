// tag::sample[]
package calc.entity;

import javax.persistence.*;
import javax.validation.constraints.Null;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Entity
@NamedQuery(name = "User.findByUserName", query = "SELECT u FROM User u WHERE u.userName = ?1")
public class User {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long userId;
    private String firstName;
    private String lastName;
    @Column(unique = true, nullable = false)
    private String userName;
    @Column(unique = true, nullable = false)
    private String externalId;
    private String externalIdProvider;
//    @Column(nullable = false)
    private String password;
//    @Column(unique = true, nullable = false)
    private String email;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Stats> stats;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Outcome> outcomes;

    public User() {}

    public User(String userName) {
        this.userName = userName;
        this.stats = new ArrayList<Stats>();
    }

    public User(String userName, String email) {
        this.userName = userName;
        this.email = email;
        this.stats = new ArrayList<Stats>();
    }

    public User(String firstName, String lastName, String userName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.email = email;
        this.stats = new ArrayList<Stats>();
    }

    @Override
    public String toString() {
        return String.format(
                "User[id=%d, firstName='%s', lastName='%s']",
                userId, firstName, lastName);
    }

    public boolean equals(User user){
        boolean b = true;

        for(Field f : this.getClass().getDeclaredFields()){
            try {

                if(f.get(this) == null){
                    b = b && f.get(this) == f.get(user);
                }else{
                    b = b && f.get(this).equals(f.get(user));
                }

                if(!b) {
                    System.out.print("Field: " + f.getName() + " value of obj1 " + f.get(this) + (b ? " is  " : " isn't ") + " obj2 : " + f.get(user) + "\n");
                }
            } catch (IllegalAccessException e) {

                System.out.print(this.getUserName() + " isn't " + user.getUserName());
                e.printStackTrace();
                b = false;
            }
        }

        if(b) {
            System.out.print(this.getUserName() + " is " + user.getUserName());
        }
        return b;
    }

    public Long getUserId() {
        return userId;
    }

    public String getFirst() { return firstName; }

    public String getLast() { return lastName; }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setFirst(String firstName) {
        this.firstName = firstName;
    }

    public void setLast(String lastName) {
        this.lastName = lastName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getExternalId() { return externalId; }

    public void setExternalId(String externalId) {  this.externalId = externalId; }

    public String getExternalIdProvider() { return externalIdProvider;}

    public void setExternalIdProvider(String externalIdProvider) { this.externalIdProvider = externalIdProvider; }

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email;}

    public List<Stats> getStats() {
        return stats;
    }

    public Stats getStats(Tournament tournament) {
        for(Stats s : stats){
            if(s.getTournament().equals(tournament)) {
                return s;
            }
        }
        return null;
    }

    public void setStats(List<Stats> stats) {
        this.stats = stats;
    }

    public List<Outcome> getOutcomes() {
        return outcomes;
    }

    public void setOutcomes(List<Outcome> outcomes) {
        this.outcomes = outcomes;
    }
}

