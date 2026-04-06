import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Department{
    String id;
    String fullName;
    String head;
    int cabinet;
    @lombok.ToString.Exclude
    Faculty faculty;
    @lombok.ToString.Exclude
    ArrayList<Teacher> teachers;
    @lombok.ToString.Exclude
    ArrayList<Student> students;

    public Department(String id, String fullName, String head, int cabinet) {
        this.id = id;
        this.fullName = fullName;
        this.head = head;
        this.cabinet = cabinet;
        this.teachers = new ArrayList<>();
        this.students = new ArrayList<>();
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
                "id='" + id + '\'' +
                ", fullName='" + fullName + '\'' +
                ", head='" + head + '\'' +
                ", cabinet=" + cabinet +
                ", faculty=" + (faculty != null ? faculty.getFullName() : "None") +
                ", university=" + (getUniversity() != null ? getUniversity().getFullName() : "None") +
                '}';
    }

    public University getUniversity() {
        return faculty != null ? faculty.getUniversity() : null;
    }
}
