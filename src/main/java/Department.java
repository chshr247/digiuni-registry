import java.util.Objects;

public class Department extends Faculty{
    String departmentName;

    @Override
    public String toString() {
        return "Department{" +
                "departmentName='" + departmentName + '\'' +
                ", facultyName='" + facultyName + '\'' +
                ", universityName='" + universityName + '\'' +
                '}';
    }

    public Department(String departmentName) {
        super(departmentName);
        this.departmentName = departmentName;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Department that = (Department) o;
        return Objects.equals(departmentName, that.departmentName);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(departmentName);
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }
}
