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

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.lang.reflect.Method;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.lang.reflect.InvocationTargetException;

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
        List<SudokuField> fields1 = new ArrayList<>();
        List<SudokuField> fields2 = new ArrayList<>();

        for (int i = 0; i < 9; i++) {
            SudokuField field1 = new SudokuField();
            SudokuField field2 = new SudokuField();

            field1.setFieldValue(i + 1);
            field2.setFieldValue(i + 1);

            fields1.add(field1);
            fields2.add(field2);
        }

        SudokuRow row1 = new SudokuRow(fields1);
        SudokuRow row2 = new SudokuRow(fields2);

        assertEquals(row1.hashCode(), row2.hashCode(), "Equal objects should have same hash code");

        fields2.get(3).setFieldValue(8);
        assertNotEquals(row1.hashCode(), row2.hashCode(), "Different objects should have different hash codes");
    }
    
    @Test
    public void testToString() {
        List<SudokuField> fields = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            SudokuField field = new SudokuField();
            field.setFieldValue(i + 1);
            fields.add(field);
        }

        SudokuRow element = new SudokuRow(fields);
        String result = element.toString();

        assertNotNull(result, "toString should not return null");
        assertTrue(result.contains("fields"), "toString should contain field information");
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
    
    @Test
    public void testExtractFieldsWithTwoParametersThrowsException() {
        List<SudokuField> fields = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            fields.add(new SudokuField());
        }

        SudokuRow row = new SudokuRow(fields);

        try {
            Method method = SudokuElement.class.getDeclaredMethod("extractFields", 
                    SudokuBoard.class, int.class, int.class);
            method.setAccessible(true);
            method.invoke(row, null, 0, 0);
            fail("Expected UnsupportedOperationException to be thrown");
        } catch (InvocationTargetException e) {
            assertTrue(e.getCause() instanceof UnsupportedOperationException,
                    "Expected UnsupportedOperationException but got " + e.getCause());
        } catch (Exception e) {
            fail("Unexpected exception: " + e);
        }
    }

    @Test
    public void testConstructorWithTooFewFields() {
        List<SudokuField> tooFewFields = new ArrayList<>();
        for (int i = 0; i < 8; i++) { 
            tooFewFields.add(new SudokuField());
        }

        assertThrows(IllegalArgumentException.class, () -> {
            new SudokuRow(tooFewFields);
        });
    }

    @Test
    public void testConstructorWithTooManyFields() {
        List<SudokuField> tooManyFields = new ArrayList<>();
        for (int i = 0; i < 10; i++) { // 10 fields
            tooManyFields.add(new SudokuField());
        }

        assertThrows(IllegalArgumentException.class, () -> {
            new SudokuRow(tooManyFields);
        });
    }
    
    @Test
    public void testConstructorWithIndexWithInvalidSizeFromExtractFields() {
        SudokuSolver solver = new BacktrackingSudokuSolver();
        SudokuBoard board = new SudokuBoard(solver);

        assertThrows(IllegalArgumentException.class, () -> {
            new SudokuElement(board, 0) {
                @Override
                protected List<SudokuField> extractFields(SudokuBoard b, int index) {
                    List<SudokuField> fields = new ArrayList<>();
                    for (int i = 0; i < 7; i++) {
                        fields.add(new SudokuField());
                    }
                    return fields;
                }
            };
        });
    }

    @Test
    public void testConstructorWithXYWithInvalidSizeFromExtractFields() {
        SudokuSolver solver = new BacktrackingSudokuSolver();
        SudokuBoard board = new SudokuBoard(solver);

        assertThrows(IllegalArgumentException.class, () -> {
            new SudokuElement(board, 0, 0) {
                @Override
                protected List<SudokuField> extractFields(SudokuBoard b, int index) {
                    return new ArrayList<>(Arrays.asList(new SudokuField())); 
                }

                @Override
                protected List<SudokuField> extractFields(SudokuBoard b, int x, int y) {
                    List<SudokuField> fields = new ArrayList<>();
                    for (int i = 0; i < 10; i++) {
                        fields.add(new SudokuField());
                    }
                    return fields;
                }
            };
        });
    }
      
    @Test
    public void testEqualsAndHashCodeConsistency() {
        List<SudokuField> fields1 = new ArrayList<>();
        List<SudokuField> fields2 = new ArrayList<>();

        for (int i = 0; i < 9; i++) {
            SudokuField field1 = new SudokuField();
            SudokuField field2 = new SudokuField();

            field1.setFieldValue(i + 1);
            field2.setFieldValue(i + 1);

            fields1.add(field1);
            fields2.add(field2);
        }

        SudokuRow row1 = new SudokuRow(fields1);
        SudokuRow row2 = new SudokuRow(fields2);

        assertTrue(row1.equals(row1), "Object should equal itself");

        assertTrue(row1.equals(row2), "Equal objects should be equal");
        assertTrue(row2.equals(row1), "Equal objects should be equal (symmetry)");

        assertEquals(row1.hashCode(), row2.hashCode(), "Equal objects should have same hash code");

        fields2.get(3).setFieldValue(8);
        assertFalse(row1.equals(row2), "Objects with different values should not be equal");
        assertNotEquals(row1.hashCode(), row2.hashCode(), "Different objects should have different hash codes");
    }
    
    @Test
    public void testElementToString() {
        List<SudokuField> testFields = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            SudokuField field = new SudokuField();
            field.setFieldValue(i + 1);
            testFields.add(field);
        }

        SudokuElement element = new SudokuElement(testFields) {
            @Override
            protected List<SudokuField> extractFields(SudokuBoard board, int index) {
                return new ArrayList<>(testFields);
            }
        };

        String result = element.toString();

        assertNotNull(result, "toString should not return null");
        assertTrue(result.contains("fields"), "toString should contain field information");
    }
    
    @Test
    public void testEqualsWithDifferentFieldSize() throws Exception {
        final List<SudokuField> fields1 = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            fields1.add(new SudokuField());
        }

        class TestElement extends SudokuElement {
            private final List<SudokuField> testFields;

            public TestElement(List<SudokuField> fields) {
                super(fields);
                this.testFields = fields;
            }

            @Override
            protected List<SudokuField> extractFields(SudokuBoard board, int index) {
                return new ArrayList<>(testFields);
            }
        }

        TestElement element1 = new TestElement(fields1);

        List<SudokuField> fields2 = new ArrayList<>(fields1);

        TestElement element2 = new TestElement(fields2);

        java.lang.reflect.Field fieldsField = SudokuElement.class.getDeclaredField("fields");
        fieldsField.setAccessible(true);

        List<SudokuField> modifiedFields = new ArrayList<>(fields2);
        modifiedFields.remove(0); 

        fieldsField.set(element2, modifiedFields);

        assertFalse(element1.equals(element2), "Elements with different field sizes should not be equal");
    }
    
    @Test
    public void testEqualsWithIdenticalFields() {
        final List<SudokuField> commonFields = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            SudokuField field = new SudokuField();
            field.setFieldValue(i + 1);
            commonFields.add(field);
        }

        class TestElement extends SudokuElement {
            public TestElement() {
                super(commonFields);
            }

            @Override
            protected List<SudokuField> extractFields(SudokuBoard board, int index) {
                return new ArrayList<>(commonFields);
            }
        }

        SudokuElement element1 = new TestElement();
        SudokuElement element2 = new TestElement();

        assertTrue(element1.equals(element2), "Elements with identical field objects should be equal");
        assertEquals(element1.hashCode(), element2.hashCode(), "Equal elements should have same hash code");
    }
}
