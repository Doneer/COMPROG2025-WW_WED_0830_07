/*
 * The MIT License
 *
 * Copyright 2025 Daniyar Zhumatayev, Kuzma Martysiuk
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package pl.first.sudoku.sudokusolver;

import java.io.Serializable;

/**
 * Decorator that adds information about which fields are editable in a Sudoku board.
 * This decorator tracks and enforces which fields can be modified by the user.
 * @author zhuma
 */
public class EditableSudokuBoardDecorator extends SudokuBoardDecorator implements Serializable {
    private static final long serialVersionUID = 102L;
    
    private boolean[][] editableFields;
    
    public EditableSudokuBoardDecorator(SudokuBoard sudokuBoard) {
        super(sudokuBoard);
        this.editableFields = new boolean[9][9];
        
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                editableFields[row][col] = true;
            }
        }
    }
    
    public void setFieldEditable(int row, int col, boolean editable) {
        if (row < 0 || row >= 9 || col < 0 || col >= 9) {
            throw new IllegalArgumentException("Invalid board coordinates");
        }
        editableFields[row][col] = editable;
    }

    public boolean isFieldEditable(int row, int col) {
        if (row < 0 || row >= 9 || col < 0 || col >= 9) {
            throw new IllegalArgumentException("Invalid board coordinates");
        }
        return editableFields[row][col];
    }
    
    public void lockNonEmptyFields() {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (sudokuBoard.getValueAt(row, col) != 0) {
                    editableFields[row][col] = false;
                } else {
                    editableFields[row][col] = true;
                }
            }
        }
    }
    
    public void setValueAt(int row, int col, int value) {
        if (!isFieldEditable(row, col)) {
            throw new IllegalStateException("Field at [" + row + "," + col + "] is not editable");
        }
        sudokuBoard.setValueAt(row, col, value);
    }

    public int getValueAt(int row, int col) {
        return sudokuBoard.getValueAt(row, col);
    }
}
