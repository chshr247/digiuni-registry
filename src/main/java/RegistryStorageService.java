import lombok.extern.slf4j.Slf4j;

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
import java.util.StringJoiner;
import java.util.stream.Stream;

@Slf4j
public class RegistryStorageService {


    private static final Path DATA_DIR = Path.of("data");
    private static final Path UNIVERSITY_FILE = DATA_DIR.resolve("university.csv");
    private static final Path FACULTIES_FILE  = DATA_DIR.resolve("faculties.csv");
    private static final Path DEPARTMENTS_FILE = DATA_DIR.resolve("departments.csv");
    private static final Path STUDENTS_FILE   = DATA_DIR.resolve("students.csv");
    private static final Path TEACHERS_FILE   = DATA_DIR.resolve("teachers.csv");
    private static final Path USERS_FILE      = DATA_DIR.resolve("users.csv");

    private static AuthService authService;

    private static final Path LOCK_FILE = DATA_DIR.resolve(".lock");

    public static void setAuthService(AuthService auth) {
        authService = auth;
    }

    public static boolean acquireLock() {
        try {
            Files.createDirectories(DATA_DIR);
            if (Files.exists(LOCK_FILE)) {
                System.out.println("[Warning] Another instance may be running (lock file exists).");
                System.out.println("  If no other instance is running, delete: " + LOCK_FILE.toAbsolutePath());
                return false;
            }
            Files.writeString(LOCK_FILE, String.valueOf(ProcessHandle.current().pid()),
                    StandardOpenOption.CREATE_NEW);
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try { Files.deleteIfExists(LOCK_FILE); } catch (IOException ignored) {}
            }));
            return true;
        } catch (IOException e) {
            System.out.println("[Lock error] " + e.getMessage());
            log.error("LOCK ERROR: {}", e.getMessage());
            return false;
        }
    }

    public static void showStorageMenu() {
        boolean running = true;
        while (running) {
            System.out.println("""
                    
                    --- SAVE / LOAD DATA (NIO.2 + CSV) ---
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
            saveUsers();
            System.out.println("Data saved successfully.");
            log.info("DATA SAVED all files");
        } catch (IOException e) {
            System.out.println("Error while saving data: " + e.getMessage());
            log.error("SAVE ERROR: {}", e.getMessage());
        }
    }

    public static void saveStudentsSilently() {
        silently(RegistryStorageService::saveStudents);
    }

    public static void saveTeachersSilently() {
        silently(RegistryStorageService::saveTeachers);
    }

    public static void saveFacultiesSilently() {
        silently(() -> {
            saveUniversity();
            saveFaculties();
            saveDepartments();
            saveStudents();
            saveTeachers();
        });
    }

    public static void saveDepartmentsSilently() {
        silently(() -> {
            saveDepartments();
            saveStudents();
            saveTeachers();
        });
    }

    public static void saveUsersSilently() {
        silently(RegistryStorageService::saveUsers);
    }

    @FunctionalInterface
    private interface IoAction { void run() throws IOException; }

    private static void silently(IoAction action) {
        try {
            Files.createDirectories(DATA_DIR);
            action.run();
        } catch (IOException e) {
            System.out.println("[Auto-save error] " + e.getMessage());
            log.error("AUTO-SAVE ERROR: {}", e.getMessage());
        }
    }

    public static void loadAll() {
        try {
            if (!hasSavedFiles()) { System.out.println("No saved files found."); return; }
            clearAndLoad();
            System.out.println("Data loaded successfully.");
            log.info("DATA LOADED successfully");
        } catch (IOException e) {
            System.out.println("Error while loading data: " + e.getMessage());
        }
    }

    public static void loadOnStartup() {
        System.out.println("[Storage] Reading from: " + DATA_DIR.toAbsolutePath());
        if (!hasSavedFiles()) return;
        try {
            clearAndLoad();
            System.out.println("Saved data loaded: "
                    + CRUDForFaculty.faculties.size() + " faculties, "
                    + CRUDForTeacher.teachers.size() + " teachers, "
                    + CRUD.students.size() + " students.");
        } catch (Exception e) {
            System.out.println("[Startup load error] " + e.getClass().getSimpleName() + ": " + e.getMessage());
            log.error("STARTUP LOAD ERROR {}: {}", e.getClass().getSimpleName(), e.getMessage());
            e.printStackTrace();
        }
    }

    private static void clearAndLoad() throws IOException {
        CRUD.students.clear();
        CRUDForTeacher.teachers.clear();
        CRUDForFaculty.faculties.clear();

        University university = loadUniversity();
        Map<String, Faculty>    facultiesById    = loadFaculties(university);
        Map<String, Department> departmentsById  = loadDepartments(facultiesById);

        loadStudents(departmentsById);
        loadTeachers(departmentsById);
        resolveDeanAndHead();
        loadUsers();
        recalculateCounters();
    }

    private static boolean hasSavedFiles() {
        return Files.exists(UNIVERSITY_FILE) || Files.exists(FACULTIES_FILE)
                || Files.exists(DEPARTMENTS_FILE) || Files.exists(STUDENTS_FILE)
                || Files.exists(TEACHERS_FILE);
    }


    private static void saveUniversity() throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add(header("fullName", "shortName", "city", "address"));
        University u = extractUniversity();
        if (u != null) lines.add(row(u.getFullName(), u.getShortName(), u.getCity(), u.getAddress()));
        writeAll(UNIVERSITY_FILE, lines);
    }

    private static void saveFaculties() throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add(header("id", "fullName", "shortName", "contact", "deanId"));
        for (Faculty f : CRUDForFaculty.faculties) {
            String deanId = f.getDean() == null ? "" : f.getDean().getId();
            lines.add(row(f.getId(), f.getFullName(), f.getShortName(), f.getContact(), deanId));
        }
        writeAll(FACULTIES_FILE, lines);
    }

    private static void saveDepartments() throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add(header("id", "fullName", "cabinet", "facultyId", "headId"));
        for (Department d : allDepartments()) {
            String facultyId = d.getFaculty() == null ? "" : d.getFaculty().getId();
            String headId    = d.getHead()    == null ? "" : d.getHead().getId();
            lines.add(row(d.getId(), d.getFullName(), String.valueOf(d.getCabinet()), facultyId, headId));
        }
        writeAll(DEPARTMENTS_FILE, lines);
    }

    private static void saveStudents() throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add(header("id", "lastName", "firstName", "patronymic", "birthDate", "email", "phone",
                "grade", "group", "year", "formOfStudy", "status", "departmentId"));
        for (Person p : CRUD.students) {
            if (!(p instanceof Student s)) continue;
            String deptId = s.getDepartment() == null ? "" : s.getDepartment().getId();
            lines.add(row(s.getId(), s.getLastName(), s.getFirstName(), s.getPatronymic(),
                    formatDate(s.getBirthDate()), s.getEmail(), "\u0027" + s.getPhone(),
                    String.valueOf(s.getGrade()), String.valueOf(s.getGroup()), String.valueOf(s.getYear()),
                    s.getFormOfStudy(), s.getStatus(), deptId));
        }
        writeAll(STUDENTS_FILE, lines);
    }

    private static void saveTeachers() throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add(header("id", "lastName", "firstName", "patronymic", "birthDate", "email", "phone",
                "post", "degree", "academicRank", "startedJobDate", "rate", "departmentId"));
        for (Teacher t : CRUDForTeacher.teachers) {
            String deptId = t.getDepartment() == null ? "" : t.getDepartment().getId();
            lines.add(row(t.getId(), t.getLastName(), t.getFirstName(), t.getPatronymic(),
                    formatDate(t.getBirthDate()), t.getEmail(), "\u0027" + t.getPhone(),
                    t.getPost(), t.getDegree(), t.getAcademicRank(),
                    formatDate(t.getStartedJobDate()), String.valueOf(t.getRate()), deptId));
        }
        writeAll(TEACHERS_FILE, lines);
    }

    private static void saveUsers() throws IOException {
        if (authService == null) return;
        List<String> lines = new ArrayList<>();
        lines.add(header("username", "password", "role", "blocked"));
        for (AuthUser u : authService.getUsers().values())
            lines.add(row(u.getUsername(), u.getPassword(), u.getRole().name(),
                    String.valueOf(u.isBlocked())));
        writeAll(USERS_FILE, lines);
    }


    private static University loadUniversity() throws IOException {
        List<String> lines = readDataLines(UNIVERSITY_FILE);
        if (lines.isEmpty()) return null;
        String[] p = splitLine(lines.get(0), 4);
        return new University(p[0], p[1], p[2], p[3]);
    }

    private static Map<String, Faculty> loadFaculties(University university) throws IOException {
        Map<String, Faculty> byId = new LinkedHashMap<>();
        for (String line : readDataLines(FACULTIES_FILE)) {
            String[] p = splitLine(line, 5);
            Faculty f = new Faculty(p[0], p[1], p[2], p[3]);
            f.setDepartments(new ArrayList<>());
            f.setUniversity(university);
            CRUDForFaculty.faculties.add(f);
            byId.put(f.getId(), f);
        }
        if (university != null)
            university.setFaculties(new ArrayList<>(CRUDForFaculty.faculties));
        return byId;
    }

    private static Map<String, Department> loadDepartments(Map<String, Faculty> facultiesById) throws IOException {
        Map<String, Department> byId = new LinkedHashMap<>();
        for (String line : readDataLines(DEPARTMENTS_FILE)) {
            String[] p = splitLine(line, 5);
            Department d = new Department(p[0], p[1], parseInt(p[2]));
            d.setTeachers(new ArrayList<>());
            d.setStudents(new ArrayList<>());
            Faculty f = facultiesById.get(p[3]);
            d.setFaculty(f);
            if (f != null) {
                if (f.getDepartments() == null) f.setDepartments(new ArrayList<>());
                f.getDepartments().add(d);
            }
            byId.put(d.getId(), d);
        }
        return byId;
    }

    private static void loadStudents(Map<String, Department> departmentsById) throws IOException {
        for (String line : readDataLines(STUDENTS_FILE)) {
            try {
                String[] p = splitLine(line, 13);
                Student s = new Student(p[0], p[1], p[2], p[3], parseDate(p[4]), p[5], stripPhone(p[6]),
                        parseInt(p[7]), parseInt(p[8]), parseInt(p[9]), p[10], p[11]);
                CRUD.students.add(s);
                Department d = departmentsById.get(p[12]);
                if (d != null) d.addStudent(s);
            } catch (Exception e) {
                System.out.println("[Load warning] Skipped invalid student row: " + e.getMessage());
                log.warn("LOAD SKIP invalid student row: {}", e.getMessage());
            }
        }
    }

    private static void loadTeachers(Map<String, Department> departmentsById) throws IOException {
        for (String line : readDataLines(TEACHERS_FILE)) {
            try {
                String[] p = splitLine(line, 13);
                Teacher t = new Teacher(p[0], p[1], p[2], p[3], parseDate(p[4]), p[5], stripPhone(p[6]),
                        p[7], p[8], p[9], parseDate(p[10]), parseInt(p[11]));
                CRUDForTeacher.teachers.add(t);
                Department d = departmentsById.get(p[12]);
                if (d != null) d.addTeacher(t);
            } catch (Exception e) {
                System.out.println("[Load warning] Skipped invalid teacher row: " + e.getMessage());
                log.warn("LOAD SKIP invalid teacher row: {}", e.getMessage());
            }
        }
    }

    private static void resolveDeanAndHead() throws IOException {
        // Faculties — deanId у p[4]
        List<String> fLines = readDataLines(FACULTIES_FILE);
        for (String line : fLines) {
            String[] p = splitLine(line, 5);
            if (p[4].isEmpty()) continue;
            Faculty f = CRUDForFaculty.faculties.stream()
                    .filter(x -> x.getId().equals(p[0])).findFirst().orElse(null);
            if (f == null) continue;
            CRUDForTeacher.findTeacherByIdOptional(p[4]).ifPresent(f::setDean);
        }
        // Departments — headId у p[4]
        List<String> dLines = readDataLines(DEPARTMENTS_FILE);
        for (String line : dLines) {
            String[] p = splitLine(line, 5);
            if (p[4].isEmpty()) continue;
            CRUDForFaculty.faculties.stream()
                    .flatMap(f -> f.getDepartments().stream())
                    .filter(d -> d.getId().equals(p[0])).findFirst()
                    .ifPresent(d -> CRUDForTeacher.findTeacherByIdOptional(p[4]).ifPresent(d::setHead));
        }
    }

    private static void loadUsers() throws IOException {
        if (authService == null || !Files.exists(USERS_FILE)) return;
        for (String line : readDataLines(USERS_FILE)) {
            String[] p = splitLine(line, 4);
            Role role;
            try { role = Role.valueOf(p[2]); } catch (IllegalArgumentException e) { role = Role.USER; }
            boolean blocked = "true".equalsIgnoreCase(p[3]);
            AuthUser u = new AuthUser(p[0], p[1], role);
            u.setBlocked(blocked);
            authService.getUsers().put(p[0], u);
        }
    }

    private static University extractUniversity() {
        for (Faculty f : CRUDForFaculty.faculties)
            if (f.getUniversity() != null) return f.getUniversity();
        return null;
    }

    private static List<Department> allDepartments() {
        return CRUDForFaculty.faculties.stream()
                .flatMap(f -> f.getDepartments() == null
                        ? Stream.<Department>empty()
                        : f.getDepartments().stream())
                .toList();
    }

    private static void recalculateCounters() {
        CRUD.counterOfStudents = maxNumericId(
                CRUD.students.stream().map(Person::getId).toList());
        CRUDForTeacher.counterOfTeachers = maxNumericId(
                CRUDForTeacher.teachers.stream().map(Teacher::getId).toList());
        CRUDForFaculty.counterOfFaculty = maxNumericId(
                CRUDForFaculty.faculties.stream().map(Faculty::getId).toList());
        CRUDForDepartment.counterOfDepartments = maxNumericId(
                allDepartments().stream().map(Department::getId).toList());
    }

    private static int maxNumericId(List<String> ids) {
        int max = 0;
        for (String id : ids)
            if (id != null && id.matches("\\d+")) max = Math.max(max, Integer.parseInt(id));
        return max;
    }

    private static void writeAll(Path path, List<String> lines) throws IOException {
        try (BufferedWriter w = Files.newBufferedWriter(path, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            w.write('\uFEFF');
            for (String line : lines) { w.write(line); w.newLine(); }
        }
    }

    private static List<String> readDataLines(Path path) throws IOException {
        if (!Files.exists(path)) return List.of();
        List<String> result = new ArrayList<>();
        try (BufferedReader r = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line; boolean first = true;
            while ((line = r.readLine()) != null) {
                if (line.startsWith("\uFEFF")) line = line.substring(1);
                if (first) { first = false; continue; }
                if (!line.isBlank()) result.add(line);
            }
        }
        return result;
    }

    private static String header(String... values) { return row(values); }

    private static String row(String... values) {
        StringJoiner j = new StringJoiner(";");
        for (String v : values) j.add(toCsv(v));
        return j.toString();
    }

    private static String toCsv(String value) {
        if (value == null) return "\"\"";
        return "\"" + value.replace("\r", " ").replace("\n", " ").replace("\"", "\"\"") + "\"";
    }

    private static String toCsvPhone(String value) {
        if (value == null) return "\"\"";
        String safe = value.replace("\r", " ").replace("\n", " ").replace("\"", "\"\"\"");
        return "\"\u0027" + safe + "\"";
    }

    private static String[] splitLine(String line, int expectedSize) {
        char sep = line.contains(";") ? ';' : ',';
        List<String> fields = new ArrayList<>(expectedSize);
        StringBuilder cur = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (inQuotes) {
                if (ch == '"') {
                    if (i + 1 < line.length() && line.charAt(i + 1) == '"') { cur.append('"'); i++; }
                    else inQuotes = false;
                } else cur.append(ch);
            } else {
                if      (ch == '"') inQuotes = true;
                else if (ch == sep)  { fields.add(cur.toString().trim()); cur.setLength(0); }
                else cur.append(ch);
            }
        }
        fields.add(cur.toString().trim());
        while (fields.size() < expectedSize) fields.add("");
        if (fields.size() > expectedSize) return fields.subList(0, expectedSize).toArray(new String[0]);
        return fields.toArray(new String[0]);
    }

    private static String formatDate(LocalDate date) { return date == null ? "" : date.toString(); }
    private static LocalDate parseDate(String text)  { return text == null || text.isBlank() ? null : LocalDate.parse(text); }
    private static int parseInt(String text)         { return text == null || text.isBlank() ? 0 : Integer.parseInt(text); }
    private static String stripPhone(String text)    { return text != null && text.startsWith("'") ? text.substring(1) : text; }
}