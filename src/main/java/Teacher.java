import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Getter
@Setter
@ReflectiveEntity("Teacher")
public final class Teacher extends Person {
    String post;
    String degree;
    String academicRank;
    LocalDate startedJobDate;
    int rate;
    @ToString.Exclude
    Department department;

    public Teacher(String id, String fullName, LocalDate birthDate, String email, String phone, String post, String degree, String academicRank, LocalDate startedJobDate, int rate) {
        super(id, fullName, birthDate, email, phone);
        this.post = post;
        this.degree = degree;
        this.academicRank = academicRank;
        this.startedJobDate = startedJobDate;
        this.rate = rate;
    }

    @Override
    public String toString() {
        return super.toString() + ", Department: " + (department != null ? department.getFullName() : "None") +
               ", Faculty: " + (department != null && department.getFaculty() != null ? department.getFaculty().getFullName() : "None") +
               ", University: " + (getUniversity() != null ? getUniversity().getFullName() : "None");
    }

    @ToString.Include
    public Faculty getFaculty() {
        return department != null ? department.getFaculty() : null;
    }

    @ToString.Include
    public University getUniversity() {
        return getFaculty() != null ? getFaculty().getUniversity() : null;
    }
}
