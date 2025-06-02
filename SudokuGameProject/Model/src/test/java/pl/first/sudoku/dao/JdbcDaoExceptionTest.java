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

import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Enhanced tests for JdbcDaoException to achieve 100% coverage.
 * @author zhuma
 */
public class JdbcDaoExceptionTest {
    
    @Test
    public void testConstructorWithMessage() {
        String message = "Test JDBC error message";
        JdbcDaoException exception = new JdbcDaoException(message);
        
        assertEquals(message, exception.getMessage(), "Message should match");
        assertNull(exception.getCause(), "Cause should be null");
    }
    
    @Test
    public void testConstructorWithMessageAndCause() {
        String message = "Test JDBC error with cause";
        Throwable cause = new SQLException("SQL error");
        
        JdbcDaoException exception = new JdbcDaoException(message, cause);
        
        assertEquals(message, exception.getMessage(), "Message should match");
        assertEquals(cause, exception.getCause(), "Cause should match");
    }
    
    @Test
    public void testCreateConnectionException() {
        String details = "localhost:5432";
        Throwable cause = new SQLException("Connection refused");
        
        JdbcDaoException exception = JdbcDaoException.createConnectionException(details, cause);
        
        assertNotNull(exception.getMessage(), "Message should not be null");
        assertFalse(exception.getMessage().isEmpty(), "Message should not be empty");
        assertEquals(cause, exception.getCause(), "Cause should match");
        
        assertTrue(exception.getMessage().length() > 10, 
                "Message should be properly formatted");
    }
    
    @Test
    public void testCreateSqlException() {
        String details = "INSERT INTO boards";
        Throwable cause = new SQLException("Syntax error");
        
        JdbcDaoException exception = JdbcDaoException.createSqlException(details, cause);
        
        assertNotNull(exception.getMessage(), "Message should not be null");
        assertFalse(exception.getMessage().isEmpty(), "Message should not be empty");
        assertEquals(cause, exception.getCause(), "Cause should match");
        
        assertTrue(exception.getMessage().length() > 10, 
                "Message should be properly formatted");
    }
    
    @Test
    public void testCreateTransactionException() {
        String details = "COMMIT";
        Throwable cause = new SQLException("Transaction failed");
        
        JdbcDaoException exception = JdbcDaoException.createTransactionException(details, cause);
        
        assertNotNull(exception.getMessage(), "Message should not be null");
        assertFalse(exception.getMessage().isEmpty(), "Message should not be empty");
        assertEquals(cause, exception.getCause(), "Cause should match");
        
        assertTrue(exception.getMessage().length() > 10, 
                "Message should be properly formatted");
    }
    
    @Test
    public void testCreateExceptionWithDifferentLocales() {
        String details = "test connection";
        Throwable cause = new SQLException("Test error");
        
        Locale originalLocale = Locale.getDefault();
        try {
            Locale.setDefault(Locale.ENGLISH);
            JdbcDaoException exceptionEn = JdbcDaoException.createConnectionException(details, cause);
            
            Locale.setDefault(new Locale("pl"));
            JdbcDaoException exceptionPl = JdbcDaoException.createConnectionException(details, cause);
            
            assertNotNull(exceptionEn.getMessage(), "English message should not be null");
            assertNotNull(exceptionPl.getMessage(), "Polish message should not be null");
            
            assertNotEquals(exceptionEn.getMessage(), exceptionPl.getMessage(),
                    "Messages should be different for different locales");
            
        } finally {
            Locale.setDefault(originalLocale);
        }
    }
    
    @Test
    public void testExceptionInheritance() {
        JdbcDaoException exception = new JdbcDaoException("test");
        
        assertTrue(exception instanceof pl.first.sudoku.exceptions.SudokuDataException,
                "JdbcDaoException should extend SudokuDataException");
        assertTrue(exception instanceof pl.first.sudoku.exceptions.SudokuException,
                "JdbcDaoException should extend SudokuException (through inheritance)");
        assertTrue(exception instanceof Exception,
                "JdbcDaoException should extend Exception");
        assertTrue(exception instanceof Throwable,
                "JdbcDaoException should extend Throwable");
    }
    
    @Test
    public void testFactoryMethodsWithNullCause() {
        String details = "test details";
        
        JdbcDaoException connectionException = JdbcDaoException.createConnectionException(details, null);
        JdbcDaoException sqlException = JdbcDaoException.createSqlException(details, null);
        JdbcDaoException transactionException = JdbcDaoException.createTransactionException(details, null);
        
        assertNotNull(connectionException.getMessage(), "Connection exception message should not be null");
        assertNotNull(sqlException.getMessage(), "SQL exception message should not be null");
        assertNotNull(transactionException.getMessage(), "Transaction exception message should not be null");
        
        assertNull(connectionException.getCause(), "Connection exception cause should be null");
        assertNull(sqlException.getCause(), "SQL exception cause should be null");
        assertNull(transactionException.getCause(), "Transaction exception cause should be null");
    }
}
