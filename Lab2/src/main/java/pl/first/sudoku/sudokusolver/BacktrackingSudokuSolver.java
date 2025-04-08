/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package pl.first.sudoku.sudokusolver;

import java.io.Serializable;
import java.util.Random;

/**
 * Implementation of the SudokuSolver interface using a backtracking algorithm.
 * @author Zhmaggernaut
 */
public class BacktrackingSudokuSolver implements SudokuSolver, Serializable {
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
        SudokuRow sudokuRow = board.getRow(row);
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (sudokuRow.getField(i).getFieldValue() == value) {
                return false;
            }
        }
        
        SudokuColumn sudokuColumn = board.getColumn(col);
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (sudokuColumn.getField(i).getFieldValue() == value) {
                return false;
            }
        }
        
        int boxRow = row / SUBSECTION_SIZE;
        int boxCol = col / SUBSECTION_SIZE;
        SudokuBox sudokuBox = board.getBox(boxCol, boxRow);
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (sudokuBox.getField(i).getFieldValue() == value) {
                return false;
            }
        }
        
        return true;
    }
}
