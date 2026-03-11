import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.Month;

public class UniversityTest {
    @Test
    void testLoginSuccess() {
        AuthService auth = new AuthService();

        boolean result = auth.login("admin", "admin");

        assertTrue(result, "Login must be successful with correct credentials");
        assertTrue(auth.isLoggedIn(), "User must be logged in");
    }

    @Test
    void testLoginFailure() {
        AuthService auth = new AuthService();

        boolean result = auth.login("admin", "fdsfsdfdsf"); // WRONG PASS

        assertFalse(result, "Login mustn't be successful with incorrect credentials");
        assertFalse(auth.isLoggedIn(), "User must not be logged in");
    }

    @Test
    void testAdminRole() {
        AuthService auth = new AuthService();
        auth.login("admin", "admin");

        assertTrue(auth.isAdmin(), "Admin must have admin role");

        auth.logout();
        auth.login("user", "user");

        assertFalse(auth.isAdmin(), "User mustn't have admin role");
    }

    @Test
    void testStudentCreation() {
        Student student = new Student("11111",
                "Viacheslav Mokliak Serhiyovych",
                LocalDate.of(2007, Month.JANUARY, 28),
                "slava.mokliak@gmail.com",
                "+380509831589",
                1,
                2,
                2025,
                "Budget",
                "Studying");

        assertEquals("11111", student.getId());
        assertEquals("Viacheslav Mokliak Serhiyovych", student.getFullName());
        assertEquals(1, student.getGrade());
        assertEquals(2, student.getGroup());
        assertEquals("Budget", student.getFormOfStudy());
        assertEquals("Studying", student.getStatus());
    }

    @Test
    void testFacultyWithDepartment() {
        Faculty faculty = new Faculty("1", "Faculty of Computer Science", "FCS", "Shevchenko O.M.", "fcs@univ.ua");
        Department department = new Department("1", "Department of Programming", "Kovalenko V.P.", 305);

        assertTrue(faculty.getDepartments().isEmpty(), "Initially, there should be no departments");

        faculty.getDepartments().add(department);

        assertEquals(1, faculty.getDepartments().size(), "There should be one department");
        assertEquals("Department of Programming", faculty.getDepartments().get(0).getFullName());
    }
}