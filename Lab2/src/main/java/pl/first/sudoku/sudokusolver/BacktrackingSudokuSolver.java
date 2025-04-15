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
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Implementation of the SudokuSolver interface using a backtracking algorithm.
 * @author Zhmaggernaut
 */
public class BacktrackingSudokuSolver implements SudokuSolver, Serializable {
    private static final int BOARD_SIZE = 9;
    private static final int SUBSECTION_SIZE = 3;
    private static final int BOARD_START_INDEX = 0;
    private static final int NO_VALUE = 0;
    private static final int MAX_VALUE = 9;
    
    private Random random;
    
    public BacktrackingSudokuSolver() {
        random = new Random();
    }
    
    @Override
    public boolean solve(SudokuBoard board) {
        return backtrack(board, BOARD_START_INDEX, BOARD_START_INDEX);
    }
    
    private boolean backtrack(SudokuBoard board, int row, int col) {
        if (row == BOARD_SIZE) {
            return true;
        }

        int nextRow = (col == BOARD_SIZE - 1) ? row + 1 : row;
        int nextCol = (col == BOARD_SIZE - 1) ? 0 : col + 1;

        if (board.getValueAt(row, col) != NO_VALUE) {
            return backtrack(board, nextRow, nextCol);
        }

        int[] shuffledValues = getShuffledValues();

        for (int value : shuffledValues) {
            if (isValidPlacement(board, row, col, value)) {
                board.setValueAt(row, col, value);

                if (backtrack(board, nextRow, nextCol)) {
                    return true;
                }

                board.setValueAt(row, col, NO_VALUE);
            }
        }

        return false;
    }
    
    private int[] getShuffledValues() {
        List<Integer> valuesList = new ArrayList<>(MAX_VALUE);
        for (int i = 1; i <= MAX_VALUE; i++) {
            valuesList.add(i);
        }

        Collections.shuffle(valuesList, random);

        int[] values = new int[MAX_VALUE];
        for (int i = 0; i < MAX_VALUE; i++) {
            values[i] = valuesList.get(i);
        }

        return values;
    }
    
    private boolean isValidPlacement(SudokuBoard board, int row, int col, int value) {
        SudokuRow sudokuRow = board.getRow(row);
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (sudokuRow.getField(i).getFieldValue() == value) {
                return false;
            }
        }
        
        SudokuColumn sudokuColumn = board.getColumn(col);
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (sudokuColumn.getField(i).getFieldValue() == value) {
                return false;
            }
        }
        
        int boxRow = row / SUBSECTION_SIZE;
        int boxCol = col / SUBSECTION_SIZE;
        SudokuBox sudokuBox = board.getBox(boxCol, boxRow);
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (sudokuBox.getField(i).getFieldValue() == value) {
                return false;
            }
        }
        
        return true;
    }
}
