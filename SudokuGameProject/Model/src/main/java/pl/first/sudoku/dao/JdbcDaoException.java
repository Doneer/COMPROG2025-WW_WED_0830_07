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
 * Exception thrown by JDBC Dao implementations when database operations fail.
 * Wraps SQL exceptions with internationalized messages.
 * @author zhuma
 */
public class JdbcDaoException extends SudokuDataException {
    private static final String CONNECTION_ERROR_KEY = "exception.jdbc.connection_error";
    private static final String SQL_ERROR_KEY = "exception.jdbc.sql_error";
    private static final String TRANSACTION_ERROR_KEY = "exception.jdbc.transaction_error";
    
    public JdbcDaoException(String message) {
        super(message);
    }

    public JdbcDaoException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public static JdbcDaoException createConnectionException(String details, Throwable cause) {
        String message = formatMessage(CONNECTION_ERROR_KEY, Locale.getDefault(), details);
        return new JdbcDaoException(message, cause);
    }
    
    public static JdbcDaoException createSqlException(String details, Throwable cause) {
        String message = formatMessage(SQL_ERROR_KEY, Locale.getDefault(), details);
        return new JdbcDaoException(message, cause);
    }
    
    public static JdbcDaoException createTransactionException(String details, Throwable cause) {
        String message = formatMessage(TRANSACTION_ERROR_KEY, Locale.getDefault(), details);
        return new JdbcDaoException(message, cause);
    }
    
    private static String formatMessage(String key, Locale locale, String details) {
        String template = pl.first.sudoku.exceptions.ExceptionMessages.getMessage(key, locale);
        return MessageFormat.format(template, details);
    }
}
