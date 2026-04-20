import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class UniversityTest {

    private Teacher teacher;
    private Faculty faculty;
    private Department department;
    private Student student;
    private AuthService auth;

    @BeforeEach
    void setUp() {
        teacher = new Teacher("1", "Kovalenko", "Ivan", "Petrovych",
                LocalDate.of(1980, 5, 15), "k@ukma.edu.ua", "+380671234567",
                "Professor", "PhD", "Professor", LocalDate.of(2010, 9, 1), 1);

        faculty = new Faculty("1", "Faculty of Informatics", "FI", "fi@ukma.edu.ua");

        department = new Department("1", "Dept of CS", 305);
        department.setFaculty(faculty);
        faculty.getDepartments().add(department);

        student = new Student("1", "Mokliak", "Viacheslav", "Serhiyovych",
                LocalDate.of(2007, 1, 28), "v@ukma.edu.ua", "+380501234567",
                1, 1, 2024, "Budget", "Studying");

        auth = new AuthService();
    }

    @AfterEach
    void tearDown() {
        CRUD.students.clear();
        CRUDForTeacher.teachers.clear();
        CRUDForFaculty.faculties.clear();
    }

    @AfterAll
    static void cleanUp() {
        try {
            Path lock = Path.of("data", ".lock");
            Files.deleteIfExists(lock);
        } catch (Exception ignored) {}
    }

    // ── 1-3: Auth — login ────────────────────────────────────────────────────

    @Test
    void testLoginSuccess() {
        assertTrue(auth.login("admin", "admin"));
        assertTrue(auth.isLoggedIn());
        assertTrue(auth.isAdmin());
    }

    @Test
    void testLoginWrongPassword() {
        assertFalse(auth.login("admin", "wrong"));
        assertFalse(auth.isLoggedIn());
    }

    @Test
    void testLoginUnknownUser() {
        assertFalse(auth.login("ghost", "ghost"));
        assertFalse(auth.isLoggedIn());
    }

    // ── 4-5: Auth — roles ────────────────────────────────────────────────────

    @ParameterizedTest
    @CsvSource({"admin,admin,true,false", "manager,manager,false,true", "user,user,false,false"})
    void testRoles(String username, String password, boolean expectAdmin, boolean expectManager) {
        auth.login(username, password);
        assertEquals(expectAdmin,   auth.isAdmin());
        assertEquals(expectManager, auth.isManager());
    }

    // ── 6-7: Auth — block ────────────────────────────────────────────────────

    @Test
    void testBlockedUserCannotLogin() {
        AuthUser u = auth.findUserOptional("user").orElseThrow();
        u.setBlocked(true);
        assertFalse(auth.login("user", "user"), "Blocked user must not log in");
        assertFalse(auth.isLoggedIn());
    }

    @Test
    void testUnblockRestoresAccess() {
        AuthUser u = auth.findUserOptional("user").orElseThrow();
        u.setBlocked(true);
        assertFalse(auth.login("user", "user"));
        u.setBlocked(false);
        assertTrue(auth.login("user", "user"), "Unblocked user must be able to log in");
    }

    // ── 8-9: Student fields ──────────────────────────────────────────────────

    @Test
    void testStudentFullName() {
        assertEquals("Mokliak Viacheslav Serhiyovych", student.getFullName());
    }

    @Test
    void testStudentAge() {
        int expected = LocalDate.now().getYear() - 2007;
        int age = student.getAge();
        assertTrue(age == expected || age == expected - 1,
                "Age should be around " + expected);
    }

    // ── 10: Teacher experience ───────────────────────────────────────────────

    @Test
    void testTeacherExperience() {
        int years = teacher.getExperienceYears();
        assertTrue(years >= 14, "Experience should be at least 14 years");
        String formatted = teacher.getExperienceFormatted();
        assertTrue(formatted.contains("yr."), "Formatted experience must contain 'yr.'");
    }

    // ── 11-12: Department ↔ Student bidirectional link ───────────────────────

    @Test
    void testAddStudentToDepartment() {
        department.addStudent(student);
        assertEquals(1, department.getStudents().size());
        assertSame(department, student.getDepartment(),
                "Student.department must point to the department");
    }

    @Test
    void testRemoveStudentFromDepartment() {
        department.addStudent(student);
        department.removeStudent(student);
        assertTrue(department.getStudents().isEmpty());
        assertNull(student.getDepartment(),
                "After removal student.department must be null");
    }

    // ── 13: Student navigates to Faculty via Department ──────────────────────

    @Test
    void testStudentGetFacultyViaDepart() {
        department.addStudent(student);
        assertSame(faculty, student.getFaculty(),
                "Student must reach faculty through department");
    }

    // ── 14-15: Dean dangling reference fix ───────────────────────────────────

    @Test
    void testDeanAssignedAndRetrieved() {
        faculty.setDean(teacher);
        assertEquals("Kovalenko Ivan Petrovych", faculty.getDeanName());
    }

    @Test
    void testDeanNullAfterUnassign() {
        faculty.setDean(teacher);
        faculty.setDean(null);
        assertEquals("Not assigned", faculty.getDeanName(),
                "getDeanName() must return 'Not assigned' when dean is null");
    }

    // ── 16-17: Head dangling reference fix ───────────────────────────────────

    @Test
    void testHeadAssignedAndRetrieved() {
        department.setHead(teacher);
        assertEquals("Kovalenko Ivan Petrovych", department.getHeadName());
    }

    @Test
    void testHeadNullAfterDepartmentHeadUnassigned() {
        department.addTeacher(teacher);
        department.setHead(teacher);
        department.removeTeacher(teacher);
        department.setHead(null);
        assertEquals("Not assigned", department.getHeadName(),
                "getHeadName() must return 'Not assigned' when head is null");
    }

    // ── 18: Delete department -> student.department becomes null ──────────────

    @Test
    void testStudentDepartmentNullAfterDeptRemoved() {
        department.addStudent(student);
        for (Student s : department.getStudents()) s.setDepartment(null);
        assertNull(student.getDepartment(),
                "Student.department must be null after department is cleared");
    }

    // ── 19: addStudent idempotent (no duplicates) ────────────────────────────

    @Test
    void testAddStudentTwiceNoDuplicate() {
        department.addStudent(student);
        department.addStudent(student);
        assertEquals(1, department.getStudents().size(),
                "Same student must not be added twice");
    }

    // ── 20: Teacher in multiple contexts ─────────────────────────────────────

    @Test
    void testTeacherAddedToDepartment() {
        department.addTeacher(teacher);
        assertTrue(department.getTeachers().contains(teacher));
        assertSame(department, teacher.getDepartment());
    }

    // ── 21: Parametrized — student grade validation range ────────────────────

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5, 6})
    void testValidGrades(int grade) {
        student.setGrade(grade);
        assertEquals(grade, student.getGrade());
        assertTrue(student.getGrade() >= 1 && student.getGrade() <= 6);
    }

    // ── 22: Parametrized — student form of study ─────────────────────────────

    @ParameterizedTest
    @CsvSource({"Budget,Budget", "Contract,Contract"})
    void testFormOfStudy(String form, String expected) {
        student.setFormOfStudy(form);
        assertEquals(expected, student.getFormOfStudy());
    }

    // ── 23: Repository — findById and existsById ─────────────────────────────

    @Test
    void testRepositoryFindById() {
        CRUD.students.clear();
        CRUD.students.add(student);

        StudentRepository repo = new StudentRepository();

        assertTrue(repo.existsById("1"));
        assertTrue(repo.findById("1").isPresent());
        assertEquals("Mokliak", repo.findById("1").get().getLastName());
        assertFalse(repo.existsById("999"));
        assertTrue(repo.findById("999").isEmpty());

        CRUD.students.clear();
    }
}