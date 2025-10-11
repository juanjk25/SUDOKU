package com.example.sudoku.model;

import java.util.*;


public class sudokuModel {
    public static final int SIZE = 6;
    private final int[][] board = new int[SIZE][SIZE];
    private final boolean[][] fixed = new boolean[SIZE][SIZE];

    /** Load puzzle (0 = empty, 1–6 = value). */
    public void loadPuzzle(int[][] puzzle) {
        clearAll();
        for (int r = 0; r < SIZE; r++)
            for (int c = 0; c < SIZE; c++) {
                int v = puzzle[r][c];
                board[r][c] = v;
                fixed[r][c] = (v != 0);
            }
    }

    /** Clear board. */
    public void clearAll() {
        for (int r = 0; r < SIZE; r++) Arrays.fill(board[r], 0);
        for (int r = 0; r < SIZE; r++) Arrays.fill(fixed[r], false);
    }

    public int get(int row, int col) { return board[row][col]; }
    public boolean isFixed(int row, int col) { return fixed[row][col]; }

    public void set(int row, int col, int value) {
        if (value < 0 || value > SIZE)
            throw new IllegalArgumentException("Invalid value");
        board[row][col] = value;
    }

    /** Checks if placing a value at (row, col) is valid. */
    public boolean isValidPlacement(int row, int col, int value) {
        if (value == 0) return true;

        for (int c = 0; c < SIZE; c++)
            if (c != col && board[row][c] == value) return false;

        for (int r = 0; r < SIZE; r++)
            if (r != row && board[r][col] == value) return false;

        int blockRow = (row / 2) * 2;
        int blockCol = (col / 3) * 3;
        for (int r = blockRow; r < blockRow + 2; r++)
            for (int c = blockCol; c < blockCol + 3; c++)
                if (!(r == row && c == col) && board[r][c] == value)
                    return false;

        return true;
    }

    /** Finds all conflicting cells (duplicates). */
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

    /** Suggests valid numbers for a given cell. */
    public List<Integer> candidates(int row, int col) {
        List<Integer> valid = new ArrayList<>();
        if (board[row][col] != 0) return valid;
        for (int v = 1; v <= SIZE; v++)
            if (isValidPlacement(row, col, v)) valid.add(v);
        return valid;
    }

    public int[] firstEmptyCell() {
        for (int r = 0; r < SIZE; r++)
            for (int c = 0; c < SIZE; c++)
                if (board[r][c] == 0) return new int[]{r, c};
        return null;
    }
}
