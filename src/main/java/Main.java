public class Main {
    public static void main(String[] args) {
        AuthService auth = new AuthService();
        RegistryStorageService.setAuthService(auth);
        if (!RegistryStorageService.acquireLock()) {
            System.out.println("Exiting to prevent data corruption.");
            return;
        }
        RegistryStorageService.loadOnStartup();

        UniversityServer server = new UniversityServer();
        Thread serverThread = new Thread(server);
        serverThread.setDaemon(true);
        serverThread.start();
        try { Thread.sleep(200); } catch (InterruptedException ignored) {}
        System.out.println("""
                admin admin
                user user
                manager manager
                """);
        while (!auth.isLoggedIn()) {
            System.out.print("Username: ");
            String username = CRUD.scanner.nextLine().trim();
            System.out.print("Password: ");
            String password = CRUD.scanner.nextLine().trim();
            auth.login(username, password);
        }

        boolean running = true;


        while (running) {
            System.out.println("""
                   --- MAIN MENU ---
                    0. Logout and exit
                    1. Search and reports
                   --- MANAGER ONLY ---
                    2. Add entity
                    3. Update entity
                   --- ADMIN ONLY ---
                    4. Delete entity
                    5. Manage users
                   """);

            int choice = CRUD.intInRange("Your choice: ", 0, 5);

            try {
                switch (choice) {
                    case 1 -> { auth.requireAuth(); showSearchMenu(auth); }
                    case 2 -> { auth.requireManager(); showAddMenu(auth); }
                    case 3 -> { auth.requireManager(); showUpdateMenu(auth); }
                    case 4 -> { auth.requireAdmin(); showDeleteMenu(auth); }
                    case 5 -> { auth.requireAdmin(); showUserManagementMenu(auth); }
                    case 0 -> { auth.logout(); running = false; }
                }
            } catch (RuntimeException e) {
                System.err.println("Operation failed: " + e.getMessage());
                e.printStackTrace(System.err);
            }
        }

        System.out.println("Program finished.");
    }

    private static void showSearchMenu(AuthService auth) {
        StreamReports.showReportsMenu();
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

    private static void showUserManagementMenu(AuthService auth) {
        System.out.println("\n--- ADMIN PANEL ---");
        System.out.println("-- Users --");
        System.out.println("1. View all users");
        System.out.println("2. Add user");
        System.out.println("3. Update user");
        System.out.println("4. Delete user");
        System.out.println("-- System --");
        System.out.println("5. Show all entities");
        System.out.println("6. Save / Load data (CSV)");
        System.out.println("7. Reflection and annotations");
        System.out.println("0. Back");

        int choice = CRUD.intInRange("Your choice: ", 0, 7);
        switch (choice) {
            case 1 -> CRUDForUser.showUsers(auth);
            case 2 -> CRUDForUser.addUser(auth);
            case 3 -> CRUDForUser.updateUser(auth);
            case 4 -> CRUDForUser.deleteUser(auth);
            case 5 -> showShowMenu(auth);
            case 6 -> RegistryStorageService.showStorageMenu();
            case 7 -> ReflectionModule.showReflectionMenu();
            case 0 -> {}
        }
    }
}