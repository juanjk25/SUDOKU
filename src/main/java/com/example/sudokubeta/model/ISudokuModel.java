package com.example.sudokubeta.model;

import java.util.List;

/**
 * Interface for Sudoku model operations
 */
public interface ISudokuModel {
    int SIZE = 6;

    void loadPuzzle(int[][] puzzle);
    void clearAll();
    int get(int row, int col);
    boolean isFixed(int row, int col);
    void set(int row, int col, int value);
    boolean isValidPlacement(int row, int col, int value);
    List<int[]> findAllConflicts();
    List<Integer> candidates(int row, int col);
    int[] firstEmptyCell();
}