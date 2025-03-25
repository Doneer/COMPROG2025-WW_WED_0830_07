/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pl.first.sudoku.sudokusolver;

import java.util.Random;
/**
 *
 * @author Zhmaggernaut
 */
public class BacktrackingSudokuSolver implements ISudokuSolver{
    private static final int BOARD_SIZE = 9;
    private static final int SUBSECTION_SIZE = 3;
    private static final int BOARD_START_INDEX = 0;
    private static final int NO_VALUE = 0;
    private static final int MIN_VALUE = 1;
    private static final int MAX_VALUE = 9;
    
    private Random random;
    
    public BacktrackingSudokuSolver() {
        random = new Random();
    }
    
    @Override
    public boolean solve(SudokuBoard board) {
        for (int row = BOARD_START_INDEX; row < BOARD_SIZE; row++) {
            for (int col = BOARD_START_INDEX; col < BOARD_SIZE; col++) {
                board.setValueAt(row, col, NO_VALUE);
            }
        }
        
        return backtrack(board, BOARD_START_INDEX, BOARD_START_INDEX);
    }
    private boolean backtrack(SudokuBoard board, int row, int col) {
        if (row == BOARD_SIZE) {
            return true;
        }

        int nextRow = (col == BOARD_SIZE - 1) ? row + 1 : row;
        int nextCol = (col == BOARD_SIZE - 1) ? 0 : col + 1;

        if (board.getValueAt(row, col) != NO_VALUE) {
            return backtrack(board, nextRow, nextCol);
        }

        int[] shuffledValues = getShuffledValues();

        for (int value : shuffledValues) {
            if (isValidPlacement(board, row, col, value)) {
                board.setValueAt(row, col, value);

                if (backtrack(board, nextRow, nextCol)) {
                    return true;
                }

                board.setValueAt(row, col, NO_VALUE);
            }
        }

        return false;
    }
    private int[] getShuffledValues() {
        int[] values = new int[MAX_VALUE];

        for (int i = 0; i < MAX_VALUE; i++) {
            values[i] = i + 1;
        }

        for (int i = MAX_VALUE - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int temp = values[i];
            values[i] = values[j];
            values[j] = temp;
        }
        
        return values;
    }
    
    private boolean isValidPlacement(SudokuBoard board, int row, int col, int value) {
        return isRowValid(board, row, value) 
                && isColumnValid(board, col, value) 
                && isBoxValid(board, row, col, value);
    }
    
    private boolean isRowValid(SudokuBoard board, int row, int value) {
        for (int col = BOARD_START_INDEX; col < BOARD_SIZE; col++) {
            if (board.getValueAt(row, col) == value) {
                return false;
            }
        }
        return true;
    }
    
    private boolean isColumnValid(SudokuBoard board, int col, int value) {
        for (int row = BOARD_START_INDEX; row < BOARD_SIZE; row++) {
            if (board.getValueAt(row, col) == value) {
                return false;
            }
        }
        return true;
    }
    
    private boolean isBoxValid(SudokuBoard board, int row, int col, int value) {
        int boxRowStart = (row / SUBSECTION_SIZE) * SUBSECTION_SIZE;
        int boxColStart = (col / SUBSECTION_SIZE) * SUBSECTION_SIZE;
        
        for (int r = 0; r < SUBSECTION_SIZE; r++) {
            for (int c = 0; c < SUBSECTION_SIZE; c++) {
                if (board.getValueAt(boxRowStart + r, boxColStart + c) == value) {
                    return false;
                }
            }
        }
        return true;
    }
}
