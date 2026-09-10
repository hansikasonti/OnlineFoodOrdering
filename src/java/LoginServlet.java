import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        PrintWriter out = response.getWriter();

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
                    "SELECT name FROM users "
                    + "WHERE email = ? AND password = ?";

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ps.setString(1, email);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                String name = rs.getString("name");

                HttpSession session =
                        request.getSession();

                session.setAttribute("name", name);
                session.setAttribute("email", email);

                out.println("<html>");
                out.println("<head><title>Login</title></head>");
                out.println("<body>");

                out.println("<h1>Login Successful!</h1>");

                out.println("<h3>Welcome "
                        + name
                        + "</h3>");

                out.println("<a href='foods.html'>View Food Items</a>");

                out.println("<br><br>");

                out.println("<a href='index.html'>Home</a>");

                out.println("</body>");
                out.println("</html>");

            } else {

                out.println("<h2>Invalid Email or Password</h2>");

                out.println("<a href='login.html'>Try Again</a>");
            }

            rs.close();
            ps.close();
            con.close();

        } catch (Exception e) {

            out.println("<h2>Error: "
                    + e.getMessage()
                    + "</h2>");
        }
    }
}