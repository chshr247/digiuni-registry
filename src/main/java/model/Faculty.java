package model;
import reflection.ReflectiveEntity;
import repository.Identifiable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ReflectiveEntity("Faculty")
public class Faculty implements Identifiable<String> {
    String id;
    String fullName;
    String shortName;
    @lombok.ToString.Exclude
    Teacher dean;           // посилання на викладача-декана
    String contact;
    @lombok.ToString.Exclude
    ArrayList<Department> departments;
    @lombok.ToString.Exclude
    University university;

    public Faculty(String id, String fullName, String shortName, String contact) {
        this.id = id;
        this.fullName = fullName;
        this.shortName = shortName;
        this.contact = contact;
        this.departments = new ArrayList<>();
    }

    public String getDeanName() {
        return dean != null ? dean.getFullName() : "Not assigned";
    }

    @Override
    public String toString() {
        return "Faculty{" +
                "id='" + id + "'" +
                ", fullName='" + fullName + "'" +
                ", shortName='" + shortName + "'" +
                ", dean=" + getDeanName() +
                ", contact='" + contact + "'" +
                ", university=" + (university != null ? university.getFullName() : "None") +
                "}";
    }
}