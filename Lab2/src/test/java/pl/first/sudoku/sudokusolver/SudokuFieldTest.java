/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */

package pl.first.sudoku.sudokusolver;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author zhuma
 */
public class SudokuFieldTest {
    
    @Test
    public void testGetSetFieldValue() {
        SudokuField field = new SudokuField();
        
        assertEquals(0, field.getFieldValue(), "Default value should be 0");
        
        field.setFieldValue(5);
        assertEquals(5, field.getFieldValue(), "Value should be updated to 5");
        
        field.setFieldValue(0);
        assertEquals(0, field.getFieldValue(), "Value should be updated to 0");
        
        field.setFieldValue(9);
        assertEquals(9, field.getFieldValue(), "Value should be updated to 9");
    }
    
    @Test
    public void testSetFieldValueWithInvalidValues() {
        SudokuField field = new SudokuField();
        
        assertThrows(IllegalArgumentException.class, () -> {
            field.setFieldValue(-1);
        }, "Should throw exception for negative value");
        
        assertThrows(IllegalArgumentException.class, () -> {
            field.setFieldValue(10);
        }, "Should throw exception for value out of bounds");
    }
    
    @Test
    public void testEqualsAndHashCode() {
        SudokuField field1 = new SudokuField();
        SudokuField field2 = new SudokuField();

        assertEquals(field1, field2, "Fields with same value should be equal");
        assertEquals(field1.hashCode(), field2.hashCode(), "Hash codes should match");
        
        field1.setFieldValue(5);
        assertNotEquals(field1, field2, "Fields with different values should not be equal");
        assertNotEquals(field1.hashCode(), field2.hashCode(), "Hash codes should differ");
        
        field2.setFieldValue(5);
        assertEquals(field1, field2, "Fields with same value should be equal");
        assertEquals(field1.hashCode(), field2.hashCode(), "Hash codes should match");
    }
    
    @Test
    public void testToString() {
        SudokuField field = new SudokuField();
        assertEquals("0", field.toString(), "toString should return the value as string");
        
        field.setFieldValue(7);
        assertEquals("7", field.toString(), "toString should return the updated value");
    }
    
    @Test
    public void testCompareTo() {
        SudokuField field1 = new SudokuField();
        SudokuField field2 = new SudokuField();
        
        assertEquals(0, field1.compareTo(field2), "Fields with same value should be equal");
        
        field1.setFieldValue(5);
        assertTrue(field1.compareTo(field2) > 0, "Field with larger value should be greater");
        
        field2.setFieldValue(9);
        assertTrue(field1.compareTo(field2) < 0, "Field with smaller value should be less");
    }
    
    @Test
    public void testClone() {
        SudokuField original = new SudokuField();
        original.setFieldValue(5);
        
        SudokuField clone = original.clone();

        assertEquals(original, clone, "Cloned field should equal original");
        assertEquals(original.getFieldValue(), clone.getFieldValue(), "Values should be the same");

        clone.setFieldValue(8);
        assertNotEquals(original.getFieldValue(), clone.getFieldValue(), "Changes to clone shouldn't affect original");
        assertEquals(5, original.getFieldValue(), "Original value should remain unchanged");
    }
    
    @Test
    public void testPropertyChangeListener() {
        SudokuField field = new SudokuField();
        
        final boolean[] propertyChanged = {false};
        final int[] oldValue = {0};
        final int[] newValue = {0};
        
        PropertyChangeListener listener = (PropertyChangeEvent evt) -> {
            propertyChanged[0] = true;
            oldValue[0] = (int) evt.getOldValue();
            newValue[0] = (int) evt.getNewValue();
        };
        
        field.addPropertyChangeListener(listener);
        
        field.setFieldValue(5);
        
        assertTrue(propertyChanged[0], "PropertyChanged should be true");
        assertEquals(0, oldValue[0], "Old value should be 0");
        assertEquals(5, newValue[0], "New value should be 5");

        propertyChanged[0] = false;
        field.removePropertyChangeListener(listener);
        
        field.setFieldValue(8);
        assertFalse(propertyChanged[0], "PropertyChanged should still be false after listener removal");
    }
}
