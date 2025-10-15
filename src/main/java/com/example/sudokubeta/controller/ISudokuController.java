package com.example.sudokubeta.controller;

/**
 * Interface for Sudoku controller operations
 */
public interface ISudokuController {
    void initialize();
    void onHelp();
    void performValidation();
    void updateAllCellsFromModel();
    void setPlayerName(String playerName);
}