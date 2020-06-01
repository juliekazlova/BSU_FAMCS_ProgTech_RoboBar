package server.utils;

import models.*;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DBUtils implements RemoteRobobarService {
    private static final DBUtils instance = new DBUtils();
    /*
    in case of timezone error:
    SET GLOBAL time_zone = '+3:00';
     */
    Connection connection;
    //  private Statement statement;

    private DBUtils() {
    }

    public static DBUtils getInstance() {
        return instance;
    }

    public void connect(String url, String user, String password) {
        try {
            Class.forName(Options.JDBC_DRIVER);
            System.out.println("[dbDriver] Connecting to database...");
            connection = DriverManager.getConnection(Options.DB_URL + Options.DB_NAME, Options.DB_USER, Options.DB_PASS);
            //    statement = connection.createStatement();

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println("Connection established...");
    }

    @Override
    public Collection<Ingredient> getAllIngredients() throws RemoteException {
        String query = "select name from ingredients;";
        List<Ingredient> ingredients = new ArrayList<>();
        try (ResultSet rs = connection.createStatement().executeQuery(query)) {
            while (rs.next()) {
                ingredients.add(new Ingredient(rs.getString(1)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ingredients;
    }

    @Override
    public Collection<Product> getAllProducts() throws RemoteException {
        String query = "select name, ingredients_list from products;";
        List<String> productNames = new ArrayList<>();
        List<String> productIds = new ArrayList<>();
        try (ResultSet rs = connection.createStatement().executeQuery(query)) {
            while (rs.next()) {
                productNames.add(rs.getString(1));
                productIds.add(rs.getString(2));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        List<Product> products = new ArrayList<>();
        for (int i = 0; i < productNames.size(); i++) {
            products.add(new Product(productNames.get(i), getIngredientsById(productIds.get(i))));
        }
        return products;
    }

    private Collection<Ingredient> getIngredientsById(String id) {
        Collection<Ingredient> ingredients = new ArrayList<>();
        String queryIngr = "select name from ingredients where id=";
        String[] ids = id.split("\\s+");
        for (String cur : ids) {
            try (ResultSet resultSet = connection.createStatement().executeQuery(queryIngr + cur + ";")) {
                while (resultSet.next()) {
                    ingredients.add(new Ingredient(resultSet.getString(1)));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return ingredients;
    }

    @Override
    public Collection<Order> getAllOrders() throws RemoteException {
        String query = "select id, product_id, status, client_id from orders;";
        List<Order> orders = new ArrayList<>();
        List<Integer> id = new ArrayList<>();
        List<Integer> product_id = new ArrayList<>();
        List<Integer> status = new ArrayList<>();
        List<Integer> client_id = new ArrayList<>();
        try (ResultSet rs = connection.createStatement().executeQuery(query)) {
            while (rs.next()) {
                id.add(rs.getInt(1));
                product_id.add(rs.getInt(2));
                status.add(rs.getInt(3));
                client_id.add(rs.getInt(4));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < id.size(); i++) {
            orders.add(new Order(
                    getProductById(product_id.get(i)),
                    OrderStatus.orderStatusByInt(status.get(i)),
                    getClientById(client_id.get(i)),
                    id.get(i)));
        }
        return orders;
    }

    public Client getClientById(int id) {
        String query = "select username from users where id=" + id + ";";
        String name = null;
        try (ResultSet rs = connection.createStatement().executeQuery(query)) {
            while (rs.next()) {
                name = rs.getString(1);
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
        try (ResultSet rs = connection.createStatement().executeQuery(query)) {
            while (rs.next()) {
                name = rs.getString(1);
                ingredients_id = rs.getString(2);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new Product(name, getIngredientsById(ingredients_id));
    }

    private int getIdByProduct(Product product) {
        String query = "select id from products where name='" + product.getName() + "';";
        int id = 0;
        try (ResultSet rs = connection.createStatement().executeQuery(query)) {
            while (rs.next()) {
                id = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }

    private int getIdByClient(String client) {
        String query = "select id from users where username='" + client + "';";
        int id = 0;
        try (ResultSet rs = connection.createStatement().executeQuery(query)) {
            while (rs.next()) {
                id = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }

    @Override
    public Collection<Order> getClientOrders(int clientId) throws RemoteException {
        String query = "select id, product_id, status from orders where client_id=" + clientId + ";";
        List<Order> orders = new ArrayList<>();
        List<Integer> id = new ArrayList<>();
        List<Integer> product_id = new ArrayList<>();
        List<Integer> status = new ArrayList<>();
        try (ResultSet rs = connection.createStatement().executeQuery(query)) {
            while (rs.next()) {
                id.add(rs.getInt(1));
                product_id.add(rs.getInt(2));
                status.add(rs.getInt(3));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Client client = getClientById(clientId);
        for (int i = 0; i < id.size(); i++) {
            orders.add(new Order(
                    getProductById(product_id.get(i)),
                    OrderStatus.orderStatusByInt(status.get(i)),
                    client,
                    id.get(i)));
        }
        return orders;
    }

    @Override
    public void updateOrderStatus(Order order, OrderStatus status) throws RemoteException {
        String query = "UPDATE orders SET status=" + (status.ordinal() + 1) + " WHERE client_id=" +
                order.getClient().getId() + " and product_id=" + getIdByProduct(order.getProduct()) + ";";
        try {
            connection.createStatement().executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addOrder(Order order) throws RemoteException {

        StringBuilder sb = new StringBuilder("insert into orders (product_id, status, client_id) values (");
        sb.append(getIdByProduct(order.getProduct())).append(", ");
        sb.append(order.getStatus().ordinal() + 1);
        sb.append(",");
        sb.append(order.getClient().getId());
        sb.append(");");

        try {
            connection.createStatement().execute(sb.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Client registerClient(Client client) throws RemoteException {
        System.out.println(client);
        String query = "insert into users (username) values ('" + client.getFullName() + "');";
        try {
            connection.createStatement().execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        client.setId(getIdByClient(client.getFullName()));
        System.out.println(client);
        return client;
    }

    @Override
    public boolean checkBartenderCredentials(String name, String password) throws RemoteException {
        String query = "SELECT COUNT(id) FROM bartenders WHERE username='" + name + "' AND password='" + password + "';";
        int result = 0;
        try (ResultSet rs = connection.createStatement().executeQuery(query)) {
            while (rs.next()) {
                result = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result != 0;
    }


}
