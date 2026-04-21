import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamReports {

    public static void showReportsMenu() {
        boolean running = true;
        while (running) {
            System.out.println("""
                    --- SEARCH AND REPORTS ---
                    -- Search --
                    1. Find student by full name
                    2. Find student by grade
                    3. Find student by group
                    4. Find teacher by full name
                    -- Students --
                    5. All students sorted by grade
                    6. Students of faculty (alphabetically)
                    7. Students of department sorted by grade
                    8. Students of department (alphabetically)
                    9. Students of department by specific grade
                    -- Teachers --
                    10. Teachers of faculty (alphabetically)
                    11. Teachers of department (alphabetically)
                    -- Statistics --
                    12. Student count by grade
                    13. Student count by form of study
                    14. Teacher count by department
                    15. Average teacher rate by department
                    16. Top departments by student count
                    0. Back
                    """);

            int choice = CRUD.intInRange("Your choice: ", 0, 16);
            switch (choice) {
                case 1  -> searchStudentByName();
                case 2  -> searchStudentByGrade();
                case 3  -> searchStudentByGroup();
                case 4  -> searchTeacherByName();
                case 5  -> showAllStudentsByGrade();
                case 6  -> showStudentsOfFacultyAlpha();
                case 7  -> showStudentsOfDepartmentByGrade();
                case 8  -> showStudentsOfDepartmentAlpha();
                case 9  -> showStudentsOfDepartmentBySpecificGrade();
                case 10 -> showTeachersOfFacultyAlpha();
                case 11 -> showTeachersOfDepartmentAlpha();
                case 12 -> showStudentCountByGrade();
                case 13 -> showStudentCountByFormOfStudy();
                case 14 -> showTeacherCountByDepartment();
                case 15 -> showAverageTeacherRateByDepartment();
                case 16 -> showTopDepartmentsByStudentCount();
                case 0  -> running = false;
            }
        }
    }

    private static void searchStudentByName() {
        String query = prompt("Enter full name (or part): ").toLowerCase();
        if (query.isEmpty()) { System.out.println("Search query cannot be empty."); return; }
        List<Student> found = studentStream()
                .filter(s -> s.getFullName().toLowerCase().contains(query))
                .sorted(Comparator.comparing(Student::getLastName))
                .toList();
        printStudents("Search results", found);
    }

    private static void searchStudentByGrade() {
        int grade = CRUD.intInRange("Enter grade (1-6): ", 1, 6);
        List<Student> found = studentStream()
                .filter(s -> s.getGrade() == grade)
                .sorted(Comparator.comparing(Student::getLastName))
                .toList();
        printStudents("Students of grade " + grade, found);
    }

    private static void searchStudentByGroup() {
        int group = CRUD.intInRange("Enter group (1-3): ", 1, 3);
        List<Student> found = studentStream()
                .filter(s -> s.getGroup() == group)
                .sorted(Comparator.comparing(Student::getLastName))
                .toList();
        printStudents("Students of group " + group, found);
    }

    private static void searchTeacherByName() {
        String query = prompt("Enter teacher name (or part): ").toLowerCase();
        if (query.isEmpty()) { System.out.println("Search query cannot be empty."); return; }
        List<Teacher> found = CRUDForTeacher.teachers.stream()
                .filter(t -> t.getFullName().toLowerCase().contains(query))
                .sorted(Comparator.comparing(Teacher::getLastName))
                .toList();
        printTeachers("Search results", found);
    }

    private static void showAllStudentsByGrade() {
        Map<Integer, List<Student>> byGrade = studentStream()
                .collect(Collectors.groupingBy(
                        Student::getGrade,
                        TreeMap::new,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> list.stream()
                                        .sorted(Comparator.comparing(Student::getLastName))
                                        .toList()
                        )
                ));
        if (byGrade.isEmpty()) { System.out.println("No students found."); return; }
        System.out.println("\n=== All students by grade ===");
        byGrade.forEach((grade, students) -> {
            System.out.println("\n-- Grade " + grade + " (" + students.size() + " students) --");
            students.forEach(s -> System.out.println("  " + formatStudent(s)));
        });
    }

    private static void showStudentsOfFacultyAlpha() {
        Faculty faculty = chooseFaculty();
        if (faculty == null) return;
        List<Student> students = faculty.getDepartments().stream()
                .filter(Objects::nonNull)
                .flatMap(d -> d.getStudents().stream())
                .sorted(Comparator.comparing(Student::getLastName)
                        .thenComparing(Student::getFirstName))
                .toList();
        printStudents("Students of faculty '" + faculty.getFullName() + "' (alphabetically)", students);
    }

    private static void showStudentsOfDepartmentByGrade() {
        Department dept = chooseDepartment();
        if (dept == null) return;
        Map<Integer, List<Student>> byGrade = dept.getStudents().stream()
                .collect(Collectors.groupingBy(
                        Student::getGrade,
                        TreeMap::new,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> list.stream()
                                        .sorted(Comparator.comparing(Student::getLastName))
                                        .toList()
                        )
                ));
        if (byGrade.isEmpty()) { System.out.println("No students in this department."); return; }
        System.out.println("\n=== Students of '" + dept.getFullName() + "' by grade ===");
        byGrade.forEach((grade, students) -> {
            System.out.println("\n-- Grade " + grade + " (" + students.size() + " students) --");
            students.forEach(s -> System.out.println("  " + formatStudent(s)));
        });
    }

    private static void showStudentsOfDepartmentAlpha() {
        Department dept = chooseDepartment();
        if (dept == null) return;
        List<Student> students = dept.getStudents().stream()
                .sorted(Comparator.comparing(Student::getLastName)
                        .thenComparing(Student::getFirstName))
                .toList();
        printStudents("Students of '" + dept.getFullName() + "' (alphabetically)", students);
    }

    private static void showStudentsOfDepartmentBySpecificGrade() {
        Department dept = chooseDepartment();
        if (dept == null) return;
        int grade = CRUD.intInRange("Enter grade (1-6): ", 1, 6);
        List<Student> students = dept.getStudents().stream()
                .filter(s -> s.getGrade() == grade)
                .sorted(Comparator.comparing(Student::getLastName))
                .toList();
        System.out.println("\n=== Department '" + dept.getFullName() + "', grade " + grade + " ===");
        System.out.println("-- List --");
        if (students.isEmpty()) System.out.println("  No students.");
        else students.forEach(s -> System.out.println("  " + formatStudent(s)));
        System.out.println("\n-- Alphabetically --");
        students.stream()
                .sorted(Comparator.comparing(Student::getLastName)
                        .thenComparing(Student::getFirstName)
                        .thenComparing(Student::getPatronymic))
                .forEach(s -> System.out.println("  " + formatStudent(s)));
    }

    private static void showTeachersOfFacultyAlpha() {
        Faculty faculty = chooseFaculty();
        if (faculty == null) return;
        List<Teacher> teachers = faculty.getDepartments().stream()
                .filter(Objects::nonNull)
                .flatMap(d -> d.getTeachers().stream())
                .sorted(Comparator.comparing(Teacher::getLastName)
                        .thenComparing(Teacher::getFirstName))
                .toList();
        printTeachers("Teachers of faculty '" + faculty.getFullName() + "' (alphabetically)", teachers);
    }

    private static void showTeachersOfDepartmentAlpha() {
        Department dept = chooseDepartment();
        if (dept == null) return;
        List<Teacher> teachers = dept.getTeachers().stream()
                .sorted(Comparator.comparing(Teacher::getLastName)
                        .thenComparing(Teacher::getFirstName))
                .toList();
        printTeachers("Teachers of '" + dept.getFullName() + "' (alphabetically)", teachers);
    }

    private static void showStudentCountByGrade() {
        Map<Integer, Long> stats = studentStream()
                .collect(Collectors.groupingBy(Student::getGrade, TreeMap::new, Collectors.counting()));
        if (stats.isEmpty()) { System.out.println("No students found."); return; }
        System.out.println("\nStudents by grade:");
        stats.forEach((grade, count) -> System.out.println("  Grade " + grade + ": " + count));
    }

    private static void showStudentCountByFormOfStudy() {
        Map<String, Long> stats = studentStream()
                .collect(Collectors.groupingBy(
                        s -> safeText(s.getFormOfStudy(), "Unknown"),
                        TreeMap::new, Collectors.counting()));
        if (stats.isEmpty()) { System.out.println("No students found."); return; }
        System.out.println("\nStudents by form of study:");
        stats.forEach((form, count) -> System.out.println("  " + form + ": " + count));
    }

    private static void showTeacherCountByDepartment() {
        Map<String, Long> stats = CRUDForTeacher.teachers.stream()
                .collect(Collectors.groupingBy(
                        t -> deptName(t.getDepartment()),
                        TreeMap::new, Collectors.counting()));
        if (stats.isEmpty()) { System.out.println("No teachers found."); return; }
        System.out.println("\nTeachers by department:");
        stats.forEach((dept, count) -> System.out.println("  " + dept + ": " + count));
    }

    private static void showAverageTeacherRateByDepartment() {
        Map<String, Double> stats = CRUDForTeacher.teachers.stream()
                .collect(Collectors.groupingBy(
                        t -> deptName(t.getDepartment()),
                        TreeMap::new, Collectors.averagingInt(Teacher::getRate)));
        if (stats.isEmpty()) { System.out.println("No teachers found."); return; }
        System.out.println("\nAverage teacher rate by department:");
        stats.forEach((dept, avg) -> System.out.printf("  %s: %.2f%n", dept, avg));
    }

    private static void showTopDepartmentsByStudentCount() {
        Map<String, Integer> stats = CRUDForFaculty.faculties.stream()
                .flatMap(f -> f.getDepartments() == null
                        ? Stream.<Department>empty()
                        : f.getDepartments().stream())
                .collect(Collectors.toMap(
                        d -> safeText(d.getFullName(), "Unknown"),
                        d -> d.getStudents() == null ? 0 : d.getStudents().size(),
                        Integer::sum));
        if (stats.isEmpty()) { System.out.println("No departments found."); return; }
        System.out.println("\nTop departments by student count:");
        stats.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue(Comparator.reverseOrder())
                        .thenComparing(Map.Entry.comparingByKey()))
                .forEach(e -> System.out.println("  " + e.getKey() + ": " + e.getValue()));
    }

    private static Stream<Student> studentStream() {
        return CRUD.students.stream()
                .filter(Student.class::isInstance)
                .map(Student.class::cast);
    }

    private static Faculty chooseFaculty() {
        if (CRUDForFaculty.faculties.isEmpty()) { System.out.println("No faculties found."); return null; }
        System.out.println("Available faculties:");
        CRUDForFaculty.faculties.forEach(f -> System.out.println("  " + f.getId() + " - " + f.getFullName()));
        String id = prompt("Enter faculty ID: ");
        Faculty f = CRUDForFaculty.findFacultyById(id);
        if (f == null) System.out.println("Faculty not found.");
        return f;
    }

    private static Department chooseDepartment() {
        Faculty faculty = chooseFaculty();
        if (faculty == null) return null;
        if (faculty.getDepartments().isEmpty()) { System.out.println("No departments in this faculty."); return null; }
        System.out.println("Departments of '" + faculty.getFullName() + "':");
        faculty.getDepartments().forEach(d -> System.out.println("  " + d.getId() + " - " + d.getFullName()));
        String id = prompt("Enter department ID: ");
        Department d = CRUDForDepartment.findDepartmentById(faculty.getDepartments(), id);
        if (d == null) System.out.println("Department not found.");
        return d;
    }

    private static void printStudents(String title, List<Student> list) {
        System.out.println("\n=== " + title + " ===");
        if (list.isEmpty()) { System.out.println("  No students found."); return; }
        list.forEach(s -> System.out.println("  " + formatStudent(s)));
        System.out.println("  Total: " + list.size());
    }

    private static void printTeachers(String title, List<Teacher> list) {
        System.out.println("\n=== " + title + " ===");
        if (list.isEmpty()) { System.out.println("  No teachers found."); return; }
        list.forEach(t -> System.out.println("  " + formatTeacher(t)));
        System.out.println("  Total: " + list.size());
    }

    private static String formatStudent(Student s) {
        return String.format("%-30s | grade %d, group %d | %s | %s",
                s.getFullName(), s.getGrade(), s.getGroup(), s.getFormOfStudy(), s.getStatus());
    }

    private static String formatTeacher(Teacher t) {
        return String.format("%-30s | %s | %s | exp. %d yr.",
                t.getFullName(), t.getPost(), t.getDegree(), t.getExperience());
    }

    private static String deptName(Department d) {
        return d == null ? "No department" : safeText(d.getFullName(), "No department");
    }

    private static String safeText(String value, String def) {
        return value == null || value.trim().isEmpty() ? def : value;
    }

    private static String prompt(String message) {
        System.out.print(message);
        return CRUD.scanner.nextLine().trim();
    }
}