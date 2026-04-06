import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class RegistryStorageService {

    private static final Path DATA_DIR = Path.of("data");
    private static final Path UNIVERSITY_FILE = DATA_DIR.resolve("university.tsv");
    private static final Path FACULTIES_FILE = DATA_DIR.resolve("faculties.tsv");
    private static final Path DEPARTMENTS_FILE = DATA_DIR.resolve("departments.tsv");
    private static final Path STUDENTS_FILE = DATA_DIR.resolve("students.tsv");
    private static final Path TEACHERS_FILE = DATA_DIR.resolve("teachers.tsv");

    public static void showStorageMenu() {
        boolean running = true;

        while (running) {
            System.out.println("""
                    
                    --- SAVE / LOAD DATA (NIO.2 + TSV) ---
                    1. Save all data
                    2. Load all data
                    0. Back
                    """);

            int choice = CRUD.intInRange("Your choice: ", 0, 2);

            switch (choice) {
                case 1 -> saveAll();
                case 2 -> loadAll();
                case 0 -> running = false;
            }
        }
    }

    public static void saveAll() {
        try {
            Files.createDirectories(DATA_DIR);

            saveUniversity();
            saveFaculties();
            saveDepartments();
            saveStudents();
            saveTeachers();

            System.out.println("Data saved successfully.");
        } catch (IOException e) {
            System.out.println("Error while saving data: " + e.getMessage());
        }
    }

    public static void loadAll() {
        try {
            if (!hasSavedFiles()) {
                System.out.println("No saved files found.");
                return;
            }

            CRUD.students.clear();
            CRUDForTeacher.teachers.clear();
            CRUDForFaculty.faculties.clear();

            University university = loadUniversity();
            Map<String, Faculty> facultiesById = loadFaculties(university);
            Map<String, Department> departmentsById = loadDepartments(facultiesById);

            loadStudents(departmentsById);
            loadTeachers(departmentsById);

            recalculateCounters();

            System.out.println("Data loaded successfully.");
        } catch (IOException e) {
            System.out.println("Error while loading data: " + e.getMessage());
        }
    }

    private static boolean hasSavedFiles() {
        return Files.exists(UNIVERSITY_FILE)
                || Files.exists(FACULTIES_FILE)
                || Files.exists(DEPARTMENTS_FILE)
                || Files.exists(STUDENTS_FILE)
                || Files.exists(TEACHERS_FILE);
    }

    private static void saveUniversity() throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add(header("fullName", "shortName", "city", "address"));

        University university = extractUniversity();
        if (university != null) {
            lines.add(row(
                    university.getFullName(),
                    university.getShortName(),
                    university.getCity(),
                    university.getAddress()
            ));
        }

        writeAll(UNIVERSITY_FILE, lines);
    }

    private static void saveFaculties() throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add(header("id", "fullName", "shortName", "dean", "contact"));

        for (Faculty faculty : CRUDForFaculty.faculties) {
            lines.add(row(
                    faculty.getId(),
                    faculty.getFullName(),
                    faculty.getShortName(),
                    faculty.getDean(),
                    faculty.getContact()
            ));
        }

        writeAll(FACULTIES_FILE, lines);
    }

    private static void saveDepartments() throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add(header("id", "fullName", "head", "cabinet", "facultyId"));

        for (Department department : allDepartments()) {
            String facultyId = department.getFaculty() == null ? "" : department.getFaculty().getId();

            lines.add(row(
                    department.getId(),
                    department.getFullName(),
                    department.getHead(),
                    String.valueOf(department.getCabinet()),
                    facultyId
            ));
        }

        writeAll(DEPARTMENTS_FILE, lines);
    }

    private static void saveStudents() throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add(header(
                "id", "fullName", "birthDate", "email", "phone",
                "grade", "group", "year", "formOfStudy", "status", "departmentId"
        ));

        for (Person person : CRUD.students) {
            if (person instanceof Student student) {
                String departmentId = student.getDepartment() == null ? "" : student.getDepartment().getId();

                lines.add(row(
                        student.getId(),
                        student.getFullName(),
                        formatDate(student.getBirthDate()),
                        student.getEmail(),
                        student.getPhone(),
                        String.valueOf(student.getGrade()),
                        String.valueOf(student.getGroup()),
                        String.valueOf(student.getYear()),
                        student.getFormOfStudy(),
                        student.getStatus(),
                        departmentId
                ));
            }
        }

        writeAll(STUDENTS_FILE, lines);
    }

    private static void saveTeachers() throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add(header(
                "id", "fullName", "birthDate", "email", "phone",
                "post", "degree", "academicRank", "startedJobDate", "rate", "departmentId"
        ));

        for (Teacher teacher : CRUDForTeacher.teachers) {
            String departmentId = teacher.getDepartment() == null ? "" : teacher.getDepartment().getId();

            lines.add(row(
                    teacher.getId(),
                    teacher.getFullName(),
                    formatDate(teacher.getBirthDate()),
                    teacher.getEmail(),
                    teacher.getPhone(),
                    teacher.getPost(),
                    teacher.getDegree(),
                    teacher.getAcademicRank(),
                    formatDate(teacher.getStartedJobDate()),
                    String.valueOf(teacher.getRate()),
                    departmentId
            ));
        }

        writeAll(TEACHERS_FILE, lines);
    }

    private static University loadUniversity() throws IOException {
        List<String> lines = readDataLines(UNIVERSITY_FILE);
        if (lines.isEmpty()) {
            return null;
        }

        String[] parts = splitLine(lines.get(0), 4);
        return new University(parts[0], parts[1], parts[2], parts[3]);
    }

    private static Map<String, Faculty> loadFaculties(University university) throws IOException {
        Map<String, Faculty> facultiesById = new LinkedHashMap<>();
        List<String> lines = readDataLines(FACULTIES_FILE);

        for (String line : lines) {
            String[] parts = splitLine(line, 5);

            Faculty faculty = new Faculty(
                    parts[0],
                    parts[1],
                    parts[2],
                    parts[3],
                    parts[4]
            );

            faculty.setDepartments(new ArrayList<>());
            faculty.setUniversity(university);

            CRUDForFaculty.faculties.add(faculty);
            facultiesById.put(faculty.getId(), faculty);
        }

        if (university != null) {
            university.setFaculties(new ArrayList<>(CRUDForFaculty.faculties));
        }

        return facultiesById;
    }

    private static Map<String, Department> loadDepartments(Map<String, Faculty> facultiesById) throws IOException {
        Map<String, Department> departmentsById = new LinkedHashMap<>();
        List<String> lines = readDataLines(DEPARTMENTS_FILE);

        for (String line : lines) {
            String[] parts = splitLine(line, 5);

            Department department = new Department(
                    parts[0],
                    parts[1],
                    parts[2],
                    parseInt(parts[3])
            );

            department.setTeachers(new ArrayList<>());
            department.setStudents(new ArrayList<>());

            Faculty faculty = facultiesById.get(parts[4]);
            department.setFaculty(faculty);

            if (faculty != null) {
                if (faculty.getDepartments() == null) {
                    faculty.setDepartments(new ArrayList<>());
                }
                faculty.getDepartments().add(department);
            }

            departmentsById.put(department.getId(), department);
        }

        return departmentsById;
    }

    private static void loadStudents(Map<String, Department> departmentsById) throws IOException {
        List<String> lines = readDataLines(STUDENTS_FILE);

        for (String line : lines) {
            String[] parts = splitLine(line, 11);

            Student student = new Student(
                    parts[0],
                    parts[1],
                    parseDate(parts[2]),
                    parts[3],
                    parts[4],
                    parseInt(parts[5]),
                    parseInt(parts[6]),
                    parseInt(parts[7]),
                    parts[8],
                    parts[9]
            );

            CRUD.students.add(student);

            Department department = departmentsById.get(parts[10]);
            if (department != null) {
                department.addStudent(student);
            }
        }
    }

    private static void loadTeachers(Map<String, Department> departmentsById) throws IOException {
        List<String> lines = readDataLines(TEACHERS_FILE);

        for (String line : lines) {
            String[] parts = splitLine(line, 11);

            Teacher teacher = new Teacher(
                    parts[0],
                    parts[1],
                    parseDate(parts[2]),
                    parts[3],
                    parts[4],
                    parts[5],
                    parts[6],
                    parts[7],
                    parseDate(parts[8]),
                    parseInt(parts[9])
            );

            CRUDForTeacher.teachers.add(teacher);

            Department department = departmentsById.get(parts[10]);
            if (department != null) {
                department.addTeacher(teacher);
            }
        }
    }

    private static University extractUniversity() {
        for (Faculty faculty : CRUDForFaculty.faculties) {
            if (faculty.getUniversity() != null) {
                return faculty.getUniversity();
            }
        }
        return null;
    }

    private static List<Department> allDepartments() {
        return CRUDForFaculty.faculties.stream()
                .flatMap(faculty -> faculty.getDepartments() == null
                        ? Stream.<Department>empty()
                        : faculty.getDepartments().stream())
                .toList();
    }

    private static void recalculateCounters() {
        CRUD.counterOfStudents = maxNumericId(
                CRUD.students.stream()
                        .map(Person::getId)
                        .toList()
        );

        CRUDForTeacher.counterOfTeachers = maxNumericId(
                CRUDForTeacher.teachers.stream()
                        .map(Teacher::getId)
                        .toList()
        );

        CRUDForFaculty.counterOfFaculty = maxNumericId(
                CRUDForFaculty.faculties.stream()
                        .map(Faculty::getId)
                        .toList()
        );

        CRUDForDepartment.counterOfDepartments = maxNumericId(
                allDepartments().stream()
                        .map(Department::getId)
                        .toList()
        );
    }

    private static int maxNumericId(List<String> ids) {
        int max = 0;

        for (String id : ids) {
            if (id != null && id.matches("\\d+")) {
                max = Math.max(max, Integer.parseInt(id));
            }
        }

        return max;
    }

    private static void writeAll(Path path, List<String> lines) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(
                path,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        )) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        }
    }

    private static List<String> readDataLines(Path path) throws IOException {
        if (!Files.exists(path)) {
            return List.of();
        }

        List<String> result = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            boolean firstLine = true;

            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                if (!line.isBlank()) {
                    result.add(line);
                }
            }
        }

        return result;
    }

    private static String header(String... values) {
        return String.join("\t", values);
    }

    private static String row(String... values) {
        String[] escaped = new String[values.length];

        for (int i = 0; i < values.length; i++) {
            escaped[i] = escape(values[i]);
        }

        return String.join("\t", escaped);
    }

    private static String[] splitLine(String line, int expectedSize) {
        String[] raw = line.split("\t", -1);
        String[] result = new String[expectedSize];

        for (int i = 0; i < expectedSize; i++) {
            result[i] = i < raw.length ? unescape(raw[i]) : "";
        }

        return result;
    }

    private static String escape(String value) {
        if (value == null) {
            return "";
        }

        return value
                .replace("\\", "\\\\")
                .replace("\t", "\\t")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }

    private static String unescape(String value) {
        StringBuilder sb = new StringBuilder();
        boolean escaping = false;

        for (int i = 0; i < value.length(); i++) {
            char ch = value.charAt(i);

            if (escaping) {
                switch (ch) {
                    case 't' -> sb.append('\t');
                    case 'n' -> sb.append('\n');
                    case 'r' -> sb.append('\r');
                    case '\\' -> sb.append('\\');
                    default -> sb.append(ch);
                }
                escaping = false;
            } else if (ch == '\\') {
                escaping = true;
            } else {
                sb.append(ch);
            }
        }

        if (escaping) {
            sb.append('\\');
        }

        return sb.toString();
    }

    private static String formatDate(LocalDate date) {
        return date == null ? "" : date.toString();
    }

    private static LocalDate parseDate(String text) {
        return text == null || text.isBlank() ? null : LocalDate.parse(text);
    }

    private static int parseInt(String text) {
        return text == null || text.isBlank() ? 0 : Integer.parseInt(text);
    }
}