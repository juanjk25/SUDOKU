package com.example.sudokubeta.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML private TextField playerNameField;
    @FXML private Button loginButton;
    @FXML private Label errorLabel;

    private static String playerName;

    @FXML
    public void initialize() {
        // Limpiar mensaje de error cuando el usuario empiece a escribir
        playerNameField.textProperty().addListener((obs, oldText, newText) -> {
            errorLabel.setText("");
        });
    }

    @FXML
    private void onLoginButtonClick() {
        String name = playerNameField.getText().trim();

        if (name.isEmpty()) {
            errorLabel.setText("❌ Please enter your name to continue!");
            return;
        }

        if (name.length() < 2) {
            errorLabel.setText("❌ Name must be at least 2 characters long!");
            return;
        }

        if (name.length() > 15) {
            errorLabel.setText("❌ Name must be less than 15 characters!");
            return;
        }

        // Guardar el nombre del jugador
        playerName = name;

        // Cargar la pantalla del juego
        loadGameScreen();
    }

    private void loadGameScreen() {
        try {
            Stage currentStage = (Stage) loginButton.getScene().getWindow();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sudokubeta/view/sudoku.fxml"));
            Parent gameRoot = loader.load();

            // Pasar el nombre del jugador al controlador del juego
            SudokuController gameController = loader.getController();
            gameController.setPlayerName(playerName);

            Scene gameScene = new Scene(gameRoot);
            gameScene.getStylesheets().add(getClass().getResource("/com/example/sudokubeta/view/style.css").toExternalForm());

            // ← AGREGAR EL ICONO A LA VENTANA DEL JUEGO
            try {
                Image icon = new Image(getClass().getResourceAsStream("/com/example/sudokubeta/images/sudoku-logo.png"));
                currentStage.getIcons().add(icon);
            } catch (Exception e) {
                System.err.println("No se pudo cargar el icono: " + e.getMessage());
                // No detener la aplicación si hay error con el icono
            }

            currentStage.setScene(gameScene);
            currentStage.setTitle("Sudoku 6x6 - Player: " + playerName);
            currentStage.centerOnScreen();

        } catch (IOException e) {
            showErrorAlert("Error loading game screen: " + e.getMessage());
        }
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Application Error");
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static String getPlayerName() {
        return playerName;
    }
}