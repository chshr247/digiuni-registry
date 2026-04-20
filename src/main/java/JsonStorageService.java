import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

public class JsonStorageService {

    private static final Logger log = LoggerFactory.getLogger(JsonStorageService.class);
    private static final Path JSON_FILE = Path.of("data", "backup.json");

    private static final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .enable(SerializationFeature.INDENT_OUTPUT)
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);


    public record UniversitySnapshot(String fullName, String shortName, String city, String address) {}

    public record FacultySnapshot(String id, String fullName, String shortName,
                                  String contact, String deanId) {}

    public record DepartmentSnapshot(String id, String fullName, int cabinet,
                                     String facultyId, String headId) {}

    public record StudentSnapshot(String id, String lastName, String firstName, String patronymic,
                                  LocalDate birthDate, String email, String phone,
                                  int grade, int group, int year,
                                  String formOfStudy, String status, String departmentId) {}

    public record TeacherSnapshot(String id, String lastName, String firstName, String patronymic,
                                  LocalDate birthDate, String email, String phone,
                                  String post, String degree, String academicRank,
                                  LocalDate startedJobDate, int rate, String departmentId) {}

    @Data @NoArgsConstructor @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DatabaseSnapshot {
        private UniversitySnapshot university;
        private List<FacultySnapshot> faculties;
        private List<DepartmentSnapshot> departments;
        private List<StudentSnapshot> students;
        private List<TeacherSnapshot> teachers;
    }


    public static void exportToJson() {
        try {
            Files.createDirectories(JSON_FILE.getParent());

            University uni = extractUniversity();
            UniversitySnapshot uniSnap = uni == null ? null
                    : new UniversitySnapshot(uni.getFullName(), uni.getShortName(),
                    uni.getCity(), uni.getAddress());

            List<FacultySnapshot> facSnaps = CRUDForFaculty.faculties.stream()
                    .map(f -> new FacultySnapshot(
                            f.getId(), f.getFullName(), f.getShortName(), f.getContact(),
                            f.getDean() == null ? null : f.getDean().getId()))
                    .toList();

            List<DepartmentSnapshot> deptSnaps = allDepartments()
                    .map(d -> new DepartmentSnapshot(
                            d.getId(), d.getFullName(), d.getCabinet(),
                            d.getFaculty() == null ? null : d.getFaculty().getId(),
                            d.getHead() == null ? null : d.getHead().getId()))
                    .toList();

            List<StudentSnapshot> stuSnaps = CRUD.students.stream()
                    .filter(Student.class::isInstance).map(Student.class::cast)
                    .map(s -> new StudentSnapshot(
                            s.getId(), s.getLastName(), s.getFirstName(), s.getPatronymic(),
                            s.getBirthDate(), s.getEmail(), s.getPhone(),
                            s.getGrade(), s.getGroup(), s.getYear(),
                            s.getFormOfStudy(), s.getStatus(),
                            s.getDepartment() == null ? null : s.getDepartment().getId()))
                    .toList();

            List<TeacherSnapshot> tchSnaps = CRUDForTeacher.teachers.stream()
                    .map(t -> new TeacherSnapshot(
                            t.getId(), t.getLastName(), t.getFirstName(), t.getPatronymic(),
                            t.getBirthDate(), t.getEmail(), t.getPhone(),
                            t.getPost(), t.getDegree(), t.getAcademicRank(),
                            t.getStartedJobDate(), t.getRate(),
                            t.getDepartment() == null ? null : t.getDepartment().getId()))
                    .toList();

            DatabaseSnapshot snapshot = new DatabaseSnapshot(uniSnap, facSnaps, deptSnaps, stuSnaps, tchSnaps);
            mapper.writeValue(JSON_FILE.toFile(), snapshot);

            System.out.println("JSON exported: " + JSON_FILE.toAbsolutePath());
            System.out.println("  Faculties: " + facSnaps.size() + ", Departments: " + deptSnaps.size()
                    + ", Students: " + stuSnaps.size() + ", Teachers: " + tchSnaps.size());
            log.info("JSON EXPORT success path={}", JSON_FILE);

        } catch (IOException e) {
            System.out.println("JSON export error: " + e.getMessage());
            log.error("JSON EXPORT ERROR: {}", e.getMessage());
        }
    }

    public static void importFromJson() {
        if (!Files.exists(JSON_FILE)) {
            System.out.println("JSON file not found: " + JSON_FILE.toAbsolutePath());
            return;
        }
        try {
            DatabaseSnapshot snap = mapper.readValue(JSON_FILE.toFile(), DatabaseSnapshot.class);

            CRUD.students.clear();
            CRUDForTeacher.teachers.clear();
            CRUDForFaculty.faculties.clear();

            // University
            if (snap.getUniversity() != null) {
                UniversitySnapshot u = snap.getUniversity();
                University uni = new University(u.fullName(), u.shortName(), u.city(), u.address());

                // Faculties
                for (FacultySnapshot fs : snap.getFaculties()) {
                    Faculty f = new Faculty(fs.id(), fs.fullName(), fs.shortName(), fs.contact());
                    f.setUniversity(uni);
                    CRUDForFaculty.faculties.add(f);
                }
                uni.setFaculties(new java.util.ArrayList<>(CRUDForFaculty.faculties));
            }

            // Departments
            for (DepartmentSnapshot ds : snap.getDepartments()) {
                Department d = new Department(ds.id(), ds.fullName(), ds.cabinet());
                Faculty f = findFacultyById(ds.facultyId());
                if (f != null) { d.setFaculty(f); f.getDepartments().add(d); }
            }

            // Teachers
            for (TeacherSnapshot ts : snap.getTeachers()) {
                Teacher t = new Teacher(ts.id(), ts.lastName(), ts.firstName(), ts.patronymic(),
                        ts.birthDate(), ts.email(), ts.phone(),
                        ts.post(), ts.degree(), ts.academicRank(), ts.startedJobDate(), ts.rate());
                CRUDForTeacher.teachers.add(t);
                Department d = findDepartmentById(ts.departmentId());
                if (d != null) d.addTeacher(t);
            }

            // Students
            for (StudentSnapshot ss : snap.getStudents()) {
                Student s = new Student(ss.id(), ss.lastName(), ss.firstName(), ss.patronymic(),
                        ss.birthDate(), ss.email(), ss.phone(),
                        ss.grade(), ss.group(), ss.year(), ss.formOfStudy(), ss.status());
                CRUD.students.add(s);
                Department d = findDepartmentById(ss.departmentId());
                if (d != null) d.addStudent(s);
            }

            // Resolve dean / head
            for (FacultySnapshot fs : snap.getFaculties()) {
                if (fs.deanId() == null) continue;
                Faculty f = findFacultyById(fs.id());
                CRUDForTeacher.findTeacherByIdOptional(fs.deanId()).ifPresent(t -> { if (f != null) f.setDean(t); });
            }
            for (DepartmentSnapshot ds : snap.getDepartments()) {
                if (ds.headId() == null) continue;
                Department d = findDepartmentById(ds.id());
                CRUDForTeacher.findTeacherByIdOptional(ds.headId()).ifPresent(t -> { if (d != null) d.setHead(t); });
            }

            System.out.println("JSON imported successfully.");
            System.out.println("  Faculties: " + CRUDForFaculty.faculties.size()
                    + ", Students: " + CRUD.students.size()
                    + ", Teachers: " + CRUDForTeacher.teachers.size());
            log.info("JSON IMPORT success");

        } catch (IOException e) {
            System.out.println("JSON import error: " + e.getMessage());
            log.error("JSON IMPORT ERROR: {}", e.getMessage());
        }
    }

    private static University extractUniversity() {
        return CRUDForFaculty.faculties.stream()
                .filter(f -> f.getUniversity() != null)
                .map(Faculty::getUniversity)
                .findFirst().orElse(null);
    }

    private static Stream<Department> allDepartments() {
        return CRUDForFaculty.faculties.stream()
                .filter(f -> f.getDepartments() != null)
                .flatMap(f -> f.getDepartments().stream());
    }

    private static Faculty findFacultyById(String id) {
        if (id == null) return null;
        return CRUDForFaculty.faculties.stream()
                .filter(f -> f.getId().equals(id)).findFirst().orElse(null);
    }

    private static Department findDepartmentById(String id) {
        if (id == null) return null;
        return allDepartments().filter(d -> d.getId().equals(id)).findFirst().orElse(null);
    }
}