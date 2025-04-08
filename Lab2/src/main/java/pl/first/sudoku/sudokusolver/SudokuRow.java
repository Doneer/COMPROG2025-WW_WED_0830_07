/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package pl.first.sudoku.sudokusolver;

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
    
    @Override
    public SudokuRow clone() throws CloneNotSupportedException {
        return (SudokuRow) super.clone();
    }
}
