import java.util.HashMap;
import java.util.Map;

public class AuthService {
    protected Map<String, AuthUser> users = new HashMap<>();
    private AuthUser currentUser = null;

    public AuthService() {
        users.put("admin", new AuthUser("admin", "admin", Role.ADMIN));
        users.put("user", new AuthUser("user", "user", Role.USER));
        users.put("manager", new AuthUser("manager", "manager", Role.MANAGER));
    }

    public boolean login(String username, String password) {
        AuthUser found = users.get(username);
        if (found == null) {
            return false;
        }
        if (found != null && found.getPassword().equals(password)) {
            currentUser = found;
            System.out.println("Logged in as " + username);
            return true;
        }
        System.out.println("Invalid username or password");
        return false;
    }

    public void logout(){
        currentUser = null;
        System.out.println("Logged out");
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public boolean isAdmin() {
        return isLoggedIn() && currentUser.getRole() == Role.ADMIN;
    }

    public boolean isManager() {
        return isLoggedIn() && currentUser.getRole() == Role.MANAGER;
    }

    public AuthUser getCurrentUser() {
        return currentUser;
    }

    public void requireAuth() {
        if (!isLoggedIn()) {
            System.out.println("Access denied. Please log in first.");
            throw new RuntimeException("Not authenticated");
        }
    }

    public void requireAdmin() {
        requireAuth();
        if (!isAdmin()) {
            System.out.println("Access denied. This action requires ADMIN role.");
            throw new RuntimeException("Not authorized");
        }
    }

    public void requireManager() {
        requireAuth();
        if (!isManager() && !isAdmin()) {
            System.out.println("Access denied. This action requires MANAGER or ADMIN role.");
            throw new RuntimeException("Not authorized");
        }
    }

    public Map<String, AuthUser> getUsers() {
        return users;
    }

    public void addUser(AuthUser user) {
        users.put(user.getUsername(), user);
    }

    public void removeUser(String username) {
        users.remove(username);
    }

    public AuthUser getUser(String username) {
        return users.get(username);
    }
}
