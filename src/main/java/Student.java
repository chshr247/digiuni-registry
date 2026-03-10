import java.time.LocalDate;
import java.util.Objects;

public class Student extends Person{
    int grade;
    int group;
    int year;
    String formOfStudy;
    String status;

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public int getGroup() {
        return group;
    }

    public void setGroup(int group) {
        this.group = group;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getFormOfStudy() {
        return formOfStudy;
    }

    public void setFormOfStudy(String formOfStudy) {
        this.formOfStudy = formOfStudy;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Student student = (Student) o;
        return grade == student.grade && group == student.group && year == student.year && Objects.equals(formOfStudy, student.formOfStudy) && Objects.equals(status, student.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), grade, group, year, formOfStudy, status);
    }

    @Override
    public String toString() {
        return "Student{" +
                "phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", birthDate='" + birthDate + '\'' +
                ", fullName='" + fullName + '\'' +
                ", id='" + id + '\'' +
                ", status='" + status + '\'' +
                ", formOfStudy='" + formOfStudy + '\'' +
                ", year=" + year +
                ", group=" + group +
                ", grade=" + grade +
                '}';
    }

    public Student(String id, String fullName, LocalDate birthDate, String email, String phone, int grade, int group, int year, String formOfStudy, String status) {
        super(id, fullName, birthDate, email, phone);
        this.grade = grade;
        this.group = group;
        this.year = year;
        this.formOfStudy = formOfStudy;
        this.status = status;
    }
}
