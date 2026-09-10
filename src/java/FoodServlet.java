import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/FoodServlet")
public class FoodServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        response.sendRedirect("foods.html");
    }

    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String food = request.getParameter("food");
        String priceString = request.getParameter("price");

        if (food == null || priceString == null) {
            response.sendRedirect("foods.html");
            return;
        }

        double price = Double.parseDouble(priceString);

        HttpSession session = request.getSession();

        Map<String, Integer> cart =
                (Map<String, Integer>)
                session.getAttribute("cart");

        Map<String, Double> prices =
                (Map<String, Double>)
                session.getAttribute("prices");

        if (cart == null) {

            cart = new LinkedHashMap<String, Integer>();

            session.setAttribute("cart", cart);
        }

        if (prices == null) {

            prices = new LinkedHashMap<String, Double>();

            session.setAttribute("prices", prices);
        }

        if (cart.containsKey(food)) {

            int quantity = cart.get(food);

            cart.put(food, quantity + 1);

        } else {

            cart.put(food, 1);

            prices.put(food, price);
        }

        response.sendRedirect("CartServlet");
    }
}