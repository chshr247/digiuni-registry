import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Getter
@Setter
public final class Teacher extends Person {
    String post;
    String degree;
    String academicRank;
    LocalDate startedJobDate;
    int rate;
    Department department;

    public Teacher(String id, String fullName, LocalDate birthDate, String email, String phone, String post, String degree, String academicRank, LocalDate startedJobDate, int rate) {
        super(id, fullName, birthDate, email, phone);
        this.post = post;
        this.degree = degree;
        this.academicRank = academicRank;
        this.startedJobDate = startedJobDate;
        this.rate = rate;
    }
}
