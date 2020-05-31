package utils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import models.*;

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
//        try (ResultSet rs = statement.executeQuery(query)) {
//            while (rs.next()) {
//                ingredients.add(new Ingredient(rs.getString(1)));
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
        return FXCollections.observableArrayList(ingredients);
    }

    public ObservableList<Product> getAllProducts() {
        String query = "select name, ingredients_list from products;";
        List<String> productNames = new ArrayList<>();
        List<String> productIds = new ArrayList<>();
//        try (ResultSet rs = statement.executeQuery(query)) {
//            while (rs.next()) {
//               productNames.add(rs.getString(1));
//               productIds.add(rs.getString(2));
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
        //Ира, не ругай за говнокод! ResultSetы пареллельно, как оказалось,
        //работать не могут и путают друг друга :(
        List<Product> products = new ArrayList<>();
        for (int i = 0; i < productNames.size(); i++) {
            products.add(new Product(productNames.get(i), getIngredientsById(productIds.get(i))));
        }
        return FXCollections.observableArrayList(products);
    }

    private Collection<Ingredient> getIngredientsById(String id) {
        Collection<Ingredient> ingredients = new ArrayList<>();
//        String queryIngr = "select name from ingredients where id=";
//        String[] ids=id.split("\\s+");
//        for(String cur: ids){
//            try (ResultSet resultSet = statement.executeQuery(queryIngr+cur+";")) {
//                while (resultSet.next()) {
//                    ingredients.add(new Ingredient(resultSet.getString(1)));
//                }
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
        return ingredients;
    }

    public ObservableList<Order> getAllOrders() {
        String query = "select id, product_id, status, client_id from orders;";
        List<Order> orders = new ArrayList<>();
//        List<Integer> id=new ArrayList<>();
//        List<Integer> product_id=new ArrayList<>();
//        List<Integer> status=new ArrayList<>();
//        List<Integer> client_id=new ArrayList<>();
//        try (ResultSet rs = statement.executeQuery(query)) {
//            while (rs.next()) {
//                id.add(rs.getInt(1));
//                product_id.add(rs.getInt(2));
//                status.add(rs.getInt(3));
//                client_id.add(rs.getInt(4));
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        for(int i=0; i<id.size(); i++){
//            orders.add(new Order(
//                    getProductById(product_id.get(i)),
//                    OrderStatus.orderStatusByInt(status.get(i)),
//                    getClientById(client_id.get(i)),
//                    id.get(i)));
//        }
        return FXCollections.observableArrayList(orders);
    }

    public Client getClientById(int id){
        String query = "select username from users where id="+id+";";
        String name=null;
        try (ResultSet rs = statement.executeQuery(query)) {
            while (rs.next()) {
               name=rs.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new Client(name, id);
    }

    public Product getProductById(int id) {
        String query = "select name, ingredients_list from products where id=" + id + ";";
        String name = null;
        String ingredients_id = null;
//        try (ResultSet rs = statement.executeQuery(query)) {
//            while (rs.next()) {
//                name=rs.getString(1);
//                ingredients_id=rs.getString(2);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
        return new Product(name, getIngredientsById(ingredients_id));
    }

    private int getIdByProduct(Product product) {
        String query = "select id from products where name='" + product.getName() + "';";
        int id = 0;
//        try (ResultSet rs = statement.executeQuery(query)) {
//            while (rs.next()) {
//                id=rs.getInt(1);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
        return id;
    }

    private int getIdByClient(String client) {
        String query = "select id from users where username='" + client + "';";
        int id = 0;
//        try (ResultSet rs = statement.executeQuery(query)) {
//            while (rs.next()) {
//                id=rs.getInt(1);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
        return id;
    }

    public ObservableList<Order> getClientOrders(int clientId) {
        String query = "select id, product_id, status from orders where client_id=" + clientId + ";";
        List<Order> orders = new ArrayList<>();
//        List<Integer> id=new ArrayList<>();
//        List<Integer> product_id=new ArrayList<>();
//        List<Integer> status=new ArrayList<>();
//        try (ResultSet rs = statement.executeQuery(query)) {
//            while (rs.next()) {
//                id.add(rs.getInt(1));
//                product_id.add(rs.getInt(2));
//                status.add(rs.getInt(3));
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        Client client=getClientById(clientId);
//        for(int i=0; i<id.size(); i++){
//            orders.add(new Order(
//                    getProductById(product_id.get(i)),
//                    OrderStatus.orderStatusByInt(status.get(i)),
//                    client,
//                    id.get(i)));
//        }
        return FXCollections.observableArrayList(orders);
    }

    public boolean updateOrderStatus(int id, OrderStatus status) {
//        String query="UPDATE orders SET status="+(status.ordinal()+1)+" WHERE id="+id+";";
//        try {
//            statement.execute(query);
//        } catch (SQLException e){
//            e.printStackTrace();
//            return false;
//        }
        return true;
    }

    public boolean addOrder(Order order) {

//        StringBuilder sb=new StringBuilder("insert into orders (product_id, status, client_id) values (");
//        sb.append(getIdByProduct(order.getProducts())+", ");
//        sb.append(order.getStatus().ordinal()+1);
//        sb.append(","+order.getClient().getId()+");");
//
//        try {
//            statement.execute(sb.toString());
//        } catch (SQLException e){
//            e.printStackTrace();
//        }
        return true;
    }

    public boolean registerClient(Client client) {
//        String query="insert into users (username) values ('"+client.getFullName()+"');";
//        try {
//            statement.execute(query);
//        } catch (SQLException e){
//            e.printStackTrace();
//        }
//        client.setId(getIdByClient(client.getFullName()));
        return true;
    }

    public boolean checkBartenderCredentials(String name, String password) {
        String query = "SELECT COUNT(id) FROM bartenders WHERE username='" + name + "' AND password='" + password + "';";
//        int result=0;
//        try (ResultSet rs = statement.executeQuery(query)) {
//            while (rs.next()) {
//                result=rs.getInt(1);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        if(result==0) {
//            return false;
//        }
        return true;
    }



}
