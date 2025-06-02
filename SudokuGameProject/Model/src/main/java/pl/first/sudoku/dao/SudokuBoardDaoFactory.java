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

package pl.first.sudoku.dao;

import pl.first.sudoku.sudokusolver.EditableSudokuBoardDecorator;
import pl.first.sudoku.sudokusolver.SudokuBoard;

/**
 * Factory class for creating Dao objects for SudokuBoard instances.
 * Uses the factory pattern to abstract the creation of specific Dao implementations.
 * Includes database support for EditableSudokuBoardDecorator.
 * @author zhuma
 */
public class SudokuBoardDaoFactory {
    public static Dao<SudokuBoard> getFileDao(String directoryName) {
        return new FileSudokuBoardDao(directoryName);
    }
    
    public static Dao<EditableSudokuBoardDecorator> getEditableFileDao(String directoryName) {
        return new EditableSudokuBoardDao(directoryName);
    }
    
    public static Dao<EditableSudokuBoardDecorator> getJdbcDao() throws JdbcDaoException {
        return new JdbcSudokuBoardDao();
    }
    
    @Deprecated
    public static Dao<SudokuBoard> getLegacyJdbcDao() throws JdbcDaoException {
        throw new UnsupportedOperationException(
            "Use getJdbcDao() which returns Dao<EditableSudokuBoardDecorator> for full functionality");
    }
}
