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

@WebServlet("/AdminServlet")
public class AdminServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        PrintWriter out = response.getWriter();

        out.println("<html>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<title>Admin Orders</title>");
        out.println("</head>");

        out.println("<body>");


        out.println("<h1>⚙️ CUSTOMER ORDERS</h1>");

        try {

            Class.forName("com.mysql.jdbc.Driver");

            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/foodordering",
                    "root",
                    "1234"
            );

            String sql =
                    "SELECT users.name, "
                    + "orders.email, "
                    + "orders.order_group_id, "
                    + "orders.food_name, "
                    + "orders.quantity, "
                    + "orders.total "
                    + "FROM orders "
                    + "JOIN users "
                    + "ON orders.email = users.email "
                    + "ORDER BY orders.order_group_id ASC";

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ResultSet rs =
                    ps.executeQuery();

            int displayOrderId = 1;

            int currentOrderGroupId = -1;

            double orderTotal = 0;

            boolean hasOrders = false;

            while (rs.next()) {

                hasOrders = true;

                int groupId =
                        rs.getInt("order_group_id");

                if (groupId != currentOrderGroupId) {

                    if (currentOrderGroupId != -1) {

                        out.println("</table>");

                        out.println("<h3>Order Total: Rs."
                                + orderTotal
                                + "</h3>");

                        out.println("<br><br>");
                    }

                    currentOrderGroupId = groupId;

                    orderTotal = 0;

                    out.println("<h2>Order ID: "
                            + displayOrderId
                            + "</h2>");

                    out.println("<p><b>Customer Name:</b> "
                            + rs.getString("name")
                            + "</p>");

                    out.println("<p><b>Email:</b> "
                            + rs.getString("email")
                            + "</p>");

                    out.println("<table border='1' "
                            + "cellpadding='10' "
                            + "cellspacing='0'>");

                    out.println("<tr>");

                    out.println("<th>Food</th>");
                    out.println("<th>Quantity</th>");
                    out.println("<th>Total</th>");

                    out.println("</tr>");

                    displayOrderId++;
                }

                double foodTotal =
                        rs.getDouble("total");

                orderTotal =
                        orderTotal + foodTotal;

                out.println("<tr>");

                out.println("<td>"
                        + rs.getString("food_name")
                        + "</td>");

                out.println("<td>"
                        + rs.getInt("quantity")
                        + "</td>");

                out.println("<td>Rs."
                        + foodTotal
                        + "</td>");

                out.println("</tr>");
            }

            if (currentOrderGroupId != -1) {

                out.println("</table>");

                out.println("<h3>Order Total: Rs."
                        + orderTotal
                        + "</h3>");
            }

            if (!hasOrders) {

                out.println("<h2>No orders found.</h2>");
            }

            rs.close();
            ps.close();
            con.close();

        } catch (Exception e) {

            out.println("<h2>Error: "
                    + e.getMessage()
                    + "</h2>");
        }

        out.println("<br>");

        out.println("<a href='index.html'>Home</a>");

       

        out.println("</body>");
        out.println("</html>");
    }
}