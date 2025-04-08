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
}
