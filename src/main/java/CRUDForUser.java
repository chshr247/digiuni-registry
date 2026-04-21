import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Scanner;

public class CRUDForUser {
    private static final Logger log = LoggerFactory.getLogger(CRUDForUser.class);

    static Scanner scanner = new Scanner(System.in);

    private static String readNonEmptyString(String message) {
        String input;
        do {
            System.out.print(message);
            input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("Error: field cannot be empty.");
            }
        } while (input.isEmpty());
        return input;
    }

    static int intInRange(String message, int min, int max) {
        int value;
        while (true) {
            try {
                System.out.print(message);
                value = Integer.parseInt(scanner.nextLine());
                if (value < min || value > max) {
                    System.out.println("Error: value must be between " + min + " and " + max);
                } else {
                    return value;
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: enter a number.");
            }
        }
    }

    public static void showUsers(AuthService auth) {
        System.out.println("All users:");
        auth.getUsers().forEach((username, user) ->
                System.out.println("  " + username
                        + " | role: " + user.getRole()
                        + (user.isBlocked() ? " | [BLOCKED]" : " | [active]")));
    }

    public static void addUser(AuthService auth) {
        String username = readNonEmptyString("Enter username: ");
        if (auth.findUserOptional(username).isPresent()) {
            System.out.println("User already exists!");
            return;
        }
        String password = readNonEmptyString("Enter password: ");
        System.out.println("Choose role:");
        System.out.println("1. USER");
        System.out.println("2. MANAGER");
        System.out.println("3. ADMIN");
        int roleChoice = intInRange("Your choice: ", 1, 3);
        Role role = switch (roleChoice) {
            case 1 -> Role.USER;
            case 2 -> Role.MANAGER;
            case 3 -> Role.ADMIN;
            default -> Role.USER;
        };

        AuthUser newUser = new AuthUser(username, password, role);
        auth.addUser(newUser);
        System.out.println("User added successfully!");
        log.info("USER CREATED username={} role={}", username, role);
        RegistryStorageService.saveUsersSilently();
    }

    public static void updateUser(AuthService auth) {
        String username = readNonEmptyString("Enter username to update: ");
        AuthUser user = auth.findUserOptional(username).orElse(null);
        if (user == null) {
            System.out.println("User not found!");
            return;
        }
        System.out.println("Choose what to update:");
        System.out.println("1. Password");
        System.out.println("2. Role");
        System.out.println("3. " + (user.isBlocked() ? "Unblock" : "Block") + " user");
        int choice = intInRange("Your choice: ", 1, 3);
        if (choice == 1) {
            String newPass = readNonEmptyString("Enter new password: ");
            user.setPassword(newPass);
            System.out.println("Password updated!");
            log.info("USER PASSWORD CHANGED username={}", username);
            RegistryStorageService.saveUsersSilently();
        } else if (choice == 2) {
            System.out.println("Choose new role:");
            System.out.println("1. USER");
            System.out.println("2. MANAGER");
            System.out.println("3. ADMIN");
            int roleChoice = intInRange("Your choice: ", 1, 3);
            Role newRole = switch (roleChoice) {
                case 1 -> Role.USER;
                case 2 -> Role.MANAGER;
                case 3 -> Role.ADMIN;
                default -> Role.USER;
            };
            user.setRole(newRole);
            System.out.println("Role updated!");
            log.info("USER ROLE CHANGED username={} newRole={}", username, newRole);
            RegistryStorageService.saveUsersSilently();
        } else {
            if (auth.getCurrentUser() != null && auth.getCurrentUser().getUsername().equals(username)) {
                System.out.println("Error: cannot block the currently logged-in user.");
                return;
            }
            boolean wouldBlock = !user.isBlocked();
            if (wouldBlock && user.getRole() == Role.ADMIN) {
                long activeAdmins = auth.getUsers().values().stream()
                        .filter(u -> u.getRole() == Role.ADMIN && !u.isBlocked())
                        .count();
                if (activeAdmins <= 1) {
                    System.out.println("Error: cannot block the last active admin.");
                    return;
                }
            }
            user.setBlocked(!user.isBlocked());
            String status = user.isBlocked() ? "blocked" : "unblocked";
            System.out.println("User " + username + " is now " + status + ".");
            log.info("USER {} username={}", status.toUpperCase(), username);
            RegistryStorageService.saveUsersSilently();
        }
    }

    public static void deleteUser(AuthService auth) {
        String username = readNonEmptyString("Enter username to delete: ");
        if (auth.findUserOptional(username).isEmpty()) {
            System.out.println("User not found!");
            return;
        }
        if (auth.getCurrentUser() != null && auth.getCurrentUser().getUsername().equals(username)) {
            System.out.println("Error: cannot delete the currently logged-in user.");
            return;
        }
        auth.removeUser(username);
        System.out.println("User deleted!");
        log.info("USER DELETED username={}", username);
        RegistryStorageService.saveUsersSilently();
    }
}