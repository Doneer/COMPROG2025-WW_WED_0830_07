/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */

package pl.first.sudoku.sudokusolver;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author zhuma
 */
public class BacktrackingSudokuSolverTest {
    @Test
    public void testSolve() {
        SudokuSolver solver = new BacktrackingSudokuSolver();
        SudokuBoard board = new SudokuBoard(solver);
        
        assertTrue(solver.solve(board), "Solving should be successful");
        assertTrue(board.isValid(), "Board should be valid after solving");
        
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                int value = board.getValueAt(row, col);
                assertTrue(value >= 1 && value <= 9, 
                        "All cells should have values between 1 and 9");
            }
        }
    }

    @Test
    public void testSolveWithPrefilledValues() {
        SudokuSolver solver = new BacktrackingSudokuSolver();
        SudokuBoard board = new SudokuBoard(solver);

        board.setValueAt(0, 0, 5);
        board.setValueAt(1, 1, 3);
        board.setValueAt(2, 2, 1);
        
        assertTrue(solver.solve(board));
        assertTrue(board.isValid());

        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                int value = board.getValueAt(row, col);
                assertTrue(value >= 1 && value <= 9);
            }
        }
    }
    
    @Test
    public void testRandomSolutions() {
        SudokuSolver solver1 = new BacktrackingSudokuSolver();
        SudokuSolver solver2 = new BacktrackingSudokuSolver();
        
        SudokuBoard board1 = new SudokuBoard(solver1);
        SudokuBoard board2 = new SudokuBoard(solver2);
        
        assertTrue(solver1.solve(board1), "First board solving should succeed");
        assertTrue(solver2.solve(board2), "Second board solving should succeed");

        boolean different = false;
        int differences = 0;
        
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (board1.getValueAt(row, col) != board2.getValueAt(row, col)) {
                    different = true;
                    differences++;
                }
            }
        }
        
        assertTrue(different, "Two independent solutions should be different");
        System.out.println("Number of different positions between two solutions: " + differences);
    }
}
