/*
 * MIT License
 *
 * Copyright (c) 2025 Daniyar Zhumatayev, Kuzma Martysiuk
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package pl.first.sudoku.view;

import javafx.util.StringConverter;

/**
 * Converter for SudokuField values to and from strings.
 * Used for bidirectional binding between TextFields and SudokuField values.
 * @author zhuma
 */
public class SudokuFieldConverter extends StringConverter<Integer> {
    
    @Override
    public String toString(Integer value) {
        if (value == null || value == 0) {
            return "";
        }
        return value.toString();
    }
    
    @Override
    public Integer fromString(String string) {
        if (string == null || string.trim().isEmpty()) {
            return 0;
        }
        try {
            int value = Integer.parseInt(string.trim());
            if (value >= 1 && value <= 9) {
                return value;
            } else {
                return 0;
            }
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
