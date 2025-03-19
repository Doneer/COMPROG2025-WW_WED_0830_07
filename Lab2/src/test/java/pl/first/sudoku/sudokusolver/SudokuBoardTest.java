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
    
    /**
     * Test if fillBoard generates a valid Sudoku board
     * Checks all rows, columns, and boxes for duplicates
     */
    @Test
    public void testFillBoardGeneratesValidBoard() {
        SudokuBoard board = new SudokuBoard();
        
        boolean result = board.fillBoard();
        
        //Check if filling was successful
        assertTrue(result, "Board filling should be successful");
        
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
    
    /**
     * here we test if subsequent calls to fillBoard generate different layouts
     */
    @Test
    public void testSubsequentFillBoardCallsGenerateDifferentLayouts() {
        SudokuBoard board = new SudokuBoard();
        
        assertTrue(board.fillBoard(), "First filling should be successful");

        int[][] firstBoard = new int[BOARD_SIZE][BOARD_SIZE];
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                firstBoard[row][col] = board.getValueAt(row, col);
            }
        }

        assertTrue(board.fillBoard(), "Second filling should be successful");

        int differences = 0;
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (firstBoard[row][col] != board.getValueAt(row, col)) {
                    differences++;
                }
            }
        }

        assertTrue(differences > 0, 
                "Subsequent fillBoard calls should generate different layouts");
        System.out.println("Number of different positions between two boards: " + differences);
    }
    
    /**
     * testing if getValueAt throws exception for invalid coordinates
     */
    @Test
    public void testGetValueAtWithInvalidCoordinates() {
        SudokuBoard board = new SudokuBoard();
        board.fillBoard();
        
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
}
