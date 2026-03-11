import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;

public class CRUDForTeacher {
    static Scanner scanner = new Scanner(System.in);
    static ArrayList<Teacher> teachers = new ArrayList<>();
    public static int counterOfTeachers = 0;

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
    public static void create() {
        counterOfTeachers++;
        String id = String.valueOf(counterOfTeachers);
        String fullName = readNonEmptyString("Enter Full Name: ");
        LocalDate birthDate = LocalDate.parse(readNonEmptyString("Enter Birth Date (yyyy-mm-dd): "));
        String email = readNonEmptyString("Enter Email: ");
        String phone = readNonEmptyString("Enter Phone Number: ");
        String post = readNonEmptyString("Enter Post: ");
        String degree = readNonEmptyString("Enter Degree: ");
        String academicRank = readNonEmptyString("Enter Academic Rank: ");
        LocalDate startedJobDate = LocalDate.parse(readNonEmptyString("Enter Started Job Date (yyyy-mm-dd): "));
        int rate = intInRange("Enter Rate: ", 1, 10);

        Teacher newTeacher = new Teacher(
                id,
                fullName,
                birthDate,
                email,
                phone,
                post,
                degree,
                academicRank,
                startedJobDate,
                rate
        );

        teachers.add(newTeacher);
        System.out.println("Teacher registered successfully!");
    }

    public static void showTeachers() {
        if (teachers.isEmpty()) {
            System.out.println("No teachers found.");
        } else {
            teachers.forEach(System.out::println);
        }
    }

    public static void update() {
        String id = readNonEmptyString("Enter teacher ID for updating: ");
        Teacher targetTeacher = null;

        for (Teacher teacher : teachers) {
            if (teacher.getId().equals(id)) {
                targetTeacher = teacher;
                break;
            }
        }

        if (targetTeacher == null) {
            System.out.println("No teacher found for this ID: " + id);
            return;
        }

        System.out.println("""
                Enter number of what you want to update:
                1 - ID
                2 - Full Name
                3 - Birth Date
                4 - Email
                5 - Phone Number
                6 - Post
                7 - Degree
                8 - Academic Rank
                9 - Started Job Date
                10 - Rate
                0 - Exit
                """);

        int choice = intInRange("Your choice: ", 0, 10);

        switch (choice) {
            case 1 -> targetTeacher.setId(readNonEmptyString("Enter new ID: "));
            case 2 -> targetTeacher.setFullName(readNonEmptyString("Enter new Full Name: "));
            case 3 -> targetTeacher.setBirthDate(LocalDate.parse(readNonEmptyString("Enter new Birth Date (yyyy-mm-dd): ")));
            case 4 -> targetTeacher.setEmail(readNonEmptyString("Enter new Email: "));
            case 5 -> targetTeacher.setPhone(readNonEmptyString("Enter new Phone Number: "));
            case 6 -> targetTeacher.setPost(readNonEmptyString("Enter new Post: "));
            case 7 -> targetTeacher.setDegree(readNonEmptyString("Enter new Degree: "));
            case 8 -> targetTeacher.setAcademicRank(readNonEmptyString("Enter new Academic Rank: "));
            case 9 -> targetTeacher.setStartedJobDate(LocalDate.parse(readNonEmptyString("Enter new Started Job Date (yyyy-mm-dd): ")));
            case 10 -> targetTeacher.setRate(intInRange("Enter new Rate: ", 1, 10));
            case 0 -> {
                System.out.println("Exiting update menu.");
                return;
            }
            default -> System.out.println("Invalid option.");
        }

        System.out.println("Teacher information updated successfully!");
    }

    public static void delete() {
        String id = readNonEmptyString("Enter teacher ID to remove: ");
        boolean isRemoved = teachers.removeIf(teacher -> teacher.getId().equals(id));

        if (isRemoved) {
            System.out.println("Success: Teacher with ID " + id + " has been removed.");
        } else {
            System.out.println("Error: No teacher found with ID " + id);
        }
    }
}