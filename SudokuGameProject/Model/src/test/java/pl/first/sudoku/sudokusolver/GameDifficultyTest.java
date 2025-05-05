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

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author zhuma
 */
public class GameDifficultyTest {
    
    @Test
    public void testGetCellsToRemove() {
        assertEquals(20, GameDifficulty.EASY.getCellsToRemove());
        assertEquals(40, GameDifficulty.MEDIUM.getCellsToRemove());
        assertEquals(60, GameDifficulty.HARD.getCellsToRemove());
    }
    
    @Test
    public void testPrepareBoard() {
        SudokuSolver solver = new BacktrackingSudokuSolver();
        SudokuBoard sourceBoard = new SudokuBoard(solver);
        sourceBoard.solveGame();
        
        SudokuBoard easyBoard = GameDifficulty.EASY.prepareBoard(sourceBoard);
        assertNotSame(sourceBoard, easyBoard, "Should return a different board instance");
        
        int emptyCells = countEmptyCells(easyBoard);
        assertEquals(20, emptyCells, "Easy difficulty should remove 20 cells");
        
        SudokuBoard mediumBoard = GameDifficulty.MEDIUM.prepareBoard(sourceBoard);
        emptyCells = countEmptyCells(mediumBoard);
        assertEquals(40, emptyCells, "Medium difficulty should remove 40 cells");
        
        SudokuBoard hardBoard = GameDifficulty.HARD.prepareBoard(sourceBoard);
        emptyCells = countEmptyCells(hardBoard);
        assertEquals(60, emptyCells, "Hard difficulty should remove 60 cells");
    }
    
    private int countEmptyCells(SudokuBoard board) {
        int count = 0;
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (board.getValueAt(row, col) == 0) {
                    count++;
                }
            }
        }
        return count;
    }
}
