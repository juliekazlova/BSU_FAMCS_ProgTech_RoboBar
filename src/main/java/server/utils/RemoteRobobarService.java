package server.utils;

import models.*;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Collection;

public interface RemoteRobobarService extends Remote {
    Collection<Ingredient> getAllIngredients() throws RemoteException;

    Collection<Product> getAllProducts() throws RemoteException;

    Collection<Order> getAllOrders() throws RemoteException;

    Collection<Order> getClientOrders(int clientId) throws RemoteException;

    void updateOrderStatus(Order order, OrderStatus status) throws RemoteException;

    void addOrder(Order order) throws RemoteException;

    Client registerClient(Client client) throws RemoteException;

    boolean checkBartenderCredentials(String name, String password) throws RemoteException;
}
