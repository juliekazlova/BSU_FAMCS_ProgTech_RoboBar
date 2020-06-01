package gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Pair;
import models.Client;
import server.utils.RemoteRobobarService;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import static server.utils.Options.REMOTE_SERVICE;

public class Robobar extends Application {
    private RemoteRobobarService robobarService;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws RemoteException, NotBoundException {

        initializeRobobarService();
        List<String> choices = new ArrayList<>();
        choices.add("User");
        choices.add("Bartender");

        ChoiceDialog<String> dialog = new ChoiceDialog<>("User", choices);
        dialog.setTitle("Choose who you are");
        dialog.setHeaderText("Hi from robobar!");
        dialog.setContentText("Choose your role:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(s -> System.out.println("Your choice: " + s));
        result.ifPresent(data -> {
            switch (data) {
                case "User":
                    showUserInputDialog();
                    break;
                case "Bartender":
                    if (showLoginDialog()) {
                        new BartenderWindow().start(new Stage());
                    }
                    break;
            }
        });

    }

    private boolean showLoginDialog() {
        // Create the custom dialog.
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Login Bartender");
        dialog.setHeaderText("Enter your credentials");

        ButtonType loginButtonType = new ButtonType("Login", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

// Create the username and password labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField username = new TextField();
        username.setPromptText("Username");
        PasswordField password = new PasswordField();
        password.setPromptText("Password");

        grid.add(new Label("Username:"), 0, 0);
        grid.add(username, 1, 0);
        grid.add(new Label("Password:"), 0, 1);
        grid.add(password, 1, 1);

// Enable/Disable login button depending on whether a username was entered.
        Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
        loginButton.setDisable(true);

// Do some validation (using the Java 8 lambda syntax).
        username.textProperty().addListener((observable, oldValue, newValue) -> {
            loginButton.setDisable(newValue.trim().isEmpty());
        });

        dialog.getDialogPane().setContent(grid);

// Request focus on the username field by default.
        Platform.runLater(username::requestFocus);

// Convert the result to a username-password-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                return new Pair<>(username.getText(), password.getText());
            }
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();
        AtomicBoolean ok = new AtomicBoolean(false);
        result.ifPresent(usernamePassword -> {
            try {
                ok.set(robobarService.checkBartenderCredentials(usernamePassword.getKey(), usernamePassword.getValue()));
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            if (!ok.get()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("NOT VALID");
                alert.showAndWait();
            }
        });
        return ok.get();
    }

    private void showUserInputDialog() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Login User");
        dialog.setHeaderText("Enter your credentials");

// Set the icon (must be included in the project).
//        dialog.setGraphic(new ImageView(this.getClass().getResource("login.png").toString()));

// Set the button types.
        ButtonType loginButtonType = new ButtonType("Login", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

// Create the username and password labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField username = new TextField();
        username.setPromptText("Username");

        grid.add(new Label("Username:"), 0, 0);
        grid.add(username, 1, 0);

// Enable/Disable login button depending on whether a username was entered.
        Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
        loginButton.setDisable(true);

// Do some validation (using the Java 8 lambda syntax).
        username.textProperty().addListener((observable, oldValue, newValue) -> {
            loginButton.setDisable(newValue.trim().isEmpty());
        });

        dialog.getDialogPane().setContent(grid);

// Request focus on the username field by default.
        Platform.runLater(username::requestFocus);

// Convert the result to a username-password-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                return username.getText();
            }
            return null;
        });

        Optional<String> result = dialog.showAndWait();

        result.ifPresent(name -> {

            try {
                Client user = new Client(name);
                new UserWindow(robobarService.registerClient(user)).start(new Stage());
            } catch (RemoteException e) {
                e.printStackTrace();
            }


        });
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
