package model;
import auth.AuthUser;
import reflection.ReflectIgnore;
import repository.Identifiable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor
@AllArgsConstructor
public sealed class Person implements Identifiable<String> permits Student, Teacher {
    String id;
    String lastName;
    String firstName;
    String patronymic;
    LocalDate birthDate;
    String email;
    String phone;
    @ReflectIgnore
    AuthUser authUser;
    @ReflectIgnore
    LocalDateTime createdAt;

    public Person(String id, String lastName, String firstName, String patronymic,
                  LocalDate birthDate, String email, String phone) {
        this.id = id;
        this.lastName = lastName;
        this.firstName = firstName;
        this.patronymic = patronymic;
        this.birthDate = birthDate;
        this.email = email;
        this.phone = phone;
        this.createdAt = LocalDateTime.now();
    }

    public String getFullName() {
        return lastName + " " + firstName + " " + patronymic;
    }

    public int getAge() {
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    public String getBirthDateFormatted() {
        return birthDate == null ? "N/A"
                : birthDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    public String getCreatedAtFormatted() {
        return createdAt == null ? "N/A"
                : createdAt.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
    }
}