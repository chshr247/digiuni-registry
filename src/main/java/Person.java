import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.Period;

@Data
@NoArgsConstructor
@AllArgsConstructor
public sealed class Person permits Student, Teacher {
    String id;
    String fullName;
    LocalDate birthDate;
    String email;
    String phone;
    AuthUser authUser;

    public Person(String id, String fullName, LocalDate birthDate, String email, String phone) {
        this.id = id;
        this.fullName = fullName;
        this.birthDate = birthDate;
        this.email = email;
        this.phone = phone;
    }

    public int getAge() {
        return Period.between(birthDate, LocalDate.now()).getYears();
    }
}
