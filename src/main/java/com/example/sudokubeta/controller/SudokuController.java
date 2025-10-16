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

    /**
     * Container for the board's grid of cells, injected by FXML.
     */
    @FXML private GridPane gridPane;

    /**
     * Button to request help (suggestion) from the user, injected by FXML.
     */
    @FXML private Button btnHelp;

    /**
     * Button to reset the dashboard to its initial state, injected by FXML.
     */
    @FXML private Button btnReset;

    /**
     * Label to display the current game state and messages to the user, injected by FXML.
     */
    @FXML private Label statusLabel;

    /**
     * Sudoku model containing the logic and state of the board.
     */
    private final ISudokuModel model = new SudokuModel();

    /**
     * Array of visual components representing the board cells.
     * The size is defined by {@link ISudokuModel#SIZE}.
     */
    private final SudokuCell[][] cells = new SudokuCell[ISudokuModel.SIZE][ISudokuModel.SIZE];

    /**
     * Current player name. Can be {@code null} if not set.
     */
    private String playerName;

    /**
     * Initial configuration (puzzle starter) used to load or reset the board.
     */
    private final int[][] starter = {
            {1, 4, 0, 0, 2, 0},
            {0, 0, 3, 0, 0, 5},
            {0, 2, 0, 0, 0, 6},
            {0, 0, 6, 0, 3, 0},
            {5, 0, 0, 3, 0, 2},
            {0, 3, 0, 0, 1, 0}
    };


    /**
     * Initialization automatically invoked by JavaFX after loading the FXML.
     *
     * <p>Performs the following actions:
     * <ul>
     *  <li>Loads the initial puzzle into the model.</li>
     *  <li>Builds the grid of cells in the interface.</li>
     *  <li>Attaches keyboard handlers and button events.</li>
     *  <li>Updates the cells and the state label.</li>
     * </ul>
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
     * Adds the necessary keyboard event handlers for navigation and input.
     *
     * <p>Registers a filter for {@link KeyEvent#KEY_PRESSED} on the {@link #gridPane}.
     */
    private void attachKeyboardHandlers() {
        gridPane.addEventFilter(KeyEvent.KEY_PRESSED, this::onKeyPressed);
    }

    /**
     * Handles keyboard events within the grid.
     *
     * <p>Supports digit input (1-6), deletion, and arrow navigation.
     * Ignores events when the current cell is marked as fixed in the model.
     *
     * @param e user-generated keyboard event
     */
    private void onKeyPressed(KeyEvent e) {
        Object focus = gridPane.getScene().getFocusOwner();
        if (!(focus instanceof SudokuCell cell)) return;

        int r = cell.getRow(), c = cell.getCol();

        // ← LOCK KEYS ON FIXED CELLS
        if (model.isFixed(r, c)) {
            e.consume();  // ← Ignore the keyboard event
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
     * Synchronizes all visual cells with the current model values.
     *
     * <p>Updates text, styles, and editing properties based on whether each cell is fixed,
     * conflicting, or marked as helpful.
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
                    cell.setEditable(false);  // ← NOT editable
                    cell.setFocusTraversable(false); // ← Cannot receive focus
                } else {
                    cell.setEditable(true);
                    cell.setFocusTraversable(true);
                }
            }
        performValidation();
    }


    /**
     * Performs full board validation, flags conflicts, and updates the status.
     *
     * <p>If the board is complete and conflict-free, displays the congratulations screen.
     */
    @Override
    public void performValidation() {
        // Clear conflicting styles
        for (int r = 0; r < ISudokuModel.SIZE; r++)
            for (int c = 0; c < ISudokuModel.SIZE; c++)
                cells[r][c].getStyleClass().remove("conflict-cell");

        // Find conflicts
        List<int[]> conflicts = model.findAllConflicts();
        for (int[] rc : conflicts)
            cells[rc[0]][rc[1]].getStyleClass().add("conflict-cell");

        // Update status label
        updateStatusLabel();

        // Check if the game is complete (no empty cells and no conflicts)
        if (isBoardComplete() && conflicts.isEmpty()) {
            showCongratulations();
        }
    }

    /**
     * Provides a hint (help) by placing a valid number in the first empty cell.
     *
     * <p>Selects the first available candidate and applies it; flags conflicts if there are no
     * valid candidates.
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

    /**
     * Updates the status label based on current conflicts and player name.
     */
    private void updateStatusLabel() {
        List<int[]> conflicts = model.findAllConflicts();
        String statusMessage = conflicts.isEmpty() ? "✅ All good!" : "⚠️ Conflicts found!";
        statusLabel.setText(getPlayerGreeting() + statusMessage);
    }

    /**
     * Displays a specific message in the status tag, preserving the player's greeting.
     *
     * @param message : Text of the message to display
     */
    private void updateStatusLabelWithMessage(String message) {
        statusLabel.setText(getPlayerGreeting() + message);
    }


    /**
     * Constructs the greeting prefix for the status tag based on the player's name.
     *
     * @return prefix with the player's name or an empty string if there is no name
     */
    private String getPlayerGreeting() {
        return (playerName != null && !playerName.isEmpty()) ? "Player: " + playerName + " | " : "";
    }


    /**
     * Checks if the board is complete (no zeros) and no conflicts are detected.
     *
     * @return {@code true} if the board is complete and valid; {@code false} otherwise.
     */
    private boolean isBoardComplete() {
        for (int r = 0; r < ISudokuModel.SIZE; r++) {
            for (int c = 0; c < ISudokuModel.SIZE; c++) {
                // If there is any empty (0) or conflicting cell, it is not complete
                if (model.get(r, c) == 0) {
                    return false;
                }
            }
        }

        // In addition to not having empty cells, it must not have conflicts
        List<int[]> conflicts = model.findAllConflicts();
        return conflicts.isEmpty();
    }

    /**
     * Displays the congratulations window when the user successfully completes the puzzle.
     *
     * <p>Applies visual completion styles, displays an {@link Alert}, and restarts the game upon acceptance.
     */
    private void showCongratulations() {
        // Apply completion style to all cells
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
            // Remove completion style on restart
            for (int r = 0; r < ISudokuModel.SIZE; r++) {
                for (int c = 0; c < ISudokuModel.SIZE; c++) {
                    cells[r][c].getStyleClass().remove("completed-cell");
                }
            }
            resetGame();
        });
    }

    /**
     * Generates the congratulatory message, including the player's name if available.
     *
     * @return congratulatory message to display in the dialog
     */
    private String getCongratulationsMessage() {
        String playerText = (playerName != null && !playerName.isEmpty()) ?
                "¡Felicidades " + playerName + "! " : "¡Felicidades! ";

        return playerText + "Has completado exitosamente el Sudoku 6x6.\n\n" +
                "¡Eres un verdadero maestro del Sudoku!";
    }

    /**
     * Resets the game to its initial state using the predefined starter configuration.
     *
     * <p>Reloads the puzzle, updates all cells, and resets the status label.
     */
    private void resetGame() {
        model.loadPuzzle(starter);
        updateAllCellsFromModel();
        statusLabel.setText("🔁 ¡Nuevo juego! Good luck " + (playerName != null ? playerName : "") + "!");
    }

}