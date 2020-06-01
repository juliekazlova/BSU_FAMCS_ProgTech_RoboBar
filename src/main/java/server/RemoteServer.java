package server;

import server.utils.DBUtils;
import server.utils.Options;
import server.utils.RemoteRobobarService;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import static server.utils.Options.REMOTE_SERVICE;

public class RemoteServer {
    public static void main(String[] args) {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        RemoteRobobarService remoteRobobarService = null;

        remoteRobobarService = DBUtils.getInstance();

        DBUtils.getInstance().connect(Options.DB_URL, Options.DB_USER, Options.DB_PASS);
        try {
            System.setProperty("java.rmi.server.hostname", "127.0.0.1");
            RemoteRobobarService remote = (RemoteRobobarService) UnicastRemoteObject.exportObject(remoteRobobarService, 0);
            Registry registry = LocateRegistry.createRegistry(1099);

            registry.bind(REMOTE_SERVICE, remote);
            System.out.println("bound remote object");
        } catch (RemoteException | AlreadyBoundException e) {
            e.printStackTrace();
        }

    }
}
