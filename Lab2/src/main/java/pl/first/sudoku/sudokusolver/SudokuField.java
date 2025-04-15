/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package pl.first.sudoku.sudokusolver;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;

/**
 * Class representing a single field in a Sudoku puzzle.
 * Contains the value and provides notification when value changes.
 * @author zhuma
 */
public class SudokuField implements Serializable, Cloneable, Comparable<SudokuField> {
    private int value;
    private final PropertyChangeSupport changes = new PropertyChangeSupport(this);
    
    public SudokuField() {
        this.value = 0;
    }
    
    public int getFieldValue() {
        return value;
    }
    
    public void setFieldValue(int value) {
        if (value < 0 || value > 9) {
            throw new IllegalArgumentException("Value must be between 0 and 9");
        }
        
        int oldValue = this.value;
        this.value = value;
        changes.firePropertyChange("value", oldValue, value);
    }
    
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changes.addPropertyChangeListener(listener);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changes.removePropertyChangeListener(listener);
    }
    
    @Override
    public String toString() {
        return Integer.toString(value);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        SudokuField other = (SudokuField) obj;
        return value == other.value;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(value);
    }
    
    @Override
    public int compareTo(SudokuField other) {
        return Integer.compare(this.value, other.value);
    }
    
    @Override
    public SudokuField clone() {
        try {
            return (SudokuField) super.clone();
        } catch (CloneNotSupportedException e) {
            //This exception is impossible if we properly implement Cloneable
            throw new InternalError("Should not happen since we implement Cloneable", e);
        }
    }
}
