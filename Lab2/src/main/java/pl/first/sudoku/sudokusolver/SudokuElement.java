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

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package pl.first.sudoku.sudokusolver;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class for Sudoku board elements (rows, columns, boxes).
 * Provides common functionality for verifying element correctness.
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
    public SudokuElement clone() {
        try {
            SudokuElement cloned = (SudokuElement) super.clone();
            cloned.fields = new ArrayList<>();
            for (SudokuField field : fields) {
                cloned.fields.add(field.clone());
            }
            return cloned;
        } catch (CloneNotSupportedException e) {
            //This should never happen since we implement Cloneable
            throw new InternalError("Should not happen since we implement Cloneable", e);
        }
    }
}
