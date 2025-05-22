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

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Class representing a single field in a Sudoku puzzle.
 * Contains the value and provides notification when value changes.
 * Now uses JavaFX Properties for proper bidirectional binding.
 * @author zhuma
 */
public class SudokuField implements Serializable, Cloneable, Comparable<SudokuField> {
    private static final long serialVersionUID = 1L;
    
    private transient IntegerProperty value;
    
    private int persistentValue = 0;
    
    private final PropertyChangeSupport changes = new PropertyChangeSupport(this);
    
    public SudokuField() {
        initializeProperty();
    }
    
    private void initializeProperty() {
        this.value = new SimpleIntegerProperty(persistentValue) {
            @Override
            public void set(int newValue) {
                if (newValue < 0 || newValue > 9) {
                    throw new IllegalArgumentException("Value must be between 0 and 9");
                }
                
                int oldValue = get();
                super.set(newValue);
                
                persistentValue = newValue;
                
                changes.firePropertyChange("value", oldValue, newValue);
            }
        };
    }
    
    public IntegerProperty valueProperty() {
        if (value == null) {
            initializeProperty();
        }
        return value;
    }
    
    public int getFieldValue() {
        return valueProperty().get();
    }

    public void setFieldValue(int value) {
        if (value < 0 || value > 9) {
            throw new IllegalArgumentException("Value must be between 0 and 9");
        }
        valueProperty().set(value);
    }
    
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changes.addPropertyChangeListener(listener);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changes.removePropertyChangeListener(listener);
    }
    
    @Override
    public String toString() {
        return Integer.toString(getFieldValue());
    }
    
    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj, "changes", "value");
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, "changes", "value");
    }
    
    @Override
    public int compareTo(SudokuField other) {
        return Integer.compare(this.getFieldValue(), other.getFieldValue());
    }
    
    @Override
    public SudokuField clone() {
        try {
            SudokuField cloned = (SudokuField) super.clone();
            cloned.persistentValue = this.persistentValue;
            cloned.initializeProperty();
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new InternalError("Should not happen since we implement Cloneable", e);
        }
    }
    
    private void writeObject(ObjectOutputStream out) throws IOException {
        if (value != null) {
            persistentValue = value.get();
        }
        out.defaultWriteObject();
    }
    
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        initializeProperty();
    }
}