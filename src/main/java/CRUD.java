import java.util.ArrayList;
import java.util.Scanner;

public class CRUD {
    static Scanner scanner = new Scanner(System.in);
    static ArrayList<Person> students = new ArrayList<>();
    // CRUD: Create Read Update Delete
    // Create
    public static void create(){

        System.out.println("Enter ID: ");
        String id = scanner.nextLine();;

        System.out.print("Enter Full Name: ");
        String fullName = scanner.nextLine();

        System.out.println("Enter Birth Date: ");
        String birthDate = scanner.nextLine();

        System.out.print("Enter Email: ");
        String email = scanner.nextLine();

        System.out.print("Enter Phone Number: ");
        String phone = scanner.nextLine();

        System.out.print("Enter Grade (1-6): ");
        int grade = Integer.parseInt(scanner.nextLine());

        System.out.print("Enter Group (1-3): ");
        int group = Integer.parseInt(scanner.nextLine());

        System.out.print("Enter Year of Entering university: ");
        int year = Integer.parseInt(scanner.nextLine());

        System.out.print("Enter choice of study (1 - Budget; 2 - Contract)");
        String form = "Unknown";
        int choice = Integer.parseInt(scanner.nextLine());
        if (choice == 1) form = "Budget";
        else if (choice == 2) form = "Contract";

        System.out.println("Enter status of studying (1 - Studying; 2 - Academic leave; 3 - Deducted");
        String status = "Unknown";
        int choice2 = Integer.parseInt(scanner.nextLine());
        if (choice2 == 1) status = "Studying";
        else if (choice2 == 2) status = "Academic leave";
        else if (choice2 == 3) status = "Deducted";

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

    // Update
    public static void update() {
        System.out.println("Enter student's ID for updating: ");
        String id = scanner.nextLine();

        Student targetStudent = null;

        for (Person p : students) {
            if (p.getId().equals(id)) {
                if (p instanceof Student) {
                    targetStudent = (Student) p;
                    break;
                }
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
        0 - Exit""");

        try {
            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1 -> {
                    System.out.print("Enter new ID: ");
                    targetStudent.setId(scanner.nextLine());
                }
                case 2 -> {
                    System.out.print("Enter new Full Name: ");
                    targetStudent.setFullName(scanner.nextLine());
                }
                case 3 -> {
                    System.out.print("Enter new Birthdate: ");
                    targetStudent.setBirthDate(scanner.nextLine());
                }
                case 4 -> {
                    System.out.print("Enter new Email: ");
                    targetStudent.setEmail(scanner.nextLine());
                }
                case 5 -> {
                    System.out.print("Enter new Phone Number: ");
                    targetStudent.setPhone(scanner.nextLine());
                }
                case 6 -> {
                    System.out.print("Enter new Grade: ");
                    targetStudent.setGrade(Integer.parseInt(scanner.nextLine()));
                }
                case 7 -> {
                    System.out.print("Enter new Group: ");
                    targetStudent.setGroup(Integer.parseInt(scanner.nextLine()));
                }
                case 8 -> {
                    System.out.print("Enter new Year: ");
                    targetStudent.setYear(Integer.parseInt(scanner.nextLine()));
                }
                case 9 -> {
                    System.out.print("Enter new Form of Study: ");
                    targetStudent.setFormOfStudy(scanner.nextLine());
                }
                case 10 -> {
                    System.out.print("Enter new Status: ");
                    targetStudent.setStatus(scanner.nextLine());
                }
                case 0 -> System.out.println("Exiting update menu.");
                default -> System.out.println("Invalid option.");
            }

            if (choice != 0) {
                System.out.println("Student information updated successfully!");
            }

        } catch (NumberFormatException e) {
            System.out.println("Error: Please enter a valid number for Grade/Group/Year.");
        }
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

