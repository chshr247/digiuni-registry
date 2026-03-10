import java.util.ArrayList;
import java.util.Scanner;

public class CRUDForFaculty {
    static Scanner scanner = new Scanner(System.in);
    static ArrayList<Faculty> faculties = new ArrayList<>();
    public static int counterOfFaculty = 0;

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

    public static Faculty findFacultyById(String id) {
        for (Faculty faculty : faculties) {
            if (faculty.getId().equals(id)) {
                return faculty;
            }
        }
        return null;
    }

    public static void create() {
        counterOfFaculty++;
        String id = String.valueOf(counterOfFaculty);
        String fullName = readNonEmptyString("Enter name for faculty: ");
        String shortName = readNonEmptyString("Enter short name for faculty: ");
        String dean = readNonEmptyString("Enter dean for faculty: ");
        String contact = readNonEmptyString("Enter contact for faculty: ");
        Faculty newFaculty = new Faculty(id, fullName, shortName, dean, contact);
        faculties.add(newFaculty);
        System.out.println("Faculty registered successfully!");
    }

    public static void showFaculties() {
        if (faculties.isEmpty()) {
            System.out.println("No faculties found.");
        } else {
            faculties.forEach(System.out::println);
        }
    }

    public static void deleteFaculty() {
        String id = readNonEmptyString("Enter faculty ID to remove: ");
        boolean isRemoved = faculties.removeIf(faculty -> faculty.getId().equals(id));

        if (isRemoved) {
            System.out.println("Success: Faculty with ID " + id + " has been removed.");
        } else {
            System.out.println("Error: No faculty found with ID " + id);
        }
    }

    public static void update() {
        String id = readNonEmptyString("Enter faculty ID for updating: ");
        Faculty targetFaculty = findFacultyById(id);

        if (targetFaculty == null) {
            System.out.println("No faculty found for this ID: " + id);
            return;
        }

        System.out.println("Faculty found: " + targetFaculty.getFullName());
        System.out.println("""
                Enter number of what you want to update:
                1 - ID
                2 - Name
                3 - Short Name
                4 - Dean
                5 - Contact
                0 - Exit
                """);

        int choice = intInRange("Your choice: ", 0, 5);

        switch (choice) {
            case 1 -> targetFaculty.setId(readNonEmptyString("Enter new ID: "));
            case 2 -> targetFaculty.setFullName(readNonEmptyString("Enter new full name: "));
            case 3 -> targetFaculty.setShortName(readNonEmptyString("Enter new short name: "));
            case 4 -> targetFaculty.setDean(readNonEmptyString("Enter new dean: "));
            case 5 -> targetFaculty.setContact(readNonEmptyString("Enter new contact: "));
            case 0 -> {
                System.out.println("Exiting update menu.");
                return;
            }
            default -> System.out.println("Invalid option.");
        }
        System.out.println("Faculty information updated successfully!");
    }
}