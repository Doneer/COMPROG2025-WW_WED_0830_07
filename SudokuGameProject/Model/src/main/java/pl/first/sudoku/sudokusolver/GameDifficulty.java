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

/**
 * Enum representing difficulty levels for a Sudoku game.
 * Each difficulty level determines how many cells are removed from a solved board.
 * @author zhuma
 */

public enum GameDifficulty {
    EASY(20),
    MEDIUM(40),
    HARD(60);
    
    private final int cellsToRemove;
    
    GameDifficulty(int cellsToRemove) {
        this.cellsToRemove = cellsToRemove;
    }
    
    public int getCellsToRemove() {
        return cellsToRemove;
    }
    
    public SudokuBoard prepareBoard(SudokuBoard sourceBoard) {
        SudokuBoard gameBoard = sourceBoard.clone();
        int removed = 0;
        
        java.util.Random random = new java.util.Random();
        
        while (removed < cellsToRemove) {
            int row = random.nextInt(9);
            int col = random.nextInt(9);
            
            if (gameBoard.getValueAt(row, col) != 0) {
                gameBoard.setValueAt(row, col, 0);
                removed++;
            }
        }
        
        return gameBoard;
    }
    
    public EditableSudokuBoardDecorator prepareDecoratedBoard(SudokuBoard sourceBoard) {
        SudokuBoard gameBoard = sourceBoard.clone();
        int removed = 0;

        java.util.Random random = new java.util.Random();

        while (removed < cellsToRemove) {
            int row = random.nextInt(9);
            int col = random.nextInt(9);

            if (gameBoard.getValueAt(row, col) != 0) {
                gameBoard.setValueAt(row, col, 0);
                removed++;
            }
        }

        EditableSudokuBoardDecorator decorator = new EditableSudokuBoardDecorator(gameBoard);
        decorator.lockNonEmptyFields();
        return decorator;
    }
}
