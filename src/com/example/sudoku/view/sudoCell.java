package com.example.sudoku.view;

import javafx.scene.control.TextField;

/**
 * Represents a Sudoku cell in the UI.
 */
public class sudokuCell extends TextField {

    private final int row;
    private final int col;

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

    public int getRow() { return row; }
    public int getCol() { return col; }
}
