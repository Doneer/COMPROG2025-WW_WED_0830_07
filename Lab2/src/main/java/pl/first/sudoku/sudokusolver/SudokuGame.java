/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package pl.first.sudoku.sudokusolver;

/**
 * Check the checkstyle file command - checkstyle:check.
 * @author zhuma
 */

public class SudokuGame {
    public static void main(String[] args) {
        System.out.println("Sudoku Solver Demo");
        
        SudokuSolver solver = new BacktrackingSudokuSolver();
        SudokuBoard board = new SudokuBoard(solver);
        
        if (board.solveGame()) {
            System.out.println("Successfully filled Sudoku board:");
            printBoard(board);
        } else {
            System.out.println("Failed to fill Sudoku board");
        }
    }
    
    private static void printBoard(SudokuBoard board) {
        for (int row = 0; row < 9; row++) {
            if (row % 3 == 0 && row != 0) {
                System.out.println("------+-------+------");
            }
            
            for (int col = 0; col < 9; col++) {
                if (col % 3 == 0 && col != 0) {
                    System.out.print("| ");
                }
                
                System.out.print(board.getValueAt(row, col) + " ");
            }
            System.out.println();
        }
    }
}
