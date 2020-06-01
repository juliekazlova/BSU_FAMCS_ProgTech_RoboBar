package gui;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import models.Ingredient;
import models.Order;
import models.OrderStatus;
import server.utils.RemoteRobobarService;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import static server.utils.Options.REMOTE_SERVICE;

public class BartenderWindow extends Application {
    private ObservableList<Ingredient> ingredients;
    private ObservableList<Order> orders;
    private RemoteRobobarService robobarService;

    @Override
    public void start(Stage primaryStage) {
        try {
            initializeRobobarService();
            primaryStage.setTitle("BartenderWindow");
            primaryStage.show();
            orders = FXCollections.observableArrayList(robobarService.getAllOrders());
            ingredients = FXCollections.observableArrayList(robobarService.getAllIngredients());
            TabPane tabBarPane = new TabPane();
            Tab ingredientsTab = new Tab();
            ingredientsTab.setText("ingredients");
            TableView<Ingredient> ingredientTable = new TableView<>(ingredients);
            TableColumn<Ingredient, String> ingredientTableColumn = new TableColumn<>("Ingredients");
            ingredientTableColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
            ingredientTable.getColumns().add(ingredientTableColumn);
            ingredientsTab.setContent(ingredientTable);
            ingredientTable.setPrefSize(500, 450);


            Tab ordersTab = new Tab();
            ordersTab.setText("orders");
            TableView<Order> ordersTable = new TableView<>(orders);
            TableColumn<Order, String> productColumn = new TableColumn<>("Products");
            productColumn.setCellValueFactory(new PropertyValueFactory<>("product"));
            TableColumn<Order, String> statusColumn = new TableColumn<>("status");
            statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
            TableColumn<Order, String> clientColumn = new TableColumn<>("Client");
            clientColumn.setCellValueFactory(new PropertyValueFactory<>("client"));
            ordersTable.getColumns().add(productColumn);
            ordersTable.getColumns().add(statusColumn);
            ordersTable.getColumns().add(clientColumn);
            ordersTable.setPrefSize(500, 450);

            Button ordersButton = new Button("Give order");
            ordersButton.setOnAction((click) -> {
                completeOrder(ordersTable);
                updateTables(ordersTable, ingredientTable);
            });
            Button update = new Button("update");
            update.setOnAction(click -> updateTables(ordersTable, ingredientTable));

            FlowPane buttons = new FlowPane(ordersButton);
            FlowPane ordersPane = new FlowPane(ordersTable, buttons);
            ordersTab.setContent(ordersPane);

            tabBarPane.getTabs().addAll(ordersTab, ingredientsTab);
            FlowPane pane = new FlowPane(tabBarPane);
            BorderPane root = new BorderPane();
            root.setCenter(pane);

            root.setBottom(new FlowPane(update));
            Scene scene = new Scene(root, 500, 550);
            primaryStage.setScene(scene);

        } catch (RemoteException | NotBoundException e) {
            new Alert(Alert.AlertType.ERROR, "Unexpected remote error").showAndWait();
            e.printStackTrace();
        }
    }

    private void updateTables(TableView<Order> orderTable, TableView<Ingredient> ingredientTable) {
        try {
            orders = FXCollections.observableArrayList(robobarService.getAllOrders());
            orderTable.setItems(orders);
            ingredients = FXCollections.observableArrayList(robobarService.getAllIngredients());
            ingredientTable.setItems(ingredients);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void completeOrder(TableView<Order> table) {
        try {
            TableView.TableViewSelectionModel<Order> selectionModel = table.getSelectionModel();
            for (Integer selectedIndex : selectionModel.getSelectedIndices()) {
                robobarService.updateOrderStatus(orders.get(selectedIndex), OrderStatus.READY_FOR_CLIENT);
            }

        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Unexpected error").showAndWait();
            ex.printStackTrace();
        }
    }

    private void initializeRobobarService() throws RemoteException, NotBoundException {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        Registry registry = LocateRegistry.getRegistry("127.0.0.1", 1099);
        robobarService = (RemoteRobobarService) registry.lookup(REMOTE_SERVICE);
        System.out.println("Remote service successfully initialized");
    }
}
