/*
 * MIT License
 *
 * Copyright (c) 2025 Daniyar Zhumatayev, Kuzma Martysiuk
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
        SudokuSolver solver = new BacktrackingSudokuSolver();
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
        SudokuSolver solver = new BacktrackingSudokuSolver();
        SudokuBoard board1 = new SudokuBoard(solver);

        assertTrue(board1.solveGame(), "First solving should be successful");

        int[][] firstBoard = new int[BOARD_SIZE][BOARD_SIZE];
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                firstBoard[row][col] = board1.getValueAt(row, col);
            }
        }

        SudokuBoard board2 = new SudokuBoard(solver);
        assertTrue(board2.solveGame(), "Second solving should be successful");

        int differences = 0;
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (firstBoard[row][col] != board2.getValueAt(row, col)) {
                    differences++;
                }
            }
        }

        assertTrue(differences > 0, 
                "Different board instances should generate different layouts");
        System.out.println("Number of different positions between two boards: " + differences);
    }
    
    @Test
    public void testGetValueAtWithInvalidCoordinates() {
        SudokuSolver solver = new BacktrackingSudokuSolver();
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
        SudokuSolver solver = new BacktrackingSudokuSolver();
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
        SudokuSolver solver = new BacktrackingSudokuSolver();
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
        SudokuSolver solver = new BacktrackingSudokuSolver();
        SudokuBoard board = new SudokuBoard(solver);
        
        assertTrue(board.fillBoard(), "fillBoard should work through backward compatibility");
        assertTrue(board.isValid(), "Board should be valid after using fillBoard");
    }
    
    @Test
    public void testGetRow() {
        SudokuSolver solver = new BacktrackingSudokuSolver();
        SudokuBoard board = new SudokuBoard(solver);

        board.setValueAt(3, 0, 1);
        board.setValueAt(3, 4, 5);
        board.setValueAt(3, 8, 9);

        SudokuRow row = board.getRow(3);

        assertEquals(1, row.getField(0).getFieldValue(), "First field value should be 1");
        assertEquals(5, row.getField(4).getFieldValue(), "Fifth field value should be 5");
        assertEquals(9, row.getField(8).getFieldValue(), "Ninth field value should be 9");

        row.getField(2).setFieldValue(3);
        assertEquals(3, board.getValueAt(3, 2), "Board should reflect changes to row fields");

        assertThrows(IllegalArgumentException.class, () -> {
            board.getRow(-1);
        }, "Negative row index should throw exception");

        assertThrows(IllegalArgumentException.class, () -> {
            board.getRow(9);
        }, "Row index out of bounds should throw exception");
    }

    @Test
    public void testGetColumn() {
        SudokuSolver solver = new BacktrackingSudokuSolver();
        SudokuBoard board = new SudokuBoard(solver);

        board.setValueAt(1, 5, 2);
        board.setValueAt(4, 5, 6);
        board.setValueAt(7, 5, 8);

        SudokuColumn column = board.getColumn(5);

        assertEquals(2, column.getField(1).getFieldValue(), "Second field value should be 2");
        assertEquals(6, column.getField(4).getFieldValue(), "Fifth field value should be 6");
        assertEquals(8, column.getField(7).getFieldValue(), "Eighth field value should be 8");

        column.getField(3).setFieldValue(4);
        assertEquals(4, board.getValueAt(3, 5), "Board should reflect changes to column fields");

        assertThrows(IllegalArgumentException.class, () -> {
            board.getColumn(-1);
        }, "Negative column index should throw exception");

        assertThrows(IllegalArgumentException.class, () -> {
            board.getColumn(9);
        }, "Column index out of bounds should throw exception");
    }

    @Test
    public void testGetBox() {
        SudokuSolver solver = new BacktrackingSudokuSolver();
        SudokuBoard board = new SudokuBoard(solver);

        board.setValueAt(0, 3, 1); 
        board.setValueAt(1, 4, 5); 
        board.setValueAt(2, 5, 9); 

        SudokuBox box = board.getBox(1, 0);

        assertEquals(1, box.getField(0).getFieldValue(), "First field value should be 1");
        assertEquals(5, box.getField(4).getFieldValue(), "Fifth field value should be 5");
        assertEquals(9, box.getField(8).getFieldValue(), "Ninth field value should be 9");

        box.getField(3).setFieldValue(7); 
        assertEquals(7, board.getValueAt(1, 3), "Board should reflect changes to box fields");

        assertThrows(IllegalArgumentException.class, () -> {
            board.getBox(-1, 0);
        }, "Negative box index should throw exception");

        assertThrows(IllegalArgumentException.class, () -> {
            board.getBox(3, 0);
        }, "Box column index out of bounds should throw exception");

        assertThrows(IllegalArgumentException.class, () -> {
            board.getBox(0, 3);
        }, "Box row index out of bounds should throw exception");
    }

    @Test
    public void testObserverPattern() {
        SudokuSolver solver = new BacktrackingSudokuSolver();
        SudokuBoard board = new SudokuBoard(solver);

        final boolean[] valueChanged = {false};

        board.getSudokuField(2, 3).addPropertyChangeListener(evt -> {
            valueChanged[0] = true;
        });

        board.setValueAt(2, 3, 5);

        assertTrue(valueChanged[0], "PropertyChangeListener should have been notified");
    }

    @Test
    public void testEquals() {
        SudokuSolver solver = new BacktrackingSudokuSolver();
        SudokuBoard board1 = new SudokuBoard(solver);
        SudokuBoard board2 = new SudokuBoard(solver);

        assertTrue(board1.equals(board1), "Board should equal itself");
        assertTrue(board1.equals(board2), "Empty boards should be equal");
        assertFalse(board1.equals(null), "Board should not equal null");
        assertFalse(board1.equals("string"), "Board should not equal other types");

        board1.setValueAt(0, 0, 5);
        assertFalse(board1.equals(board2), "Boards with different values should not be equal");

        board2.setValueAt(0, 0, 5);
        assertTrue(board1.equals(board2), "Boards with same values should be equal");
    }

    @Test
    public void testHashCode() {
        SudokuSolver solver = new BacktrackingSudokuSolver();
        SudokuBoard board1 = new SudokuBoard(solver);
        SudokuBoard board2 = new SudokuBoard(solver);

        assertEquals(board1.hashCode(), board2.hashCode(), "Empty boards should have same hash code");

        board1.setValueAt(0, 0, 5);
        assertNotEquals(board1.hashCode(), board2.hashCode(), "Different boards should have different hash codes");

        board2.setValueAt(0, 0, 5);
        assertEquals(board1.hashCode(), board2.hashCode(), "Same boards should have same hash code");
    }

    @Test
    public void testToString() {
        SudokuSolver solver = new BacktrackingSudokuSolver();
        SudokuBoard board = new SudokuBoard(solver);

        board.setValueAt(0, 0, 1);
        board.setValueAt(1, 1, 2);

        String result = board.toString();
        assertNotNull(result, "toString should not return null");
        assertTrue(result.contains("1"), "toString should contain the values in the board");
        assertTrue(result.contains("2"), "toString should contain the values in the board");
    }

    @Test
    public void testClone() {
        SudokuSolver solver = new BacktrackingSudokuSolver();
        SudokuBoard original = new SudokuBoard(solver);

        original.setValueAt(0, 0, 5);
        original.setValueAt(1, 1, 6);

        SudokuBoard cloned = (SudokuBoard) original.clone();

        assertEquals(original.getValueAt(0, 0), cloned.getValueAt(0, 0), "Cloned board should have same values");
        assertEquals(original.getValueAt(1, 1), cloned.getValueAt(1, 1), "Cloned board should have same values");

        cloned.setValueAt(0, 0, 9);
        assertEquals(5, original.getValueAt(0, 0), "Original should not be affected by clone changes");
        assertEquals(9, cloned.getValueAt(0, 0), "Clone should have the new value");
    }

    @Test
    public void testGetSudokuField() {
        SudokuSolver solver = new BacktrackingSudokuSolver();
        SudokuBoard board = new SudokuBoard(solver);

        board.setValueAt(2, 3, 5);
        SudokuField field = board.getSudokuField(2, 3);

        assertEquals(5, field.getFieldValue(), "Field value should match");

        field.setFieldValue(7);
        assertEquals(7, board.getValueAt(2, 3), "Board should reflect field changes");

        assertThrows(IllegalArgumentException.class, () -> {
            board.getSudokuField(-1, 0);
        }, "Negative row should throw exception");

        assertThrows(IllegalArgumentException.class, () -> {
            board.getSudokuField(0, -1);
        }, "Negative column should throw exception");

        assertThrows(IllegalArgumentException.class, () -> {
            board.getSudokuField(9, 0);
        }, "Row out of bounds should throw exception");

        assertThrows(IllegalArgumentException.class, () -> {
            board.getSudokuField(0, 9);
        }, "Column out of bounds should throw exception");
    }
}
