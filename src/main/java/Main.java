public class Main {
    public static void main(String[] args) {
        boolean running = true;

        while (running) {
            System.out.println("""
                    --- MENU ---
                    1. Add Student
                    2. List Students
                    3. Update Student
                    4. Delete Student
                    5. Search Student by name
                    6. Search Student by group
                    7. Search Student by grade
                    0. Exit
                    """);

            int choice = CRUD.intInRange("Your choice: ", 0, 7);

            switch (choice) {
                case 1 -> CRUD.create();
                case 2 -> CRUD.showStudents();
                case 3 -> CRUD.update();
                case 4 -> CRUD.delete();
                case 5 -> CRUD.searchByFullName();
                case 6 -> CRUD.searchByGroup();
                case 7 -> CRUD.searchByGrade();
                case 0 -> running = false;
            }
        }
        System.out.println("Program finished.");
    }
}