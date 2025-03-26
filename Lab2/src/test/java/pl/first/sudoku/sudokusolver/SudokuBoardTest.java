/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */

package pl.first.sudoku.sudokusolver;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author zhuma
 */
public class SudokuBoardTest {
    private static final int BOARD_SIZE = 9;
    private static final int SUBSECTION_SIZE = 3;
    
    @Test
    public void testSolveGameGeneratesValidBoard() {
        ISudokuSolver solver = new BacktrackingSudokuSolver();
        SudokuBoard board = new SudokuBoard(solver);
        
        boolean result = board.solveGame();
        
        //Check if solving was successful
        assertTrue(result, "Board solving should be successful");
        
        //Check all rows for duplicates
        for (int row = 0; row < BOARD_SIZE; row++) {
            Set<Integer> rowNumbers = new HashSet<>();
            for (int col = 0; col < BOARD_SIZE; col++) {
                int value = board.getValueAt(row, col);
                assertTrue(value >= 1 && value <= 9, 
                        "Value at (" + row + "," + col + ") should be between 1 and 9");
                assertTrue(rowNumbers.add(value), 
                        "Duplicate value " + value + " found in row " + row);
            }
            assertEquals(9, rowNumbers.size(), "Row " + row + " should contain all 9 digits");
        }
        
        //Check all columns for duplicates
        for (int col = 0; col < BOARD_SIZE; col++) {
            Set<Integer> colNumbers = new HashSet<>();
            for (int row = 0; row < BOARD_SIZE; row++) {
                int value = board.getValueAt(row, col);
                assertTrue(colNumbers.add(value), 
                        "Duplicate value " + value + " found in column " + col);
            }
            assertEquals(9, colNumbers.size(), "Column " + col + " should contain all 9 digits");
        }
        
        //Check all 3x3 boxes for duplicates
        for (int boxRow = 0; boxRow < SUBSECTION_SIZE; boxRow++) {
            for (int boxCol = 0; boxCol < SUBSECTION_SIZE; boxCol++) {
                Set<Integer> boxNumbers = new HashSet<>();
                for (int row = 0; row < SUBSECTION_SIZE; row++) {
                    for (int col = 0; col < SUBSECTION_SIZE; col++) {
                        int r = boxRow * SUBSECTION_SIZE + row;
                        int c = boxCol * SUBSECTION_SIZE + col;
                        int value = board.getValueAt(r, c);
                        assertTrue(boxNumbers.add(value), 
                                "Duplicate value " + value + " found in box at (" 
                                + boxRow + "," + boxCol + ")");
                    }
                }
                assertEquals(9, boxNumbers.size(), 
                        "Box at (" + boxRow + "," + boxCol + ") should contain all 9 digits");
            }
        }
    }
    
    @Test
    public void testSubsequentSolveGameCallsGenerateDifferentLayouts() {
        ISudokuSolver solver = new BacktrackingSudokuSolver();
        SudokuBoard board = new SudokuBoard(solver);
        
        assertTrue(board.solveGame(), "First solving should be successful");

        int[][] firstBoard = new int[BOARD_SIZE][BOARD_SIZE];
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                firstBoard[row][col] = board.getValueAt(row, col);
            }
        }

        assertTrue(board.solveGame(), "Second solving should be successful");

        int differences = 0;
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (firstBoard[row][col] != board.getValueAt(row, col)) {
                    differences++;
                }
            }
        }

        assertTrue(differences > 0, 
                "Subsequent solveGame calls should generate different layouts");
        System.out.println("Number of different positions between two boards: " + differences);
    }
    
    @Test
    public void testGetValueAtWithInvalidCoordinates() {
        ISudokuSolver solver = new BacktrackingSudokuSolver();
        SudokuBoard board = new SudokuBoard(solver);
        board.solveGame();
        
        //with negative row
        assertThrows(IllegalArgumentException.class, () -> {
            board.getValueAt(-1, 0);
        }, "Should throw exception for negative row");
        
        //with negative column
        assertThrows(IllegalArgumentException.class, () -> {
            board.getValueAt(0, -1);
        }, "Should throw exception for negative column");
        
        //with row too large
        assertThrows(IllegalArgumentException.class, () -> {
            board.getValueAt(9, 0);
        }, "Should throw exception for row out of bounds");
        
        //with column too large
        assertThrows(IllegalArgumentException.class, () -> {
            board.getValueAt(0, 9);
        }, "Should throw exception for column out of bounds");
    }
    
    @Test
    public void testSetValueAtWithInvalidValues() {
        ISudokuSolver solver = new BacktrackingSudokuSolver();
        SudokuBoard board = new SudokuBoard(solver);
        
        //with negative value
        assertThrows(IllegalArgumentException.class, () -> {
            board.setValueAt(0, 0, -1);
        }, "Should throw exception for negative value");
        
        //with value too large
        assertThrows(IllegalArgumentException.class, () -> {
            board.setValueAt(0, 0, 10);
        }, "Should throw exception for value out of bounds");
        
        //with invalid coordinates
        assertThrows(IllegalArgumentException.class, () -> {
            board.setValueAt(-1, 0, 5);
        }, "Should throw exception for negative row");
        
        assertThrows(IllegalArgumentException.class, () -> {
            board.setValueAt(0, -1, 5);
        }, "Should throw exception for negative column");
        
        assertThrows(IllegalArgumentException.class, () -> {
            board.setValueAt(9, 0, 5);
        }, "Should throw exception for row out of bounds");
        
        assertThrows(IllegalArgumentException.class, () -> {
            board.setValueAt(0, 9, 5);
        }, "Should throw exception for column out of bounds");
    }
    
    @Test
    public void testIsValid() {
        ISudokuSolver solver = new BacktrackingSudokuSolver();
        SudokuBoard board = new SudokuBoard(solver);
        
        assertTrue(board.isValid(), "Empty board should be valid");
        
        board.setValueAt(0, 0, 1);
        board.setValueAt(0, 1, 2);
        board.setValueAt(0, 2, 3);
        assertTrue(board.isValid(), "Board with valid values should be valid");

        board.setValueAt(0, 3, 1); 
        assertFalse(board.isValid(), "Board with duplicate in row should be invalid");
        
        board.setValueAt(0, 3, 0);
        assertTrue(board.isValid(), "Board should be valid after removing duplicate");
        
        board.setValueAt(1, 0, 1); 
        assertFalse(board.isValid(), "Board with duplicate in column should be invalid");
        
        board.setValueAt(1, 0, 0);
        assertTrue(board.isValid(), "Board should be valid after removing duplicate");
        
        board.setValueAt(1, 1, 1);
        assertFalse(board.isValid(), "Board with duplicate in box should be invalid");
    }

    @Test
    public void testFillBoardBackwardCompatibility() {
        ISudokuSolver solver = new BacktrackingSudokuSolver();
        SudokuBoard board = new SudokuBoard(solver);
        
        assertTrue(board.fillBoard(), "fillBoard should work through backward compatibility");
        assertTrue(board.isValid(), "Board should be valid after using fillBoard");
    }
}
