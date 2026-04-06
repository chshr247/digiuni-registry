public class AuthUser {
    private String username;
    private String password;
    private Role role;
    private Person person;

    public AuthUser(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
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
