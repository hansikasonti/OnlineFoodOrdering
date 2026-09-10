import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        PrintWriter out = response.getWriter();

        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        try {

            Class.forName("com.mysql.jdbc.Driver");

            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/foodordering",
                    "root",
                    "1234"
            );

            String sql =
                    "INSERT INTO users (email, name, password) "
                    + "VALUES (?, ?, ?)";

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ps.setString(1, email);
            ps.setString(2, name);
            ps.setString(3, password);

            ps.executeUpdate();

            ps.close();
            con.close();

            out.println("<html>");
            out.println("<head><title>Registration</title></head>");
            out.println("<body>");

            out.println("<h1>Registration Successful!</h1>");

            out.println("<h3>Welcome " + name + "</h3>");

            out.println("<a href='login.html'>Login</a>");

            out.println("</body>");
            out.println("</html>");

        } catch (Exception e) {

            out.println("<h2>Error: "
                    + e.getMessage()
                    + "</h2>");
        }
    }
}