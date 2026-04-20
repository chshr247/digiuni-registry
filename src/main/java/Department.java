import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ReflectiveEntity("Department")
public class Department implements Identifiable<String> {
    String id;
    String fullName;
    @lombok.ToString.Exclude
    Teacher head;           // посилання на завідувача кафедри
    int cabinet;
    @lombok.ToString.Exclude
    Faculty faculty;
    @lombok.ToString.Exclude
    ArrayList<Teacher> teachers;
    @lombok.ToString.Exclude
    ArrayList<Student> students;

    public Department(String id, String fullName, int cabinet) {
        this.id = id;
        this.fullName = fullName;
        this.cabinet = cabinet;
        this.teachers = new ArrayList<>();
        this.students = new ArrayList<>();
    }

    public String getHeadName() {
        return head != null ? head.getFullName() : "Not assigned";
    }

    public void addTeacher(Teacher teacher) {
        if (teacher != null && !teachers.contains(teacher)) {
            teachers.add(teacher);
            teacher.setDepartment(this);
        }
    }

    public void removeTeacher(Teacher teacher) {
        if (teachers.remove(teacher)) {
            teacher.setDepartment(null);
        }
    }

    public void addStudent(Student student) {
        if (student != null && !students.contains(student)) {
            students.add(student);
            student.setDepartment(this);
        }
    }

    public void removeStudent(Student student) {
        if (students.remove(student)) {
            student.setDepartment(null);
        }
    }

    @Override
    public String toString() {
        return "Department{" +
                "id='" + id + "'" +
                ", fullName='" + fullName + "'" +
                ", head=" + getHeadName() +
                ", cabinet=" + cabinet +
                ", faculty=" + (faculty != null ? faculty.getFullName() : "None") +
                ", university=" + (getUniversity() != null ? getUniversity().getFullName() : "None") +
                "}";
    }

    public University getUniversity() {
        return faculty != null ? faculty.getUniversity() : null;
    }
}