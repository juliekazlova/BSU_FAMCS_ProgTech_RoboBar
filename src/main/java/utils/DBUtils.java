package utils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import models.Ingredient;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBUtils {
    Connection connection;
    private Statement statement;
    private static ResultSet rs;


    public boolean connect(String url, String user, String password) {
        try {
            Class.forName(Options.JDBC_DRIVER);
            System.out.println("[dbDriver] Connecting to database...");
            connection = DriverManager.getConnection(Options.DB_URL + Options.DB_NAME, Options.DB_USER, Options.DB_PASS);
            statement = connection.createStatement();

        } catch (SQLException|ClassNotFoundException e){
            e.printStackTrace();
        }
        System.out.println("Connection established...");
        return true;
    }

    public ObservableList<Ingredient> getIngredientsList(){
        String query = "select name from ingredients;";
        List<Ingredient> ingredients=new ArrayList<>();
        try{
            rs=statement.executeQuery(query);
            while (rs.next()) {
                 ingredients.add(new Ingredient( rs.getString(1)));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return FXCollections.observableArrayList(ingredients);
    }


}
