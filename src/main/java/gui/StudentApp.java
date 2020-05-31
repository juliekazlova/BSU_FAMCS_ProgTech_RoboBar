package gui;


import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.Product;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Map;

public class StudentApp extends Application {
    private ObservableList<Product> products;
    private RemoteStudentService studentService;

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        try {
            initializeStudentService();
            products = FXCollections.observableArrayList(studentService.getAllStudents());
            notYetStudents = FXCollections.observableArrayList(studentService.getNotYetStudents());
            TableView<Product> table = new TableView<>(products);

            TableColumn<Product, String> nameColumn = new TableColumn<>("Last Name");
            nameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
            TableColumn<Student, Integer> groupColumn = new TableColumn<>("Group");
            groupColumn.setCellValueFactory(new PropertyValueFactory<>("groupNum"));
            TableColumn<Student, Integer> mathColumn = new TableColumn<>("Marks");
            mathColumn.setCellValueFactory(new PropertyValueFactory<>("examResults"));
            TableColumn<Student, String> nameCol = new TableColumn<>("Name");
            nameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));
            TableColumn<Student, String> group = new TableColumn<>("Group");
            group.setCellValueFactory(new PropertyValueFactory<>("groupNum"));
            TableColumn<Student, String> notYetMarksColumn = new TableColumn<>("Not Yet Marks");
            notYetMarksColumn.setCellValueFactory(new PropertyValueFactory<>("examResults"));

            table.getColumns().add(nameColumn);
            table.getColumns().add(groupColumn);
            table.getColumns().add(mathColumn);
            table.setPrefSize(500, 450);

            notYetTable.setPrefSize(350, 450);
            notYetTable.getColumns().add(group);
            notYetTable.getColumns().add(nameCol);
            notYetTable.getColumns().add(notYetMarksColumn);
            Button add = new Button("Add");
            Button delete = new Button("Delete");
            add.setOnAction((click) -> addButtonImpl(stage, table, notYetTable));
            delete.setOnAction((click) -> deleteButtonImpl(table, notYetTable));
            FlowPane buttons = new FlowPane(add, delete);
            FlowPane pane = new FlowPane(table, buttons);
            FlowPane pane1 = new FlowPane(notYetTable);
            BorderPane root = new BorderPane();
            root.setLeft(pane);
            root.setRight(pane1);

            Scene scene = new Scene(root, 1100, 700);
            stage.setScene(scene);
            stage.setWidth(1000);
            stage.setHeight(550);
            stage.show();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Unexpected error").showAndWait();
            e.printStackTrace();
        }
    }

    private void addButtonImpl(Stage stage, TableView<Student> table, TableView<Student> notYetTable) {
        TextField name = new TextField("Last Name");
        TextField group = new TextField("Group");
        TextField subject1 = new TextField("Math");
        TextField subject2 = new TextField("Computer Science");
        TextField subject3 = new TextField("Chemistry");
        TextField mark1 = new TextField("0");
        TextField mark2 = new TextField("0");
        TextField mark3 = new TextField("0");
        Button ok = new Button("OK");
        FlowPane nameGroup = new FlowPane(name, group);
        FlowPane firstExam = new FlowPane(subject1, mark1);
        FlowPane secondExam = new FlowPane(subject2, mark2);
        FlowPane thirdExam = new FlowPane(subject3, mark3);
        FlowPane pane = new FlowPane(nameGroup, firstExam, secondExam, thirdExam, ok);
        Scene scene = new Scene(pane);
        Stage window = new Stage();
        window.setScene(scene);
        window.initModality(Modality.APPLICATION_MODAL);
        window.initOwner(stage);
        window.show();
        ok.setOnAction((event) -> {
            try {
                Map<String, Integer> examRes = new HashMap<>();
                examRes.put(subject1.getText(), Integer.parseInt(mark1.getText()));
                examRes.put(subject2.getText(), Integer.parseInt(mark2.getText()));
                examRes.put(subject3.getText(), Integer.parseInt(mark3.getText()));
                Student newStudent = new Student();
                newStudent.setLastName(name.getText());
                newStudent.setGroupNum(Integer.parseInt(group.getText()));
                newStudent.setExamResults(examRes);
                studentService.addStudent(newStudent);
                window.close();
                updateTables(table, notYetTable);
            } catch (NumberFormatException ex) {
                new Alert(Alert.AlertType.ERROR, "Enter valid values:group is number, marks (0..10)").showAndWait();
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, "Unexpected error").showAndWait();
                ex.printStackTrace();
            }
        });
    }

    private void deleteButtonImpl(TableView<Student> table, TableView<Student> notYetTable) {
        try {
            TableView.TableViewSelectionModel<Student> selectionModel = table.getSelectionModel();
            int selectedIndex = selectionModel.getSelectedIndex();
            if (selectedIndex != -1) {
                studentService.deleteStudent(products.get(selectedIndex));
            } else {
                TableView.TableViewSelectionModel<Student> notYetTableSelectionModel = notYetTable.getSelectionModel();
                int index = notYetTableSelectionModel.getSelectedIndex();
                if (index != -1) {
                    studentService.deleteStudent(notYetStudents.get(index));
                }
            }
            updateTables(table, notYetTable);
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Unexpected error").showAndWait();
            ex.printStackTrace();
        }
    }

    private void updateTables(TableView<Student> table, TableView<Student> notYetTable) throws RemoteException {
        products = FXCollections.observableArrayList(studentService.getAllStudents());
        notYetStudents = FXCollections.observableArrayList(studentService.getNotYetStudents());
        table.setItems(products);
        notYetTable.setItems(notYetStudents);
    }

    private void initializeStudentService() throws RemoteException, NotBoundException {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        Registry registry = LocateRegistry.getRegistry("192.168.0.108", 1099);
        studentService = (RemoteStudentService) registry.lookup("RemoteStudentServiceImpl");
        System.out.println("Student service successfully initialized");
    }
}
