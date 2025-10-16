package com.example.sudokubeta.controller;

import com.example.sudokubeta.model.ISudokuModel;
import com.example.sudokubeta.model.SudokuModel;
import com.example.sudokubeta.view.SudokuCell;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;

import java.util.List;

/**
 * Main controller for the Sudoku user interface.
 * Handles user interaction, validation, and help systems.
 * Implements the MVC pattern by connecting the view (FXML) to the model
 *
 * @author Juan David Lopez, Oscar Rengifo
 * @version 1.0 beta
 * @since 2025
 */

public class SudokuController implements ISudokuController {

    @FXML private GridPane gridPane;
    @FXML private Button btnHelp;
    @FXML private Button btnReset;
    @FXML private Label statusLabel;

    private final ISudokuModel model = new SudokuModel();
    private final SudokuCell[][] cells = new SudokuCell[ISudokuModel.SIZE][ISudokuModel.SIZE];
    private String playerName;

    private final int[][] starter = {
            {1, 4, 0, 0, 2, 0},
            {0, 0, 3, 0, 0, 5},
            {0, 2, 0, 0, 0, 6},
            {0, 0, 6, 0, 3, 0},
            {5, 0, 0, 3, 0, 2},
            {0, 3, 0, 0, 1, 0}
    };


    /**
     * Initialization method called automatically by JavaFX after loading the FXML.
     *  Configures the user interface, loads the initial dashboard, and sets event handlers.
     *
     */

    @Override
    @FXML
    public void initialize() {
        model.loadPuzzle(starter);
        buildGrid();
        attachKeyboardHandlers();
        updateAllCellsFromModel();
        btnHelp.setOnAction(e -> onHelp());
        btnReset.setOnAction(e -> {
            model.loadPuzzle(starter);
            updateAllCellsFromModel();
            updateStatusLabel();
        });
        updateStatusLabel();
    }

    /**
     * Stabilish the current player name and update the interface
     *
     * @param playerName The name of the player, can´t be null
     */

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
        updateStatusLabel();
    }


    /**
     * Build the Sudoku grid in the graphical interface.
     * Create and configure each individual cell with its corresponding listeners.
     */
    private void buildGrid() {
        gridPane.getChildren().clear();
        for (int r = 0; r < ISudokuModel.SIZE; r++) { // For loop to fill in the blanks in sudoku
            for (int c = 0; c < ISudokuModel.SIZE; c++) {
                SudokuCell cell = new SudokuCell(r, c);
                cells[r][c] = cell;

                // Effectively final variables for the lambda
                int currentRow = r;
                int currentCol = c;

                cell.textProperty().addListener((obs, oldV, newV) -> {
                    try {
                        if (newV == null || newV.isEmpty()) {
                            model.set(currentRow, currentCol, 0);
                        } else {
                            model.set(currentRow, currentCol, Integer.parseInt(newV));
                        }
                        performValidation();
                    } catch (NumberFormatException e) {
                        model.set(currentRow, currentCol, 0);
                    }
                });

                gridPane.add(cell, c, r);
            }
        }
    }


    /**
     * Method to configure keyboard event handlers for navigation and data entry.
     * Allows you to use arrow keys to navigate and number keys to enter values.
     *
     */
    private void attachKeyboardHandlers() {
        gridPane.addEventFilter(KeyEvent.KEY_PRESSED, this::onKeyPressed);
    }

    /**
     * Handles keyboard events for the Sudoku grid.
     *
     *  @param e The user-generated keyboard event
     */
    private void onKeyPressed(KeyEvent e) {
        Object focus = gridPane.getScene().getFocusOwner();
        if (!(focus instanceof SudokuCell cell)) return;

        int r = cell.getRow(), c = cell.getCol();

        // ← BLOQUEAR TECLAS EN CELDAS FIJAS
        if (model.isFixed(r, c)) {
            e.consume();  // ← Ignora el evento de teclado
            return;
        }

        if (e.getCode().isDigitKey()) {
            String ch = e.getText();
            if (ch.matches("[1-6]")) cell.setText(ch);
        } else if (e.getCode() == KeyCode.BACK_SPACE || e.getCode() == KeyCode.DELETE) {
            cell.setText("");
        } else if (e.getCode() == KeyCode.LEFT) moveFocus(r, c - 1);
        else if (e.getCode() == KeyCode.RIGHT) moveFocus(r, c + 1);
        else if (e.getCode() == KeyCode.UP) moveFocus(r - 1, c);
        else if (e.getCode() == KeyCode.DOWN) moveFocus(r + 1, c);
    }

    /**
     * Moves focus to a specific grid cell.
     * @param r Row of the destination cell (0-based)
     * @param c Column of the destination cell (0-based)
     */
    private void moveFocus(int r, int c) {
        if (r < 0 || r >= ISudokuModel.SIZE || c < 0 || c >= ISudokuModel.SIZE) return;
        cells[r][c].requestFocus();
    }

    /**
     * Method Updates all interface cells with the current model values.
     * Applies visual styles based on each cell's state (fixed, conflict, help).
     */
    @Override
    public void updateAllCellsFromModel() {
        for (int r = 0; r < ISudokuModel.SIZE; r++)
            for (int c = 0; c < ISudokuModel.SIZE; c++) {
                int v = model.get(r, c);
                SudokuCell cell = cells[r][c];
                cell.setText(v == 0 ? "" : String.valueOf(v));
                cell.getStyleClass().removeAll("fixed-cell", "conflict-cell", "help-cell");

                if (model.isFixed(r, c)) {
                    cell.getStyleClass().add("fixed-cell");
                    cell.setEditable(false);  // ← NO editable
                    cell.setFocusTraversable(false); // ← No puede recibir foco
                } else {
                    cell.setEditable(true);
                    cell.setFocusTraversable(true);
                }
            }
        performValidation();
    }


    /** Method performs full validation of the dashboard, flagging cells with conflicts.
     * Also updates the help system counters.
     */
    @Override
    public void performValidation() {
        // Limpiar estilos de conflicto
        for (int r = 0; r < ISudokuModel.SIZE; r++)
            for (int c = 0; c < ISudokuModel.SIZE; c++)
                cells[r][c].getStyleClass().remove("conflict-cell");

        // Encontrar conflictos
        List<int[]> conflicts = model.findAllConflicts();
        for (int[] rc : conflicts)
            cells[rc[0]][rc[1]].getStyleClass().add("conflict-cell");

        // Actualizar label de estado
        updateStatusLabel();

        // Verificar si el juego está completo (sin celdas vacías y sin conflictos)
        if (isBoardComplete() && conflicts.isEmpty()) {
            showCongratulations();
        }
    }

    /**
     * Method that provides help to the player by suggesting a valid number for an empty cell.
     * It has no help limits and continues the game cycle.
     */
    @Override
    public void onHelp() {
        int[] empty = model.firstEmptyCell();
        if (empty == null) {
            updateStatusLabelWithMessage("🎉 Puzzle complete!");
            return;
        }
        int r = empty[0], c = empty[1];
        List<Integer> cand = model.candidates(r, c);
        if (cand.isEmpty()) {
            updateStatusLabelWithMessage("❌ No valid candidates here.");
            cells[r][c].getStyleClass().add("conflict-cell");
            return;
        }
        int suggestion = cand.get(0);
        model.set(r, c, suggestion);
        SudokuCell cell = cells[r][c];
        cell.setText(String.valueOf(suggestion));
        cell.getStyleClass().add("help-cell");
        updateStatusLabelWithMessage("💡 Help: placed " + suggestion + " at (" + (r+1) + "," + (c+1) + ").");
        performValidation();
    }

    // METODO PRIVADO AUXILIAR CORREGIDO
    private void updateStatusLabel() {
        List<int[]> conflicts = model.findAllConflicts();
        String statusMessage = conflicts.isEmpty() ? "✅ All good!" : "⚠️ Conflicts found!";
        statusLabel.setText(getPlayerGreeting() + statusMessage);
    }

    // METODO PRIVADO AUXILIAR PARA MENSAJES ESPECÍFICOS
    private void updateStatusLabelWithMessage(String message) {
        statusLabel.setText(getPlayerGreeting() + message);
    }

    // METODO PRIVADO AUXILIAR
    private String getPlayerGreeting() {
        return (playerName != null && !playerName.isEmpty()) ? "Player: " + playerName + " | " : "";
    }

    // Función para validar si el tablero completo está correcto
    private boolean isBoardComplete() {
        for (int r = 0; r < ISudokuModel.SIZE; r++) {
            for (int c = 0; c < ISudokuModel.SIZE; c++) {
                // Si hay alguna celda vacía (0) o con conflicto, no está completo
                if (model.get(r, c) == 0) {
                    return false;
                }
            }
        }

        // Además de no tener celdas vacías, debe no tener conflictos
        List<int[]> conflicts = model.findAllConflicts();
        return conflicts.isEmpty();
    }

    private void showCongratulations() {
        // Aplicar estilo de completado a todas las celdas
        for (int r = 0; r < ISudokuModel.SIZE; r++) {
            for (int c = 0; c < ISudokuModel.SIZE; c++) {
                cells[r][c].getStyleClass().add("completed-cell");
            }
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("🎉 ¡Felicidades!");
        alert.setHeaderText("¡JUEGO COMPLETADO!");
        alert.setContentText(getCongratulationsMessage());

        ButtonType okButton = new ButtonType("¡Jugar de nuevo!");
        alert.getButtonTypes().setAll(okButton);

        alert.showAndWait().ifPresent(response -> {
            // Remover estilo de completado al reiniciar
            for (int r = 0; r < ISudokuModel.SIZE; r++) {
                for (int c = 0; c < ISudokuModel.SIZE; c++) {
                    cells[r][c].getStyleClass().remove("completed-cell");
                }
            }
            resetGame();
        });
    }

    private String getCongratulationsMessage() {
        String playerText = (playerName != null && !playerName.isEmpty()) ?
                "¡Felicidades " + playerName + "! " : "¡Felicidades! ";

        return playerText + "Has completado exitosamente el Sudoku 6x6.\n\n" +
                "¡Eres un verdadero maestro del Sudoku!";
    }

    private void resetGame() {
        model.loadPuzzle(starter);
        updateAllCellsFromModel();
        statusLabel.setText("🔁 ¡Nuevo juego! Good luck " + (playerName != null ? playerName : "") + "!");
    }

}