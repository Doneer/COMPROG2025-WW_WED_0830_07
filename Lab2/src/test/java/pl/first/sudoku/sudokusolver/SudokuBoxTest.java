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
public class SudokuBoxTest {
    
    @Test
    public void testClone() throws CloneNotSupportedException {
        List<SudokuField> fields = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            SudokuField field = new SudokuField();
            field.setFieldValue(i + 1);
            fields.add(field);
        }
        
        SudokuBox original = new SudokuBox(fields);
        SudokuBox cloned = original.clone();
        
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
        
        SudokuBox box = new SudokuBox(fields);
        assertTrue(box.verify(), "Empty box should be valid");
        
        for (int i = 0; i < 9; i++) {
            fields.get(i).setFieldValue(i + 1);
        }
        assertTrue(box.verify(), "Box with values 1-9 should be valid");
        
        fields.get(0).setFieldValue(3);
        fields.get(5).setFieldValue(3);
        assertFalse(box.verify(), "Box with duplicate values should be invalid");
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
}
