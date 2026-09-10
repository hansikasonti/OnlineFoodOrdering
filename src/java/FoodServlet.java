import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

        response.setContentType("text/html;charset=UTF-8");

        PrintWriter out = response.getWriter();

        String restaurantId = request.getParameter("restaurant");

        if (restaurantId == null) {
            response.sendRedirect("foods.html");
            return;
        }

        try {

            Class.forName("com.mysql.jdbc.Driver");

            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/foodordering",
                    "root",
                    "1234"
            );

            String restaurantSql =
                    "SELECT name, location FROM restaurants WHERE id=?";

            PreparedStatement restaurantPs =
                    con.prepareStatement(restaurantSql);

            restaurantPs.setInt(
                    1,
                    Integer.parseInt(restaurantId)
            );

            ResultSet restaurantRs =
                    restaurantPs.executeQuery();

            String restaurantName = "";
            String location = "";

            if (restaurantRs.next()) {

                restaurantName =
                        restaurantRs.getString("name");

                location =
                        restaurantRs.getString("location");
            }

            restaurantRs.close();
            restaurantPs.close();

            out.println("<html>");
            out.println("<head>");
            out.println("<meta charset='UTF-8'>");
            out.println("<title>Food Menu</title>");
            out.println("</head>");

            out.println("<body>");

            out.println("<h1>🍴 "
                    + restaurantName
                    + "</h1>");

            out.println("<p>Location: "
                    + location
                    + "</p>");

            out.println("<h2>Food Menu</h2>");

            String foodSql =
                    "SELECT id, name, price "
                    + "FROM food "
                    + "WHERE restaurant_id=? "
                    + "ORDER BY id";

            PreparedStatement foodPs =
                    con.prepareStatement(foodSql);

            foodPs.setInt(
                    1,
                    Integer.parseInt(restaurantId)
            );

            ResultSet foodRs =
                    foodPs.executeQuery();

            while (foodRs.next()) {

                int foodId =
                        foodRs.getInt("id");

                String foodName =
                        foodRs.getString("name");

                double price =
                        foodRs.getDouble("price");

                out.println("<h3>"
                        + foodName
                        + "</h3>");

                out.println("<p>Rs."
                        + price
                        + "</p>");

                out.println("<form action='FoodServlet' method='post'>");

                out.println("<input type='hidden' "
                        + "name='foodId' "
                        + "value='" + foodId + "'>");

                out.println("<input type='hidden' "
                        + "name='food' "
                        + "value='" + foodName + "'>");

                out.println("<input type='hidden' "
                        + "name='price' "
                        + "value='" + price + "'>");

                out.println("<input type='hidden' "
                        + "name='restaurantId' "
                        + "value='" + restaurantId + "'>");

                out.println("<input type='hidden' "
                        + "name='restaurantName' "
                        + "value='" + restaurantName + "'>");

                out.println("<input type='submit' "
                        + "value='Add to Cart'>");

                out.println("</form>");

                out.println("<br><br>");
            }

            foodRs.close();
            foodPs.close();
            con.close();

            out.println("<br>");

            out.println("<a href='foods.html'>Choose Another Restaurant</a>");

            out.println("<br><br>");

            out.println("<a href='CartServlet'>🛒 View Cart</a>");

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


    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String foodId =
                request.getParameter("foodId");

        String food =
                request.getParameter("food");

        String priceString =
                request.getParameter("price");

        String restaurantId =
                request.getParameter("restaurantId");

        String restaurantName =
                request.getParameter("restaurantName");

        double price =
                Double.parseDouble(priceString);

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


        if (cart == null) {

            cart =
                    new LinkedHashMap<String, Integer>();

            session.setAttribute("cart", cart);
        }


        if (prices == null) {

            prices =
                    new LinkedHashMap<String, Double>();

            session.setAttribute("prices", prices);
        }


        if (foodNames == null) {

            foodNames =
                    new LinkedHashMap<String, String>();

            session.setAttribute("foodNames", foodNames);
        }


        if (cartRestaurants == null) {

            cartRestaurants =
                    new LinkedHashMap<String, String>();

            session.setAttribute(
                    "cartRestaurants",
                    cartRestaurants
            );
        }


        if (cartRestaurantIds == null) {

            cartRestaurantIds =
                    new LinkedHashMap<String, Integer>();

            session.setAttribute(
                    "cartRestaurantIds",
                    cartRestaurantIds
            );
        }


        /*
         * Food ID is used as the unique cart key.
         * Therefore foods from different restaurants
         * can be stored separately.
         */

        String cartKey = foodId;


        if (cart.containsKey(cartKey)) {

            cart.put(
                    cartKey,
                    cart.get(cartKey) + 1
            );

        } else {

            cart.put(cartKey, 1);

            prices.put(
                    cartKey,
                    price
            );

            foodNames.put(
                    cartKey,
                    food
            );

            cartRestaurants.put(
                    cartKey,
                    restaurantName
            );

            cartRestaurantIds.put(
                    cartKey,
                    Integer.parseInt(restaurantId)
            );
        }


        response.sendRedirect("CartServlet");
    }
}