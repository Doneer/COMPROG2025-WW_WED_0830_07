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

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Constructor;
import java.util.Locale;

/**
 *
 * @author zhuma
 */
public class ExceptionMessagesTest {

    @Test
    public void testGetMessage() {
        String message = ExceptionMessages.getMessage("exception.sudoku.load_error", Locale.ENGLISH);
        assertNotNull(message);
        assertFalse(message.isEmpty());
        
        String missingMessage = ExceptionMessages.getMessage("non.existent.key", Locale.ENGLISH);
        assertTrue(missingMessage.startsWith("!"));
        assertTrue(missingMessage.endsWith("!"));
        
        String englishMessage = ExceptionMessages.getMessage("exception.sudoku.load_error", Locale.ENGLISH);
        String polishMessage = ExceptionMessages.getMessage("exception.sudoku.load_error", new Locale("pl"));
        assertNotEquals(englishMessage, polishMessage);
    }
    
    @Test
    public void testGetMessageWithMissingResource() {
        try {
            ClassLoader mockClassLoader = new ClassLoader() {
                @Override
                public java.net.URL getResource(String name) {
                    return null;
                }
            };

            ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();

            try {
                Thread.currentThread().setContextClassLoader(mockClassLoader);

                String result = ExceptionMessages.getMessage("non.existent.key", Locale.ENGLISH);

                assertTrue(result.startsWith("!"), "Result should start with !");
                assertTrue(result.endsWith("!"), "Result should end with !");
                assertEquals("!non.existent.key!", result, "Result should be in format !key!");

            } finally {
                Thread.currentThread().setContextClassLoader(originalClassLoader);
            }
        } catch (Exception e) {
            fail("Test failed with exception: " + e.getMessage());
        }
    }
    
    @Test
    public void testExceptionMessagesLocalization() {
        String englishMessage = ExceptionMessages.getMessage("exception.sudoku.load_error", Locale.ENGLISH);
        String polishMessage = ExceptionMessages.getMessage("exception.sudoku.load_error", new Locale("pl"));

        assertNotNull(englishMessage);
        assertNotNull(polishMessage);
        assertNotEquals(englishMessage, polishMessage, "English and Polish messages should be different");

        assertTrue(englishMessage.contains("{0}"), "English message should contain parameter placeholder");
        assertTrue(polishMessage.contains("{0}"), "Polish message should contain parameter placeholder");
    }
    
    @Test
    public void testExceptionMessagesConstructor() throws Exception {
        Constructor<ExceptionMessages> constructor = ExceptionMessages.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        ExceptionMessages instance = constructor.newInstance();
        assertNotNull(instance, "Should be able to create an instance");
    }
}
