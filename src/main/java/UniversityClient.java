import java.io.*;
import java.net.*;

public class UniversityClient {

    private static final String BASE_URL = "http://localhost:" + UniversityServer.PORT;

    public static String get(String path) {
        try {
            URL url = new URI(BASE_URL + path).toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(2000);
            conn.setReadTimeout(2000);
            try (BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) sb.append(line).append("\n");
                return sb.toString();
            }
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}