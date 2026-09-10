import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/CartServlet")
public class CartServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        PrintWriter out = response.getWriter();

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


        out.println("<html>");

        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<title>Shopping Cart</title>");
        out.println("</head>");

        out.println("<body>");

        out.println("<h1>🛒 YOUR SHOPPING CART</h1>");


        if (cart == null || cart.isEmpty()) {

            out.println("<h2>Your cart is empty.</h2>");

            out.println("<a href='foods.html'>Select Restaurant</a>");

        } else {

            String currentRestaurant = "";

            double grandTotal = 0;

            for (String cartKey : cart.keySet()) {

                String restaurant =
                        cartRestaurants.get(cartKey);

                /*
                 * Print restaurant heading whenever
                 * restaurant changes.
                 */

                if (!restaurant.equals(currentRestaurant)) {

                    if (!currentRestaurant.equals("")) {

                        out.println("</table>");

                        out.println("<br>");
                    }

                    currentRestaurant =
                            restaurant;

                    out.println("<h2>Restaurant: "
                            + restaurant
                            + "</h2>");

                    out.println("<table border='1' "
                            + "cellpadding='10' "
                            + "cellspacing='0'>");

                    out.println("<tr>");

                    out.println("<th>Food</th>");
                    out.println("<th>Price</th>");
                    out.println("<th>Quantity</th>");
                    out.println("<th>Subtotal</th>");

                    out.println("</tr>");
                }


                String food =
                        foodNames.get(cartKey);

                int quantity =
                        cart.get(cartKey);

                double price =
                        prices.get(cartKey);

                double subtotal =
                        price * quantity;

                grandTotal =
                        grandTotal + subtotal;


                out.println("<tr>");

                out.println("<td>"
                        + food
                        + "</td>");

                out.println("<td>Rs."
                        + price
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

            out.println("<br>");

            out.println("<a href='OrderServlet'>Place Order</a>");

            out.println("<br><br>");

            out.println("<a href='foods.html'>Choose Another Restaurant</a>");
        }


        out.println("<br><br>");

        out.println("<a href='index.html'>Home</a>");

        out.println("</body>");

        out.println("</html>");
    }
}