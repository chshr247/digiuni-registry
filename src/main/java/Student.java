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
    Department department;

    public Student(String id, String fullName, LocalDate birthDate, String email, String phone, int grade, int group, int year, String formOfStudy, String status) {
        super(id, fullName, birthDate, email, phone);
        this.grade = grade;
        this.group = group;
        this.year = year;
        this.formOfStudy = formOfStudy;
        this.status = status;
    }
}
