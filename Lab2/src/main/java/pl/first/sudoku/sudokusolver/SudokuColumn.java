/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package pl.first.sudoku.sudokusolver;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing a column in a Sudoku puzzle.
 * A column consists of 9 SudokuField objects.
 * @author Zhmaggernaut
 */
public class SudokuColumn extends SudokuElement {
    public SudokuColumn(List<SudokuField> fields) {
        super(fields);
    }
    
    public SudokuColumn(SudokuBoard board, int x) {
        super(extractFields(board, x));
    }
    
    private static List<SudokuField> extractFields(SudokuBoard board, int x) {
        if (x < 0 || x >= 9) {
            throw new IllegalArgumentException("Invalid column index");
        }
        
        List<SudokuField> fields = new ArrayList<>();
        for (int row = 0; row < 9; row++) {
            fields.add(board.getSudokuField(row, x));
        }
        
        return fields;
    }
    
    @Override
    public SudokuColumn clone() {
        SudokuColumn cloned = (SudokuColumn) super.clone();
        return cloned;
    }
}
