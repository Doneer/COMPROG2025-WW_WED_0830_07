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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for SudokuField with JavaFX Properties support.
 * Uses reflection to avoid direct JavaFX dependencies in tests.
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
        
        IllegalArgumentException exception1 = assertThrows(IllegalArgumentException.class, () -> {
            field.setFieldValue(-1);
        }, "Should throw exception for negative value");
        
        assertTrue(exception1.getMessage().contains("0 and 9") || 
                  exception1.getMessage().contains("between"), 
                  "Exception message should mention valid range");
        
        IllegalArgumentException exception2 = assertThrows(IllegalArgumentException.class, () -> {
            field.setFieldValue(10);
        }, "Should throw exception for value out of bounds");
        
        assertTrue(exception2.getMessage().contains("0 and 9") || 
                  exception2.getMessage().contains("between"), 
                  "Exception message should mention valid range");
        
        assertDoesNotThrow(() -> {
            field.setFieldValue(1);
            field.setFieldValue(9);
            field.setFieldValue(0);
        }, "Valid values should not throw exceptions");
    }
    
    @Test
    public void testJavaFXPropertyExists() {
        SudokuField field = new SudokuField();
        
        try {
            Method valuePropertyMethod = field.getClass().getMethod("valueProperty");
            Object property = valuePropertyMethod.invoke(field);
            
            assertNotNull(property, "valueProperty() should return non-null");
            
            String className = property.getClass().getName();
            String simpleClassName = property.getClass().getSimpleName();
            
            boolean isJavaFXProperty = className.startsWith("javafx.beans.property") ||
                                     simpleClassName.contains("IntegerProperty") ||
                                     simpleClassName.contains("Property") ||
                                     className.contains("IntegerProperty");
            
            if (!isJavaFXProperty) {
                Class<?>[] interfaces = property.getClass().getInterfaces();
                for (Class<?> iface : interfaces) {
                    if (iface.getName().contains("IntegerProperty") || 
                        iface.getSimpleName().contains("Property")) {
                        isJavaFXProperty = true;
                        break;
                    }
                }
            }
            
            if (!isJavaFXProperty) {
                Class<?> superClass = property.getClass().getSuperclass();
                while (superClass != null && !isJavaFXProperty) {
                    if (superClass.getName().contains("IntegerProperty") ||
                        superClass.getSimpleName().contains("Property")) {
                        isJavaFXProperty = true;
                        break;
                    }
                    superClass = superClass.getSuperclass();
                }
            }
            
            assertTrue(isJavaFXProperty, 
                    "Should return an IntegerProperty or JavaFX Property, got: " + className + 
                    " (simple: " + simpleClassName + ")");
            
        } catch (NoSuchMethodException e) {
            fail("SudokuField must have valueProperty() method for JavaFX binding");
        } catch (Exception e) {
            fail("Error accessing valueProperty(): " + e.getMessage());
        }
    }
    
    @Test
    public void testJavaFXPropertyValueSync() {
        SudokuField field = new SudokuField();
        
        try {
            Method valuePropertyMethod = field.getClass().getMethod("valueProperty");
            Object property = valuePropertyMethod.invoke(field);
            Method getMethod = property.getClass().getMethod("get");
            
            Object propertyValue = getMethod.invoke(property);
            assertEquals(field.getFieldValue(), ((Number) propertyValue).intValue(), 
                    "Property value should match field value initially");
            
            field.setFieldValue(7);
            propertyValue = getMethod.invoke(property);
            assertEquals(7, ((Number) propertyValue).intValue(), 
                    "Property should be updated when field value changes");
            
        } catch (Exception e) {
            fail("Error testing property value synchronization: " + e.getMessage());
        }
    }
    
    @Test
    public void testJavaFXPropertySetValue() {
        SudokuField field = new SudokuField();
        
        try {
            Method valuePropertyMethod = field.getClass().getMethod("valueProperty");
            Object property = valuePropertyMethod.invoke(field);
            Method setMethod = property.getClass().getMethod("set", int.class);
            
            setMethod.invoke(property, 6);
            assertEquals(6, field.getFieldValue(), "Setting through property should update field value");
            
        } catch (Exception e) {
            fail("Error testing property set value: " + e.getMessage());
        }
    }
    
    @Test
    public void testJavaFXPropertyValidation() {
        SudokuField field = new SudokuField();
        
        try {
            Method valuePropertyMethod = field.getClass().getMethod("valueProperty");
            Object property = valuePropertyMethod.invoke(field);
            Method setMethod = property.getClass().getMethod("set", int.class);
            
            assertThrows(IllegalArgumentException.class, () -> {
                try {
                    setMethod.invoke(property, -1);
                } catch (InvocationTargetException e) {
                    if (e.getCause() instanceof IllegalArgumentException) {
                        throw (IllegalArgumentException) e.getCause();
                    }
                    throw new RuntimeException("Unexpected exception type", e);
                }
            }, "Property should validate negative values");
            
            assertThrows(IllegalArgumentException.class, () -> {
                try {
                    setMethod.invoke(property, 10);
                } catch (InvocationTargetException e) {
                    if (e.getCause() instanceof IllegalArgumentException) {
                        throw (IllegalArgumentException) e.getCause();
                    }
                    throw new RuntimeException("Unexpected exception type", e);
                }
            }, "Property should validate values > 9");
            
        } catch (NoSuchMethodException e) {
            fail("Property should have set method for JavaFX binding");
        } catch (Exception e) {
            fail("Error testing property validation: " + e.getMessage());
        }
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

        assertEquals(original.getFieldValue(), clone.getFieldValue(), "Cloned field should have same value");
        
        try {
            Method valuePropertyMethod = clone.getClass().getMethod("valueProperty");
            Object clonedProperty = valuePropertyMethod.invoke(clone);
            assertNotNull(clonedProperty, "Cloned field should have its own property");
            
            Method getMethod = clonedProperty.getClass().getMethod("get");
            Object propertyValue = getMethod.invoke(clonedProperty);
            assertEquals(5, ((Number) propertyValue).intValue(), "Cloned property should have correct value");
            
        } catch (Exception e) {
            fail("Error testing cloned field property: " + e.getMessage());
        }

        clone.setFieldValue(8);

        assertEquals(5, original.getFieldValue(), "Original should not be affected by clone changes");
        assertEquals(8, clone.getFieldValue(), "Clone should have the new value");

        assertNotEquals(original, clone, "Modified clone should not be equal to original");
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
    
    @Test
    public void testEqualsEdgeCases() {
        SudokuField field = new SudokuField();

        assertTrue(field.equals(field), "Field should equal itself");
        assertFalse(field.equals(null), "Field should not equal null");
        assertFalse(field.equals("string"), "Field should not equal other types");

        SudokuField another = new SudokuField();
        assertTrue(field.equals(another), "Fields with same value should be equal");

        field.setFieldValue(5);
        assertFalse(field.equals(another), "Fields with different values should not be equal");

        another.setFieldValue(5);
        assertTrue(field.equals(another), "Fields with same value should be equal");
    }
    
    @Test
    public void testEqualsAndHashCodeConsistency() {
        SudokuField field1 = new SudokuField();
        SudokuField field2 = new SudokuField();

        assertTrue(field1.equals(field1), "Field should equal itself");

        assertTrue(field1.equals(field2), "Fields with same value should be equal");
        assertTrue(field2.equals(field1), "Fields with same value should be equal (symmetry)");
        assertEquals(field1.hashCode(), field2.hashCode(), "Equal fields should have same hash code");

        field1.setFieldValue(5);
        field2.setFieldValue(5);
        assertTrue(field1.equals(field2), "Fields with same value should be equal");
        assertEquals(field1.hashCode(), field2.hashCode(), "Equal fields should have same hash code");

        field2.setFieldValue(7);
        assertFalse(field1.equals(field2), "Fields with different values should not be equal");
        assertNotEquals(field1.hashCode(), field2.hashCode(), "Different fields should have different hash codes");
    }
    
    @Test
    public void testSerializationAndPropertyRecreation() {
        SudokuField field = new SudokuField();
        field.setFieldValue(6);
        
        assertEquals(6, field.getFieldValue(), "Field should maintain value");
        
        try {
            Method valuePropertyMethod = field.getClass().getMethod("valueProperty");
            Object property = valuePropertyMethod.invoke(field);
            Method getMethod = property.getClass().getMethod("get");
            Object propertyValue = getMethod.invoke(property);
            assertEquals(6, ((Number) propertyValue).intValue(), "Property should have same value");
        } catch (Exception e) {
            fail("Property should work after field creation: " + e.getMessage());
        }
        
        SudokuField cloned = field.clone();
        assertEquals(6, cloned.getFieldValue(), "Cloned field should have same value");
        
        try {
            Method valuePropertyMethod = cloned.getClass().getMethod("valueProperty");
            Object property = valuePropertyMethod.invoke(cloned);
            Method getMethod = property.getClass().getMethod("get");
            Object propertyValue = getMethod.invoke(property);
            assertEquals(6, ((Number) propertyValue).intValue(), "Cloned property should have same value");
        } catch (Exception e) {
            fail("Cloned property should work: " + e.getMessage());
        }
    }
    
    @Test
    public void testValidValueRange() {
        SudokuField field = new SudokuField();
        
        for (int i = 0; i <= 9; i++) {
            final int value = i;
            assertDoesNotThrow(() -> field.setFieldValue(value), 
                    "Value " + value + " should be valid");
            assertEquals(value, field.getFieldValue(), 
                    "Field should store value " + value);
        }
        
        int[] invalidValues = {-10, -1, 10, 15, 100};
        for (int invalid : invalidValues) {
            assertThrows(IllegalArgumentException.class, () -> field.setFieldValue(invalid), 
                    "Value " + invalid + " should be invalid");
        }
    }
}