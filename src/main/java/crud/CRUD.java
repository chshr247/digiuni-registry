package crud;
import model.*;
import repository.*;
import service.RegistryStorageService;
import auth.AuthService;
import exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Scanner;

public class CRUD {
    private static final Logger log = LoggerFactory.getLogger(CRUD.class);

    public static Scanner scanner = new Scanner(System.in);
    public static ArrayList<Person> students = new ArrayList<>();
    public static int counterOfStudents = 0;

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

    static String readEmail(String message) {
        while (true) {
            System.out.print(message);
            String val = scanner.nextLine().trim();
            if (val.matches("^[\\w.+-]+@[\\w-]+\\.[\\w.]{2,}$")) return val;
            System.out.println("Error: invalid email format (e.g. name@domain.com)");
        }
    }

    static String readPhone(String message) {
        while (true) {
            System.out.print(message);
            String val = scanner.nextLine().trim();
            if (val.matches("^\\+?[0-9]{7,15}$")) return val;
            System.out.println("Error: invalid phone format (e.g. +380671234567)");
        }
    }

    static String readName(String message) {
        while (true) {
            System.out.print(message);
            String val = scanner.nextLine().trim();
            if (val.isEmpty()) { System.out.println("Error: field cannot be empty."); continue; }
            if (!val.matches("[\\p{L}\\s'-]+")) {
                System.out.println("Error: name must contain only letters, spaces, apostrophes or hyphens.");
                continue;
            }
            return val;
        }
    }

    public static int intInRange(String message, int min, int max) {
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

    static LocalDate readDate(String message) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        while (true) {
            System.out.print(message + " (dd-MM-yyyy): ");
            String raw = scanner.nextLine().trim();
            try {
                return LocalDate.parse(raw, fmt);
            } catch (DateTimeParseException e) {
                System.out.println("Error: use format dd-MM-yyyy");
            }
        }
    }

    public static Optional<Student> findStudentByIdOptional(String id) {
        return RepositoryRegistry.students().findById(id);
    }

    public static Student requireStudentById(String id) {
        return findStudentByIdOptional(id)
                .orElseThrow(() -> new EntityNotFoundException("No student found with ID: " + id));
    }

    private static Department chooseDepartment() {
        String facultyId = readNonEmptyString("Enter faculty ID: ");
        Faculty faculty = CRUDForFaculty.findFacultyByIdOptional(facultyId).orElse(null);
        if (faculty == null) {
            System.out.println("No faculty found for this ID.");
            return null;
        }

        String departmentId = readNonEmptyString("Enter department ID: ");
        Department department = CRUDForDepartment
                .findDepartmentByIdOptional(faculty.getDepartments(), departmentId)
                .orElse(null);

        if (department == null) {
            System.out.println("No department found for this ID.");
            return null;
        }

        return department;
    }

    public static void create() {
        Department department = chooseDepartment();
        if (department == null) {
            return;
        }

        counterOfStudents++;
        String id = String.valueOf(counterOfStudents);
        String lastName = readName("Enter Last Name: ");
        String firstName = readName("Enter First Name: ");
        String patronymic = readName("Enter Patronymic: ");
        LocalDate birthDate = readDate("Enter Birth Date");
        String email = readEmail("Enter Email: ");
        String phone = readPhone("Enter Phone Number: ");
        int grade = intInRange("Enter Grade (1-6): ", 1, 6);
        int group = intInRange("Enter Group (1-3): ", 1, 3);
        int year = intInRange("Enter Year of Entering: ", 2000, 2100);

        System.out.println("Form of study: 1 - Budget  2 - Contract");
        String form = intInRange("Your choice: ", 1, 2) == 1 ? "Budget" : "Contract";

        System.out.println("Status: 1 - Studying  2 - Academic leave  3 - Deducted");
        String status = switch (intInRange("Your choice: ", 1, 3)) {
            case 1 -> "Studying";
            case 2 -> "Academic leave";
            default -> "Deducted";
        };

        Student s = new Student(
                id, lastName, firstName, patronymic, birthDate, email, phone,
                grade, group, year, form, status
        );

        students.add(s);
        s.setDepartment(department);
        department.addStudent(s);

        System.out.println("Student registered successfully!");
        log.info("STUDENT CREATED id={} name={}", s.getId(), s.getFullName());
        RegistryStorageService.saveStudentsSilently();
    }

    public static void showStudents() {
        if (students.isEmpty()) {
            System.out.println("No students found.");
        } else {
            students.forEach(System.out::println);
        }
    }

    public static void searchByFullName() {
        String query = readNonEmptyString("Enter name (or part of it): ").toLowerCase();
        boolean found = false;

        for (Person p : students) {
            if (p instanceof Student s && s.getFullName().toLowerCase().contains(query)) {
                System.out.println(s);
                found = true;
            }
        }

        if (!found) {
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
            System.out.println("*==* Grade " + grade + " *==*");
            boolean found = false;

            for (Person p : students) {
                if (p instanceof Student s && s.getGrade() == grade) {
                    System.out.println(s);
                    found = true;
                }
            }

            if (!found) {
                System.out.println("  No students in this grade");
            }
        }
    }

    public static void update() {
        String id = readNonEmptyString("Enter student ID for updating: ");
        Student target;

        try {
            target = requireStudentById(id);
        } catch (EntityNotFoundException e) {
            System.out.println(e.getMessage());
            return;
        }

        System.out.println("Student found: " + target.getFullName());
        System.out.println("""
            1 - Last Name     2 - First Name    3 - Patronymic
            4 - Birth Date    5 - Email         6 - Phone
            7 - Grade         8 - Group         9 - Year
            10 - Form         11 - Status       12 - Transfer department
            0 - Exit""");

        int choice = intInRange("Your choice: ", 0, 12);
        switch (choice) {
            case 1 -> target.setLastName(readName("New Last Name: "));
            case 2 -> target.setFirstName(readName("New First Name: "));
            case 3 -> target.setPatronymic(readName("New Patronymic: "));
            case 4 -> target.setBirthDate(readDate("New Birth Date"));
            case 5 -> target.setEmail(readNonEmptyString("New Email: "));
            case 6 -> target.setPhone(readNonEmptyString("New Phone: "));
            case 7 -> target.setGrade(intInRange("New Grade (1-6): ", 1, 6));
            case 8 -> target.setGroup(intInRange("New Group (1-3): ", 1, 3));
            case 9 -> target.setYear(intInRange("New Year: ", 2000, 2100));
            case 10 -> {
                System.out.println("1 - Budget  2 - Contract");
                target.setFormOfStudy(intInRange("Your choice: ", 1, 2) == 1 ? "Budget" : "Contract");
            }
            case 11 -> {
                System.out.println("1 - Studying  2 - Academic leave  3 - Deducted");
                target.setStatus(switch (intInRange("Your choice: ", 1, 3)) {
                    case 1 -> "Studying";
                    case 2 -> "Academic leave";
                    default -> "Deducted";
                });
            }
            case 12 -> {
                Department newDept = chooseDepartment();
                if (newDept != null) {
                    if (newDept.equals(target.getDepartment())) {
                        System.out.println("Student is already in this department.");
                        break;
                    }
                    if (target.getDepartment() != null) {
                        target.getDepartment().removeStudent(target);
                    }
                    newDept.addStudent(target);
                    System.out.println("Student transferred to: " + newDept.getFullName());
                    log.info("STUDENT TRANSFERRED id={} to dept={}", target.getId(), newDept.getFullName());
                }
            }
            case 0 -> {
                System.out.println("Cancelled.");
                return;
            }
        }

        System.out.println("Student updated successfully!");
        log.info("STUDENT UPDATED id={} name={}", target.getId(), target.getFullName());
        RegistryStorageService.saveStudentsSilently();
    }

    public static void delete() {
        String id = readNonEmptyString("Enter student ID to remove: ");
        Student toRemove;

        try {
            toRemove = requireStudentById(id);
        } catch (EntityNotFoundException e) {
            System.out.println(e.getMessage());
            return;
        }

        students.remove(toRemove);
        if (toRemove.getDepartment() != null) {
            toRemove.getDepartment().removeStudent(toRemove);
        }

        System.out.println("Success: Student with ID " + id + " has been removed.");
        log.info("STUDENT DELETED id={}", id);
        RegistryStorageService.saveStudentsSilently();
    }
}