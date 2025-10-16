package com.example.sudokubeta;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sudokubeta/view/PlayerLogin.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 400, 500);
        scene.getStylesheets().add(getClass().getResource("/com/example/sudokubeta/view/style.css").toExternalForm());

        // ← AGREGAR EL ICONO A LA VENTANA
        try {
            // Cambia "sudoku-logo.png" por el nombre real de tu imagen
            Image icon = new Image(getClass().getResourceAsStream("/com/example/sudokubeta/view/sudoku.png"));
            primaryStage.getIcons().add(icon);
            System.out.println("✅ Logo cargado exitosamente");
        } catch (Exception e) {
            System.err.println("❌ No se pudo cargar el logo: " + e.getMessage());
            // No detener la aplicación si hay error con el icono
        }

        primaryStage.setTitle("Sudoku 6x6 - Welcome");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}