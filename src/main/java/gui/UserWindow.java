package gui;


import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import models.Client;
import models.Order;
import models.OrderStatus;
import models.Product;
import utils.DBUtils;

public class UserWindow extends Application {
    private ObservableList<Product> products;
    private ObservableList<Order> clientOrders;
    private DBUtils productService = DBUtils.getInstance();
    private Client currentUser;

    public UserWindow() {

    }

    public UserWindow(Client user) {
        this.currentUser = user;
    }

    @Override
    public void start(Stage stage) {
        try {
            stage.setTitle("Client window");
            products = FXCollections.observableArrayList(productService.getAllProducts());
            clientOrders = productService.getClientOrders(currentUser.getId());
            TabPane tabPane = new TabPane();
            Tab productsTab = new Tab();
            productsTab.setText("products");
            TableView<Product> table = new TableView<>(products);
            table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            TableColumn<Product, String> nameColumn = new TableColumn<>("Product");
            nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
            TableColumn<Product, Integer> ingredientsColumn = new TableColumn<>("Ingredients");
            ingredientsColumn.setCellValueFactory(new PropertyValueFactory<>("ingredients"));
            table.getColumns().add(nameColumn);
            table.getColumns().add(ingredientsColumn);
            table.setPrefSize(500, 450);

            Tab ordersTab = new Tab();
            ordersTab.setText("orders");

            TableView<Order> orderTable = new TableView<>(clientOrders);
            TableColumn<Order, String> productColumn = new TableColumn<>("Products");
            productColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getProduct().getName()));
            TableColumn<Order, String> statusColumn = new TableColumn<>("status");
            statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
            orderTable.getColumns().add(productColumn);
            orderTable.getColumns().add(statusColumn);
            orderTable.setPrefSize(500, 450);

            Button orderButton = new Button("Order");
            orderButton.setOnAction((click) -> {
                orderButtonImpl(table);
                updateTables(table, orderTable);
            });
            FlowPane buttons = new FlowPane(orderButton);
            FlowPane productsPane = new FlowPane(table, buttons);
            FlowPane ordersPane = new FlowPane(orderTable);
            productsTab.setContent(productsPane);
            ordersTab.setContent(orderTable);
            tabPane.getTabs().addAll(productsTab, ordersTab);
            FlowPane pane = new FlowPane(tabPane);
            BorderPane root = new BorderPane();
            root.setLeft(pane);
            Button update = new Button("update");
            update.setOnAction(click -> updateTables(table, orderTable));
            root.setBottom(new FlowPane(update));
            Scene scene = new Scene(root, 500, 550);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Unexpected error").showAndWait();
            e.printStackTrace();
        }
    }

    private void updateTables(TableView<Product> table, TableView<Order> orderTable) {
        products = FXCollections.observableArrayList(productService.getAllProducts());
        table.setItems(products);
        clientOrders = FXCollections.observableArrayList(productService.getClientOrders(currentUser.getId()));
        orderTable.setItems(clientOrders);
    }

    private void orderButtonImpl(TableView<Product> table) {
        try {
            TableView.TableViewSelectionModel<Product> selectionModel = table.getSelectionModel();
            for (Integer selectedIndex : selectionModel.getSelectedIndices()) {
                productService.addOrder(new Order(products.get(selectedIndex), OrderStatus.CREATED, currentUser));
            }

        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Unexpected error").showAndWait();
            ex.printStackTrace();
        }
    }
}
