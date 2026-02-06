import java.util.Objects;

public class Teacher extends Person {
    String post;
    String degree;
    String academicRank;
    String startedJobDate;
    int rate;

    public Teacher(String id, String fullName, String birthDate, String email, String phone, String post, String degree, String academicRank, String startedJobDate, int rate) {
        super(id, fullName, birthDate, email, phone);
        this.post = post;
        this.degree = degree;
        this.academicRank = academicRank;
        this.startedJobDate = startedJobDate;
        this.rate = rate;
    }

    @Override
    public String toString() {
        return "Teacher{" +
                "phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", birthDate='" + birthDate + '\'' +
                ", fullName='" + fullName + '\'' +
                ", id='" + id + '\'' +
                ", rate=" + rate +
                ", startedJobDate='" + startedJobDate + '\'' +
                ", academicRank='" + academicRank + '\'' +
                ", degree='" + degree + '\'' +
                ", post='" + post + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Teacher teacher = (Teacher) o;
        return rate == teacher.rate && Objects.equals(post, teacher.post) && Objects.equals(degree, teacher.degree) && Objects.equals(academicRank, teacher.academicRank) && Objects.equals(startedJobDate, teacher.startedJobDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), post, degree, academicRank, startedJobDate, rate);
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public String getAcademicRank() {
        return academicRank;
    }

    public void setAcademicRank(String academicRank) {
        this.academicRank = academicRank;
    }

    public String getStartedJobDate() {
        return startedJobDate;
    }

    public void setStartedJobDate(String startedJobDate) {
        this.startedJobDate = startedJobDate;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }
}
