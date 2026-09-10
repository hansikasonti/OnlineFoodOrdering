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

        PrintWriter out =
                response.getWriter();

        HttpSession session =
                request.getSession();

        Map<String, Integer> cart =
                (Map<String, Integer>)
                session.getAttribute("cart");

        Map<String, Double> prices =
                (Map<String, Double>)
                session.getAttribute("prices");

        Map<String, String> foodNames =
                (Map<String, String>)
                session.getAttribute("foodNames");

        Map<String, String> cartRestaurants =
                (Map<String, String>)
                session.getAttribute("cartRestaurants");


        String name =
                (String) session.getAttribute("name");


        if (cart == null || cart.isEmpty()) {

            out.println("<html>");
            out.println("<body>");

            out.println("<h2>Your cart is empty.</h2>");

            out.println("<a href='foods.html'>Select Restaurant</a>");

            out.println("<br><br>");

            out.println("<a href='index.html'>Home</a>");

            out.println("</body>");
            out.println("</html>");

            return;
        }


        double grandTotal = 0;


        for (String cartKey : cart.keySet()) {

            int quantity =
                    cart.get(cartKey);

            double price =
                    prices.get(cartKey);

            grandTotal =
                    grandTotal + (price * quantity);
        }


        out.println("<html>");

        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<title>Payment</title>");
        out.println("</head>");

        out.println("<body>");

        out.println("<h1>💳 PAYMENT</h1>");

        out.println("<h3>Customer Name: "
                + name
                + "</h3>");


        out.println("<h3>Restaurants in this Order:</h3>");

        String currentRestaurant = "";

        for (String cartKey : cart.keySet()) {

            String restaurant =
                    cartRestaurants.get(cartKey);

            if (!restaurant.equals(currentRestaurant)) {

                out.println("<p>"
                        + restaurant
                        + "</p>");

                currentRestaurant =
                        restaurant;
            }
        }


        out.println("<h2>Order Total: Rs."
                + grandTotal
                + "</h2>");

        out.println("<br>");


        out.println("<form action='OrderServlet' method='post'>");

        out.println("<h3>Select Payment Method</h3>");


        out.println("<input type='radio' "
                + "name='payment' "
                + "value='Cash on Delivery' "
                + "required> Cash on Delivery");

        out.println("<br><br>");


        out.println("<input type='radio' "
                + "name='payment' "
                + "value='UPI'> UPI");

        out.println("<br><br>");


        out.println("<input type='radio' "
                + "name='payment' "
                + "value='Card'> Card");

        out.println("<br><br><br>");


        out.println("<input type='submit' "
                + "value='Confirm Payment'>");

        out.println("</form>");


        out.println("<br>");

        out.println("<a href='CartServlet'>Back to Cart</a>");

        out.println("<br><br>");

        out.println("<a href='index.html'>Home</a>");

        out.println("</body>");

        out.println("</html>");
    }


    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        PrintWriter out =
                response.getWriter();


        String payment =
                request.getParameter("payment");


        HttpSession session =
                request.getSession();


        Map<String, Integer> cart =
                (Map<String, Integer>)
                session.getAttribute("cart");

        Map<String, Double> prices =
                (Map<String, Double>)
                session.getAttribute("prices");

        Map<String, String> foodNames =
                (Map<String, String>)
                session.getAttribute("foodNames");

        Map<String, String> cartRestaurants =
                (Map<String, String>)
                session.getAttribute("cartRestaurants");

        Map<String, Integer> cartRestaurantIds =
                (Map<String, Integer>)
                session.getAttribute("cartRestaurantIds");


        String name =
                (String) session.getAttribute("name");

        String email =
                (String) session.getAttribute("email");


        if (cart == null || cart.isEmpty()) {

            out.println("<html>");
            out.println("<body>");

            out.println("<h2>Your cart is empty.</h2>");

            out.println("<a href='foods.html'>Select Restaurant</a>");

            out.println("</body>");
            out.println("</html>");

            return;
        }


        try {

            Class.forName("com.mysql.jdbc.Driver");


            Connection con =
                    DriverManager.getConnection(
                            "jdbc:mysql://localhost:3306/foodordering",
                            "root",
                            "1234"
                    );


            /*
             * Generate the next Order ID.
             */

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


            /*
             * Insert each food item.
             */

            String sql =
                    "INSERT INTO orders "
                    + "(email, food_name, quantity, total, "
                    + "order_group_id, restaurant_id) "
                    + "VALUES (?, ?, ?, ?, ?, ?)";


            PreparedStatement ps =
                    con.prepareStatement(sql);


            double grandTotal = 0;


            for (String cartKey : cart.keySet()) {

                String food =
                        foodNames.get(cartKey);

                String restaurant =
                        cartRestaurants.get(cartKey);

                int restaurantId =
                        cartRestaurantIds.get(cartKey);

                int quantity =
                        cart.get(cartKey);

                double price =
                        prices.get(cartKey);

                double subtotal =
                        price * quantity;


                grandTotal =
                        grandTotal + subtotal;


                ps.setString(1, email);

                ps.setString(2, food);

                ps.setInt(3, quantity);

                ps.setDouble(4, subtotal);

                ps.setInt(5, orderGroupId);

                ps.setInt(6, restaurantId);

                ps.executeUpdate();
            }


            ps.close();

            con.close();


            /*
             * ORDER CONFIRMATION
             */

            out.println("<html>");

            out.println("<head>");
            out.println("<meta charset='UTF-8'>");
            out.println("<title>Order Confirmation</title>");
            out.println("</head>");

            out.println("<body>");


            out.println("<h1>✅ PAYMENT SUCCESSFUL!</h1>");

            out.println("<h2>ORDER PLACED SUCCESSFULLY!</h2>");


            out.println("<h3>Order ID: "
                    + orderGroupId
                    + "</h3>");


            out.println("<h3>Customer Name: "
                    + name
                    + "</h3>");


            out.println("<h3>Payment Method: "
                    + payment
                    + "</h3>");


            out.println("<h3>Amount Paid: Rs."
                    + grandTotal
                    + "</h3>");


            out.println("<br>");


            out.println("<h2>Order Details</h2>");


            out.println("<table border='1' "
                    + "cellpadding='10' "
                    + "cellspacing='0'>");


            out.println("<tr>");

            out.println("<th>Restaurant</th>");

            out.println("<th>Food</th>");

            out.println("<th>Quantity</th>");

            out.println("<th>Subtotal</th>");

            out.println("</tr>");


            for (String cartKey : cart.keySet()) {

                String restaurant =
                        cartRestaurants.get(cartKey);

                String food =
                        foodNames.get(cartKey);

                int quantity =
                        cart.get(cartKey);

                double price =
                        prices.get(cartKey);

                double subtotal =
                        price * quantity;


                out.println("<tr>");

                out.println("<td>"
                        + restaurant
                        + "</td>");

                out.println("<td>"
                        + food
                        + "</td>");

                out.println("<td>"
                        + quantity
                        + "</td>");

                out.println("<td>Rs."
                        + subtotal
                        + "</td>");

                out.println("</tr>");
            }


            out.println("</table>");


            out.println("<br>");


            out.println("<h2>Total: Rs."
                    + grandTotal
                    + "</h2>");


            /*
             * Clear cart after successful order.
             */

            session.removeAttribute("cart");

            session.removeAttribute("prices");

            session.removeAttribute("foodNames");

            session.removeAttribute("cartRestaurants");

            session.removeAttribute("cartRestaurantIds");


            out.println("<br>");

            out.println("<p><b>Thank you for ordering!</b></p>");


            out.println("<br>");

            out.println("<a href='foods.html'>Order More Food</a>");

            out.println("<br><br>");

            out.println("<a href='index.html'>Home</a>");


            out.println("</body>");

            out.println("</html>");


        } catch (Exception e) {

            out.println("<html>");

            out.println("<body>");

            out.println("<h2>Error: "
                    + e.getMessage()
                    + "</h2>");

            out.println("<br>");

            out.println("<a href='CartServlet'>Back to Cart</a>");

            out.println("</body>");

            out.println("</html>");
        }
    }
}