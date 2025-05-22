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

package pl.first.sudoku.view.resources;

import java.util.ListResourceBundle;

/**
 * Resource bundle containing information about the application authors in Polish.
 * This class implements ListResourceBundle to provide author-related information
 * that can be accessed through the ResourceBundle.getBundle mechanism.
 * @author zhuma
 */
public class AuthorsBundle_pl extends ListResourceBundle {
    @Override
    protected Object[][] getContents() {
        return new Object[][] {
            {"authors.title", "Autorzy"},
            {"authors.names", "Daniyar Zhumatayev, Kuzma Martysiuk"},
            {"authors.university", "Politechnika Łódzka"},
            {"authors.email", "253857@edu.p.lodz.pl, 253854@edu.p.lodz.pl"},
            {"authors.year", "2025"}
        };
    }
}