/*
 * The MIT License
 *
 * Copyright 2025 Daniyar Zhumatayev, Kuzma Martysiuk
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package pl.first.sudoku.sudokusolver;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Enhanced tests for EditableSudokuBoardDecorator to achieve 100% coverage.
 * @author zhuma
 */
public class EditableSudokuBoardDecoratorTest {
    
    private SudokuBoard board;
    private EditableSudokuBoardDecorator decorator;
    
    @BeforeEach
    public void setUp() {
        board = new SudokuBoard(new BacktrackingSudokuSolver());
        board.solveGame();
        decorator = new EditableSudokuBoardDecorator(board);
    }
    
    @Test
    public void testConstructor() {
        EditableSudokuBoardDecorator newDecorator = new EditableSudokuBoardDecorator(board);
        
        assertNotNull(newDecorator, "Decorator should not be null");
        assertEquals(board, newDecorator.getSudokuBoard(), "Should wrap the same board");
        
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                assertTrue(newDecorator.isFieldEditable(row, col), 
                        "Field [" + row + "," + col + "] should be editable initially");
            }
        }
    }
    
    @Test
    public void testSetFieldEditable() {
        decorator.setFieldEditable(0, 0, false);
        assertFalse(decorator.isFieldEditable(0, 0), "Field [0,0] should not be editable");
        
        decorator.setFieldEditable(0, 0, true);
        assertTrue(decorator.isFieldEditable(0, 0), "Field [0,0] should be editable again");
        
        decorator.setFieldEditable(1, 1, false);
        decorator.setFieldEditable(2, 2, false);
        decorator.setFieldEditable(3, 3, false);
        
        assertFalse(decorator.isFieldEditable(1, 1), "Field [1,1] should not be editable");
        assertFalse(decorator.isFieldEditable(2, 2), "Field [2,2] should not be editable");
        assertFalse(decorator.isFieldEditable(3, 3), "Field [3,3] should not be editable");
        
        assertTrue(decorator.isFieldEditable(0, 1), "Field [0,1] should still be editable");
        assertTrue(decorator.isFieldEditable(4, 4), "Field [4,4] should still be editable");
    }
    
    @Test
    public void testSetFieldEditableInvalidCoordinates() {
        assertThrows(IllegalArgumentException.class, () -> {
            decorator.setFieldEditable(-1, 0, true);
        }, "Should throw exception for negative row");
        
        assertThrows(IllegalArgumentException.class, () -> {
            decorator.setFieldEditable(9, 0, true);
        }, "Should throw exception for row >= 9");
        
        assertThrows(IllegalArgumentException.class, () -> {
            decorator.setFieldEditable(0, -1, true);
        }, "Should throw exception for negative column");
        
        assertThrows(IllegalArgumentException.class, () -> {
            decorator.setFieldEditable(0, 9, true);
        }, "Should throw exception for column >= 9");
        
        assertThrows(IllegalArgumentException.class, () -> {
            decorator.setFieldEditable(-1, -1, true);
        }, "Should throw exception for both coordinates negative");
        
        assertThrows(IllegalArgumentException.class, () -> {
            decorator.setFieldEditable(10, 10, false);
        }, "Should throw exception for both coordinates too large");
    }
    
    @Test
    public void testIsFieldEditableInvalidCoordinates() {
        assertThrows(IllegalArgumentException.class, () -> {
            decorator.isFieldEditable(-1, 0);
        }, "Should throw exception for negative row");
        
        assertThrows(IllegalArgumentException.class, () -> {
            decorator.isFieldEditable(9, 0);
        }, "Should throw exception for row >= 9");
        
        // Test invalid column coordinates
        assertThrows(IllegalArgumentException.class, () -> {
            decorator.isFieldEditable(0, -1);
        }, "Should throw exception for negative column");
        
        assertThrows(IllegalArgumentException.class, () -> {
            decorator.isFieldEditable(0, 9);
        }, "Should throw exception for column >= 9");
        
        assertThrows(IllegalArgumentException.class, () -> {
            decorator.isFieldEditable(-100, 0);
        }, "Should throw exception for very negative row");
        
        assertThrows(IllegalArgumentException.class, () -> {
            decorator.isFieldEditable(0, 100);
        }, "Should throw exception for very large column");
    }
    
    @Test
    public void testLockNonEmptyFields() {
        SudokuBoard testBoard = new SudokuBoard(new BacktrackingSudokuSolver());
        testBoard.solveGame();
        
        testBoard.setValueAt(0, 0, 0);
        testBoard.setValueAt(1, 1, 0);
        testBoard.setValueAt(2, 2, 0);
        testBoard.setValueAt(8, 8, 0);
        
        EditableSudokuBoardDecorator testDecorator = new EditableSudokuBoardDecorator(testBoard);
        
        assertTrue(testDecorator.isFieldEditable(0, 0), "Empty field should be editable initially");
        assertTrue(testDecorator.isFieldEditable(0, 1), "Non-empty field should be editable initially");
        
        testDecorator.lockNonEmptyFields();
        
        assertTrue(testDecorator.isFieldEditable(0, 0), "Empty field [0,0] should remain editable");
        assertTrue(testDecorator.isFieldEditable(1, 1), "Empty field [1,1] should remain editable");
        assertTrue(testDecorator.isFieldEditable(2, 2), "Empty field [2,2] should remain editable");
        assertTrue(testDecorator.isFieldEditable(8, 8), "Empty field [8,8] should remain editable");
        
        assertFalse(testDecorator.isFieldEditable(0, 1), "Non-empty field should be locked");
        assertFalse(testDecorator.isFieldEditable(0, 2), "Non-empty field should be locked");
        assertFalse(testDecorator.isFieldEditable(1, 0), "Non-empty field should be locked");
        
        int editableCount = 0;
        int lockedCount = 0;
        
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (testDecorator.isFieldEditable(row, col)) {
                    editableCount++;
                } else {
                    lockedCount++;
                }
            }
        }
        
        assertEquals(4, editableCount, "Should have 4 editable (empty) fields");
        assertEquals(77, lockedCount, "Should have 77 locked (non-empty) fields");
    }
    
    @Test
    public void testSetValueAtEditable() {
        decorator.setFieldEditable(0, 0, true);
        
        int originalValue = decorator.getValueAt(0, 0);
        int newValue = (originalValue == 1) ? 2 : 1; 
        
        assertDoesNotThrow(() -> {
            decorator.setValueAt(0, 0, newValue);
        }, "Should be able to set value on editable field");
        
        assertEquals(newValue, decorator.getValueAt(0, 0), "Value should be updated");
    }
    
    @Test
    public void testSetValueAtNonEditable() {
        decorator.setFieldEditable(0, 0, false);
        
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            decorator.setValueAt(0, 0, 5);
        }, "Should throw exception when setting value on non-editable field");
        
        assertTrue(exception.getMessage().contains("not editable"), 
                "Exception message should mention field is not editable");
        assertTrue(exception.getMessage().contains("[0,0]"), 
                "Exception message should contain field coordinates");
    }
    
    @Test
    public void testSetValueAtMultipleFields() {
        decorator.setFieldEditable(0, 0, true);
        decorator.setFieldEditable(1, 1, true);
        decorator.setFieldEditable(2, 2, false);
        
        assertDoesNotThrow(() -> {
            decorator.setValueAt(0, 0, 1);
            decorator.setValueAt(1, 1, 2);
        }, "Should be able to set values on editable fields");
        
        assertThrows(IllegalStateException.class, () -> {
            decorator.setValueAt(2, 2, 3);
        }, "Should throw exception for non-editable field");
    }
    
    @Test
    public void testGetValueAt() {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                int decoratorValue = decorator.getValueAt(row, col);
                int boardValue = board.getValueAt(row, col);
                
                assertEquals(boardValue, decoratorValue, 
                        "Decorator should return same value as underlying board at [" + row + "," + col + "]");
            }
        }
    }
    
    @Test
    public void testGetValueAtAfterSetValueAt() {
        decorator.setFieldEditable(4, 4, true);
        
        decorator.setValueAt(4, 4, 7);
        assertEquals(7, decorator.getValueAt(4, 4), "Should return the value that was set");
        
        assertEquals(7, board.getValueAt(4, 4), "Underlying board should also be updated");
    }
    
    @Test
    public void testEditabilityPattern() {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (row == col) {
                    decorator.setFieldEditable(row, col, false);
                } else {
                    decorator.setFieldEditable(row, col, true);
                }
            }
        }
        
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (row == col) {
                    assertFalse(decorator.isFieldEditable(row, col), 
                            "Diagonal field [" + row + "," + col + "] should not be editable");
                } else {
                    assertTrue(decorator.isFieldEditable(row, col), 
                            "Non-diagonal field [" + row + "," + col + "] should be editable");
                }
            }
        }
    }
    
    @Test
    public void testSerialization() {
        decorator.setFieldEditable(0, 0, false);
        decorator.setFieldEditable(1, 1, false);
        decorator.setFieldEditable(2, 2, true);
        
        assertFalse(decorator.isFieldEditable(0, 0), "Field [0,0] should remain non-editable");
        assertFalse(decorator.isFieldEditable(1, 1), "Field [1,1] should remain non-editable");
        assertTrue(decorator.isFieldEditable(2, 2), "Field [2,2] should remain editable");
        
        assertSame(board, decorator.getSudokuBoard(), "Should return the same board instance");
    }
    
    @Test
    public void testBoundaryValues() {
        int[][] boundaryPositions = {
            {0, 0}, {0, 8}, {8, 0}, {8, 8},
            {0, 4}, {4, 0}, {4, 8}, {8, 4}  
        };
        
        for (int[] pos : boundaryPositions) {
            int row = pos[0];
            int col = pos[1];
            
            decorator.setFieldEditable(row, col, false);
            assertFalse(decorator.isFieldEditable(row, col), 
                    "Boundary field [" + row + "," + col + "] should be non-editable");
            
            decorator.setFieldEditable(row, col, true);
            assertTrue(decorator.isFieldEditable(row, col), 
                    "Boundary field [" + row + "," + col + "] should be editable");
            
            assertDoesNotThrow(() -> {
                int value = decorator.getValueAt(row, col);
                assertTrue(value >= 0 && value <= 9, "Value should be valid Sudoku value");
            }, "Should be able to get value from boundary position [" + row + "," + col + "]");
        }
    }
}