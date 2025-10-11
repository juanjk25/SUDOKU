package com.example.sudoku;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main entry point for the Sudoku 6x6 application.
 */
public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sudoku/view/sudoku.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/com/example/sudoku/view/style.css").toExternalForm());

        primaryStage.setTitle("Sudoku 6x6 - Mini Project");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
