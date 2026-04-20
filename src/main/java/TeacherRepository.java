import java.util.List;

public class TeacherRepository extends AbstractListRepository<Teacher, String> {
    @Override
    protected List<Teacher> source() {
        return CRUDForTeacher.teachers;
    }
}