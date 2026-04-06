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
            System.out.println("6. Show Faculties");
            System.out.println("7. Show Teachers");

            if (auth.isAdmin()) {
                System.out.println("--- ADMIN only ---");
                System.out.println("8. Add Student");
                System.out.println("9. Update Student");
                System.out.println("10. Delete Student");
                System.out.println("11. Create Faculty");
                System.out.println("12. Update Faculty");
                System.out.println("13. Delete Faculty");
                System.out.println("14. Create Department for Faculty");
                System.out.println("15. Show Departments of Faculty");
                System.out.println("16. Update Department of Faculty");
                System.out.println("17. Delete Department of Faculty");
                System.out.println("18. Add Teacher");
                System.out.println("19. Update Teacher");
                System.out.println("20. Delete Teacher");

            }
            System.out.println("0. Exit");

            int choice = CRUD.intInRange("Your choice: ", 0, 20);

            try {
                switch (choice) {
                    case 1 -> { auth.requireAuth(); CRUD.showStudents(); }
                    case 2 -> { auth.requireAuth(); CRUD.searchByFullName(); }
                    case 3 -> { auth.requireAuth(); CRUD.searchByGroup(); }
                    case 4 -> { auth.requireAuth(); CRUD.searchByGrade(); }
                    case 5 -> { auth.requireAuth(); CRUD.showAllStudentsByCourse(); }
                    case 6 -> { auth.requireAuth(); CRUDForFaculty.showFaculties(); }
                    case 7 -> { auth.requireAuth(); CRUDForTeacher.showTeachers(); }
                    case 8 -> { auth.requireAdmin(); CRUD.create(); }
                    case 9 -> { auth.requireAdmin(); CRUD.update(); }
                    case 10 -> { auth.requireAdmin(); CRUD.delete(); }
                    case 11 -> { auth.requireAdmin(); CRUDForFaculty.create(); }
                    case 12 -> { auth.requireAdmin(); CRUDForFaculty.update(); }
                    case 13 -> { auth.requireAdmin(); CRUDForFaculty.deleteFaculty(); }
                    case 14 -> { auth.requireAdmin(); CRUDForDepartment.createDepartment(); }
                    case 15 -> { auth.requireAuth(); CRUDForDepartment.showDepartmentsOfFaculty(); }
                    case 16 -> { auth.requireAdmin(); CRUDForDepartment.updateDepartment(); }
                    case 17 -> { auth.requireAdmin(); CRUDForDepartment.deleteDepartment(); }
                    case 18 -> { auth.requireAdmin(); CRUDForTeacher.create(); }
                    case 19 -> { auth.requireAdmin(); CRUDForTeacher.update(); }
                    case 20 -> { auth.requireAdmin(); CRUDForTeacher.delete(); }
                    case 0 -> { auth.logout(); running = false; }
                }
            } catch (RuntimeException e) {
            }
        }

        System.out.println("Program finished.");
    }
}
