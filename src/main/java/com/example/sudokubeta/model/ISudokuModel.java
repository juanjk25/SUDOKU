package com.example.sudokubeta.model;

import java.util.List;


/**
 * Interface defining core operations for a Sudoku model.
 *
 * <p>This contract provides methods to load and clear puzzles, query and modify
 * cell values, verify placements, discover conflicts and compute candidates.
 * Implementations are expected to use a fixed board size defined by {@link #SIZE}.
 */
public interface ISudokuModel {

    /**
     * Board size (number of rows and columns) for this Sudoku variant.
     *
     * <p>All implementations should assume a square board of SIZE x SIZE.
     */
    int SIZE = 6;


    /**
     * Load the provided puzzle into the model.
     *
     * @param puzzle a two-dimensional int array with dimensions {@link #SIZE} x {@link #SIZE};
     *               use 0 to represent empty cells
     */
    void loadPuzzle(int[][] puzzle);


    /**
     * Clear the entire board, setting every cell to empty (0) and removing any fixed marks.
     */
    void clearAll();


    /**
     * Get the value stored at the specified cell.
     *
     * @param row zero-based row index (0 .. {@link #SIZE}-1)
     * @param col zero-based column index (0 .. {@link #SIZE}-1)
     * @return the value at the cell, or 0 if the cell is empty
     */
    int get(int row, int col);


    /**
     * Determine whether the cell at the given coordinates is fixed (part of the initial puzzle).
     *
     * @param row zero-based row index
     * @param col zero-based column index
     * @return {@code true} if the cell is fixed and should not be edited by the player
     */
    boolean isFixed(int row, int col);


    /**
     * Set the value of a cell.
     *
     * @param row   zero-based row index
     * @param col   zero-based column index
     * @param value the value to place (use 0 to clear the cell)
     */
    void set(int row, int col, int value);


    /**
     * Check whether placing a value at the given position would be valid according
     * to Sudoku constraints (no duplicates in the same row, column or block).
     *
     * @param row   zero-based row index
     * @param col   zero-based column index
     * @param value candidate value to test
     * @return {@code true} if the placement is valid, {@code false} otherwise
     */
    boolean isValidPlacement(int row, int col, int value);


    /**
     * Find all cells that are currently involved in any rule violation (conflict).
     *
     * @return a list of int arrays where each element is a two-item array {row, col}
     *         identifying a conflicting cell; the list is empty if there are no conflicts
     */
    List<int[]> findAllConflicts();


    /**
     * Compute the valid candidate values for the specified empty cell.
     *
     * @param row zero-based row index
     * @param col zero-based column index
     * @return a list of integers representing allowed values for the cell;
     *         returns an empty list if the cell is not empty or no candidates exist
     */
    List<Integer> candidates(int row, int col);


    /**
     * Return the coordinates of the first empty cell found in row-major order.
     *
     * @return a two-element int array {@code {row, col}} for the first empty cell,
     *         or {@code null} if the board is full
     */
    int[] firstEmptyCell();
}