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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * SudokuBoard class represents a 9x9 Sudoku puzzle board.
 * It provides methods to generate and access a valid Sudoku board.
 * @author zhuma
 */
public class SudokuBoard implements Serializable, Cloneable {
    private static final int BOARD_SIZE = 9;
    private static final int SUBSECTION_SIZE = 3;
    private static final int NO_VALUE = 0;
    
    private List<List<SudokuField>> board;
    private SudokuSolver solver;
    
    public SudokuBoard(SudokuSolver solver) {
        this.solver = solver;
        this.board = new ArrayList<>(BOARD_SIZE);

        for (int row = 0; row < BOARD_SIZE; row++) {
            List<SudokuField> rowList = new ArrayList<>(BOARD_SIZE);
            for (int col = 0; col < BOARD_SIZE; col++) {
                rowList.add(new SudokuField());
            }
            board.add(Collections.unmodifiableList(rowList));
        }
    }
    
    public boolean solveGame() {
        return solver.solve(this);
    }
    
    public int getValueAt(int row, int col) {
        if (row < 0 || row >= BOARD_SIZE || col < 0 || col >= BOARD_SIZE) {
            throw new IllegalArgumentException("Invalid board coordinates");
        }
        return board.get(row).get(col).getFieldValue();
    }
    
    public void setValueAt(int row, int col, int value) {
        if (row < 0 || row >= BOARD_SIZE || col < 0 || col >= BOARD_SIZE) {
            throw new IllegalArgumentException("Invalid board coordinates");
        }
        if (value < NO_VALUE || value > 9) {
            throw new IllegalArgumentException("Value must be between 0 and 9");
        }
        board.get(row).get(col).setFieldValue(value);
    }
    
    public SudokuRow getRow(int y) {
        return new SudokuRow(this, y);
    }
    
    public SudokuColumn getColumn(int x) {
        return new SudokuColumn(this, x);
    }
    
    public SudokuBox getBox(int x, int y) {
        return new SudokuBox(this, x, y);
    }
    
    public boolean isValid() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            if (!getRow(row).verify()) {
                return false;
            }
        }

        for (int col = 0; col < BOARD_SIZE; col++) {
            if (!getColumn(col).verify()) {
                return false;
            }
        }

        for (int boxRow = 0; boxRow < SUBSECTION_SIZE; boxRow++) {
            for (int boxCol = 0; boxCol < SUBSECTION_SIZE; boxCol++) {
                if (!getBox(boxCol, boxRow).verify()) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    public boolean fillBoard() {
        return solveGame();
    }
    
    public SudokuField getSudokuField(int row, int col) {
        if (row < 0 || row >= BOARD_SIZE || col < 0 || col >= BOARD_SIZE) {
            throw new IllegalArgumentException("Invalid board coordinates");
        }
        return board.get(row).get(col);
    }
    
    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("board", board)
            .toString();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        SudokuBoard other = (SudokuBoard) obj;

        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (!board.get(row).get(col).equals(other.board.get(row).get(col))) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = 17;
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                result = 31 * result + board.get(row).get(col).hashCode();
            }
        }
        return result;
    }
    
    @Override
    public SudokuBoard clone() {
        try {
            SudokuBoard cloned = (SudokuBoard) super.clone();
            cloned.board = new ArrayList<>(BOARD_SIZE);

            for (int row = 0; row < BOARD_SIZE; row++) {
                List<SudokuField> rowList = new ArrayList<>(BOARD_SIZE);
                for (int col = 0; col < BOARD_SIZE; col++) {
                    rowList.add(this.board.get(row).get(col).clone());
                }
                cloned.board.add(Collections.unmodifiableList(rowList));
            }

            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new InternalError("Should not happen since we implement Cloneable", e);
        }
    }
}
