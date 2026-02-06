import java.util.Objects;

public class University {
    String universityName;

    public String getUniversityName() {
        return universityName;
    }

    @Override
    public String toString() {
        return "University{" +
                "universityName='" + universityName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        University that = (University) o;
        return Objects.equals(universityName, that.universityName);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(universityName);
    }

    public void setUniversityName(String universityName) {
        this.universityName = universityName;
    }

    public University(String name){
        this.universityName = name;

    }
}
