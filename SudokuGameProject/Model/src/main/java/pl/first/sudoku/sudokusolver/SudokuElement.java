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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class for Sudoku board elements (rows, columns, boxes).
 * Provides common functionality for verifying element correctness.
 * @author zhuma
 */
public abstract class SudokuElement implements Serializable, Cloneable {
    private static final long serialVersionUID = 2L;
    protected static final int SIZE = 9;
    protected List<SudokuField> fields;
    
    protected abstract List<SudokuField> extractFields(SudokuBoard board, int index);
    
     protected List<SudokuField> extractFields(SudokuBoard board, int x, int y) {
        throw new UnsupportedOperationException("This method should be overridden by classes that need two indices");
    }
    
    public SudokuElement(List<SudokuField> fields) {
        if (fields.size() != SIZE) {
            throw new IllegalArgumentException("SudokuElement must contain exactly 9 fields");
        }
        this.fields = fields;
    }
    
    protected SudokuElement(SudokuBoard board, int index) {
        this.fields = extractFields(board, index);
        if (this.fields.size() != SIZE) {
            throw new IllegalArgumentException("SudokuElement must contain exactly 9 fields");
        }
    }
    
    protected SudokuElement(SudokuBoard board, int x, int y) {
        this.fields = extractFields(board, x, y);
        if (this.fields.size() != SIZE) {
            throw new IllegalArgumentException("SudokuElement must contain exactly 9 fields");
        }
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
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
            .append("fields", fields)
            .toString();
    }
    
    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
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
