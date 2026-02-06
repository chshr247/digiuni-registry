import java.util.Objects;

public class Department{
    String id;
    String fullName;
    String head;
    int cabinet;

    public Department(String id, String fullName, String head, int cabinet) {
        this.id = id;
        this.fullName = fullName;
        this.head = head;
        this.cabinet = cabinet;
    }

    @Override
    public String toString() {
        return "Department{" +
                "id='" + id + '\'' +
                ", fullName='" + fullName + '\'' +
                ", head='" + head + '\'' +
                ", cabinet=" + cabinet +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Department that = (Department) o;
        return cabinet == that.cabinet && Objects.equals(id, that.id) && Objects.equals(fullName, that.fullName) && Objects.equals(head, that.head);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fullName, head, cabinet);
    }

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

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public int getCabinet() {
        return cabinet;
    }

    public void setCabinet(int cabinet) {
        this.cabinet = cabinet;
    }
}
