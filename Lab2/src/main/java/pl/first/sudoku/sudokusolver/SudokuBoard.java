/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pl.first.sudoku.sudokusolver;

import java.util.Random;

/**
 *
 * @author zhuma
 * SudokuBoard class represents a 9x9 Sudoku puzzle board
 * It provides methods to generate and access a valid Sudoku board
 */
public class SudokuBoard {
    private static final int BOARD_SIZE = 9;
    private static final int SUBSECTION_SIZE = 3;
    private static final int BOARD_START_INDEX = 0;
    private static final int NO_VALUE = 0;
    private static final int MIN_VALUE = 1;
    private static final int MAX_VALUE = 9;

    private int[][] board;
    
    private Random random;
    
    /**
     * Constructor for SudokuBoard
     * Initializes an empty board
     */
    public SudokuBoard() {
        board = new int[BOARD_SIZE][BOARD_SIZE];
        random = new Random();
    }
    
    /**
     * Fills the board with valid values according to Sudoku rules
     * Uses a backtracking algorithm
     * 
     * @return true if the board was successfully filled, false otherwise
     */
    public boolean fillBoard() {
        clearBoard();

        return backtrack(BOARD_START_INDEX, BOARD_START_INDEX);
    }
    
    /**
     * Gets the value at specified coordinates
     * 
     * @param row row coordinate (0-8)
     * @param col column coordinate (0-8)
     * @return value at the specified position
     * @throws IllegalArgumentException if coordinates are invalid
     */
    public int getValueAt(int row, int col) {
        if (row < 0 || row >= BOARD_SIZE || col < 0 || col >= BOARD_SIZE) {
            throw new IllegalArgumentException("Invalid board coordinates");
        }
        return board[row][col];
    }
    
    /**
     * Clears the board by setting all positions to NO_VALUE
     */
    private void clearBoard() {
        for (int row = BOARD_START_INDEX; row < BOARD_SIZE; row++) {
            for (int col = BOARD_START_INDEX; col < BOARD_SIZE; col++) {
                board[row][col] = NO_VALUE;
            }
        }
    }
    
    /**
     * Recursive backtracking algorithm to fill the board
     * For each position, try values 1-9 in random order
     * 
     * @param row current row
     * @param col current column
     * @return true if the board is filled successfully, false otherwise
     */
    private boolean backtrack(int row, int col) {
        if (row == BOARD_SIZE) {
            return true;
        }

        int nextRow = (col == BOARD_SIZE - 1) ? row + 1 : row;
        int nextCol = (col == BOARD_SIZE - 1) ? 0 : col + 1;

        if (board[row][col] != NO_VALUE) {
            return backtrack(nextRow, nextCol);
        }

        int[] shuffledValues = getShuffledValues();

        for (int value : shuffledValues) {
            if (isValidPlacement(row, col, value)) {
                board[row][col] = value;

                if (backtrack(nextRow, nextCol)) {
                    return true;
                }

                board[row][col] = NO_VALUE;
            }
        }

        return false;
    }
    
    /**
     * Generate an array of integers from 1-9 in random order
     * 
     * @return shuffled array of integers 1-9
     */
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
    
    /**
     * Checks if placing a value at the specified position is valid
     * 
     * @param row row coordinate
     * @param col column coordinate
     * @param value value to check
     * @return true if the placement is valid, false otherwise
     */
    private boolean isValidPlacement(int row, int col, int value) {
        return isRowValid(row, value) 
                && isColumnValid(col, value) 
                && isBoxValid(row, col, value);
    }
    
    /**
     * Checks if placing a value in the specified row is valid
     * 
     * @param row row to check
     * @param value value to check
     * @return true if the placement is valid, false otherwise
     */
    private boolean isRowValid(int row, int value) {
        for (int col = BOARD_START_INDEX; col < BOARD_SIZE; col++) {
            if (board[row][col] == value) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Checks if placing a value in the specified column is valid
     * 
     * @param col column to check
     * @param value value to check
     * @return true if the placement is valid, false otherwise
     */
    private boolean isColumnValid(int col, int value) {
        for (int row = BOARD_START_INDEX; row < BOARD_SIZE; row++) {
            if (board[row][col] == value) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Checks if placing a value in the 3x3 box containing the specified position is valid
     * 
     * @param row row coordinate
     * @param col column coordinate
     * @param value value to check
     * @return true if the placement is valid, false otherwise
     */
    private boolean isBoxValid(int row, int col, int value) {
        int boxRowStart = (row / SUBSECTION_SIZE) * SUBSECTION_SIZE;
        int boxColStart = (col / SUBSECTION_SIZE) * SUBSECTION_SIZE;
        
        for (int r = 0; r < SUBSECTION_SIZE; r++) {
            for (int c = 0; c < SUBSECTION_SIZE; c++) {
                if (board[boxRowStart + r][boxColStart + c] == value) {
                    return false;
                }
            }
        }
        return true;
    }
}
