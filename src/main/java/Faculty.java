import java.util.Objects;

public class Faculty extends University{
    String facultyName;

    public String getFacultyName() {
        return facultyName;
    }

    @Override
    public String toString() {
        return "Faculty{" +
                "facultyName='" + facultyName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Faculty faculty = (Faculty) o;
        return Objects.equals(facultyName, faculty.facultyName);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(facultyName);
    }

    public void setFacultyName(String facultyName) {
        this.facultyName = facultyName;
    }

    public Faculty(String name){
        super(name);
        this.facultyName = name;
    }
}
