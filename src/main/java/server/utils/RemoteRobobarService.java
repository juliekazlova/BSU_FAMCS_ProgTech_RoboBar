package server.utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import models.*;


import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteRobobarService extends Remote {
    ObservableList<Ingredient> getAllIngredients() throws RemoteException;

    ObservableList<Product> getAllProducts() throws RemoteException;

    ObservableList<Order> getAllOrders() throws RemoteException;

    ObservableList<Order> getClientOrders(int clientId) throws RemoteException;

    void updateOrderStatus(Order order, OrderStatus status) throws RemoteException;

    void addOrder(Order order) throws RemoteException;

    void registerClient(Client client) throws RemoteException;

    boolean checkBartenderCredentials(String name, String password) throws RemoteException;
}
