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

package pl.first.sudoku.dao;

import pl.first.sudoku.exceptions.SudokuDataException;

import java.text.MessageFormat;
import java.util.Locale;

/**
 * Exception thrown by Dao implementations when errors occur during data access operations.
 * @author zhuma
 */
public class DaoException extends SudokuDataException {
    private static final String READ_ERROR_KEY = "exception.dao.read_error";
    private static final String WRITE_ERROR_KEY = "exception.dao.write_error";
    private static final String NAMES_ERROR_KEY = "exception.dao.names_error";
    
    public DaoException(String message) {
        super(message);
    }

    public DaoException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public static DaoException createReadException(String details, Throwable cause) {
        String message = formatMessage(READ_ERROR_KEY, Locale.getDefault(), details);
        return new DaoException(message, cause);
    }
    
    public static DaoException createWriteException(String details, Throwable cause) {
        String message = formatMessage(WRITE_ERROR_KEY, Locale.getDefault(), details);
        return new DaoException(message, cause);
    }
    
    public static DaoException createNamesException(String details, Throwable cause) {
        String message = formatMessage(NAMES_ERROR_KEY, Locale.getDefault(), details);
        return new DaoException(message, cause);
    }
    
    private static String formatMessage(String key, Locale locale, String details) {
        String template = pl.first.sudoku.exceptions.ExceptionMessages.getMessage(key, locale);
        return MessageFormat.format(template, details);
    }
}
