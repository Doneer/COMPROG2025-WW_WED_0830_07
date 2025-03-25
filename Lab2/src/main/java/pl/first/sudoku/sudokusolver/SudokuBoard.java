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
    private static final int NO_VALUE = 0;
    
    private int[][] board;
    private ISudokuSolver solver;
    public SudokuBoard(ISudokuSolver solver) {
        this.board = new int[BOARD_SIZE][BOARD_SIZE];
        this.solver = solver;
    }
    
    public boolean solveGame() {
        return solver.solve(this);
    }
    
    public int getValueAt(int row, int col) {
        if (row < 0 || row >= BOARD_SIZE || col < 0 || col >= BOARD_SIZE) {
            throw new IllegalArgumentException("Invalid board coordinates");
        }
        return board[row][col];
    }
    
    public void setValueAt(int row, int col, int value) {
        if (row < 0 || row >= BOARD_SIZE || col < 0 || col >= BOARD_SIZE) {
            throw new IllegalArgumentException("Invalid board coordinates");
        }
        if (value < NO_VALUE || value > 9) {
            throw new IllegalArgumentException("Value must be between 0 and 9");
        }
        board[row][col] = value;
    }
    
    public boolean isValid() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            boolean[] used = new boolean[10];
            for (int col = 0; col < BOARD_SIZE; col++) {
                int value = board[row][col];
                if (value != NO_VALUE) {
                    if (used[value]) {
                        return false;
                    }
                    used[value] = true;
                }
            }
        }
        
        for (int col = 0; col < BOARD_SIZE; col++) {
            boolean[] used = new boolean[10];
            for (int row = 0; row < BOARD_SIZE; row++) {
                int value = board[row][col];
                if (value != NO_VALUE) {
                    if (used[value]) {
                        return false;
                    }
                    used[value] = true;
                }
            }
        }
        
        for (int boxRow = 0; boxRow < 3; boxRow++) {
            for (int boxCol = 0; boxCol < 3; boxCol++) {
                boolean[] used = new boolean[10];
                for (int row = 0; row < 3; row++) {
                    for (int col = 0; col < 3; col++) {
                        int value = board[boxRow * 3 + row][boxCol * 3 + col];
                        if (value != NO_VALUE) {
                            if (used[value]) {
                                return false;
                            }
                            used[value] = true;
                        }
                    }
                }
            }
        }
        
        return true;
    }
    
    public boolean fillBoard() {
        return solveGame();
    }
}
