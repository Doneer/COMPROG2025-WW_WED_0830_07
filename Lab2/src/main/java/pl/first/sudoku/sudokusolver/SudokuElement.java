/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package pl.first.sudoku.sudokusolver;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author zhuma
 */
public abstract class SudokuElement implements Serializable, Cloneable {
    protected static final int SIZE = 9;
    protected List<SudokuField> fields;
    
    public SudokuElement(List<SudokuField> fields) {
        if (fields.size() != SIZE) {
            throw new IllegalArgumentException("SudokuElement must contain exactly 9 fields");
        }
        this.fields = fields;
    }
    
    public boolean verify() {
        boolean[] used = new boolean[SIZE + 1];
        
        for (SudokuField field : fields) {
            int value = field.getFieldValue();
            if (value != 0) {
                if (used[value]) {
                    return false;
                }
                used[value] = true;
            }
        }
        
        return true;
    }
    
    public SudokuField getField(int index) {
        if (index < 0 || index >= SIZE) {
            throw new IllegalArgumentException("Index out of bounds");
        }
        return fields.get(index);
    }
    
    @Override
    public String toString() {
        return fields.toString();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        SudokuElement other = (SudokuElement) obj;
        return fields.equals(other.fields);
    }
    
    @Override
    public int hashCode() {
        return fields.hashCode();
    }
    
    @Override
    public SudokuElement clone() throws CloneNotSupportedException {
        SudokuElement cloned = (SudokuElement) super.clone();
        cloned.fields = Arrays.asList(
                fields.get(0).clone(),
                fields.get(1).clone(),
                fields.get(2).clone(),
                fields.get(3).clone(),
                fields.get(4).clone(),
                fields.get(5).clone(),
                fields.get(6).clone(),
                fields.get(7).clone(),
                fields.get(8).clone()
        );
        return cloned;
    }
}
