/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package pl.first.sudoku.sudokusolver;

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
    
    @Override
    public SudokuColumn clone() throws CloneNotSupportedException {
        return (SudokuColumn) super.clone();
    }
}
