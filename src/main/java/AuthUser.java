import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class AuthUser {
    private final String username;
    private String password;
    private Role role;
    private Person person;

    public AuthUser(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    @Override
    public String toString() {
        return "AuthUser{" +
                "username='" + username + '\'' +
                ", role=" + role +
                ", person='" + (person != null ? person.getFullName() : "null") + '\'' +
                '}';
    }
}
