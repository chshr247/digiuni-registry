import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Scanner;

public class CRUDForFaculty {
    private static final Logger log = LoggerFactory.getLogger(CRUDForFaculty.class);

    static Scanner scanner = new Scanner(System.in);
    static ArrayList<Faculty> faculties = new ArrayList<>();
    public static int counterOfFaculty = 0;

    private static String readNonEmptyString(String message) {
        String input;
        do {
            System.out.print(message);
            input = scanner.nextLine().trim();
            if (input.isEmpty()) System.out.println("Error: field cannot be empty.");
        } while (input.isEmpty());
        return input;
    }

    static int intInRange(String message, int min, int max) {
        int value;
        while (true) {
            try {
                System.out.print(message);
                value = Integer.parseInt(scanner.nextLine());
                if (value < min || value > max)
                    System.out.println("Error: value must be between " + min + " and " + max);
                else return value;
            } catch (NumberFormatException e) {
                System.out.println("Error: enter a number.");
            }
        }
    }

    public static Optional<Faculty> findFacultyByIdOptional(String id) {
        return RepositoryRegistry.faculties().findById(id);
    }

    public static Faculty findFacultyById(String id) {
        return findFacultyByIdOptional(id).orElse(null);
    }

    public static Faculty requireFacultyById(String id) {
        return findFacultyByIdOptional(id)
                .orElseThrow(() -> new EntityNotFoundException("No faculty found with ID: " + id));
    }

    public static void create() {
        counterOfFaculty++;
        String id        = String.valueOf(counterOfFaculty);
        String fullName  = readNonEmptyString("Enter faculty name: ");
        String shortName = readNonEmptyString("Enter short name: ");
        String contact   = readNonEmptyString("Enter contact: ");

        Faculty f = new Faculty(id, fullName, shortName, contact);

        System.out.println("Assign dean? Enter teacher ID or press Enter to skip: ");
        String deanId = scanner.nextLine().trim();
        if (!deanId.isEmpty()) {
            CRUDForTeacher.findTeacherByIdOptional(deanId).ifPresentOrElse(
                    teacher -> {
                        boolean belongsToFaculty = f.getDepartments().stream()
                                .anyMatch(d -> d.getTeachers().contains(teacher));
                        if (!belongsToFaculty) {
                            System.out.println("Warning: teacher does not belong to any department of this faculty.");
                        }
                        f.setDean(teacher);
                        System.out.println("Dean set: " + teacher.getFullName());
                    },
                    () -> System.out.println("Teacher not found, dean not assigned.")
            );
        }

        faculties.add(f);
        System.out.println("Faculty registered successfully!");
        log.info("FACULTY CREATED id={} name={}", f.getId(), f.getFullName());
        RegistryStorageService.saveFacultiesSilently();
    }

    public static void showFaculties() {
        if (faculties.isEmpty()) System.out.println("No faculties found.");
        else faculties.forEach(System.out::println);
    }

    public static void deleteFaculty() {
        String id = readNonEmptyString("Enter faculty ID to remove: ");
        Faculty toRemove;
        try {
            toRemove = requireFacultyById(id);
        } catch (EntityNotFoundException e) {
            System.out.println(e.getMessage());
            return;
        }

        for (Department d : toRemove.getDepartments()) {
            d.setFaculty(null);
            for (Student s : d.getStudents()) s.setDepartment(null);
            for (Teacher t : d.getTeachers()) t.setDepartment(null);
        }
        if (toRemove.getUniversity() != null) {
            toRemove.getUniversity().getFaculties().remove(toRemove);
            toRemove.setUniversity(null);
        }
        faculties.remove(toRemove);
        System.out.println("Success: Faculty with ID " + id + " has been removed.");
        log.info("FACULTY DELETED id={}", id);
        RegistryStorageService.saveFacultiesSilently();
    }

    public static void update() {
        String id = readNonEmptyString("Enter faculty ID for updating: ");
        Faculty target;
        try {
            target = requireFacultyById(id);
        } catch (EntityNotFoundException e) {
            System.out.println(e.getMessage());
            return;
        }

        System.out.println("Faculty: " + target.getFullName());
        System.out.println("""
            1 - Full Name    2 - Short Name    3 - Contact
            4 - Assign Dean
            0 - Exit""");

        int choice = intInRange("Your choice: ", 0, 4);
        switch (choice) {
            case 1 -> target.setFullName(readNonEmptyString("New full name: "));
            case 2 -> target.setShortName(readNonEmptyString("New short name: "));
            case 3 -> target.setContact(readNonEmptyString("New contact: "));
            case 4 -> {
                System.out.print("Enter teacher ID for dean: ");
                String deanId = scanner.nextLine().trim();
                CRUDForTeacher.findTeacherByIdOptional(deanId).ifPresentOrElse(
                        teacher -> {
                            boolean belongsToFaculty = target.getDepartments().stream()
                                    .anyMatch(d -> d.getTeachers().contains(teacher));
                            if (!belongsToFaculty) {
                                System.out.println("Warning: teacher does not belong to any department of this faculty.");
                            }
                            target.setDean(teacher);
                            System.out.println("Dean set: " + teacher.getFullName());
                        },
                        () -> System.out.println("Teacher not found.")
                );
            }
            case 0 -> {
                System.out.println("Cancelled.");
                return;
            }
        }
        System.out.println("Faculty updated successfully!");
        log.info("FACULTY UPDATED id={} name={}", target.getId(), target.getFullName());
        RegistryStorageService.saveFacultiesSilently();
    }
}