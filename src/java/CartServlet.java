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

        HttpSession session = request.getSession();

        Map<String, Integer> cart =
                (Map<String, Integer>)
                session.getAttribute("cart");

        Map<String, Double> prices =
                (Map<String, Double>)
                session.getAttribute("prices");

        out.println("<html>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<title>Shopping Cart</title>");
        out.println("</head>");

        out.println("<body>");

        out.println("<h1>Your Shopping Cart</h1>");

        if (cart == null || cart.isEmpty()) {

            out.println("<h2>Your cart is empty.</h2>");

            out.println("<a href='foods.html'>View Food Items</a>");

        } else {

            out.println("<table border='1' cellpadding='10'>");

            out.println("<tr>");
            out.println("<th>Food</th>");
            out.println("<th>Price</th>");
            out.println("<th>Quantity</th>");
            out.println("<th>Subtotal</th>");
            out.println("</tr>");

            double grandTotal = 0;

            for (String food : cart.keySet()) {

                int quantity = cart.get(food);

                double price = prices.get(food);

                double subtotal = price * quantity;

                grandTotal += subtotal;

                out.println("<tr>");

                out.println("<td>" + food + "</td>");

                out.println("<td>Rs." + price + "</td>");

                out.println("<td>" + quantity + "</td>");

                out.println("<td>Rs." + subtotal + "</td>");

                out.println("</tr>");
            }

            out.println("</table>");

            out.println("<h2>Total: Rs."
                    + grandTotal
                    + "</h2>");

            out.println("<a href='OrderServlet'>Place Order</a>");

            out.println("<br><br>");

            out.println("<a href='foods.html'>Continue Shopping</a>");
        }

        out.println("<br><br>");

        out.println("<a href='index.html'>Home</a>");

        out.println("</body>");
        out.println("</html>");
    }
}