package gui;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import models.Ingredient;
import models.Order;
import models.Product;

public class BartenderWindow extends Application {
    private ObservableList<Ingredient> ingredients;
    private ObservableList<Order> orders;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Bartender window");
        primaryStage.show();
        TabPane tabBarPane = new TabPane();
        Tab ingredientsTab = new Tab();
        ingredientsTab.setText("ingredients");
        TableView<Ingredient> ingredientTable = new TableView<>(ingredients);
        //заполнить вот тут таблицу
        ingredientsTab.setContent(ingredientTable);

        Tab ordersTab = new Tab();
        ordersTab.setText("orders");
        TableView<Order> ordersTable = new TableView<>(orders);
        //заполнить вот тут таблицу
        Button ordersButton=new Button("Give order");
        FlowPane buttons = new FlowPane(ordersButton);
        FlowPane ordersPane = new FlowPane(ordersTable, buttons);
        ordersTab.setContent(ordersPane);

        tabBarPane.getTabs().addAll(ingredientsTab, ordersTab);
        FlowPane pane = new FlowPane(tabBarPane);
        BorderPane root = new BorderPane();
        root.setLeft(pane);

        Scene scene = new Scene(root, 500, 700);
        primaryStage.setScene(scene);
        primaryStage.setWidth(400);
        primaryStage.setHeight(550);
        primaryStage.show();
    }
}
