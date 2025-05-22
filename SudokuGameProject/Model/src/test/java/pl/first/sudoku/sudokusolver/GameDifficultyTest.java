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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Enhanced tests for GameDifficulty to achieve 100% coverage.
 * @author zhuma
 */
public class GameDifficultyTest {
    
    private SudokuBoard sourceBoard;
    
    @BeforeEach
    public void setUp() {
        SudokuSolver solver = new BacktrackingSudokuSolver();
        sourceBoard = new SudokuBoard(solver);
        sourceBoard.solveGame();
    }
    
    @Test
    public void testGetCellsToRemove() {
        assertEquals(20, GameDifficulty.EASY.getCellsToRemove(), 
                "Easy difficulty should remove 20 cells");
        assertEquals(40, GameDifficulty.MEDIUM.getCellsToRemove(), 
                "Medium difficulty should remove 40 cells");
        assertEquals(60, GameDifficulty.HARD.getCellsToRemove(), 
                "Hard difficulty should remove 60 cells");
    }
    
    @Test
    public void testEnumValues() {
        GameDifficulty[] difficulties = GameDifficulty.values();
        assertEquals(3, difficulties.length, "Should have exactly 3 difficulty levels");
        
        assertEquals(GameDifficulty.EASY, difficulties[0], "First difficulty should be EASY");
        assertEquals(GameDifficulty.MEDIUM, difficulties[1], "Second difficulty should be MEDIUM");
        assertEquals(GameDifficulty.HARD, difficulties[2], "Third difficulty should be HARD");
    }
    
    @Test
    public void testEnumValueOf() {
        assertEquals(GameDifficulty.EASY, GameDifficulty.valueOf("EASY"), 
                "valueOf should return EASY for 'EASY'");
        assertEquals(GameDifficulty.MEDIUM, GameDifficulty.valueOf("MEDIUM"), 
                "valueOf should return MEDIUM for 'MEDIUM'");
        assertEquals(GameDifficulty.HARD, GameDifficulty.valueOf("HARD"), 
                "valueOf should return HARD for 'HARD'");
        
        assertThrows(IllegalArgumentException.class, () -> {
            GameDifficulty.valueOf("INVALID");
        }, "valueOf should throw exception for invalid value");
    }
    
    @Test
    public void testPrepareBoard() {
        SudokuBoard easyBoard = GameDifficulty.EASY.prepareBoard(sourceBoard);
        assertNotSame(sourceBoard, easyBoard, "Should return a different board instance");
        
        int emptyCellsEasy = countEmptyCells(easyBoard);
        assertEquals(20, emptyCellsEasy, "Easy difficulty should have exactly 20 empty cells");
        
        SudokuBoard mediumBoard = GameDifficulty.MEDIUM.prepareBoard(sourceBoard);
        assertNotSame(sourceBoard, mediumBoard, "Should return a different board instance");
        
        int emptyCellsMedium = countEmptyCells(mediumBoard);
        assertEquals(40, emptyCellsMedium, "Medium difficulty should have exactly 40 empty cells");
        
        SudokuBoard hardBoard = GameDifficulty.HARD.prepareBoard(sourceBoard);
        assertNotSame(sourceBoard, hardBoard, "Should return a different board instance");
        
        int emptyCellsHard = countEmptyCells(hardBoard);
        assertEquals(60, emptyCellsHard, "Hard difficulty should have exactly 60 empty cells");
    }
    
    @Test
    public void testPrepareBoardRandomness() {
        SudokuBoard board1 = GameDifficulty.EASY.prepareBoard(sourceBoard);
        SudokuBoard board2 = GameDifficulty.EASY.prepareBoard(sourceBoard);
        
        assertEquals(countEmptyCells(board1), countEmptyCells(board2), 
                "Both boards should have same number of empty cells");
        
        boolean different = false;
        for (int row = 0; row < 9 && !different; row++) {
            for (int col = 0; col < 9 && !different; col++) {
                if ((board1.getValueAt(row, col) == 0) != (board2.getValueAt(row, col) == 0)) {
                    different = true;
                }
            }
        }
        
        assertNotNull(board1, "First board should not be null");
        assertNotNull(board2, "Second board should not be null");
    }
    
    @Test
    public void testPrepareBoardDoesNotModifySource() {
        int[][] originalValues = new int[9][9];
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                originalValues[row][col] = sourceBoard.getValueAt(row, col);
            }
        }
        
        GameDifficulty.EASY.prepareBoard(sourceBoard);
        GameDifficulty.MEDIUM.prepareBoard(sourceBoard);
        GameDifficulty.HARD.prepareBoard(sourceBoard);
        
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                assertEquals(originalValues[row][col], sourceBoard.getValueAt(row, col),
                        "Source board should not be modified at position [" + row + "," + col + "]");
            }
        }
    }
    
    @Test
    public void testPrepareDecoratedBoard() {
        EditableSudokuBoardDecorator easyDecorator = GameDifficulty.EASY.prepareDecoratedBoard(sourceBoard);
        assertNotNull(easyDecorator, "Decorated board should not be null");
        assertNotSame(sourceBoard, easyDecorator.getSudokuBoard(), 
                "Decorated board should wrap a different board instance");
        
        SudokuBoard easyBoard = easyDecorator.getSudokuBoard();
        int emptyCells = countEmptyCells(easyBoard);
        assertEquals(20, emptyCells, "Easy decorated board should have 20 empty cells");
        
        EditableSudokuBoardDecorator mediumDecorator = GameDifficulty.MEDIUM.prepareDecoratedBoard(sourceBoard);
        assertNotNull(mediumDecorator, "Decorated board should not be null");
        
        SudokuBoard mediumBoard = mediumDecorator.getSudokuBoard();
        emptyCells = countEmptyCells(mediumBoard);
        assertEquals(40, emptyCells, "Medium decorated board should have 40 empty cells");
        
        EditableSudokuBoardDecorator hardDecorator = GameDifficulty.HARD.prepareDecoratedBoard(sourceBoard);
        assertNotNull(hardDecorator, "Decorated board should not be null");
        
        SudokuBoard hardBoard = hardDecorator.getSudokuBoard();
        emptyCells = countEmptyCells(hardBoard);
        assertEquals(60, emptyCells, "Hard decorated board should have 60 empty cells");
    }
    
    @Test
    public void testPrepareDecoratedBoardEditability() {
        EditableSudokuBoardDecorator decorator = GameDifficulty.MEDIUM.prepareDecoratedBoard(sourceBoard);
        SudokuBoard board = decorator.getSudokuBoard();
        
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                boolean isEmpty = (board.getValueAt(row, col) == 0);
                boolean isEditable = decorator.isFieldEditable(row, col);
                
                if (isEmpty) {
                    assertTrue(isEditable, 
                            "Empty cell at [" + row + "," + col + "] should be editable");
                } else {
                    assertFalse(isEditable, 
                            "Non-empty cell at [" + row + "," + col + "] should not be editable");
                }
            }
        }
    }
    
    @Test
    public void testPrepareDecoratedBoardVsPrepareBoard() {
        SudokuBoard regularBoard = GameDifficulty.HARD.prepareBoard(sourceBoard);
        EditableSudokuBoardDecorator decorator = GameDifficulty.HARD.prepareDecoratedBoard(sourceBoard);
        SudokuBoard decoratedBoard = decorator.getSudokuBoard();
        
        int regularEmpty = countEmptyCells(regularBoard);
        int decoratedEmpty = countEmptyCells(decoratedBoard);
        
        assertEquals(regularEmpty, decoratedEmpty, 
                "Regular and decorated boards should have same number of empty cells");
        assertEquals(60, regularEmpty, "Both should have 60 empty cells for HARD difficulty");
    }
    
    @Test
    public void testAllDifficultiesWithSameSourceBoard() {
        SudokuBoard easy = GameDifficulty.EASY.prepareBoard(sourceBoard);
        SudokuBoard medium = GameDifficulty.MEDIUM.prepareBoard(sourceBoard);
        SudokuBoard hard = GameDifficulty.HARD.prepareBoard(sourceBoard);
        
        EditableSudokuBoardDecorator easyDec = GameDifficulty.EASY.prepareDecoratedBoard(sourceBoard);
        EditableSudokuBoardDecorator mediumDec = GameDifficulty.MEDIUM.prepareDecoratedBoard(sourceBoard);
        EditableSudokuBoardDecorator hardDec = GameDifficulty.HARD.prepareDecoratedBoard(sourceBoard);
        
        assertNotNull(easy, "Easy board should not be null");
        assertNotNull(medium, "Medium board should not be null");
        assertNotNull(hard, "Hard board should not be null");
        assertNotNull(easyDec, "Easy decorator should not be null");
        assertNotNull(mediumDec, "Medium decorator should not be null");
        assertNotNull(hardDec, "Hard decorator should not be null");
        
        assertEquals(20, countEmptyCells(easy), "Easy should have 20 empty cells");
        assertEquals(40, countEmptyCells(medium), "Medium should have 40 empty cells");
        assertEquals(60, countEmptyCells(hard), "Hard should have 60 empty cells");
        
        assertEquals(20, countEmptyCells(easyDec.getSudokuBoard()), "Easy decorator should have 20 empty cells");
        assertEquals(40, countEmptyCells(mediumDec.getSudokuBoard()), "Medium decorator should have 40 empty cells");
        assertEquals(60, countEmptyCells(hardDec.getSudokuBoard()), "Hard decorator should have 60 empty cells");
    }
    
    @Test
    public void testPrepareWithEmptySourceBoard() {
        SudokuBoard partialBoard = new SudokuBoard(new BacktrackingSudokuSolver());
        partialBoard.solveGame();
        
        partialBoard.setValueAt(0, 0, 0);
        partialBoard.setValueAt(1, 1, 0);
        partialBoard.setValueAt(2, 2, 0);
        
        int initialEmptyCells = countEmptyCells(partialBoard);
        assertEquals(3, initialEmptyCells, "Should start with 3 empty cells");
        
        SudokuBoard result = GameDifficulty.EASY.prepareBoard(partialBoard);
        assertNotNull(result, "Result should not be null even with partial source board");
        
        int finalEmptyCells = countEmptyCells(result);

        assertTrue(finalEmptyCells >= 20, "Should have at least 20 empty cells");
        assertTrue(finalEmptyCells <= 23, "Should not have more than 23 empty cells (3 initial + 20 target)");
        
        assertNotSame(partialBoard, result, "Should return a different board instance");
    }
    
    @Test
    public void testEnumOrdering() {
        GameDifficulty[] values = GameDifficulty.values();
        
        assertTrue(values[0].getCellsToRemove() < values[1].getCellsToRemove(), 
                "EASY should remove fewer cells than MEDIUM");
        assertTrue(values[1].getCellsToRemove() < values[2].getCellsToRemove(), 
                "MEDIUM should remove fewer cells than HARD");
        
        assertEquals("EASY", values[0].name(), "First enum should be EASY");
        assertEquals("MEDIUM", values[1].name(), "Second enum should be MEDIUM");
        assertEquals("HARD", values[2].name(), "Third enum should be HARD");
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