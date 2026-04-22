package repository;
import crud.CRUD;
import model.Student;
import java.util.List;

public class StudentRepository extends AbstractListRepository<Student, String> {
    @Override
    protected List<Student> source() {
        return CRUD.students.stream()
                .filter(Student.class::isInstance)
                .map(Student.class::cast)
                .toList();
    }
}