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

package pl.first.sudoku.view;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import pl.first.sudoku.resources.AuthorsBundleAccessor;
import pl.first.sudoku.viewresources.ViewResourcesInfo;

import java.util.ListResourceBundle;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Manages language resources for the application.
 * Provides functionality to switch between different locales and access localized resources.
 * @author zhuma
 */
public class LanguageManager {
    private static final String RESOURCE_BUNDLE_BASE_NAME = ViewResourcesInfo.getResourceBaseName();
    private static final String AUTHORS_BUNDLE_BASE_NAME = 
            pl.first.sudoku.view.resources.AuthorsBundleAccessor.getBundleName();
    
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
        try {
            messagesBundle = ResourceBundle.getBundle(RESOURCE_BUNDLE_BASE_NAME, currentLocale.get());
        } catch (Exception e) {
            System.err.println("Error loading messages bundle: " + e.getMessage());
            messagesBundle = new ListResourceBundle() {
                @Override
                protected Object[][] getContents() {
                    return new Object[0][0];
                }
            };
        }

        try {
            authorsBundle = ResourceBundle.getBundle(AUTHORS_BUNDLE_BASE_NAME, currentLocale.get());
        } catch (Exception e) {
            System.err.println("Error loading authors bundle: " + e.getMessage());
            try {
                ClassLoader modelLoader = Class.forName(
                        "pl.first.sudoku.resources.AuthorsBundleAccessor").getClassLoader();
                authorsBundle = ResourceBundle.getBundle(AUTHORS_BUNDLE_BASE_NAME, currentLocale.get(), modelLoader);
            } catch (Exception e2) {
                System.err.println("Second attempt failed: " + e2.getMessage());
                try {
                    String className = "pl.first.sudoku.resources.AuthorsBundle_" + currentLocale.get().getLanguage();
                    Class<?> bundleClass = Class.forName(className);
                    authorsBundle = (ResourceBundle) bundleClass.getDeclaredConstructor().newInstance();
                } catch (Exception e3) {
                    System.err.println("Third attempt failed: " + e3.getMessage());
                    authorsBundle = messagesBundle;
                }
            }
        }
    }
    
    public static String getMessage(String key) {
        return getInstance().getMessagesBundle().getString(key);
    }
    
    public static String getAuthorInfo(String key) {
        try {
            return getInstance().getAuthorsBundle().getString(key);
        } catch (Exception e) {
            return AuthorsBundleAccessor.getAuthorInfo(key, getInstance().getLocale());
        }
    }
}
