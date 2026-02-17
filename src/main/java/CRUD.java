import java.util.ArrayList;
import java.util.Scanner;

public class CRUD {
    static Scanner scanner = new Scanner(System.in);
    static ArrayList<Person> students = new ArrayList<>();
    public static int counterOfStudents = 0;
    // CRUD: Create Read Update Delete
    // Create
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
        counterOfStudents++;
        String id = String.valueOf(counterOfStudents);
        String fullName = readNonEmptyString("Enter Full Name: ");
        String birthDate = readNonEmptyString("Enter Birth Date: ");
        String email = readNonEmptyString("Enter Email: ");
        String phone = readNonEmptyString("Enter Phone Number: ");
        int grade = intInRange("Enter Grade (1-6): ", 1, 6);
        int group = intInRange("Enter Group (1-3): ", 1, 3);
        int year = intInRange("Enter Year of Entering university: ", 1, 2026);
        System.out.println("Enter choice of study:");
        System.out.println("1 - Budget");
        System.out.println("2 - Contract");
        String form;
        int choice = intInRange("Your choice: ", 1, 2);
        if (choice == 1) {
            form = "Budget";
        } else {
            form = "Contract";
        }
        System.out.println("Enter status of studying:");
        System.out.println("1 - Studying");
        System.out.println("2 - Academic leave");
        System.out.println("3 - Deducted");
        String status;
        int choice2 = intInRange("Your choice: ", 1, 3);
        if (choice2 == 1) {
            status = "Studying";
        } else if (choice2 == 2) {
            status = "Academic leave";
        } else {
            status = "Deducted";
        }

        Person newStudent = new Student(id, fullName, birthDate, email, phone, grade, group, year, form, status);

        students.add(newStudent);
        System.out.println("Student registered successfully!");
    }


    // Read
    public static void showStudents() {
        if (students.isEmpty()) {
            System.out.println("No students found.");
        } else {
            students.forEach(System.out::println);
        }
    }

    public static void searchByFullName() {
        String founder = readNonEmptyString("Enter full name (or part of it): ");
        boolean found =  false;
        for (Person p : students) {
            if (p instanceof Student s && s.getFullName().contains(founder)) {
                System.out.println(s);
                found = true;
            }
        }
        if(!found){
            System.out.println("No students found");
        }
    }

    public static void searchByGroup() {
        int group = intInRange("Enter group (1-3): ", 1, 3);
        boolean found = false;

        for (Person p : students) {
            if (p instanceof Student s && s.getGroup() == group) {
                System.out.println(s);
                found = true;
            }
        }
        if (!found) {
            System.out.println("No students found");
        }
    }
    public static void searchByGrade() {
        int grade = intInRange("Enter grade (1-6): ", 1, 6);
        boolean found = false;

        for (Person p : students) {
            if (p instanceof Student s && s.getGrade() == grade) {
                System.out.println(s);
                found = true;
            }
        }

        if (!found) {
            System.out.println("No students found");
        }
    }
    public static void showAllStudentsByCourse() {
        if (students.isEmpty()) {
            System.out.println("No students found.");
            return;
        }

        for (int grade = 1; grade <= 6; grade++) {
            System.out.println("*==* Grade " + grade + "*==*");
            boolean found = false;
            for (Person p : students) {
                if (p instanceof Student s && s.getGrade() == grade) {
                    System.out.println(s);
                    found = true;
                }
            }
            if (!found) {
                System.out.println("There are no students in this grade");
            }

        }
    }



    // Update
    public static void update() {

        String id = readNonEmptyString("Enter student's ID for updating: ");

        Student targetStudent = null;

        for (Person p : students) {
            if (p.getId().equals(id) && p instanceof Student) {
                targetStudent = (Student) p;
                break;
            }
        }

        if (targetStudent == null) {
            System.out.println("No student found for this ID: " + id);
            return;
        }

        System.out.println("Student found: " + targetStudent.getFullName());

        System.out.println("""
    Enter number of what you want to update:
    1 - ID
    2 - Name
    3 - Birthdate
    4 - Email
    5 - Phone Number
    6 - Grade
    7 - Group
    8 - Year
    9 - Form of Study
    10 - Status
    0 - Exit
    """);

        int choice = intInRange("Your choice: ", 0, 10);

        switch (choice) {
            case 1 -> targetStudent.setId(readNonEmptyString("Enter new ID: "));
            case 2 -> targetStudent.setFullName(readNonEmptyString("Enter new Full Name: "));
            case 3 -> targetStudent.setBirthDate(readNonEmptyString("Enter new Birthdate: "));
            case 4 -> targetStudent.setEmail(readNonEmptyString("Enter new Email: "));
            case 5 -> targetStudent.setPhone(readNonEmptyString("Enter new Phone Number: "));
            case 6 -> targetStudent.setGrade(intInRange("Enter new Grade (1-6): ", 1, 6));
            case 7 -> targetStudent.setGroup(intInRange("Enter new Group (1-3): ", 1, 3));
            case 8 -> targetStudent.setYear(intInRange("Enter new Year: ", 2000, 2100));
            case 9 -> {
                System.out.println("1 - Budget");
                System.out.println("2 - Contract");
                int f = intInRange("Your choice: ", 1, 2);
                targetStudent.setFormOfStudy(f == 1 ? "Budget" : "Contract");
            }
            case 10 -> {
                System.out.println("1 - Studying");
                System.out.println("2 - Academic leave");
                System.out.println("3 - Deducted");
                int s = intInRange("Your choice: ", 1, 3);

                if (s == 1) targetStudent.setStatus("Studying");
                else if (s == 2) targetStudent.setStatus("Academic leave");
                else targetStudent.setStatus("Deducted");
            }
            case 0 -> {
                System.out.println("Exiting update menu.");
                return;
            }
            default -> System.out.println("Invalid option.");
        }

        System.out.println("Student information updated successfully!");
    }


    public static void delete() {
        System.out.println("Enter student ID to remove: ");
        String id = scanner.nextLine();
        boolean isRemoved = students.removeIf(student -> student.getId().equals(id));

        if (isRemoved) {
            System.out.println("Success: Student with ID " + id + " has been removed.");
        } else {
            System.out.println("Error: No student found with ID " + id);
        }
    }

}