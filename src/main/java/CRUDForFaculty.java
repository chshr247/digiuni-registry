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


    public static void create(){
        counterOfFaculty++;
        String id = String.valueOf(counterOfFaculty);
        String fullName = readNonEmptyString("Enter name for faculty: ");
        String shortName = readNonEmptyString("Enter id for faculty: ");
        String dean = readNonEmptyString("Enter dean for faculty: ");
        String contact = readNonEmptyString("Enter contact for faculty: ");
        Faculty newFaculty = new Faculty(id , fullName, shortName, dean, contact);
        faculties.add(newFaculty);
        System.out.println("Faculty registered successfully!");
     }
    public static void showFaculties() {
        if (faculties.isEmpty()) {
            System.out.println("No students found.");
        } else {
            faculties.forEach(System.out::println);
        }
    }
    public static void deleteFaculty() {
        System.out.println("Enter faculty ID to remove: ");
        String id = scanner.nextLine();
        boolean isRemoved = faculties.removeIf(faculties -> faculties.getId().equals(id));

        if (isRemoved) {
            System.out.println("Success: Faculty with ID " + id + " has been removed.");
        } else {
            System.out.println("Error: No Faculty found with ID " + id);
        }
    }

    public static void update() {

        String id = readNonEmptyString("Enter faculty's ID for updating: ");

        Faculty targetFaculty = null;

        for (Faculty f : faculties) {
            if (f.getId().equals(id) && f instanceof Faculty) {
                targetFaculty = (Faculty) f;
                break;
            }
        }

        if (targetFaculty == null) {
            System.out.println("No faculty found for this ID: " + id);
            return;
        }

        System.out.println("Student found: " + targetFaculty.getFullName());

        System.out.println("""
                Enter number of what you want to update:
                1 - ID
                2 - Name
                3 - Short Name
                4 - Dean
                5 - Contact
                """);
        int choice = intInRange("Your choice: ", 0, 5);

        switch (choice) {
            case 1 -> targetFaculty.setId(readNonEmptyString("Enter new ID: "));
            case 2 -> targetFaculty.setFullName(readNonEmptyString("Enter new Full Name: "));
            case 3 -> targetFaculty.setShortName(readNonEmptyString("Enter new Short name: "));
            case 4 -> targetFaculty.setDean(readNonEmptyString("Enter new Dean: "));
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



