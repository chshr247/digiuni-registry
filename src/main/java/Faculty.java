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
    ArrayList<Department> departments;
    University university;

    public Faculty(String id, String fullName, String shortName, String dean, String contact) {
        this.id = id;
        this.fullName = fullName;
        this.shortName = shortName;
        this.dean = dean;
        this.contact = contact;
        this.departments = new ArrayList<>();
    }
}