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

import java.lang.reflect.Constructor;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Tests for the AuthorsBundleAccessor class.
 * @author zhuma
 */
public class AuthorsBundleAccessorTest {
    
    @Test
    public void testGetBundleName() {
        String bundleName = AuthorsBundleAccessor.getBundleName();
        assertNotNull(bundleName, "Bundle name should not be null");
        assertEquals("pl.first.sudoku.resources.AuthorsBundle", bundleName, "Bundle name should match expected value");
    }
    
    @Test
    public void testGetAuthorInfo() {
        String titleEn = AuthorsBundleAccessor.getAuthorInfo("authors.title", Locale.ENGLISH);
        assertNotNull(titleEn, "Author info for English should not be null");
        assertEquals("Authors", titleEn, "English title should match expected value");
        
        String titlePl = AuthorsBundleAccessor.getAuthorInfo("authors.title", new Locale("pl"));
        assertNotNull(titlePl, "Author info for Polish should not be null");
        assertEquals("Autorzy", titlePl, "Polish title should match expected value");
        
        String namesEn = AuthorsBundleAccessor.getAuthorInfo("authors.names", Locale.ENGLISH);
        assertNotNull(namesEn, "Names should not be null");
        assertTrue(namesEn.contains("Daniyar"), "Names should contain Daniyar");
        assertTrue(namesEn.contains("Kuzma"), "Names should contain Kuzma");
        
        String universityEn = AuthorsBundleAccessor.getAuthorInfo("authors.university", Locale.ENGLISH);
        assertNotNull(universityEn, "University should not be null");
        assertEquals("Lodz University of Technology", universityEn, "University should match expected value");
        
        String universityPl = AuthorsBundleAccessor.getAuthorInfo("authors.university", new Locale("pl"));
        assertNotNull(universityPl, "University should not be null");
        assertEquals("Politechnika Łódzka", universityPl, "University should match expected value");
        
        String emailEn = AuthorsBundleAccessor.getAuthorInfo("authors.email", Locale.ENGLISH);
        assertNotNull(emailEn, "Email should not be null");
        assertTrue(emailEn.contains("@edu.p.lodz.pl"), "Email should contain @edu.p.lodz.pl");
        
        String yearEn = AuthorsBundleAccessor.getAuthorInfo("authors.year", Locale.ENGLISH);
        assertNotNull(yearEn, "Year should not be null");
        assertEquals("2025", yearEn, "Year should be 2025");
    }
    
    @Test
    public void testResourceBundleLoading() {
        ResourceBundle bundleEn = ResourceBundle.getBundle(AuthorsBundleAccessor.getBundleName(), Locale.ENGLISH);
        assertNotNull(bundleEn, "English bundle should be loadable");
        assertEquals("Authors", bundleEn.getString("authors.title"), "Title from direct loading should match");
        
        ResourceBundle bundlePl = ResourceBundle.getBundle(AuthorsBundleAccessor.getBundleName(), new Locale("pl"));
        assertNotNull(bundlePl, "Polish bundle should be loadable");
        assertEquals("Autorzy", bundlePl.getString("authors.title"), "Title from direct loading should match");
    }
    
    @Test
    public void testMissingKey() {
        try {
            String result = AuthorsBundleAccessor.getAuthorInfo("non.existent.key", Locale.ENGLISH);
            fail("Should have thrown MissingResourceException for non-existent key");
        } catch (java.util.MissingResourceException e) {
            assertNotNull(e.getMessage(), "Exception message should not be null");
        }
    }
    
    @Test
    public void testAuthorsBundleAccessorConstructor() throws Exception {
        Constructor<AuthorsBundleAccessor> constructor = AuthorsBundleAccessor.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        AuthorsBundleAccessor instance = constructor.newInstance();
        assertNotNull(instance, "Should be able to create an instance");
    }
}
