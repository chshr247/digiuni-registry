package repository;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public abstract class AbstractListRepository<T extends Identifiable<ID>, ID> implements Repository<T, ID> {
    protected abstract List<T> source();

    @Override
    public Optional<T> findById(ID id) {
        return source().stream()
                .filter(entity -> Objects.equals(entity.getId(), id))
                .findFirst();
    }

    @Override
    public List<T> findAll() {
        return new ArrayList<>(source());
    }

    @Override
    public boolean existsById(ID id) {
        return findById(id).isPresent();
    }
}