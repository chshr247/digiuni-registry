package crud;
import model.*;
import repository.*;
import service.RegistryStorageService;
import exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Scanner;

public class CRUDForDepartment {
    private static final Logger log = LoggerFactory.getLogger(CRUDForDepartment.class);

    static Scanner scanner = new Scanner(System.in);
    public static int counterOfDepartments = 0;

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

    public static Optional<Department> findDepartmentByIdOptional(ArrayList<Department> departments, String id) {
        if (departments == null) {
            return Optional.empty();
        }
        return departments.stream()
                .filter(department -> department.getId().equals(id))
                .findFirst();
    }

    public static Optional<Department> findDepartmentByIdOptional(String id) {
        return RepositoryRegistry.departments().findById(id);
    }

    public static Department findDepartmentById(ArrayList<Department> departments, String id) {
        return findDepartmentByIdOptional(departments, id).orElse(null);
    }

    public static Department requireDepartmentById(ArrayList<Department> departments, String id) {
        return findDepartmentByIdOptional(departments, id)
                .orElseThrow(() -> new EntityNotFoundException("No department found with ID: " + id));
    }

    public static void createDepartment() {
        String facultyId = readNonEmptyString("Enter faculty ID: ");
        Faculty faculty = CRUDForFaculty.findFacultyByIdOptional(facultyId).orElse(null);
        if (faculty == null) {
            System.out.println("No faculty found for ID: " + facultyId);
            return;
        }

        counterOfDepartments++;
        String id       = String.valueOf(counterOfDepartments);
        String fullName = readNonEmptyString("Enter department name: ");
        int cabinet     = intInRange("Enter cabinet number: ", 1, 10000);

        Department d = new Department(id, fullName, cabinet);
        d.setFaculty(faculty);
        faculty.getDepartments().add(d);

        System.out.print("Assign head? Enter teacher ID or press Enter to skip: ");
        String headId = scanner.nextLine().trim();
        if (!headId.isEmpty()) {
            CRUDForTeacher.findTeacherByIdOptional(headId).ifPresentOrElse(
                    teacher -> {
                        if (!d.getTeachers().contains(teacher)) {
                            System.out.println("Warning: teacher does not belong to this department.");
                        }
                        d.setHead(teacher);
                        System.out.println("Head set: " + teacher.getFullName());
                    },
                    () -> System.out.println("Teacher not found, head not assigned.")
            );
        }

        System.out.println("Department added successfully to faculty: " + faculty.getFullName());
        log.info("DEPARTMENT CREATED id={} name={} faculty={}", d.getId(), d.getFullName(), faculty.getFullName());
        RegistryStorageService.saveDepartmentsSilently();
    }

    public static void showDepartmentsOfFaculty() {
        String facultyId = readNonEmptyString("Enter faculty ID: ");
        Faculty faculty = CRUDForFaculty.findFacultyByIdOptional(facultyId).orElse(null);
        if (faculty == null) {
            System.out.println("No faculty found for ID: " + facultyId);
            return;
        }
        if (faculty.getDepartments().isEmpty()) {
            System.out.println("This faculty has no departments.");
            return;
        }

        System.out.println("Departments of: " + faculty.getFullName());
        faculty.getDepartments().forEach(System.out::println);
    }

    public static void updateDepartment() {
        String facultyId = readNonEmptyString("Enter faculty ID: ");
        Faculty faculty = CRUDForFaculty.findFacultyByIdOptional(facultyId).orElse(null);
        if (faculty == null) {
            System.out.println("No faculty found for ID: " + facultyId);
            return;
        }

        String deptId = readNonEmptyString("Enter department ID: ");
        Department target;
        try {
            target = requireDepartmentById(faculty.getDepartments(), deptId);
        } catch (EntityNotFoundException e) {
            System.out.println(e.getMessage());
            return;
        }

        System.out.println("Department: " + target.getFullName());
        System.out.println("""
            1 - Name    2 - Cabinet    3 - Assign Head
            0 - Exit""");

        int choice = intInRange("Your choice: ", 0, 3);
        switch (choice) {
            case 1 -> target.setFullName(readNonEmptyString("New department name: "));
            case 2 -> target.setCabinet(intInRange("New cabinet number: ", 1, 10000));
            case 3 -> {
                System.out.print("Enter teacher ID for head: ");
                String headId = scanner.nextLine().trim();
                CRUDForTeacher.findTeacherByIdOptional(headId).ifPresentOrElse(
                        teacher -> {
                            if (!target.getTeachers().contains(teacher)) {
                                System.out.println("Warning: teacher does not belong to this department.");
                            }
                            target.setHead(teacher);
                            System.out.println("Head set: " + teacher.getFullName());
                        },
                        () -> System.out.println("Teacher not found.")
                );
            }
            case 0 -> {
                System.out.println("Cancelled.");
                return;
            }
        }
        System.out.println("Department updated successfully!");
        log.info("DEPARTMENT UPDATED id={} name={}", target.getId(), target.getFullName());
        RegistryStorageService.saveDepartmentsSilently();
    }

    public static void deleteDepartment() {
        String facultyId = readNonEmptyString("Enter faculty ID: ");
        Faculty faculty = CRUDForFaculty.findFacultyByIdOptional(facultyId).orElse(null);
        if (faculty == null) {
            System.out.println("No faculty found for ID: " + facultyId);
            return;
        }

        String deptId = readNonEmptyString("Enter department ID to remove: ");
        Department toRemove;
        try {
            toRemove = requireDepartmentById(faculty.getDepartments(), deptId);
        } catch (EntityNotFoundException e) {
            System.out.println(e.getMessage());
            return;
        }

        for (Teacher teacher : toRemove.getTeachers()) teacher.setDepartment(null);
        for (Student student : toRemove.getStudents()) student.setDepartment(null);
        faculty.getDepartments().remove(toRemove);
        toRemove.setFaculty(null);
        System.out.println("Success: Department with ID " + deptId + " has been removed.");
        log.info("DEPARTMENT DELETED id={}", deptId);
        RegistryStorageService.saveDepartmentsSilently();
    }
}