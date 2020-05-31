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
import models.Client;
import models.Product;
import models.User;
import utils.FakeData;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class ProductApp extends Application {
    private ObservableList<Product> products;
    private FakeData productService;
    private Client currentUser;

    public static void main(String[] args) {
        Application.launch(args);
    }

    public ProductApp() {

    }

    public ProductApp(Client user) {
        this.currentUser = user;
    }

    @Override
    public void start(Stage stage) {
        try {
            initializeProductService();
            products = FXCollections.observableArrayList(productService.getAllProducts());
            TableView<Product> table = new TableView<>(products);
            table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            TableColumn<Product, String> nameColumn = new TableColumn<>("Product");
            nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
            TableColumn<Product, Integer> ingredientsColumn = new TableColumn<>("Ingredients");
            ingredientsColumn.setCellValueFactory(new PropertyValueFactory<>("ingredients"));

            table.getColumns().add(nameColumn);
            table.getColumns().add(ingredientsColumn);
            table.setPrefSize(500, 450);


            Button orderButton = new Button("Order");
            orderButton.setOnAction((click) -> orderButtonImpl(table));
            FlowPane buttons = new FlowPane(orderButton);
            FlowPane pane = new FlowPane(table, buttons);
            BorderPane root = new BorderPane();
            root.setLeft(pane);

            Scene scene = new Scene(root, 500, 700);
            stage.setScene(scene);
            stage.setWidth(400);
            stage.setHeight(550);
            stage.show();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Unexpected error").showAndWait();
            e.printStackTrace();
        }
    }

    private void orderButtonImpl(TableView<Product> table) {
        try {
            TableView.TableViewSelectionModel<Product> selectionModel = table.getSelectionModel();
            for (Integer selectedIndex : selectionModel.getSelectedIndices()) {
                productService.orderProduct(products.get(selectedIndex), currentUser);

            }

        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Unexpected error").showAndWait();
            ex.printStackTrace();
        }
    }

    private void updateTables(TableView<Product> table) throws RemoteException {
        products = FXCollections.observableArrayList(productService.getAllProducts());
        table.setItems(products);
    }

    private void initializeProductService() throws RemoteException, NotBoundException {
        productService = new FakeData();
    }
}
