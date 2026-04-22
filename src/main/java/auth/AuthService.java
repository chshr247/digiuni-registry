package auth;
import model.Role;
import exception.AccessDeniedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class AuthService {
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    protected Map<String, AuthUser> users = new HashMap<>();
    private AuthUser currentUser = null;
    private LocalDateTime lastLoginAt = null;

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    public AuthService() {
        users.put("admin", new AuthUser("admin", "admin", Role.ADMIN));
        users.put("user", new AuthUser("user", "user", Role.USER));
        users.put("manager", new AuthUser("manager", "manager", Role.MANAGER));
    }

    public Optional<AuthUser> findUserOptional(String username) {
        return Optional.ofNullable(users.get(username));
    }

    public boolean login(String username, String password) {
        Optional<AuthUser> foundOptional = findUserOptional(username);

        if (foundOptional.isEmpty()) {
            System.out.println("Invalid username or password");
            log.warn("LOGIN FAILED unknown username={}", username);
            return false;
        }

        AuthUser found = foundOptional.get();

        if (found.isBlocked()) {
            System.out.println("Account is blocked. Contact administrator.");
            log.warn("LOGIN BLOCKED username={}", username);
            return false;
        }

        if (found.getPassword().equals(password)) {
            currentUser = found;
            lastLoginAt = LocalDateTime.now();

            System.out.println("Logged in as " + username + " at " + lastLoginAt.format(FMT));
            log.info("LOGIN user={} role={}", username, found.getRole());
            return true;
        }

        System.out.println("Invalid username or password");
        log.warn("LOGIN FAILED wrong password username={}", username);
        return false;
    }

    public void logout() {
        String username = currentUser != null ? currentUser.getUsername() : "?";
        String duration = getSessionDuration();

        System.out.println("Logged out");
        log.info("LOGOUT user={} duration={}", username, duration);

        currentUser = null;
        lastLoginAt = null;
    }

    public String getSessionDuration() {
        if (lastLoginAt == null) {
            return "N/A";
        }

        Duration duration = Duration.between(lastLoginAt, LocalDateTime.now());
        long minutes = duration.toMinutes();
        long seconds = duration.getSeconds() % 60;

        return minutes + " min " + seconds + " sec";
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

    public LocalDateTime getLastLoginAt() {
        return lastLoginAt;
    }

    public void requireAuth() {
        if (!isLoggedIn()) {
            System.out.println("Access denied. Please log in first.");
            log.warn("ACCESS DENIED not authenticated");
            throw new AccessDeniedException("Not authenticated");
        }
    }

    public void requireAdmin() {
        requireAuth();
        if (!isAdmin()) {
            System.out.println("Access denied. This action requires ADMIN role.");
            log.warn("ACCESS DENIED requires ADMIN, current={}", currentUser.getRole());
            throw new AccessDeniedException("Not authorized");
        }
    }

    public void requireManager() {
        requireAuth();
        if (!isManager() && !isAdmin()) {
            System.out.println("Access denied. This action requires MANAGER or ADMIN role.");
            log.warn("ACCESS DENIED requires MANAGER+, current={}", currentUser.getRole());
            throw new AccessDeniedException("Not authorized");
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