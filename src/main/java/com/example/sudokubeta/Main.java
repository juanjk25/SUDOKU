package com.example.sudokubeta;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;



/**
 * Application entry point for the Sudoku 6x6 JavaFX application.
 *
 * <p>This class initializes the JavaFX runtime and prepares the primary
 * stage by loading the player login FXML, applying the stylesheet, attempting
 * to load a window icon and showing the window with fixed size.
 *
 * @since 2025
 */
public class Main extends Application {

    /**
     * Called by the JavaFX runtime to set up and show the main application window.
     *
     * <p>The implementation performs the following steps:
     * <ol>
     *   <li>Loads the FXML located at {@code /com/example/sudokubeta/view/PlayerLogin.fxml}.</li>
     *   <li>Creates a {@link Scene} sized 400x500 and applies the stylesheet
     *       {@code /com/example/sudokubeta/view/style.css}.</li>
     *   <li>Attempts to load the icon image {@code /com/example/sudokubeta/view/sudoku.png};
     *       failures are logged but do not prevent the UI from showing.</li>
     *   <li>Sets title, scene and resize policy on the provided {@link Stage} and shows it.</li>
     * </ol>
     *
     * @param primaryStage the main window provided by the JavaFX runtime
     * @throws Exception if an error occurs while loading the FXML or associated resources
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sudokubeta/view/PlayerLogin.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 400, 500);
        scene.getStylesheets().add(getClass().getResource("/com/example/sudokubeta/view/style.css").toExternalForm());

        // ADD ICON TO WINDOW
        try {
            Image icon = new Image(getClass().getResourceAsStream("/com/example/sudokubeta/view/sudoku.png"));
            primaryStage.getIcons().add(icon);
            System.out.println("Logo cargado exitosamente");
        } catch (Exception e) {
            System.err.println("No se pudo cargar el logo: " + e.getMessage());
            // Continue without failing the application if icon load fails
        }

        primaryStage.setTitle("Sudoku 6x6 - Welcome");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }


    /**
     * Launches the JavaFX application.
     *
     * <p>This method delegates to {@link javafx.application.Application#launch(String...)}
     * which initializes the JavaFX toolkit and eventually calls {@link #start(Stage)}.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        launch(args);
    }
}