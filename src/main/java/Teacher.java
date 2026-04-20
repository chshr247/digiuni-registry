import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Getter
@Setter
@ReflectiveEntity("Teacher")
public final class Teacher extends Person implements Identifiable<String> {
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

    public int getExperienceYears() {
        return Period.between(startedJobDate, LocalDate.now()).getYears();
    }

    public int getExperienceMonths() {
        return Period.between(startedJobDate, LocalDate.now()).getMonths();
    }

    public String getExperienceFormatted() {
        Period p = Period.between(startedJobDate, LocalDate.now());
        return p.getYears() + " yr. " + p.getMonths() + " mo.";
    }

    public String getStartedJobDateFormatted() {
        return startedJobDate == null ? "N/A"
                : startedJobDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    public int getExperience() {
        return getExperienceYears();
    }

    @Override
    public String toString() {
        return "Teacher{" +
                "id='" + getId() + "'" +
                ", name='" + getFullName() + "'" +
                ", age=" + getAge() +
                ", post='" + post + "'" +
                ", degree='" + degree + "'" +
                ", rank='" + academicRank + "'" +
                ", started=" + getStartedJobDateFormatted() +
                ", experience=" + getExperienceFormatted() +
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