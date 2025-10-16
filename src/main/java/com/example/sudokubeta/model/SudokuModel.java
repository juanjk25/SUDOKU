package com.example.sudokubeta.model;

import java.util.*;


/**
 * Concrete implementation of {@link ISudokuModel} for a 6x6 Sudoku variant.
 *
 * <p>This class holds the board state and fixed-cell markers, and provides
 * utilities to load puzzles, validate placements, find conflicts and compute
 * candidate values for empty cells.
 */
public class SudokuModel implements ISudokuModel {
    /**
     * Board size (number of rows and columns) for this Sudoku variant.
     *
     * <p>Note: this mirrors {@link ISudokuModel#SIZE}.
     */
    public static final int SIZE = 6;


    /**
     * The board values. A value of 0 represents an empty cell.
     *
     * <p>Dimensions: SIZE x SIZE.
     */
    private final int[][] board = new int[SIZE][SIZE];


    /**
     * Flags indicating which cells are fixed (part of the initial puzzle).
     *
     * <p>True means the cell should not be edited by the player.
     */
    private final boolean[][] fixed = new boolean[SIZE][SIZE];


    /**
     * Load a puzzle into the model.
     *
     * <p>Clones values from the provided puzzle into the internal board and
     * marks non-zero cells as fixed.
     *
     * @param puzzle a two-dimensional int array with dimensions {@code SIZE x SIZE};
     *               use {@code 0} to represent empty cells
     */
    public void loadPuzzle(int[][] puzzle) {
        clearAll();
        for (int r = 0; r < SIZE; r++)
            for (int c = 0; c < SIZE; c++) {
                int v = puzzle[r][c];
                board[r][c] = v;
                fixed[r][c] = (v != 0);
            }
    }


    /**
     * Clear the entire board and remove fixed marks.
     *
     * <p>After this call every cell becomes empty ({@code 0}) and non-fixed.
     */
    public void clearAll() {
        for (int r = 0; r < SIZE; r++) Arrays.fill(board[r], 0);
        for (int r = 0; r < SIZE; r++) Arrays.fill(fixed[r], false);
    }


    /**
     * Get the value at the specified cell.
     *
     * @param row zero-based row index (0 .. {@code SIZE-1})
     * @param col zero-based column index (0 .. {@code SIZE-1})
     * @return the value at the specified cell, or {@code 0} if the cell is empty
     */
    public int get(int row, int col) { return board[row][col]; }


    /**
     * Check whether a cell is fixed (part of the initial puzzle).
     *
     * @param row zero-based row index
     * @param col zero-based column index
     * @return {@code true} if the cell is fixed and should not be edited
     */
    public boolean isFixed(int row, int col) { return fixed[row][col]; }



    /**
     * Set the value of a cell.
     *
     * <p>Use {@code 0} to clear the cell.
     *
     * @param row   zero-based row index
     * @param col   zero-based column index
     * @param value the value to place (0 .. {@code SIZE})
     * @throws IllegalArgumentException if {@code value} is out of range
     */
    public void set(int row, int col, int value) {
        if (value < 0 || value > SIZE)
            throw new IllegalArgumentException("Invalid value");
        board[row][col] = value;
    }


    /**
     * Check whether placing a value at the given coordinates is valid according
     * to Sudoku rules (no duplicates in the same row, column or block).
     *
     * <p>Placing {@code 0} is always considered valid.
     *
     * @param row   zero-based row index
     * @param col   zero-based column index
     * @param value candidate value to test
     * @return {@code true} if the placement is valid; {@code false} otherwise
     */
    public boolean isValidPlacement(int row, int col, int value) {
        if (value == 0) return true;

        for (int c = 0; c < SIZE; c++)
            if (c != col && board[row][c] == value) return false;

        for (int r = 0; r < SIZE; r++)
            if (r != row && board[r][col] == value) return false;

        // Blocks are 2x3 for a 6x6 Sudoku (rows x cols)
        int blockRow = (row / 2) * 2;
        int blockCol = (col / 3) * 3;
        for (int r = blockRow; r < blockRow + 2; r++)
            for (int c = blockCol; c < blockCol + 3; c++)
                if (!(r == row && c == col) && board[r][c] == value)
                    return false;

        return true;
    }



    /**
     * Find all cells that are currently involved in any rule violation (duplicates).
     *
     * <p>Returns a list of coordinate pairs {@code {row, col}} for each conflicting cell.
     *
     * @return list of two-element int arrays identifying conflicting cells; empty if none
     */
    public List<int[]> findAllConflicts() {
        List<int[]> conflicts = new ArrayList<>();

        // Rows
        for (int r = 0; r < SIZE; r++) {
            Set<Integer> seen = new HashSet<>();
            for (int c = 0; c < SIZE; c++) {
                int v = board[r][c];
                if (v != 0 && !seen.add(v))
                    for (int cc = 0; cc < SIZE; cc++)
                        if (board[r][cc] == v) conflicts.add(new int[]{r, cc});
            }
        }

        // Columns
        for (int c = 0; c < SIZE; c++) {
            Set<Integer> seen = new HashSet<>();
            for (int r = 0; r < SIZE; r++) {
                int v = board[r][c];
                if (v != 0 && !seen.add(v))
                    for (int rr = 0; rr < SIZE; rr++)
                        if (board[rr][c] == v) conflicts.add(new int[]{rr, c});
            }
        }

        // Blocks (2x3)
        for (int br = 0; br < SIZE; br += 2)
            for (int bc = 0; bc < SIZE; bc += 3) {
                Set<Integer> seen = new HashSet<>();
                for (int r = br; r < br + 2; r++)
                    for (int c = bc; c < bc + 3; c++) {
                        int v = board[r][c];
                        if (v != 0 && !seen.add(v))
                            for (int rr = br; rr < br + 2; rr++)
                                for (int cc = bc; cc < bc + 3; cc++)
                                    if (board[rr][cc] == v)
                                        conflicts.add(new int[]{rr, cc});
                    }
            }
        return conflicts;
    }


    /**
     * Compute valid candidate values for the specified empty cell.
     *
     * @param row zero-based row index
     * @param col zero-based column index
     * @return list of integers representing allowed values for the cell;
     *         returns empty list if the cell is not empty or no candidates exist
     */
    public List<Integer> candidates(int row, int col) {
        List<Integer> valid = new ArrayList<>();
        if (board[row][col] != 0) return valid;
        for (int v = 1; v <= SIZE; v++)
            if (isValidPlacement(row, col, v)) valid.add(v);
        return valid;
    }


    /**
     * Return the coordinates of the first empty cell found in row-major order.
     *
     * @return two-element int array {@code {row, col}} for the first empty cell,
     *         or {@code null} if the board is full
     */
    public int[] firstEmptyCell() {
        for (int r = 0; r < SIZE; r++)
            for (int c = 0; c < SIZE; c++)
                if (board[r][c] == 0) return new int[]{r, c};
        return null;
    }
}