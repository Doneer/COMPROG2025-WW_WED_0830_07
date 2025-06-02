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

import java.io.IOException;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Enhanced tests for DaoException to achieve 100% coverage.
 * @author zhuma
 */
public class DaoExceptionTest {
    
    @Test
    public void testConstructorWithMessage() {
        String message = "Test error message";
        DaoException exception = new DaoException(message);
        
        assertEquals(message, exception.getMessage(), "Message should match");
        assertNull(exception.getCause(), "Cause should be null");
    }
    
    @Test
    public void testConstructorWithMessageAndCause() {
        String message = "Test error with cause";
        Throwable cause = new IOException("IO error");
        
        DaoException exception = new DaoException(message, cause);
        
        assertEquals(message, exception.getMessage(), "Message should match");
        assertEquals(cause, exception.getCause(), "Cause should match");
    }
    
    @Test
    public void testCreateReadException() {
        String details = "file.txt";
        Throwable cause = new IOException("File not found");
        
        DaoException exception = DaoException.createReadException(details, cause);
        
        assertNotNull(exception.getMessage(), "Message should not be null");
        assertFalse(exception.getMessage().isEmpty(), "Message should not be empty");
        assertEquals(cause, exception.getCause(), "Cause should match");
        
        assertTrue(exception.getMessage().length() > 10, 
                "Message should be properly formatted and not just a template");
    }
    
    @Test
    public void testCreateWriteException() {
        String details = "output.txt";
        Throwable cause = new IOException("Permission denied");
        
        DaoException exception = DaoException.createWriteException(details, cause);
        
        assertNotNull(exception.getMessage(), "Message should not be null");
        assertFalse(exception.getMessage().isEmpty(), "Message should not be empty");
        assertEquals(cause, exception.getCause(), "Cause should match");
        
        assertTrue(exception.getMessage().length() > 10, 
                "Message should be properly formatted and not just a template");
    }
    
    @Test
    public void testCreateNamesException() {
        String details = "/invalid/directory";
        Throwable cause = new IOException("Directory not accessible");
        
        DaoException exception = DaoException.createNamesException(details, cause);
        
        assertNotNull(exception.getMessage(), "Message should not be null");
        assertFalse(exception.getMessage().isEmpty(), "Message should not be empty");
        assertEquals(cause, exception.getCause(), "Cause should match");
        
        assertTrue(exception.getMessage().length() > 10, 
                "Message should be properly formatted and not just a template");
    }
    
    @Test
    public void testCreateReadExceptionWithDifferentLocales() {
        String details = "test.sudoku";
        Throwable cause = new IOException("Test error");
        
        Locale originalLocale = Locale.getDefault();
        try {
            Locale.setDefault(Locale.ENGLISH);
            DaoException exceptionEn = DaoException.createReadException(details, cause);
            
            Locale.setDefault(new Locale("pl"));
            DaoException exceptionPl = DaoException.createReadException(details, cause);
            
            assertNotNull(exceptionEn.getMessage(), "English message should not be null");
            assertNotNull(exceptionPl.getMessage(), "Polish message should not be null");
            
            assertNotEquals(exceptionEn.getMessage(), exceptionPl.getMessage(),
                    "Messages should be different for different locales");
            
        } finally {
            Locale.setDefault(originalLocale);
        }
    }
    
    @Test
    public void testCreateWriteExceptionWithDifferentLocales() {
        String details = "save.sudoku";
        Throwable cause = new IOException("Write error");
        
        Locale originalLocale = Locale.getDefault();
        try {
            Locale.setDefault(Locale.ENGLISH);
            DaoException exceptionEn = DaoException.createWriteException(details, cause);
            
            Locale.setDefault(new Locale("pl"));
            DaoException exceptionPl = DaoException.createWriteException(details, cause);
            
            assertNotNull(exceptionEn.getMessage(), "English message should not be null");
            assertNotNull(exceptionPl.getMessage(), "Polish message should not be null");
            
            assertNotEquals(exceptionEn.getMessage(), exceptionPl.getMessage(),
                    "Messages should be different for different locales");
            
        } finally {
            Locale.setDefault(originalLocale);
        }
    }
    
    @Test
    public void testCreateNamesExceptionWithDifferentLocales() {
        String details = "/test/directory";
        Throwable cause = new IOException("Access denied");
        
        Locale originalLocale = Locale.getDefault();
        try {
            Locale.setDefault(Locale.ENGLISH);
            DaoException exceptionEn = DaoException.createNamesException(details, cause);
            
            Locale.setDefault(new Locale("pl"));
            DaoException exceptionPl = DaoException.createNamesException(details, cause);
            
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
        DaoException exception = new DaoException("test");
        
        assertTrue(exception instanceof pl.first.sudoku.exceptions.SudokuDataException,
                "DaoException should extend SudokuDataException");
        assertTrue(exception instanceof pl.first.sudoku.exceptions.SudokuException,
                "DaoException should extend SudokuException (through inheritance)");
        assertTrue(exception instanceof Exception,
                "DaoException should extend Exception");
        assertTrue(exception instanceof Throwable,
                "DaoException should extend Throwable");
    }
    
    @Test
    public void testFactoryMethodsWithNullCause() {
        String details = "test details";
        
        DaoException readException = DaoException.createReadException(details, null);
        DaoException writeException = DaoException.createWriteException(details, null);
        DaoException namesException = DaoException.createNamesException(details, null);
        
        assertNotNull(readException.getMessage(), "Read exception message should not be null");
        assertNotNull(writeException.getMessage(), "Write exception message should not be null");
        assertNotNull(namesException.getMessage(), "Names exception message should not be null");
        
        assertNull(readException.getCause(), "Read exception cause should be null");
        assertNull(writeException.getCause(), "Write exception cause should be null");
        assertNull(namesException.getCause(), "Names exception cause should be null");
    }
    
    @Test
    public void testFactoryMethodsWithEmptyDetails() {
        String emptyDetails = "";
        Throwable cause = new RuntimeException("test cause");
        
        DaoException readException = DaoException.createReadException(emptyDetails, cause);
        DaoException writeException = DaoException.createWriteException(emptyDetails, cause);
        DaoException namesException = DaoException.createNamesException(emptyDetails, cause);
        
        assertNotNull(readException.getMessage(), "Read exception message should not be null");
        assertNotNull(writeException.getMessage(), "Write exception message should not be null");
        assertNotNull(namesException.getMessage(), "Names exception message should not be null");
        
        assertEquals(cause, readException.getCause(), "Read exception cause should match");
        assertEquals(cause, writeException.getCause(), "Write exception cause should match");
        assertEquals(cause, namesException.getCause(), "Names exception cause should match");
    }
    
    @Test
    public void testFactoryMethodsWithNullDetails() {
        Throwable cause = new RuntimeException("test cause");
        
        assertDoesNotThrow(() -> {
            DaoException readException = DaoException.createReadException(null, cause);
            assertNotNull(readException.getMessage(), "Message should not be null even with null details");
        });
        
        assertDoesNotThrow(() -> {
            DaoException writeException = DaoException.createWriteException(null, cause);
            assertNotNull(writeException.getMessage(), "Message should not be null even with null details");
        });
        
        assertDoesNotThrow(() -> {
            DaoException namesException = DaoException.createNamesException(null, cause);
            assertNotNull(namesException.getMessage(), "Message should not be null even with null details");
        });
    }
    
    @Test
    public void testDaoExceptionConstructors() {
        String message = "Test error message";
        Exception cause = new RuntimeException("Test cause");

        DaoException exception1 = new DaoException(message);
        assertEquals(message, exception1.getMessage(), "Message should match");
        assertNull(exception1.getCause(), "Cause should be null");

        DaoException exception2 = new DaoException(message, cause);
        assertEquals(message, exception2.getMessage(), "Message should match");
        assertEquals(cause, exception2.getCause(), "Cause should match");
    }

    @Test
    public void testAllExceptionPaths() {
        RuntimeException cause = new RuntimeException("Test cause");

        DaoException readEx1 = DaoException.createReadException("test details", cause);
        DaoException readEx2 = DaoException.createReadException("test", null);
        DaoException readEx3 = DaoException.createReadException("", cause);

        DaoException writeEx1 = DaoException.createWriteException("test details", cause);
        DaoException writeEx2 = DaoException.createWriteException("test", null);
        DaoException writeEx3 = DaoException.createWriteException("", cause);

        DaoException namesEx1 = DaoException.createNamesException("test details", cause);
        DaoException namesEx2 = DaoException.createNamesException("test", null);
        DaoException namesEx3 = DaoException.createNamesException("", cause);

        assertNotNull(readEx1, "Read exception with details and cause should be created");
        assertNotNull(readEx2, "Read exception with details only should be created");
        assertNotNull(readEx3, "Read exception with empty details should be created");

        assertNotNull(writeEx1, "Write exception with details and cause should be created");
        assertNotNull(writeEx2, "Write exception with details only should be created");
        assertNotNull(writeEx3, "Write exception with empty details should be created");

        assertNotNull(namesEx1, "Names exception with details and cause should be created");
        assertNotNull(namesEx2, "Names exception with details only should be created");
        assertNotNull(namesEx3, "Names exception with empty details should be created");

        assertFalse(readEx1.getMessage().trim().isEmpty(), "Message should not be empty");
        assertFalse(writeEx1.getMessage().trim().isEmpty(), "Message should not be empty");
        assertFalse(namesEx1.getMessage().trim().isEmpty(), "Message should not be empty");

        assertEquals(cause, readEx1.getCause(), "Cause should be set correctly");
        assertEquals(cause, writeEx1.getCause(), "Cause should be set correctly");
        assertEquals(cause, namesEx1.getCause(), "Cause should be set correctly");

        assertNull(readEx2.getCause(), "Cause should be null when not provided");
        assertNull(writeEx2.getCause(), "Cause should be null when not provided");
        assertNull(namesEx2.getCause(), "Cause should be null when not provided");
    }

    @Test
    public void testExceptionMessageFormatting() {
        DaoException ex1 = DaoException.createReadException("", new RuntimeException());
        DaoException ex2 = DaoException.createWriteException("test file", new RuntimeException());
        DaoException ex3 = DaoException.createNamesException("directory path", new RuntimeException());

        assertNotNull(ex1.getMessage(), "Exception message should not be null");
        assertNotNull(ex2.getMessage(), "Exception message should not be null");
        assertNotNull(ex3.getMessage(), "Exception message should not be null");

        assertFalse(ex1.getMessage().trim().isEmpty(), "Message should not be empty");
        assertFalse(ex2.getMessage().trim().isEmpty(), "Message should not be empty");
        assertFalse(ex3.getMessage().trim().isEmpty(), "Message should not be empty");
    }
}