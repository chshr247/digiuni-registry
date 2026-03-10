import java.time.LocalDate;
import java.time.Month;

public class Main {
    public static void main(String[] args) {
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
            System.out.println("""
                    --- MENU ---
                    1. Add Student
                    2. List Students
                    3. Update Student
                    4. Delete Student
                    5. Search Student by name
                    6. Search Student by group
                    7. Search Student by grade
                    8. List of all students by grade
                    9. Create Faculty
                    10. Show all Faculties
                    0. Exit
                    """);

            int choice = CRUD.intInRange("Your choice: ", 0, 10);

            switch (choice) {
                case 1 -> CRUD.create();
                case 2 -> CRUD.showStudents();
                case 3 -> CRUD.update();
                case 4 -> CRUD.delete();
                case 5 -> CRUD.searchByFullName();
                case 6 -> CRUD.searchByGroup();
                case 7 -> CRUD.searchByGrade();
                case 8 -> CRUD.showAllStudentsByCourse();
                case 9 -> CRUDForFaculty.create();
                case 10 -> CRUDForFaculty.showFaculties();
                case 0 -> running = false;
            }
        }
        System.out.println("Program finished.");
    }
}