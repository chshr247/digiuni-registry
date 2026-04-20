import java.io.*;
import java.net.*;
import java.util.stream.Collectors;

public class UniversityServer implements Runnable {

    public static final int PORT = 8080;
    private volatile boolean running = true;
    private ServerSocket serverSocket;

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("[Server] Started at http://localhost:" + PORT);
            while (running) {
                try {
                    Socket client = serverSocket.accept();
                    Thread t = new Thread(new HttpHandler(client));
                    t.setDaemon(true);
                    t.start();
                } catch (SocketException e) {
                    if (running) System.out.println("[Server] Socket error: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("[Server] Failed to start: " + e.getMessage());
        }
    }

    public void stop() {
        running = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) serverSocket.close();
            System.out.println("[Server] Stopped.");
        } catch (IOException e) {
            System.out.println("[Server] Error on stop: " + e.getMessage());
        }
    }

    private static class HttpHandler implements Runnable {
        private final Socket socket;

        HttpHandler(Socket socket) { this.socket = socket; }

        @Override
        public void run() {
            try (
                    BufferedReader in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    OutputStream   out = socket.getOutputStream()
            ) {
                String requestLine = in.readLine();
                if (requestLine == null) return;

                String path = "/";
                String[] parts = requestLine.split(" ");
                if (parts.length >= 2) path = parts[1];

                String body = buildPage(path);
                byte[] bodyBytes = body.getBytes("UTF-8");

                String headers = "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: text/html; charset=UTF-8\r\n" +
                        "Content-Length: " + bodyBytes.length + "\r\n" +
                        "Connection: close\r\n\r\n";

                out.write(headers.getBytes("UTF-8"));
                out.write(bodyBytes);
                out.flush();
            } catch (IOException e) {
                System.out.println("[Server] Handler error: " + e.getMessage());
            } finally {
                try { socket.close(); } catch (IOException ignored) {}
            }
        }

        private String buildPage(String path) {
            String content = switch (path) {
                case "/students"    -> studentsTable();
                case "/teachers"    -> teachersTable();
                case "/faculties"   -> facultiesTable();
                case "/departments" -> departmentsTable();
                default             -> homePage();
            };

            return "<!DOCTYPE html><html><head><meta charset='UTF-8'>" +
                    "<title>DigiUni</title>" +
                    "<style>" +
                    "body{font-family:sans-serif;margin:0;background:#f5f5f5;color:#222}" +
                    "nav{background:#1a1a2e;padding:12px 24px;display:flex;gap:20px;align-items:center}" +
                    "nav a{color:#fff;text-decoration:none;font-size:14px;opacity:.85}" +
                    "nav a:hover{opacity:1}" +
                    "nav span{color:#fff;font-weight:600;font-size:16px;margin-right:16px}" +
                    "h2{margin:24px 24px 12px}" +
                    "table{border-collapse:collapse;margin:0 24px;background:#fff;border-radius:8px;overflow:hidden;box-shadow:0 1px 4px rgba(0,0,0,.1)}" +
                    "th{background:#1a1a2e;color:#fff;padding:10px 16px;text-align:left;font-size:13px}" +
                    "td{padding:9px 16px;border-bottom:1px solid #eee;font-size:13px}" +
                    "tr:last-child td{border-bottom:none}" +
                    "tr:hover td{background:#f9f9f9}" +
                    ".badge{padding:2px 8px;border-radius:4px;font-size:11px;font-weight:600}" +
                    ".budget{background:#e6f4ea;color:#2e7d32}" +
                    ".contract{background:#fff3e0;color:#e65100}" +
                    ".studying{background:#e3f2fd;color:#1565c0}" +
                    ".leave{background:#fff8e1;color:#f57f17}" +
                    ".deducted{background:#fce4ec;color:#c62828}" +
                    ".stat{display:inline-block;background:#fff;border-radius:8px;padding:16px 24px;margin:8px;box-shadow:0 1px 4px rgba(0,0,0,.1)}" +
                    ".stat-num{font-size:32px;font-weight:700;color:#1a1a2e}" +
                    ".stat-label{font-size:12px;color:#888;margin-top:2px}" +
                    ".home{padding:24px}" +
                    "</style></head><body>" +
                    "<nav><span>DigiUni</span>" +
                    "<a href='/'>Home</a>" +
                    "<a href='/students'>Students</a>" +
                    "<a href='/teachers'>Teachers</a>" +
                    "<a href='/faculties'>Faculties</a>" +
                    "<a href='/departments'>Departments</a>" +
                    "</nav>" +
                    content +
                    "</body></html>";
        }

        private String homePage() {
            long studentCount = CRUD.students.stream().filter(Student.class::isInstance).count();
            long teacherCount = CRUDForTeacher.teachers.size();
            long facultyCount = CRUDForFaculty.faculties.size();
            long deptCount    = CRUDForFaculty.faculties.stream()
                    .mapToLong(f -> f.getDepartments().size()).sum();

            return "<div class='home'><h2>University Registry</h2>" +
                    stat(studentCount, "Students") +
                    stat(teacherCount, "Teachers") +
                    stat(facultyCount, "Faculties") +
                    stat(deptCount,    "Departments") +
                    "</div>";
        }

        private String stat(long n, String label) {
            return "<div class='stat'><div class='stat-num'>" + n + "</div>" +
                    "<div class='stat-label'>" + label + "</div></div>";
        }

        private String studentsTable() {
            StringBuilder sb = new StringBuilder();
            sb.append("<h2>Students</h2><table>")
                    .append("<tr><th>#</th><th>Name</th><th>DOB</th><th>Age</th>")
                    .append("<th>Grade</th><th>Group</th><th>Enrolled</th>")
                    .append("<th>Form</th><th>Status</th><th>Department</th></tr>");

            CRUD.students.stream()
                    .filter(Student.class::isInstance)
                    .map(p -> (Student) p)
                    .sorted(java.util.Comparator.comparingInt(s -> Integer.parseInt(s.getId())))
                    .forEach(s -> sb.append("<tr>")
                            .append("<td>").append(s.getId()).append("</td>")
                            .append("<td>").append(esc(s.getFullName())).append("</td>")
                            .append("<td>").append(s.getBirthDateFormatted()).append("</td>")
                            .append("<td>").append(s.getAge()).append("</td>")
                            .append("<td>").append(s.getGrade()).append("</td>")
                            .append("<td>").append(s.getGroup()).append("</td>")
                            .append("<td>").append(s.getYear()).append("</td>")
                            .append("<td><span class='badge ").append(s.getFormOfStudy().toLowerCase()).append("'>")
                            .append(esc(s.getFormOfStudy())).append("</span></td>")
                            .append("<td><span class='badge ").append(statusClass(s.getStatus())).append("'>")
                            .append(esc(s.getStatus())).append("</span></td>")
                            .append("<td>").append(s.getDepartment() != null ? esc(s.getDepartment().getFullName()) : "—").append("</td>")
                            .append("</tr>"));

            sb.append("</table>");
            return sb.toString();
        }

        private String teachersTable() {
            StringBuilder sb = new StringBuilder();
            sb.append("<h2>Teachers</h2><table>")
                    .append("<tr><th>#</th><th>Name</th><th>Post</th><th>Degree</th>")
                    .append("<th>Rank</th><th>Started</th><th>Experience</th>")
                    .append("<th>Rate</th><th>Department</th></tr>");

            CRUDForTeacher.teachers.stream()
                    .sorted(java.util.Comparator.comparingInt(t -> Integer.parseInt(t.getId())))
                    .forEach(t -> sb.append("<tr>")
                            .append("<td>").append(t.getId()).append("</td>")
                            .append("<td>").append(esc(t.getFullName())).append("</td>")
                            .append("<td>").append(esc(t.getPost())).append("</td>")
                            .append("<td>").append(esc(t.getDegree())).append("</td>")
                            .append("<td>").append(esc(t.getAcademicRank())).append("</td>")
                            .append("<td>").append(t.getStartedJobDateFormatted()).append("</td>")
                            .append("<td>").append(t.getExperienceFormatted()).append("</td>")
                            .append("<td>").append(t.getRate()).append("</td>")
                            .append("<td>").append(t.getDepartment() != null ? esc(t.getDepartment().getFullName()) : "—").append("</td>")
                            .append("</tr>"));

            sb.append("</table>");
            return sb.toString();
        }

        private String facultiesTable() {
            StringBuilder sb = new StringBuilder();
            sb.append("<h2>Faculties</h2><table>")
                    .append("<tr><th>#</th><th>Name</th><th>Short</th><th>Dean</th>")
                    .append("<th>Contact</th><th>Departments</th><th>Students</th></tr>");

            CRUDForFaculty.faculties.forEach(f -> {
                long students = f.getDepartments().stream()
                        .mapToLong(d -> d.getStudents().size()).sum();
                sb.append("<tr>")
                        .append("<td>").append(f.getId()).append("</td>")
                        .append("<td>").append(esc(f.getFullName())).append("</td>")
                        .append("<td>").append(esc(f.getShortName())).append("</td>")
                        .append("<td>").append(esc(f.getDeanName())).append("</td>")
                        .append("<td>").append(esc(f.getContact())).append("</td>")
                        .append("<td>").append(f.getDepartments().size()).append("</td>")
                        .append("<td>").append(students).append("</td>")
                        .append("</tr>");
            });

            sb.append("</table>");
            return sb.toString();
        }

        private String departmentsTable() {
            StringBuilder sb = new StringBuilder();
            sb.append("<h2>Departments</h2><table>")
                    .append("<tr><th>#</th><th>Name</th><th>Head</th><th>Cabinet</th>")
                    .append("<th>Faculty</th><th>Teachers</th><th>Students</th></tr>");

            CRUDForFaculty.faculties.stream()
                    .flatMap(f -> f.getDepartments().stream())
                    .sorted(java.util.Comparator.comparingInt(d -> Integer.parseInt(d.getId())))
                    .forEach(d -> sb.append("<tr>")
                            .append("<td>").append(d.getId()).append("</td>")
                            .append("<td>").append(esc(d.getFullName())).append("</td>")
                            .append("<td>").append(esc(d.getHeadName())).append("</td>")
                            .append("<td>").append(d.getCabinet()).append("</td>")
                            .append("<td>").append(d.getFaculty() != null ? esc(d.getFaculty().getFullName()) : "—").append("</td>")
                            .append("<td>").append(d.getTeachers().size()).append("</td>")
                            .append("<td>").append(d.getStudents().size()).append("</td>")
                            .append("</tr>"));

            sb.append("</table>");
            return sb.toString();
        }

        private String statusClass(String status) {
            if (status == null) return "";
            return switch (status.toLowerCase()) {
                case "studying"       -> "studying";
                case "academic leave" -> "leave";
                case "deducted"       -> "deducted";
                default               -> "";
            };
        }

        private String esc(String s) {
            if (s == null) return "—";
            return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
        }
    }
}