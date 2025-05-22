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

package pl.first.sudoku.exceptions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Locale;

/**
 *
 * @author zhuma
 */
public class SudokuExceptionsTest {

    @Test
    public void testSudokuLoadException() {
        SudokuLoadException exception1 = new SudokuLoadException("Test details");
        assertNotNull(exception1.getMessage());
        
        Exception cause = new Exception("Original cause");
        SudokuLoadException exception2 = new SudokuLoadException("Test details", cause);
        assertEquals(cause, exception2.getCause());
        
        SudokuLoadException exception3 = new SudokuLoadException("Test details", Locale.ENGLISH);
        assertNotNull(exception3.getMessage());
        
        SudokuLoadException exception4 = new SudokuLoadException("Test details", Locale.ENGLISH, cause);
        assertEquals(cause, exception4.getCause());
    }
    
    @Test
    public void testSudokuSaveException() {
        SudokuSaveException exception1 = new SudokuSaveException("Test details");
        assertNotNull(exception1.getMessage());
        
        Exception cause = new Exception("Original cause");
        SudokuSaveException exception2 = new SudokuSaveException("Test details", cause);
        assertEquals(cause, exception2.getCause());
        
        SudokuSaveException exception3 = new SudokuSaveException("Test details", Locale.ENGLISH);
        assertNotNull(exception3.getMessage());
        
        SudokuSaveException exception4 = new SudokuSaveException("Test details", Locale.ENGLISH, cause);
        assertEquals(cause, exception4.getCause());
    }
    
    @Test
    public void testSudokuValidationException() {
        SudokuValidationException exception1 = new SudokuValidationException("Test details");
        assertNotNull(exception1.getMessage());
        
        Exception cause = new Exception("Original cause");
        SudokuValidationException exception2 = new SudokuValidationException("Test details", cause);
        assertEquals(cause, exception2.getCause());
        
        SudokuValidationException exception3 = new SudokuValidationException("Test details", Locale.ENGLISH);
        assertNotNull(exception3.getMessage());
        
        SudokuValidationException exception4 = new SudokuValidationException("Test details", Locale.ENGLISH, cause);
        assertEquals(cause, exception4.getCause());
    }
    
    @Test
    public void testSudokuExceptionConstructors() {
        Exception cause = new RuntimeException("Original cause");
        SudokuException ex1 = new SudokuException("Test message");
        SudokuException ex2 = new SudokuException("Test with cause", cause);

        assertEquals("Test message", ex1.getMessage());
        assertNull(ex1.getCause());

        assertEquals("Test with cause", ex2.getMessage());
        assertEquals(cause, ex2.getCause());
    }

    @Test
    public void testSudokuDataExceptionConstructors() {
        Exception cause = new RuntimeException("Original cause");
        SudokuDataException ex1 = new SudokuDataException("Test message");
        SudokuDataException ex2 = new SudokuDataException("Test with cause", cause);

        assertEquals("Test message", ex1.getMessage());
        assertNull(ex1.getCause());

        assertEquals("Test with cause", ex2.getMessage());
        assertEquals(cause, ex2.getCause());
    }

    @Test
    public void testExceptionsWithLocales() {
        Locale english = Locale.ENGLISH;
        Locale polish = new Locale("pl");
        Exception cause = new RuntimeException("Test cause");

        SudokuLoadException loadEx1 = new SudokuLoadException("test", english);
        SudokuLoadException loadEx2 = new SudokuLoadException("test", polish);
        SudokuLoadException loadEx3 = new SudokuLoadException("test", english, cause);
        SudokuLoadException loadEx4 = new SudokuLoadException("test", polish, cause);

        assertNotEquals(loadEx1.getMessage(), loadEx2.getMessage(), 
                "Messages with different locales should differ");
        assertEquals(cause, loadEx3.getCause());
        assertEquals(cause, loadEx4.getCause());

        SudokuSaveException saveEx1 = new SudokuSaveException("test", english);
        SudokuSaveException saveEx2 = new SudokuSaveException("test", polish);
        SudokuSaveException saveEx3 = new SudokuSaveException("test", english, cause);
        SudokuSaveException saveEx4 = new SudokuSaveException("test", polish, cause);

        assertNotEquals(saveEx1.getMessage(), saveEx2.getMessage(), 
                "Messages with different locales should differ");
        assertEquals(cause, saveEx3.getCause());
        assertEquals(cause, saveEx4.getCause());

        SudokuValidationException validEx1 = new SudokuValidationException("test", english);
        SudokuValidationException validEx2 = new SudokuValidationException("test", polish);
        SudokuValidationException validEx3 = new SudokuValidationException("test", english, cause);
        SudokuValidationException validEx4 = new SudokuValidationException("test", polish, cause);

        assertNotEquals(validEx1.getMessage(), validEx2.getMessage(), 
                "Messages with different locales should differ");
        assertEquals(cause, validEx3.getCause());
        assertEquals(cause, validEx4.getCause());
    }
}
