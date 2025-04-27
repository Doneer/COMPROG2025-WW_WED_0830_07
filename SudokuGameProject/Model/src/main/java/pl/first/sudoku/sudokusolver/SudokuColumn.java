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

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing a column in a Sudoku puzzle.
 * A column consists of 9 SudokuField objects.
 * @author Zhmaggernaut
 */
public class SudokuColumn extends SudokuElement {
    private static final long serialVersionUID = 4L;
    
    public SudokuColumn(List<SudokuField> fields) {
        super(fields);
    }
    
    public SudokuColumn(SudokuBoard board, int x) {
        super(board, x);
    }
    
    @Override
    protected List<SudokuField> extractFields(SudokuBoard board, int x) {
        if (x < 0 || x >= 9) {
            throw new IllegalArgumentException("Invalid column index");
        }
        
        List<SudokuField> fields = new ArrayList<>();
        for (int row = 0; row < 9; row++) {
            fields.add(board.getSudokuField(row, x));
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
    public SudokuColumn clone() {
        SudokuColumn cloned = (SudokuColumn) super.clone();
        return cloned;
    }
}
