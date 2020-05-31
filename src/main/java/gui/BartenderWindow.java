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
import utils.DBUtils;

public class BartenderWindow extends Application {
    private ObservableList<Ingredient> ingredients;
    private ObservableList<Order> orders;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("BartenderWindow");
        primaryStage.show();
        orders = FXCollections.observableArrayList(DBUtils.getInstance().getAllOrders());
        ingredients = FXCollections.observableArrayList(DBUtils.getInstance().getAllIngredients());
        TabPane tabBarPane = new TabPane();
        Tab ingredientsTab = new Tab();
        ingredientsTab.setText("ingredients");
        TableView<Ingredient> ingredientTable = new TableView<>(ingredients);
        TableColumn<Ingredient, String> ingredientTableColumn = new TableColumn<>("Ingredients");
        ingredientTableColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        ingredientTable.getColumns().add(ingredientTableColumn);
        ingredientsTab.setContent(ingredientTable);

        Tab ordersTab = new Tab();
        ordersTab.setText("orders");
        TableView<Order> ordersTable = new TableView<>(orders);
        TableColumn<Order, String> productColumn = new TableColumn<>("Products");
        productColumn.setCellValueFactory(new PropertyValueFactory<>("products"));
        TableColumn<Order, String> statusColumn = new TableColumn<>("status");
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        TableColumn<Order, String> clientColumn = new TableColumn<>("Client");
        clientColumn.setCellValueFactory(new PropertyValueFactory<>("client"));
        ordersTable.getColumns().add(productColumn);
        ordersTable.getColumns().add(statusColumn);
        ordersTable.getColumns().add(clientColumn);

        Button ordersButton = new Button("Give order");
        ordersButton.setOnAction((click) -> completeOrder(ordersTable));
        FlowPane buttons = new FlowPane(ordersButton);
        FlowPane ordersPane = new FlowPane(ordersTable, buttons);
        ordersTab.setContent(ordersPane);

        tabBarPane.getTabs().addAll(ordersTab, ingredientsTab);
        FlowPane pane = new FlowPane(tabBarPane);
        BorderPane root = new BorderPane();
        root.setLeft(pane);
        Button update = new Button("update");
        update.setOnAction(click -> updateTables(ordersTable, ingredientTable));
        root.setBottom(new FlowPane(update));
        Scene scene = new Scene(root, 500, 700);
        primaryStage.setScene(scene);
        primaryStage.setWidth(500);
        primaryStage.setHeight(750);
        primaryStage.show();
    }

    private void updateTables(TableView<Order> orderTable, TableView<Ingredient> ingredientTable) {
        orders = FXCollections.observableArrayList(DBUtils.getInstance().getAllOrders());
        orderTable.setItems(orders);
        ingredients = FXCollections.observableArrayList(DBUtils.getInstance().getAllIngredients());
        ingredientTable.setItems(ingredients);
    }

    private void completeOrder(TableView<Order> table) {
        try {
            TableView.TableViewSelectionModel<Order> selectionModel = table.getSelectionModel();
            for (Integer selectedIndex : selectionModel.getSelectedIndices()) {
                DBUtils.getInstance().updateOrderStatus(orders.get(selectedIndex).getId(), OrderStatus.READY_FOR_CLIENT);
            }

        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Unexpected error").showAndWait();
            ex.printStackTrace();
        }
    }
}
