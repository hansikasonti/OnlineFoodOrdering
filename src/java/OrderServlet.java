import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/OrderServlet")
public class OrderServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession();

        Map<String, Integer> cart =
                (Map<String, Integer>)
                session.getAttribute("cart");

        Map<String, Double> prices =
                (Map<String, Double>)
                session.getAttribute("prices");

        String name =
                (String) session.getAttribute("name");

        String email =
                (String) session.getAttribute("email");

        if (name == null) {
            name = "Guest";
        }

        if (cart == null || cart.isEmpty()) {

            out.println("<h2>Your cart is empty.</h2>");
            out.println("<a href='foods.html'>View Food Items</a>");

            return;
        }

        try {

            Class.forName("com.mysql.jdbc.Driver");

            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/foodordering",
                    "root",
                    "1234"
            );

            String idSql =
                    "SELECT IFNULL(MAX(order_group_id), 0) + 1 "
                    + "AS new_id FROM orders";

            PreparedStatement idPs =
                    con.prepareStatement(idSql);

            ResultSet idRs =
                    idPs.executeQuery();

            int orderGroupId = 1;

            if (idRs.next()) {
                orderGroupId =
                        idRs.getInt("new_id");
            }

            idRs.close();
            idPs.close();

            String sql =
                    "INSERT INTO orders "
                    + "(email, food_name, quantity, total, order_group_id) "
                    + "VALUES (?, ?, ?, ?, ?)";

            PreparedStatement ps =
                    con.prepareStatement(sql);

            double grandTotal = 0;

            for (String food : cart.keySet()) {

                int quantity = cart.get(food);

                double price = prices.get(food);

                double subtotal = price * quantity;

                grandTotal += subtotal;

                ps.setString(1, email);
                ps.setString(2, food);
                ps.setInt(3, quantity);
                ps.setDouble(4, subtotal);
                ps.setInt(5, orderGroupId);

                ps.executeUpdate();
            }

            ps.close();
            con.close();

            out.println("<html>");
            out.println("<head>");
            out.println("<meta charset='UTF-8'>");
            out.println("<title>Order Confirmation</title>");
            out.println("</head>");

            out.println("<body>");

            out.println("<h1>Order Placed Successfully!</h1>");

            out.println("<h3>Order ID: "
                    + orderGroupId
                    + "</h3>");

            out.println("<h3>Customer Name: "
                    + name
                    + "</h3>");

            out.println("<h2>Order Details</h2>");

            out.println("<table border='1' cellpadding='10'>");

            out.println("<tr>");
            out.println("<th>Food</th>");
            out.println("<th>Quantity</th>");
            out.println("<th>Subtotal</th>");
            out.println("</tr>");

            for (String food : cart.keySet()) {

                int quantity = cart.get(food);

                double price = prices.get(food);

                double subtotal = price * quantity;

                out.println("<tr>");

                out.println("<td>" + food + "</td>");

                out.println("<td>" + quantity + "</td>");

                out.println("<td>Rs." + subtotal + "</td>");

                out.println("</tr>");
            }

            out.println("</table>");

            out.println("<h2>Total: Rs."
                    + grandTotal
                    + "</h2>");

            session.removeAttribute("cart");
            session.removeAttribute("prices");

            out.println("<br>");

            out.println("<a href='foods.html'>Order More Food</a>");

            out.println("<br><br>");

            out.println("<a href='index.html'>Home</a>");

            out.println("</body>");
            out.println("</html>");

        } catch (Exception e) {

            out.println("<h2>Error: "
                    + e.getMessage()
                    + "</h2>");
        }
    }
}