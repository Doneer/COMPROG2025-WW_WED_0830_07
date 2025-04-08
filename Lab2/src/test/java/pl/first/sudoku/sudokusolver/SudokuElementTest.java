/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */

package pl.first.sudoku.sudokusolver;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author zhuma
 */
public class SudokuElementTest {
    
    private SudokuRow element;
    private List<SudokuField> fields;
    
    @BeforeEach
    public void setUp() {
        fields = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            SudokuField field = new SudokuField();
            field.setFieldValue(0);
            fields.add(field);
        }
        element = new SudokuRow(fields);
    }
    
    @Test
    public void testVerifyWithValidValues() {
        assertTrue(element.verify(), "Empty element should be valid");
        
        for (int i = 0; i < 9; i++) {
            fields.get(i).setFieldValue(i + 1);
        }
        assertTrue(element.verify(), "Element with values 1-9 should be valid");
    }
    
    @Test
    public void testVerifyWithDuplicates() {
        fields.get(0).setFieldValue(5);
        fields.get(4).setFieldValue(5);
        
        assertFalse(element.verify(), "Element with duplicate values should be invalid");
    }
    
    @Test
    public void testGetField() {
        fields.get(3).setFieldValue(7);
        
        SudokuField field = element.getField(3);
        assertEquals(7, field.getFieldValue(), "getField should return the correct field");

        assertThrows(IllegalArgumentException.class, () -> {
            element.getField(-1);
        }, "Negative index should throw an exception");
        
        assertThrows(IllegalArgumentException.class, () -> {
            element.getField(9);
        }, "Index out of bounds should throw an exception");
    }
    
    @Test
    public void testEquals() {
        SudokuRow element2 = new SudokuRow(fields);
        assertEquals(element, element2, "Elements with same fields should be equal");
  
        List<SudokuField> differentFields = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            SudokuField field = new SudokuField();
            field.setFieldValue(i % 3 + 1);
            differentFields.add(field);
        }
        SudokuRow element3 = new SudokuRow(differentFields);
        assertNotEquals(element, element3, "Elements with different fields should not be equal");
    }
    
    @Test
    public void testHashCode() {
        SudokuRow element2 = new SudokuRow(fields);
        assertEquals(element.hashCode(), element2.hashCode(), "Hash codes of equal elements should match");
    }
    
    @Test
    public void testToString() {
        String str = element.toString();
        assertNotNull(str, "toString should return a non-null string");
        assertTrue(str.contains("0"), "String should contain field values");
    }
    
    @Test
    public void testClone() throws CloneNotSupportedException {
        fields.get(2).setFieldValue(3);
        fields.get(5).setFieldValue(7);

        SudokuRow cloned = element.clone();

        assertEquals(element.getField(2).getFieldValue(), cloned.getField(2).getFieldValue(), 
                "Cloned field values should match");

        fields.get(2).setFieldValue(9);
        assertNotEquals(element.getField(2).getFieldValue(), cloned.getField(2).getFieldValue(), 
                "Changes to original should not affect clone");
    }
    
    @Test
    public void testConstructorInvalidSize() {
        List<SudokuField> tooFewFields = new ArrayList<>();
        for (int i = 0; i < 8; i++) { // Only 8 fields
            tooFewFields.add(new SudokuField());
        }
        
        assertThrows(IllegalArgumentException.class, () -> {
            new SudokuRow(tooFewFields);
        }, "Constructor should reject lists with wrong size");
    }
    
    @Test
    public void testToStringWithValues() {
        List<SudokuField> fields = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            SudokuField field = new SudokuField();
            field.setFieldValue(i + 1); 
            fields.add(field);
        }

        SudokuRow element = new SudokuRow(fields);
        String str = element.toString();

        for (int i = 1; i <= 9; i++) {
            assertTrue(str.contains(String.valueOf(i)), 
                    "toString should contain value " + i);
        }
    }

    @Test
    public void testEqualsEdgeCases() {
        List<SudokuField> fields1 = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            fields1.add(new SudokuField());
        }

        SudokuRow row = new SudokuRow(fields1);

        assertTrue(row.equals(row), "Element should equal itself");
        assertFalse(row.equals(null), "Element should not equal null");
        assertFalse(row.equals("string"), "Element should not equal other types");

        List<SudokuField> fields2 = new ArrayList<>(fields1);
        SudokuColumn column = new SudokuColumn(fields2);

        assertFalse(row.equals(column), "Different element types should not be equal");
    }
}
