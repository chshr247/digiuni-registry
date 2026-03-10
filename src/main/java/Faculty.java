import java.util.ArrayList;
import java.util.Objects;

public class Faculty {
    String id;
    String fullName;
    String shortName;
    String dean;
    String contact;
    ArrayList<Department> departments;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getDean() {
        return dean;
    }

    public void setDean(String dean) {
        this.dean = dean;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public ArrayList<Department> getDepartments() {
        return departments;
    }

    public void setDepartments(ArrayList<Department> departments) {
        this.departments = departments;
    }

    public Faculty(String id, String fullName, String shortName, String dean, String contact) {
        this.id = id;
        this.fullName = fullName;
        this.shortName = shortName;
        this.dean = dean;
        this.contact = contact;
        this.departments = new ArrayList<>();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Faculty faculty = (Faculty) o;
        return Objects.equals(id, faculty.id) && Objects.equals(fullName, faculty.fullName) && Objects.equals(shortName, faculty.shortName) && Objects.equals(dean, faculty.dean) && Objects.equals(contact, faculty.contact) && Objects.equals(departments, faculty.departments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fullName, shortName, dean, contact, departments);
    }

    @Override
    public String toString() {
        return "Faculty{" +
                "id='" + id + '\'' +
                ", fullName='" + fullName + '\'' +
                ", shortName='" + shortName + '\'' +
                ", dean='" + dean + '\'' +
                ", contact='" + contact + '\'' +
                ", departmentsCount=" + departments.size() +
                '}';
    }
}