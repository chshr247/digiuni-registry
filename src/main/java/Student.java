import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Getter
@Setter
public final class Student extends Person{
    int grade;
    int group;
    int year;
    String formOfStudy;
    String status;
    @ToString.Exclude
    Department department;

    public Student(String id, String fullName, LocalDate birthDate, String email, String phone, int grade, int group, int year, String formOfStudy, String status) {
        super(id, fullName, birthDate, email, phone);
        this.grade = grade;
        this.group = group;
        this.year = year;
        this.formOfStudy = formOfStudy;
        this.status = status;
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
