import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Faculty {
    String id;
    String fullName;
    String shortName;
    String dean;
    String contact;
    @lombok.ToString.Exclude
    ArrayList<Department> departments;
    @lombok.ToString.Exclude
    University university;

    public Faculty(String id, String fullName, String shortName, String dean, String contact) {
        this.id = id;
        this.fullName = fullName;
        this.shortName = shortName;
        this.dean = dean;
        this.contact = contact;
        this.departments = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Faculty{" +
                "id='" + id + '\'' +
                ", fullName='" + fullName + '\'' +
                ", shortName='" + shortName + '\'' +
                ", dean='" + dean + '\'' +
                ", contact='" + contact + '\'' +
                ", university=" + (university != null ? university.getFullName() : "None") +
                '}';
    }
}