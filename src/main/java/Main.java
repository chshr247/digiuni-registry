import java.time.LocalDate;
import java.time.Month;

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
                System.out.println("11. Update Faculty");
                System.out.println("12. Delete Faculty");
                System.out.println("13. Create Department for Faculty");
                System.out.println("14. Show Departments of Faculty");
                System.out.println("15. Update Department of Faculty");
                System.out.println("16. Delete Department of Faculty");
            }
            System.out.println("0. Exit");

            int choice = CRUD.intInRange("Your choice: ", 0, 16);

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
                    case 11 -> { auth.requireAdmin(); CRUDForFaculty.update(); }
                    case 12 -> { auth.requireAdmin(); CRUDForFaculty.deleteFaculty(); }
                    case 13 -> { auth.requireAdmin(); CRUDForDepartment.createDepartment(); }
                    case 14 -> { auth.requireAuth(); CRUDForDepartment.showDepartmentsOfFaculty(); }
                    case 15 -> { auth.requireAdmin(); CRUDForDepartment.updateDepartment(); }
                    case 16 -> { auth.requireAdmin(); CRUDForDepartment.deleteDepartment(); }
                    case 0 -> { auth.logout(); running = false; }
                }
            } catch (RuntimeException e) {
            }
        }

        System.out.println("Program finished.");
    }
}
