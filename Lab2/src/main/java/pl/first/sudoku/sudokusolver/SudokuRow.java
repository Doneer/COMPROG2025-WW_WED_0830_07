/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package pl.first.sudoku.sudokusolver;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing a row in a Sudoku puzzle.
 * A row consists of 9 SudokuField objects.
 * @author Zhmaggernaut
 */
public class SudokuRow extends SudokuElement {
    public SudokuRow(List<SudokuField> fields) {
        super(fields);
    }
    
    public SudokuRow(SudokuBoard board, int y) {
        super(extractFields(board, y));
    }
    
    private static List<SudokuField> extractFields(SudokuBoard board, int y) {
        if (y < 0 || y >= 9) {
            throw new IllegalArgumentException("Invalid row index");
        }
        
        List<SudokuField> fields = new ArrayList<>();
        for (int col = 0; col < 9; col++) {
            fields.add(board.getSudokuField(y, col));
        }
        
        return fields;
    }
    
    @Override
    public SudokuRow clone() {
        SudokuRow cloned = (SudokuRow) super.clone();
        return cloned;
    }
}
