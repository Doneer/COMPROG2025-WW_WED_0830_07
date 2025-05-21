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

package pl.first.sudoku.exceptions;

import java.text.MessageFormat;
import java.util.Locale;

/**
 * Exception thrown when loading a Sudoku board fails.
 * @author zhuma
 */
public class SudokuLoadException extends SudokuDataException {
    
    private static final String MESSAGE_KEY = "exception.sudoku.load_error";
    
    public SudokuLoadException(String details) {
        super(formatMessage(Locale.getDefault(), details));
    }
    
    public SudokuLoadException(String details, Throwable cause) {
        super(formatMessage(Locale.getDefault(), details), cause);
    }
    
    public SudokuLoadException(String details, Locale locale) {
        super(formatMessage(locale, details));
    }
    
    public SudokuLoadException(String details, Locale locale, Throwable cause) {
        super(formatMessage(locale, details), cause);
    }
    
    private static String formatMessage(Locale locale, String details) {
        String template = ExceptionMessages.getMessage(MESSAGE_KEY, locale);
        return MessageFormat.format(template, details);
    }
}
