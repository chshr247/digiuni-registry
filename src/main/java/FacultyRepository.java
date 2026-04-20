import java.util.List;

public class FacultyRepository extends AbstractListRepository<Faculty, String> {
    @Override
    protected List<Faculty> source() {
        return CRUDForFaculty.faculties;
    }
}