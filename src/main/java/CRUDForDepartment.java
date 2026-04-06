import java.util.ArrayList;
import java.util.Scanner;

public class CRUDForDepartment {
    static Scanner scanner = new Scanner(System.in);
    public static int counterOfDepartments = 0;

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

    public static Department findDepartmentById(ArrayList<Department> departments, String id) {
        for (Department department : departments) {
            if (department.getId().equals(id)) {
                return department;
            }
        }
        return null;
    }

    public static void createDepartment() {
        String facultyId = readNonEmptyString("Enter faculty ID for this department: ");
        Faculty faculty = CRUDForFaculty.findFacultyById(facultyId);

        if (faculty == null) {
            System.out.println("No faculty found for this ID: " + facultyId);
            return;
        }

        counterOfDepartments++;
        String id = String.valueOf(counterOfDepartments);
        String fullName = readNonEmptyString("Enter department name: ");
        String head = readNonEmptyString("Enter department head: ");
        int cabinet = intInRange("Enter cabinet number: ", 1, 10000);

        Department newDepartment = new Department(id, fullName, head, cabinet);
        faculty.getDepartments().add(newDepartment);
        newDepartment.setFaculty(faculty);
        System.out.println("Department added successfully to faculty " + faculty.getFullName());
    }

    public static void showDepartmentsOfFaculty() {
        String facultyId = readNonEmptyString("Enter faculty ID to show departments: ");
        Faculty faculty = CRUDForFaculty.findFacultyById(facultyId);

        if (faculty == null) {
            System.out.println("No faculty found for this ID: " + facultyId);
            return;
        }

        if (faculty.getDepartments().isEmpty()) {
            System.out.println("This faculty has no departments.");
            return;
        }

        System.out.println("Departments of faculty: " + faculty.getFullName());
        faculty.getDepartments().forEach(System.out::println);
    }

    public static void updateDepartment() {
        String facultyId = readNonEmptyString("Enter faculty ID of department: ");
        Faculty faculty = CRUDForFaculty.findFacultyById(facultyId);

        if (faculty == null) {
            System.out.println("No faculty found for this ID: " + facultyId);
            return;
        }

        String departmentId = readNonEmptyString("Enter department ID for updating: ");
        Department targetDepartment = findDepartmentById(faculty.getDepartments(), departmentId);

        if (targetDepartment == null) {
            System.out.println("No department found for this ID: " + departmentId);
            return;
        }

        System.out.println("Department found: " + targetDepartment.getFullName());
        System.out.println("""
                Enter number of what you want to update:
                1 - ID
                2 - Name
                3 - Head
                4 - Cabinet
                0 - Exit
                """);

        int choice = intInRange("Your choice: ", 0, 4);

        switch (choice) {
            case 1 -> targetDepartment.setId(readNonEmptyString("Enter new ID: "));
            case 2 -> targetDepartment.setFullName(readNonEmptyString("Enter new department name: "));
            case 3 -> targetDepartment.setHead(readNonEmptyString("Enter new head: "));
            case 4 -> targetDepartment.setCabinet(intInRange("Enter new cabinet number: ", 1, 10000));
            case 0 -> {
                System.out.println("Exiting update menu.");
                return;
            }
            default -> System.out.println("Invalid option.");
        }
        System.out.println("Department information updated successfully!");
    }

    public static void deleteDepartment() {
        String facultyId = readNonEmptyString("Enter faculty ID of department: ");
        Faculty faculty = CRUDForFaculty.findFacultyById(facultyId);

        if (faculty == null) {
            System.out.println("No faculty found for this ID: " + facultyId);
            return;
        }

        String departmentId = readNonEmptyString("Enter department ID to remove: ");
        Department departmentToRemove = findDepartmentById(faculty.getDepartments(), departmentId);

        if (departmentToRemove == null) {
            System.out.println("Error: No department found with ID " + departmentId);
            return;
        }

        for (Student student : departmentToRemove.getStudents()) {
            student.setDepartment(null);
        }
        for (Teacher teacher : departmentToRemove.getTeachers()) {
            teacher.setDepartment(null);
        }

        boolean isRemoved = faculty.getDepartments().remove(departmentToRemove);

        if (isRemoved) {
            System.out.println("Success: Department with ID " + departmentId + " has been removed.");
        } else {
            System.out.println("Error: No department found with ID " + departmentId);
        }
    }
}