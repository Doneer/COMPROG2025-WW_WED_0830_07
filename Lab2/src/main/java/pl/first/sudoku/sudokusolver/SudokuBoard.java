/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package pl.first.sudoku.sudokusolver;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * SudokuBoard class represents a 9x9 Sudoku puzzle board.
 * It provides methods to generate and access a valid Sudoku board.
 * @author zhuma
 */

public class SudokuBoard implements Serializable, Cloneable {
    private static final int BOARD_SIZE = 9;
    private static final int SUBSECTION_SIZE = 3;
    private static final int NO_VALUE = 0;
    
    private SudokuField[][] board;
    private SudokuSolver solver;
    
    public SudokuBoard(SudokuSolver solver) {
        this.board = new SudokuField[BOARD_SIZE][BOARD_SIZE];
        this.solver = solver;

        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                board[row][col] = new SudokuField();
            }
        }
    }
    
    public boolean solveGame() {
        return solver.solve(this);
    }
    
    public int getValueAt(int row, int col) {
        if (row < 0 || row >= BOARD_SIZE || col < 0 || col >= BOARD_SIZE) {
            throw new IllegalArgumentException("Invalid board coordinates");
        }
        return board[row][col].getFieldValue();
    }
    
    public void setValueAt(int row, int col, int value) {
        if (row < 0 || row >= BOARD_SIZE || col < 0 || col >= BOARD_SIZE) {
            throw new IllegalArgumentException("Invalid board coordinates");
        }
        if (value < NO_VALUE || value > 9) {
            throw new IllegalArgumentException("Value must be between 0 and 9");
        }
        board[row][col].setFieldValue(value);
    }
    
    public SudokuRow getRow(int y) {
        if (y < 0 || y >= BOARD_SIZE) {
            throw new IllegalArgumentException("Invalid row index");
        }
        
        List<SudokuField> fields = new ArrayList<>();
        for (int col = 0; col < BOARD_SIZE; col++) {
            fields.add(board[y][col]);
        }
        
        return new SudokuRow(fields);
    }
    
    public SudokuColumn getColumn(int x) {
        if (x < 0 || x >= BOARD_SIZE) {
            throw new IllegalArgumentException("Invalid column index");
        }
        
        List<SudokuField> fields = new ArrayList<>();
        for (int row = 0; row < BOARD_SIZE; row++) {
            fields.add(board[row][x]);
        }
        
        return new SudokuColumn(fields);
    }
    
    public SudokuBox getBox(int x, int y) {
        if (x < 0 || x >= SUBSECTION_SIZE || y < 0 || y >= SUBSECTION_SIZE) {
            throw new IllegalArgumentException("Invalid box coordinates");
        }
        
        List<SudokuField> fields = new ArrayList<>();
        int startRow = y * SUBSECTION_SIZE;
        int startCol = x * SUBSECTION_SIZE;
        
        for (int row = 0; row < SUBSECTION_SIZE; row++) {
            for (int col = 0; col < SUBSECTION_SIZE; col++) {
                fields.add(board[startRow + row][startCol + col]);
            }
        }
        
        return new SudokuBox(fields);
    }
    
    public boolean isValid() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            if (!getRow(row).verify()) {
                return false;
            }
        }

        for (int col = 0; col < BOARD_SIZE; col++) {
            if (!getColumn(col).verify()) {
                return false;
            }
        }

        for (int boxRow = 0; boxRow < SUBSECTION_SIZE; boxRow++) {
            for (int boxCol = 0; boxCol < SUBSECTION_SIZE; boxCol++) {
                if (!getBox(boxCol, boxRow).verify()) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    public boolean fillBoard() {
        return solveGame();
    }
    
    public SudokuField getSudokuField(int row, int col) {
        if (row < 0 || row >= BOARD_SIZE || col < 0 || col >= BOARD_SIZE) {
            throw new IllegalArgumentException("Invalid board coordinates");
        }
        return board[row][col];
    }
    
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                result.append(board[row][col].getFieldValue());
                result.append(' ');
            }
            result.append('\n');
        }
        return result.toString();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        SudokuBoard other = (SudokuBoard) obj;
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (!board[row][col].equals(other.board[row][col])) {
                    return false;
                }
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        int result = 17;
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                result = 31 * result + board[row][col].hashCode();
            }
        }
        return result;
    }
    
    @Override
    public SudokuBoard clone() {
        try {
            SudokuBoard cloned = (SudokuBoard) super.clone();
            cloned.board = new SudokuField[BOARD_SIZE][BOARD_SIZE];
            
            for (int row = 0; row < BOARD_SIZE; row++) {
                for (int col = 0; col < BOARD_SIZE; col++) {
                    cloned.board[row][col] = this.board[row][col].clone();
                }
            }
            
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
