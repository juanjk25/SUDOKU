package com.example.sudoku.controller;

import com.example.sudoku.model.sudokuModel;
import com.example.sudoku.view.sudokuCell;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.GridPane;
import java.util.*;

/**
 * Controller for Sudoku UI.
 * Connects model and view, handles input, validation and help.
 */
public class SudokuController {

    @FXML private GridPane gridPane;
    @FXML private Button btnHelp; 
    @FXML private Button btnReset;
    @FXML private Label statusLabel;

    private final sudokuCell model = new sudokuModel ();
    private final sudokuModel[][] cells = new sudokuCell[sudokuModel.SIZE][sudokuModel.SIZE];

    private final int[][] starter = {
            {0, 4, 0, 0, 2, 0},
            {1, 0, 3, 0, 0, 5},
            {0, 2, 0, 6, 0, 0},
            {0, 0, 6, 0, 3, 0},
            {5, 0, 0, 3, 0, 2},
            {0, 3, 0, 0, 6, 0}
    };

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
            statusLabel.setText("🔁 Puzzle reset!");
        });
    }

    private void buildGrid() {
        gridPane.getChildren().clear();
        for (int r = 0; r < sudokuModel.SIZE; r++) {
            for (int c = 0; c < sudokuModel.SIZE; c++) {
                sudokuCell cell = new sudokuCell(r, c);
                cells[r][c] = cell;

                cell.textProperty().addListener((obs, oldV, newV) -> {
                    if (newV == null || newV.isEmpty()) model.set(r, c, 0);
                    else model.set(r, c, Integer.parseInt(newV));
                    performValidation();
                });

                gridPane.add(cell, c, r);
            }
        }
    }

    private void attachKeyboardHandlers() {
        gridPane.addEventFilter(KeyEvent.KEY_PRESSED, this::onKeyPressed);
    }

    private void onKeyPressed(KeyEvent e) {
        Object focus = gridPane.getScene().getFocusOwner();
        if (!(focus instanceof SudokuCell cell)) return;
        int r = cell.getRow(), c = cell.getCol();

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

    private void moveFocus(int r, int c) {
        if (r < 0 || r >= sudokuModel.SIZE || c < 0 || c >= sudokuModel.SIZE) return;
        cells[r][c].requestFocus();
    }

    private void updateAllCellsFromModel() {
        for (int r = 0; r < sudokuModel.SIZE; r++)
            for (int c = 0; c < sudokuModel.SIZE; c++) {
                int v = model.get(r, c);
                sudokuCell cell = cells[r][c];
                cell.setText(v == 0 ? "" : String.valueOf(v));
                cell.getStyleClass().removeAll("fixed-cell", "conflict-cell", "help-cell");
                if (model.isFixed(r, c)) {
                    cell.getStyleClass().add("fixed-cell");
                    cell.setEditable(false);
                } else cell.setEditable(true);
            }
        performValidation();
    }

    private void performValidation() {
        for (int r = 0; r < sudokuModel.SIZE; r++)
            for (int c = 0; c < sudokuModel.SIZE; c++)
                cells[r][c].getStyleClass().remove("conflict-cell");

        List<int[]> conflicts = model.findAllConflicts();
        for (int[] rc : conflicts)
            cells[rc[0]][rc[1]].getStyleClass().add("conflict-cell");

        statusLabel.setText(conflicts.isEmpty() ? "✅ All good!" : "⚠️ Conflicts found!");
    }

    private void onHelp() {
        int[] empty = model.firstEmptyCell();
        if (empty == null) {
            statusLabel.setText("🎉 Puzzle complete!");
            return;
        }
        int r = empty[0], c = empty[1];
        List<Integer> cand = model.candidates(r, c);
        if (cand.isEmpty()) {
            statusLabel.setText("❌ No valid candidates here.");
            cells[r][c].getStyleClass().add("conflict-cell");
            return;
        }
        int suggestion = cand.get(0);
        model.set(r, c, suggestion);
        sudokuCell cell = cells[r][c];
        cell.setText(String.valueOf(suggestion));
        cell.getStyleClass().add("help-cell");
        statusLabel.setText("💡 Help: placed " + suggestion + " at (" + (r+1) + "," + (c+1) + ").");
        performValidation();
    }
}
