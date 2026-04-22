package repository;
import crud.CRUDForFaculty;
import model.Department;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class DepartmentRepository extends AbstractListRepository<Department, String> {
    @Override
    protected List<Department> source() {
        return CRUDForFaculty.faculties.stream()
                .filter(Objects::nonNull)
                .flatMap(faculty -> faculty.getDepartments() == null
                        ? Stream.<Department>empty()
                        : faculty.getDepartments().stream())
                .toList();
    }
}