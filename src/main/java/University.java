import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class University {
    String fullName;
    String shortName;
    String city;
    String address;
    @lombok.ToString.Exclude
    ArrayList<Faculty> faculties;

    public University(String fullName, String shortName, String city, String address) {
        this.fullName = fullName;
        this.shortName = shortName;
        this.city = city;
        this.address = address;
        this.faculties = new ArrayList<>();
    }

    public void addFaculty(Faculty faculty) {
        if (faculty != null && !faculties.contains(faculty)) {
            faculties.add(faculty);
            faculty.setUniversity(this);
        }
    }

    public void removeFaculty(Faculty faculty) {
        if (faculties.remove(faculty)) {
            faculty.setUniversity(null);
        }
    }
}
