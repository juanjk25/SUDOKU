package com.example.sudokubeta.controller;


/**
 * Interface defining the contract for a Sudoku controller.
 *
 * <p>Implementations manage UI initialization, user help actions,
 * board validation and synchronization between view and model.
 */
public interface ISudokuController {
    /**
     * Called by the JavaFX framework after the FXML is loaded to initialize
     * controller state, build UI components and attach event handlers.
     */
    void initialize();

    /**
     * Provide a hint or assistance to the user (for example, fill a valid candidate).
     *
     * <p>Implementations should update both the model and the view accordingly.
     */
    void onHelp();


    /**
     * Perform full board validation: detect conflicts, update visual markers
     * and any internal validation state.
     *
     * <p>Should be safe to call repeatedly as user input changes.
     */
    void performValidation();


    /**
     * Synchronize all view cells with the current state of the model.
     *
     * <p>Typically updates text, styles and editability for each cell.
     */
    void updateAllCellsFromModel();


    /**
     * Set the current player's name so the controller can personalize UI/messages.
     *
     * @param playerName the player's display name; may be {@code null} to clear
     */
    void setPlayerName(String playerName);
}