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

/**
 * Controller for the player login screen.
 *
 * <p>Handles player name entry, basic validations, and the
 * transition to the main game screen by loading the corresponding FXML.
 *
 * @since 2025
 */
public class LoginController {

    /**
     * Login screen controller.
     *
     * <p>Handles player name entry, basic validations, and the
     * transition to the main game screen by loading the corresponding FXML.
     *
     * @since 2025
     */
    @FXML private TextField playerNameField;

    /**
     * FXML-injected button that initiates the login process.
     */
    @FXML private Button loginButton;

    /**
     * Tag injected by FXML to display error or validation messages.
     */
    @FXML private Label errorLabel;

    /**
     * Name of the selected player. Static variable accessible via {@link #getPlayerName()}.
     *
     * <p>This is shared statically to allow easy access from other parts of the application when needed.
     */
    private static String playerName;

    /**
     * Initialization method invoked by JavaFX after loading the FXML.
     *
     * <p>Attaches a listener to the text field to clear error messages
     * when the user starts typing.
     */
    @FXML
    public void initialize() {
        // Clear error message when user starts typing
        playerNameField.textProperty().addListener((obs, oldText, newText) -> {
            errorLabel.setText("");
        });
    }


    /**
     * Handler for the login button click event.
     *
     * <p>Performs the following validations on the name:
     * <ul>
     * <li>Not empty.</li>
     * <li>Have at least 2 characters.</li>
     * <li>Do not exceed 15 characters.</li>
     * </ul>
     *
     * <p>If the validations pass, store the name in {@link #playerName}
     * and call {@link #loadGameScreen()} to switch to the game screen.
     */
    @FXML
    private void onLoginButtonClick() {
        String name = playerNameField.getText().trim();

        if (name.isEmpty()) {
            errorLabel.setText("Please enter your name to continue!");
            return;
        }

        if (name.length() < 2) {
            errorLabel.setText("Name must be at least 2 characters long!");
            return;
        }

        if (name.length() > 15) {
            errorLabel.setText("Name must be less than 15 characters!");
            return;
        }

        // Save the player's name
        playerName = name;

        // Load the game screen
        loadGameScreen();
    }

    /**
     * Loads the main game screen from the FXML resource and passes
     * the player name to the game controller.
     *
     * <p>Handles:
     * <ul>
     * <li>Loading `sudoku.fxml` using FXMLLoader.</li>
     * <li>Getting the view controller and setting the player name.</li>
     * <li>Applying the stylesheet and optionally adding an icon to the window.</li>
     * <li>Updating the current Stage scene and centering it on screen.</li>
     * </ul>
     *
     * <p>I/O errors while loading the FXML display an error dialog using
     * {@link #showErrorAlert(String)}.
     */
    private void loadGameScreen() {
        try {
            Stage currentStage = (Stage) loginButton.getScene().getWindow();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sudokubeta/view/sudoku.fxml"));
            Parent gameRoot = loader.load();

            // Pass the player name to the game controller
            SudokuController gameController = loader.getController();
            gameController.setPlayerName(playerName);

            Scene gameScene = new Scene(gameRoot);
            gameScene.getStylesheets().add(getClass().getResource("/com/example/sudokubeta/view/style.css").toExternalForm());

            // ADD THE ICON TO THE GAME WINDOW
            try {
                Image icon = new Image(getClass().getResourceAsStream("/com/example/sudokubeta/images/sudoku-logo.png"));
                currentStage.getIcons().add(icon);
            } catch (Exception e) {
                System.err.println("No se pudo cargar el icono: " + e.getMessage());
                // Do not stop the application if there is an error with the icon
            }

            currentStage.setScene(gameScene);
            currentStage.setTitle("Sudoku 6x6 - Player: " + playerName);
            currentStage.centerOnScreen();

        } catch (IOException e) {
            showErrorAlert("Error loading game screen: " + e.getMessage());
        }
    }

    /**
     * Displays an error dialog box with the supplied message.
     *
     * @param message text to display in the error dialog box content
     */
    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Application Error");
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Returns the currently stored player name.
     *
     * @return player name or {@code null} if not set
     */
    public static String getPlayerName() {
        return playerName;
    }
}