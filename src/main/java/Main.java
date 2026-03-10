import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        AuthService auth = new AuthService();
        System.out.println("""
                admin admin
                user user
                """);
        while (!auth.isLoggedIn()) {
            System.out.print("Username: ");
            String username = CRUD.scanner.nextLine().trim();
            System.out.print("Password: ");
            String password = CRUD.scanner.nextLine().trim();
            auth.login(username, password);
        }

        boolean running = true;

        Person newStudent = new Student("1",
                "Viacheslav Mokliak Serhiyovych",
                LocalDate.of(2007, Month.JANUARY, 28),
                "slava.mokliak@gmail.com",
                "+380509831589",
                1,
                2,
                2025,
                "Budget",
                "Studying");
        CRUD.students.add(newStudent);

        while (running) {
            System.out.println("\n--- MENU ---");
            System.out.println("1. List Students");
            System.out.println("2. Search by name");
            System.out.println("3. Search by group");
            System.out.println("4. Search by grade");
            System.out.println("5. List by grade");

            if (auth.isAdmin()) {
                System.out.println("--- ADMIN only ---");
                System.out.println("6. Add Student");
                System.out.println("7. Update Student");
                System.out.println("8. Delete Student");
                System.out.println("9. Create Faculty");
                System.out.println("10. Show Faculties");
            }
            System.out.println("0. Exit");

            int choice = CRUD.intInRange("Your choice: ", 0, 10);

            try {
                switch (choice) {
                    case 1 -> { auth.requireAuth(); CRUD.showStudents(); }
                    case 2 -> { auth.requireAuth(); CRUD.searchByFullName(); }
                    case 3 -> { auth.requireAuth(); CRUD.searchByGroup(); }
                    case 4 -> { auth.requireAuth(); CRUD.searchByGrade(); }
                    case 5 -> { auth.requireAuth(); CRUD.showAllStudentsByCourse(); }
                    case 6 -> { auth.requireAdmin(); CRUD.create(); }
                    case 7 -> { auth.requireAdmin(); CRUD.update(); }
                    case 8 -> { auth.requireAdmin(); CRUD.delete(); }
                    case 9 -> { auth.requireAdmin(); CRUDForFaculty.create(); }
                    case 10 -> { auth.requireAuth(); CRUDForFaculty.showFaculties(); }
                    case 0 -> { auth.logout(); running = false; }
                }
            } catch (RuntimeException e) {}
        }

        System.out.println("Program finished.");
    }
}
