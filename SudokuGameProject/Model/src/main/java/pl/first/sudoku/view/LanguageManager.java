/*
 * The MIT License
 *
 * Copyright 2025 Lodz University of Technology.
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
package pl.first.sudoku.view;

/**
 *
 * @author Zhmaggernaut
 */

import java.util.Locale;
import java.util.ResourceBundle;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class LanguageManager {
    private static final String RESOURCE_BUNDLE_BASE_NAME = "pl.first.sudoku.resources.messages";
    private static final String AUTHORS_BUNDLE_BASE_NAME = "pl.first.sudoku.resources.AuthorsBundle";
    
    private static LanguageManager instance;
    
    private ObjectProperty<Locale> currentLocale;
    private ResourceBundle messagesBundle;
    private ResourceBundle authorsBundle;
    
    private LanguageManager() {
        currentLocale = new SimpleObjectProperty<>(Locale.ENGLISH);
        loadBundles();
    }
    
    public static synchronized LanguageManager getInstance() {
        if (instance == null) {
            instance = new LanguageManager();
        }
        return instance;
    }
    
    public void setLocale(Locale locale) {
        currentLocale.set(locale);
        loadBundles();
    }
    
    public Locale getLocale() {
        return currentLocale.get();
    }
    
    public ObjectProperty<Locale> localeProperty() {
        return currentLocale;
    }
    
    public ResourceBundle getMessagesBundle() {
        return messagesBundle;
    }
    
    public ResourceBundle getAuthorsBundle() {
        return authorsBundle;
    }
    
    private void loadBundles() {
        messagesBundle = ResourceBundle.getBundle(RESOURCE_BUNDLE_BASE_NAME, currentLocale.get());
        authorsBundle = ResourceBundle.getBundle(AUTHORS_BUNDLE_BASE_NAME, currentLocale.get());
    }
    
    public static String getMessage(String key) {
        return getInstance().getMessagesBundle().getString(key);
    }
    
    public static String getAuthorInfo(String key) {
        return getInstance().getAuthorsBundle().getString(key);
    }
}
