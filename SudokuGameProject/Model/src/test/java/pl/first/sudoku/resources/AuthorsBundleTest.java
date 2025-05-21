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

package pl.first.sudoku.resources;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 *
 * @author zhuma
 */
public class AuthorsBundleTest {

    @Test
    public void testAuthorsBundleEnglish() {
        ResourceBundle bundle = ResourceBundle.getBundle("pl.first.sudoku.resources.AuthorsBundle", Locale.ENGLISH);
        assertNotNull(bundle);
        
        String[] expectedKeys = {
            "authors.title", 
            "authors.names", 
            "authors.university", 
            "authors.email", 
            "authors.year"
        };
        
        for (String key : expectedKeys) {
            assertTrue(bundle.containsKey(key));
            assertNotNull(bundle.getString(key));
            assertFalse(bundle.getString(key).isEmpty());
        }
        
        assertEquals("Authors", bundle.getString("authors.title"));
    }
    
    @Test
    public void testAuthorsBundlePolish() {
        ResourceBundle bundle = ResourceBundle.getBundle("pl.first.sudoku.resources.AuthorsBundle", new Locale("pl"));
        assertNotNull(bundle);
        
        String[] expectedKeys = {
            "authors.title", 
            "authors.names", 
            "authors.university", 
            "authors.email", 
            "authors.year"
        };
        
        for (String key : expectedKeys) {
            assertTrue(bundle.containsKey(key));
            assertNotNull(bundle.getString(key));
            assertFalse(bundle.getString(key).isEmpty());
        }
        
        assertEquals("Autorzy", bundle.getString("authors.title"));
    }
    
    @Test
    public void testDirectBundleAccess() {
        AuthorsBundle_en englishBundle = new AuthorsBundle_en();
        Object[][] contents = englishBundle.getContents();
        assertNotNull(contents);
        assertTrue(contents.length > 0);
        
        AuthorsBundle_pl polishBundle = new AuthorsBundle_pl();
        Object[][] plContents = polishBundle.getContents();
        assertNotNull(plContents);
        assertTrue(plContents.length > 0);
    }
}
