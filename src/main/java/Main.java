import java.time.LocalDate;
import java.time.Month;

public class Main {
    public static void main(String[] args) throws InterruptedException {
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
            System.out.println("1. Search Students");
            System.out.println("2. Show Entity");
            System.out.println("3. Add Entity");
            System.out.println("4. Update Entity");
            System.out.println("5. Delete Entity");
            System.out.println("0. Exit");

            int choice = CRUD.intInRange("Your choice: ", 0, 5);

            try {
                switch (choice) {
                    case 1 -> { auth.requireAuth(); showSearchMenu(auth); }
                    case 2 -> { auth.requireAuth(); showShowMenu(auth); }
                    case 3 -> { auth.requireAdmin(); showAddMenu(auth); }
                    case 4 -> { auth.requireAdmin(); showUpdateMenu(auth); }
                    case 5 -> { auth.requireAdmin(); showDeleteMenu(auth); }
                    case 0 -> { auth.logout(); running = false; }
                }
                Thread.sleep(2000);
            } catch (RuntimeException e) {
            }
        }

        System.out.println("Program finished.");
    }

    private static void showSearchMenu(AuthService auth) {
        System.out.println("\n--- SEARCH STUDENTS ---");
        System.out.println("1. Search by name");
        System.out.println("2. Search by group");
        System.out.println("3. Search by grade");
        System.out.println("0. Back");

        int choice = CRUD.intInRange("Your choice: ", 0, 3);
        switch (choice) {
            case 1 -> CRUD.searchByFullName();
            case 2 -> CRUD.searchByGroup();
            case 3 -> CRUD.searchByGrade();
            case 0 -> {}
        }
    }

    private static void showAddMenu(AuthService auth) {
        System.out.println("\n--- ADD ENTITY ---");
        System.out.println("1. Add Student");
        System.out.println("2. Add Faculty");
        System.out.println("3. Add Department");
        System.out.println("4. Add Teacher");
        System.out.println("0. Back");

        int choice = CRUD.intInRange("Your choice: ", 0, 4);
        switch (choice) {
            case 1 -> CRUD.create();
            case 2 -> CRUDForFaculty.create();
            case 3 -> CRUDForDepartment.createDepartment();
            case 4 -> CRUDForTeacher.create();
            case 0 -> {}
        }
    }

    private static void showUpdateMenu(AuthService auth) {
        System.out.println("\n--- UPDATE ENTITY ---");
        System.out.println("1. Update Student");
        System.out.println("2. Update Faculty");
        System.out.println("3. Update Department");
        System.out.println("4. Update Teacher");
        System.out.println("0. Back");

        int choice = CRUD.intInRange("Your choice: ", 0, 4);
        switch (choice) {
            case 1 -> CRUD.update();
            case 2 -> CRUDForFaculty.update();
            case 3 -> CRUDForDepartment.updateDepartment();
            case 4 -> CRUDForTeacher.update();
            case 0 -> {}
        }
    }

    private static void showDeleteMenu(AuthService auth) {
        System.out.println("\n--- DELETE ENTITY ---");
        System.out.println("1. Delete Student");
        System.out.println("2. Delete Faculty");
        System.out.println("3. Delete Department");
        System.out.println("4. Delete Teacher");
        System.out.println("0. Back");

        int choice = CRUD.intInRange("Your choice: ", 0, 4);
        switch (choice) {
            case 1 -> CRUD.delete();
            case 2 -> CRUDForFaculty.deleteFaculty();
            case 3 -> CRUDForDepartment.deleteDepartment();
            case 4 -> CRUDForTeacher.delete();
            case 0 -> {}
        }
    }

    private static void showShowMenu(AuthService auth) {
        System.out.println("\n--- SHOW ENTITY ---");
        System.out.println("1. Show All Students");
        System.out.println("2. Show All Faculties");
        System.out.println("3. Show All Departments");
        System.out.println("4. Show All Teachers");
        System.out.println("0. Back");

        int choice = CRUD.intInRange("Your choice: ", 0, 4);
        switch (choice) {
            case 1 -> CRUD.showStudents();
            case 2 -> CRUDForFaculty.showFaculties();
            case 3 -> CRUDForDepartment.showDepartmentsOfFaculty();
            case 4 -> CRUDForTeacher.showTeachers();
            case 0 -> {}
        }
    }
}
