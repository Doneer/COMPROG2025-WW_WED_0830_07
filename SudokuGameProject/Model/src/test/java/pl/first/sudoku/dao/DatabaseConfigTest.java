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
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Constructor;

/**
 * Test for DatabaseConfig constants and SQL queries.
 * @author zhuma
 */
public class DatabaseConfigTest {
    
    @Test
    public void testDatabaseConstants() {
        assertEquals("jdbc:postgresql://localhost:5432/sudokudb", DatabaseConfig.DB_URL);
        assertEquals("sudoku", DatabaseConfig.DB_USER);
        assertEquals("sudokupassword", DatabaseConfig.DB_PASSWORD);
        assertEquals("org.postgresql.Driver", DatabaseConfig.DB_DRIVER);
    }
    
    @Test
    public void testSqlQueries() {
        assertNotNull(DatabaseConfig.CREATE_BOARDS_TABLE);
        assertNotNull(DatabaseConfig.CREATE_FIELDS_TABLE);
        assertNotNull(DatabaseConfig.ADD_EDITABILITY_COLUMN);
        assertNotNull(DatabaseConfig.INSERT_BOARD);
        assertNotNull(DatabaseConfig.INSERT_FIELD);
        assertNotNull(DatabaseConfig.SELECT_BOARD_ID);
        assertNotNull(DatabaseConfig.SELECT_FIELDS);
        assertNotNull(DatabaseConfig.SELECT_BOARD_NAMES);
        assertNotNull(DatabaseConfig.DELETE_BOARD);
        
        assertTrue(DatabaseConfig.CREATE_BOARDS_TABLE.contains("sudoku_boards"));
        assertTrue(DatabaseConfig.CREATE_FIELDS_TABLE.contains("sudoku_fields"));
        assertTrue(DatabaseConfig.INSERT_BOARD.contains("RETURNING id"));
        assertTrue(DatabaseConfig.SELECT_FIELDS.contains("ORDER BY"));
    }
    
    @Test
    public void testConstructor() throws Exception {
        Constructor<DatabaseConfig> constructor = DatabaseConfig.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        DatabaseConfig instance = constructor.newInstance();
        assertNotNull(instance);
    }
}
