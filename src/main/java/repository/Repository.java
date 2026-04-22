package repository;
import java.util.List;
import java.util.Optional;

public interface Repository<T extends Identifiable<ID>, ID> {
    Optional<T> findById(ID id);
    List<T> findAll();
    boolean existsById(ID id);
}