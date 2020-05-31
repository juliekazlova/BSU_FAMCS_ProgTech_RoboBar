package utils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import models.Ingredient;
import models.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DBUtils {
    /*
    in case of timezone error:
    SET GLOBAL time_zone = '+3:00';
     */
    Connection connection;
    private Statement statement;
    private static final DBUtils instance = new DBUtils();

    private DBUtils() {
    }

    public static DBUtils getInstance() {
        return instance;
    }

    public boolean connect(String url, String user, String password) {
        try {
            Class.forName(Options.JDBC_DRIVER);
            System.out.println("[dbDriver] Connecting to database...");
            connection = DriverManager.getConnection(Options.DB_URL + Options.DB_NAME, Options.DB_USER, Options.DB_PASS);
            statement = connection.createStatement();

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println("Connection established...");
        return true;
    }

    public ObservableList<Ingredient> getAllIngredients() {
        String query = "select name from ingredients;";
        List<Ingredient> ingredients = new ArrayList<>();
        try (ResultSet rs = statement.executeQuery(query)) {
            while (rs.next()) {
                ingredients.add(new Ingredient(rs.getString(1)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return FXCollections.observableArrayList(ingredients);
    }

    public ObservableList<Product> getAllProducts() {
        String query = "select name from products;";
        List<Product> products = new ArrayList<>();
        try (ResultSet rs = statement.executeQuery(query)) {
            while (rs.next()) {
                products.add(new Product(rs.getString(1), getIngredientsById(rs.getString(2))));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return FXCollections.observableArrayList(products);
    }

    private Collection<Ingredient> getIngredientsById(String id){
        Collection<Ingredient> ingredients=new ArrayList<>();
        String query = "select name from ingredients where id=";
        String[] ids=id.split("\\s+");
        for(String cur: ids){
            try (ResultSet rs = statement.executeQuery(query+cur+";")) {
                while (rs.next()) {
                    ingredients.add(new Ingredient(rs.getString(1)));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return ingredients;
    }

    /*
    getAllProducts()
    getClientOrders(int id)
    getAllOrders()
    updateOrderStatus(int id, OrderStatus status)
    addOrder()
    registerClient(String name)
    checkBartenderCredentials()
         */


}
