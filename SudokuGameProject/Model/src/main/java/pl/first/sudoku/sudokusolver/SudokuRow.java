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

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing a row in a Sudoku puzzle.
 * A row consists of 9 SudokuField objects.
 * @author Zhmaggernaut
 */
public class SudokuRow extends SudokuElement {
    private static final long serialVersionUID = 3L;
    
    public SudokuRow(List<SudokuField> fields) {
        super(fields);
    }
    
    public SudokuRow(SudokuBoard board, int y) {
        super(board, y);
    }
    
    @Override
    protected List<SudokuField> extractFields(SudokuBoard board, int y) {
        if (y < 0 || y >= 9) {
            throw new IllegalArgumentException("Invalid row index");
        }
        
        List<SudokuField> fields = new ArrayList<>();
        for (int col = 0; col < 9; col++) {
            fields.add(board.getSudokuField(y, col));
        }
        
        return fields;
    }
    
    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("fields", fields)
                .toString();
    }
    
    @Override
    public SudokuRow clone() {
        SudokuRow cloned = (SudokuRow) super.clone();
        return cloned;
    }
}
