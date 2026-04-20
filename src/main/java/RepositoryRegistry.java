public final class RepositoryRegistry {
    private static final StudentRepository STUDENT_REPOSITORY = new StudentRepository();
    private static final TeacherRepository TEACHER_REPOSITORY = new TeacherRepository();
    private static final FacultyRepository FACULTY_REPOSITORY = new FacultyRepository();
    private static final DepartmentRepository DEPARTMENT_REPOSITORY = new DepartmentRepository();

    private RepositoryRegistry() {
    }

    public static Repository<Student, String> students() {
        return STUDENT_REPOSITORY;
    }

    public static Repository<Teacher, String> teachers() {
        return TEACHER_REPOSITORY;
    }

    public static Repository<Faculty, String> faculties() {
        return FACULTY_REPOSITORY;
    }

    public static Repository<Department, String> departments() {
        return DEPARTMENT_REPOSITORY;
    }
}