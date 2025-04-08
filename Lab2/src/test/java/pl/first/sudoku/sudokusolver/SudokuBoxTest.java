/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */

package pl.first.sudoku.sudokusolver;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

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
}
