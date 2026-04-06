import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamReports {

    public static void showReportsMenu() {
        boolean running = true;

        while (running) {
            System.out.println("""
                    
                    --- STREAM API REPORTS ---
                    1. Count students by grade
                    2. Count students by form of study
                    3. Count teachers by department
                    4. Average teacher rate by department
                    5. Top departments by number of students
                    0. Back
                    """);

            int choice = CRUD.intInRange("Your choice: ", 0, 5);

            switch (choice) {
                case 1 -> showStudentCountByGrade();
                case 2 -> showStudentCountByFormOfStudy();
                case 3 -> showTeacherCountByDepartment();
                case 4 -> showAverageTeacherRateByDepartment();
                case 5 -> showTopDepartmentsByStudentCount();
                case 0 -> running = false;
            }
        }
    }

    private static Stream<Student> studentStream() {
        return CRUD.students.stream()
                .filter(Student.class::isInstance)
                .map(Student.class::cast);
    }

    private static String safeText(String value, String defaultValue) {
        return value == null || value.trim().isEmpty() ? defaultValue : value;
    }

    private static String departmentName(Department department) {
        if (department == null) {
            return "Without department";
        }
        return safeText(department.getFullName(), "Without department");
    }

    private static void showStudentCountByGrade() {
        Map<Integer, Long> stats = studentStream()
                .collect(Collectors.groupingBy(
                        Student::getGrade,
                        TreeMap::new,
                        Collectors.counting()
                ));

        if (stats.isEmpty()) {
            System.out.println("No students found.");
            return;
        }

        System.out.println("\nStudents by grade:");
        stats.forEach((grade, count) ->
                System.out.println("Grade " + grade + ": " + count));
    }

    private static void showStudentCountByFormOfStudy() {
        Map<String, Long> stats = studentStream()
                .collect(Collectors.groupingBy(
                        student -> safeText(student.getFormOfStudy(), "Unknown"),
                        TreeMap::new,
                        Collectors.counting()
                ));

        if (stats.isEmpty()) {
            System.out.println("No students found.");
            return;
        }

        System.out.println("\nStudents by form of study:");
        stats.forEach((form, count) ->
                System.out.println(form + ": " + count));
    }

    private static void showTeacherCountByDepartment() {
        Map<String, Long> stats = CRUDForTeacher.teachers.stream()
                .collect(Collectors.groupingBy(
                        teacher -> departmentName(teacher.getDepartment()),
                        TreeMap::new,
                        Collectors.counting()
                ));

        if (stats.isEmpty()) {
            System.out.println("No teachers found.");
            return;
        }

        System.out.println("\nTeachers by department:");
        stats.forEach((department, count) ->
                System.out.println(department + ": " + count));
    }

    private static void showAverageTeacherRateByDepartment() {
        Map<String, Double> stats = CRUDForTeacher.teachers.stream()
                .collect(Collectors.groupingBy(
                        teacher -> departmentName(teacher.getDepartment()),
                        TreeMap::new,
                        Collectors.averagingInt(Teacher::getRate)
                ));

        if (stats.isEmpty()) {
            System.out.println("No teachers found.");
            return;
        }

        System.out.println("\nAverage teacher rate by department:");
        stats.forEach((department, averageRate) ->
                System.out.printf("%s: %.2f%n", department, averageRate));
    }

    private static void showTopDepartmentsByStudentCount() {
        Map<String, Integer> stats = CRUDForFaculty.faculties.stream()
                .flatMap(faculty -> faculty.getDepartments() == null
                        ? Stream.<Department>empty()
                        : faculty.getDepartments().stream())
                .collect(Collectors.toMap(
                        department -> safeText(department.getFullName(), "Unknown department"),
                        department -> department.getStudents() == null ? 0 : department.getStudents().size(),
                        Integer::sum
                ));

        if (stats.isEmpty()) {
            System.out.println("No departments found.");
            return;
        }

        Map<String, Integer> sortedStats = stats.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue(Comparator.reverseOrder())
                        .thenComparing(Map.Entry.comparingByKey()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (left, right) -> left,
                        LinkedHashMap::new
                ));

        System.out.println("\nTop departments by number of students:");
        sortedStats.forEach((department, count) ->
                System.out.println(department + ": " + count));
    }
}