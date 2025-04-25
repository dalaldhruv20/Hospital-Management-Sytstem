import java.io.IOException;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class CreateAssistant
 */
@WebServlet("/CreateAssistant")
public class CreateAssistant extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public CreateAssistant() {
        super();
    }

    /**
     * Handles POST requests to create a new assistant.
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String pwd = request.getParameter("pwd");
        String joindate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        PrintWriter out = response.getWriter();

        // Validate inputs
        if (name == null || name.isEmpty() || email == null || email.isEmpty() || phone == null || phone.isEmpty()) {
            sendErrorMessage(out, "newAssistant.html");
            return;
        }

        // Database connection and insertion
        try (Connection c = DriverManager.getConnection("jdbc:mysql://localhost:3306/hospital", "root", "root");
             PreparedStatement ps = c.prepareStatement(
                     "INSERT INTO assistant(name, email, phone, joindate, password) VALUES (?, ?, ?, ?, ?)")) {

            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, phone);
            ps.setString(4, joindate);
            ps.setString(5, hashPassword(pwd)); // Hashing the password

            int successCount = ps.executeUpdate();

            if (successCount == 1) {
                response.sendRedirect("login.html");
            } else {
                sendErrorMessage(out, "newAssistant.html");
            }
        } catch (SQLException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            sendErrorMessage(out, "newAssistant.html");
        }
    }

    /**
     * Hashes the password using SHA-256.
     */
    private String hashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(password.getBytes());
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }

    /**
     * Sends an error message to the client with a redirect to the specified URL.
     */
    private void sendErrorMessage(PrintWriter out, String redirectURL) {
        out.println("<br><br><br><h1 align=center><font color=\"red\">TRY AGAIN<br>REDIRECTING BACK TO REGISTRATION PAGE</font></h1>");
        out.println("<script type=\"text/javascript\">");
        out.println("setTimeout(() => location.href = '" + redirectURL + "', 5000);");
        out.println("</script>");
    }
}
