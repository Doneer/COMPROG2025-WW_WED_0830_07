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

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Implementation of the SudokuSolver interface using a backtracking algorithm.
 * @author Zhmaggernaut
 */
public class SudokuColumnTest {
    
    @Test
    public void testClone() throws CloneNotSupportedException {
        List<SudokuField> fields = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            SudokuField field = new SudokuField();
            field.setFieldValue(i + 1);
            fields.add(field);
        }
        
        SudokuColumn original = new SudokuColumn(fields);
        SudokuColumn cloned = original.clone();

        assertNotSame(original, cloned, "Clone should be a different instance");

        for (int i = 0; i < 9; i++) {
            assertEquals(original.getField(i).getFieldValue(), 
                         cloned.getField(i).getFieldValue(),
                         "Field values should match");
        }

        cloned.getField(3).setFieldValue(8);
        assertNotEquals(original.getField(3).getFieldValue(), 
                        cloned.getField(3).getFieldValue(),
                        "Modifying clone should not affect original");
    }
    
    @Test
    public void testVerify() {
        List<SudokuField> fields = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            SudokuField field = new SudokuField();
            fields.add(field);
        }
        
        SudokuColumn column = new SudokuColumn(fields);
        assertTrue(column.verify(), "Empty column should be valid");

        for (int i = 0; i < 9; i++) {
            fields.get(i).setFieldValue(i + 1);
        }
        assertTrue(column.verify(), "Column with values 1-9 should be valid");
        
        fields.get(2).setFieldValue(4);
        fields.get(7).setFieldValue(4);
        assertFalse(column.verify(), "Column with duplicate values should be invalid");
    }
    
    @Test
    public void testGetBoxOutOfBounds() {
        SudokuSolver solver = new BacktrackingSudokuSolver();
        SudokuBoard board = new SudokuBoard(solver);

        assertThrows(IllegalArgumentException.class, () -> {
            board.getBox(-1, 0);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            board.getBox(0, -1);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            board.getBox(3, 0);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            board.getBox(0, 3);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            board.getBox(3, 3);
        });
    }

    @Test
    public void testCompleteBoxCoverage() {
        SudokuSolver solver = new BacktrackingSudokuSolver();
        SudokuBoard board = new SudokuBoard(solver);

        int boxX = 1; 
        int boxY = 0;

        int startRow = boxY * 3;
        int startCol = boxX * 3;

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                board.setValueAt(startRow + row, startCol + col, (row * 3 + col + 1));
            }
        }

        SudokuBox box = board.getBox(boxX, boxY);

        for (int i = 0; i < 9; i++) {
            assertEquals(i + 1, box.getField(i).getFieldValue(), 
                    "Box field " + i + " should have value " + (i + 1));
        }
    }
    
    @Test
    public void testConstructorWithBoard() {
        SudokuSolver solver = new BacktrackingSudokuSolver();
        SudokuBoard board = new SudokuBoard(solver);

        board.setValueAt(1, 5, 2);
        board.setValueAt(4, 5, 6);
        board.setValueAt(7, 5, 8);

        SudokuColumn column = new SudokuColumn(board, 5);

        assertEquals(2, column.getField(1).getFieldValue(), "Second field value should be 2");
        assertEquals(6, column.getField(4).getFieldValue(), "Fifth field value should be 6");
        assertEquals(8, column.getField(7).getFieldValue(), "Eighth field value should be 8");

        column.getField(3).setFieldValue(4);
        assertEquals(4, board.getValueAt(3, 5), "Board should reflect changes to column fields");
    }

    @Test
    public void testConstructorWithBoardInvalidIndex() {
        SudokuSolver solver = new BacktrackingSudokuSolver();
        SudokuBoard board = new SudokuBoard(solver);

        assertThrows(IllegalArgumentException.class, () -> {
            new SudokuColumn(board, -1);
        }, "Negative index should throw exception");

        assertThrows(IllegalArgumentException.class, () -> {
            new SudokuColumn(board, 9);
        }, "Index out of bounds should throw exception");
    }
    
    @Test
    public void testToString() {
        List<SudokuField> fields = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            SudokuField field = new SudokuField();
            field.setFieldValue(i + 1);
            fields.add(field);
        }

        SudokuColumn column = new SudokuColumn(fields);
        String result = column.toString();

        assertNotNull(result, "toString should not return null");
        assertTrue(result.contains("fields"), "toString should contain field information");
    }
}
