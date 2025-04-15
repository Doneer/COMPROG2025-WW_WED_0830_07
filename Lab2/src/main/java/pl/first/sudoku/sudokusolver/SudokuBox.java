/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package pl.first.sudoku.sudokusolver;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing a 3x3 box in a Sudoku puzzle.
 * A box consists of 9 SudokuField objects.
 * @author Zhmaggernaut
 */
public class SudokuBox extends SudokuElement {
     public SudokuBox(List<SudokuField> fields) {
        super(fields);
    }
    
    public SudokuBox(SudokuBoard board, int x, int y) {
        super(extractFields(board, x, y));
    }
    
    private static List<SudokuField> extractFields(SudokuBoard board, int x, int y) {
        if (x < 0 || x >= 3 || y < 0 || y >= 3) {
            throw new IllegalArgumentException("Invalid box coordinates");
        }
        
        List<SudokuField> fields = new ArrayList<>();
        int startRow = y * 3;
        int startCol = x * 3;
        
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                fields.add(board.getSudokuField(startRow + row, startCol + col));
            }
        }
        
        return fields;
    }
    
    @Override
    public SudokuBox clone() {
        SudokuBox cloned = (SudokuBox) super.clone();
        return cloned;
    }
}
