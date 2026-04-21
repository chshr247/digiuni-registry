import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Scanner;

public class CRUDForTeacher {
    private static final Logger log = LoggerFactory.getLogger(CRUDForTeacher.class);

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

        counterOfTeachers++;
        String id = String.valueOf(counterOfTeachers);
        String lastName = CRUD.readName("Enter Last Name: ");
        String firstName = CRUD.readName("Enter First Name: ");
        String patronymic = CRUD.readName("Enter Patronymic: ");
        LocalDate birthDate = readDate("Enter Birth Date");
        String email = CRUD.readEmail("Enter Email: ");
        String phone = CRUD.readPhone("Enter Phone Number: ");
        String post = readNonEmptyString("Enter Post: ");
        String degree = readNonEmptyString("Enter Degree: ");
        String rank = readNonEmptyString("Enter Academic Rank: ");
        LocalDate startDate = readDate("Enter Start Job Date");
        int rate = intInRange("Enter Rate (1-10): ", 1, 10);

        Teacher t = new Teacher(
                id, lastName, firstName, patronymic, birthDate, email, phone,
                post, degree, rank, startDate, rate
        );

        teachers.add(t);
        t.setDepartment(department);
        department.addTeacher(t);

        System.out.println("Teacher registered successfully!");
        log.info("TEACHER CREATED id={} name={}", t.getId(), t.getFullName());
        RegistryStorageService.saveTeachersSilently();
    }

    public static void showTeachers() {
        if (teachers.isEmpty()) {
            System.out.println("No teachers found.");
        } else {
            teachers.forEach(System.out::println);
        }
    }

    public static Optional<Teacher> findTeacherByIdOptional(String id) {
        return RepositoryRegistry.teachers().findById(id);
    }

    public static Teacher findTeacherById(String id) {
        return findTeacherByIdOptional(id)
                .orElseThrow(() -> new EntityNotFoundException("No teacher found for ID: " + id));
    }

    public static void update() {
        String id = readNonEmptyString("Enter teacher ID for updating: ");
        Teacher target;

        try {
            target = findTeacherById(id);
        } catch (EntityNotFoundException e) {
            System.out.println(e.getMessage());
            return;
        }

        System.out.println("Teacher found: " + target.getFullName());
        System.out.println("""
            1 - Last Name      2 - First Name     3 - Patronymic
            4 - Birth Date     5 - Email          6 - Phone
            7 - Post           8 - Degree         9 - Academic Rank
            10 - Start Date    11 - Rate
            0 - Exit""");

        int choice = intInRange("Your choice: ", 0, 11);
        switch (choice) {
            case 1 -> target.setLastName(readNonEmptyString("New Last Name: "));
            case 2 -> target.setFirstName(readNonEmptyString("New First Name: "));
            case 3 -> target.setPatronymic(readNonEmptyString("New Patronymic: "));
            case 4 -> target.setBirthDate(readDate("New Birth Date"));
            case 5 -> target.setEmail(readNonEmptyString("New Email: "));
            case 6 -> target.setPhone(readNonEmptyString("New Phone: "));
            case 7 -> target.setPost(readNonEmptyString("New Post: "));
            case 8 -> target.setDegree(readNonEmptyString("New Degree: "));
            case 9 -> target.setAcademicRank(readNonEmptyString("New Academic Rank: "));
            case 10 -> target.setStartedJobDate(readDate("New Start Date"));
            case 11 -> target.setRate(intInRange("New Rate (1-10): ", 1, 10));
            case 0 -> {
                System.out.println("Cancelled.");
                return;
            }
        }

        System.out.println("Teacher updated successfully!");
        log.info("TEACHER UPDATED id={} name={}", target.getId(), target.getFullName());
        RegistryStorageService.saveTeachersSilently();
    }

    public static void delete() {
        String id = readNonEmptyString("Enter teacher ID to remove: ");
        Teacher toRemove;

        try {
            toRemove = findTeacherById(id);
        } catch (EntityNotFoundException e) {
            System.out.println(e.getMessage());
            return;
        }

        teachers.remove(toRemove);
        if (toRemove.getDepartment() != null) {
            toRemove.getDepartment().removeTeacher(toRemove);
        }

        for (Faculty f : CRUDForFaculty.faculties) {
            if (toRemove.equals(f.getDean())) {
                f.setDean(null);
                System.out.println("  [Dean unassigned from faculty: " + f.getFullName() + "]");
                log.info("DEAN UNASSIGNED faculty={}", f.getFullName());
            }

            for (Department d : f.getDepartments()) {
                if (toRemove.equals(d.getHead())) {
                    d.setHead(null);
                    System.out.println("  [Head unassigned from department: " + d.getFullName() + "]");
                    log.info("HEAD UNASSIGNED dept={}", d.getFullName());
                }
            }
        }

        System.out.println("Success: Teacher with ID " + id + " has been removed.");
        log.info("TEACHER DELETED id={}", id);

        RegistryStorageService.saveTeachersSilently();
        RegistryStorageService.saveFacultiesSilently();
    }
}