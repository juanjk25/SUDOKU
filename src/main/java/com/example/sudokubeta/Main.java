package com.example.sudokubeta;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Cargar primero la pantalla de login
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sudokubeta/view/PlayerLogin.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 400, 500);

        // Cargar el CSS (puedes crear uno específico para login o usar el mismo)
        scene.getStylesheets().add(getClass().getResource("/com/example/sudokubeta/view/style.css").toExternalForm());

        primaryStage.setTitle("Sudoku 6x6 - Welcome");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}