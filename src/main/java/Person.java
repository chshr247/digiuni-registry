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
    String lastName;
    String firstName;
    String patronymic;
    LocalDate birthDate;
    String email;
    String phone;
    @ReflectIgnore
    AuthUser authUser;

    public Person(String id, String lastName, String firstName, String patronymic,
                  LocalDate birthDate, String email, String phone) {
        this.id = id;
        this.lastName = lastName;
        this.firstName = firstName;
        this.patronymic = patronymic;
        this.birthDate = birthDate;
        this.email = email;
        this.phone = phone;
    }

    public String getFullName() {
        return lastName + " " + firstName + " " + patronymic;
    }

    public int getAge() {
        return Period.between(birthDate, LocalDate.now()).getYears();
    }
}