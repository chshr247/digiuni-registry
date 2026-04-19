import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Getter
@Setter
@ReflectiveEntity("Student")
public final class Student extends Person {
    int grade;
    int group;
    int year;
    String formOfStudy;
    String status;
    @ToString.Exclude
    Department department;

    public Student(String id, String lastName, String firstName, String patronymic,
                   LocalDate birthDate, String email, String phone,
                   int grade, int group, int year, String formOfStudy, String status) {
        super(id, lastName, firstName, patronymic, birthDate, email, phone);
        this.grade = grade;
        this.group = group;
        this.year = year;
        this.formOfStudy = formOfStudy;
        this.status = status;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id='" + getId() + "'" +
                ", name='" + getFullName() + "'" +
                ", grade=" + grade +
                ", group=" + group +
                ", year=" + year +
                ", form='" + formOfStudy + "'" +
                ", status='" + status + "'" +
                ", department=" + (department != null ? department.getFullName() : "None") +
                ", faculty=" + (getFaculty() != null ? getFaculty().getFullName() : "None") +
                ", university=" + (getUniversity() != null ? getUniversity().getFullName() : "None") +
                "}";
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