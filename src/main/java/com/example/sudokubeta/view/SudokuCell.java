package com.example.sudokubeta.view;

import javafx.scene.control.TextField;


/**
 * UI component representing a single Sudoku cell.
 *
 * <p>This class extends {@link TextField} to provide row/column metadata,
 * input filtering (only digits 1-6), sizing and helper methods to toggle
 * style classes.
 *
 * @since 2025
 */
public class SudokuCell extends TextField {

    /**
     * Zero-based row index of the cell within the board.
     */
    private final int row;

    /**
     * Zero-based column index of the cell within the board.
     */
    private final int col;


    /**
     * Create a SudokuCell for the specified board coordinates.
     *
     * <p>The constructor configures preferred size, base style and installs
     * a listener that enforces a single digit between 1 and 6. Any invalid
     * input will be cleared.
     *
     * @param row zero-based row index
     * @param col zero-based column index
     */
    public SudokuCell(int row, int col) {
        this.row = row;
        this.col = col;
        setPrefWidth(48);
        setPrefHeight(48);
        setStyle("-fx-font-size: 18px; -fx-alignment: center;");

        textProperty().addListener((obs, oldV, newV) -> {
            if (newV == null || newV.isEmpty()) return;
            String s = newV.trim();
            if (s.length() > 1) s = s.substring(0, 1);
            if (!s.matches("[1-6]")) setText("");
            else setText(s);
        });
    }


    /**
     * Returns the zero-based row index of this cell.
     *
     * @return row index (0 .. SIZE-1)
     */
    public int getRow() { return row; }


    /**
     * Returns the zero-based column index of this cell.
     *
     * @return column index (0 .. SIZE-1)
     */
    public int getCol() { return col; }


    /**
     * Add or remove a CSS style class for this cell.
     *
     * <p>If {@code apply} is {@code true} the {@code styleClass} will be added
     * if not already present. If {@code apply} is {@code false} the class
     * will be removed if present.
     *
     * @param styleClass the CSS class name to toggle
     * @param apply {@code true} to add the class, {@code false} to remove it
     */
    public void setCellStyle(String styleClass, boolean apply) {
        if (apply) {
            if (!getStyleClass().contains(styleClass)) {
                getStyleClass().add(styleClass);
            }
        } else {
            getStyleClass().remove(styleClass);
        }
    }
}