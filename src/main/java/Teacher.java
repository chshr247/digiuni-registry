import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.Period;

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

    public Teacher(String id, String lastName, String firstName, String patronymic,
                   LocalDate birthDate, String email, String phone,
                   String post, String degree, String academicRank,
                   LocalDate startedJobDate, int rate) {
        super(id, lastName, firstName, patronymic, birthDate, email, phone);
        this.post = post;
        this.degree = degree;
        this.academicRank = academicRank;
        this.startedJobDate = startedJobDate;
        this.rate = rate;
    }

    /** Стаж роботи в роках */
    public int getExperience() {
        return Period.between(startedJobDate, LocalDate.now()).getYears();
    }

    @Override
    public String toString() {
        return "Teacher{" +
                "id='" + getId() + "'" +
                ", name='" + getFullName() + "'" +
                ", post='" + post + "'" +
                ", degree='" + degree + "'" +
                ", rank='" + academicRank + "'" +
                ", experience=" + getExperience() + "y" +
                ", rate=" + rate +
                ", department=" + (department != null ? department.getFullName() : "None") +
                ", faculty=" + (getFaculty() != null ? getFaculty().getFullName() : "None") +
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