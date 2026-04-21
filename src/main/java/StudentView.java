public record StudentView(
        String id,
        String fullName,
        int grade,
        int group,
        int year,
        String formOfStudy,
        String status,
        String departmentName,
        String facultyName
) {
    public static StudentView from(Student s) {
        return new StudentView(
                s.getId(),
                s.getFullName(),
                s.getGrade(),
                s.getGroup(),
                s.getYear(),
                s.getFormOfStudy(),
                s.getStatus(),
                s.getDepartment() != null ? s.getDepartment().getFullName() : "None",
                s.getFaculty() != null ? s.getFaculty().getFullName() : "None"
        );
    }

    public String toDisplayString() {
        return String.format(
                "%-30s | grade %d, group %d | year %d | %s | %s | dept: %s | faculty: %s",
                fullName, grade, group, year, formOfStudy, status, departmentName, facultyName
        );
    }
}