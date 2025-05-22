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

package pl.first.sudoku.sudokusolver;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Zhmaggernaut
 */
public class SudokuRowTest {
    
    @Test
    public void testClone() throws CloneNotSupportedException {
        List<SudokuField> fields = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            SudokuField field = new SudokuField();
            field.setFieldValue(i + 1); // Set values 1-9
            fields.add(field);
        }
        
        SudokuRow original = new SudokuRow(fields);
        SudokuRow cloned = original.clone();

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
        
        SudokuRow row = new SudokuRow(fields);
        assertTrue(row.verify(), "Empty row should be valid");
        
        for (int i = 0; i < 9; i++) {
            fields.get(i).setFieldValue(i + 1);
        }
        assertTrue(row.verify(), "Row with values 1-9 should be valid");
        
        fields.get(1).setFieldValue(5);
        fields.get(8).setFieldValue(5);
        assertFalse(row.verify(), "Row with duplicate values should be invalid");
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

        board.setValueAt(3, 0, 1);
        board.setValueAt(3, 4, 5);
        board.setValueAt(3, 8, 9);

        SudokuRow row = new SudokuRow(board, 3);

        assertEquals(1, row.getField(0).getFieldValue(), "First field value should be 1");
        assertEquals(5, row.getField(4).getFieldValue(), "Fifth field value should be 5");
        assertEquals(9, row.getField(8).getFieldValue(), "Ninth field value should be 9");

        row.getField(2).setFieldValue(3);
        assertEquals(3, board.getValueAt(3, 2), "Board should reflect changes to row fields");
    }

    @Test
    public void testConstructorWithBoardInvalidIndex() {
        SudokuSolver solver = new BacktrackingSudokuSolver();
        SudokuBoard board = new SudokuBoard(solver);

        assertThrows(IllegalArgumentException.class, () -> {
            new SudokuRow(board, -1);
        }, "Negative index should throw exception");

        assertThrows(IllegalArgumentException.class, () -> {
            new SudokuRow(board, 9);
        }, "Index out of bounds should throw exception");
    }
}
