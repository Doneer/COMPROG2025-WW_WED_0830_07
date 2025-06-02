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

/**
 * Database configuration constants and SQL queries.
 * Contains connection parameters and prepared SQL statements for Sudoku board persistence.
 * @author zhuma
 */
public class DatabaseConfig {
    public static final String DB_URL = "jdbc:postgresql://localhost:5432/sudokudb";
    public static final String DB_USER = "sudoku";
    public static final String DB_PASSWORD = "sudokupassword";
    public static final String DB_DRIVER = "org.postgresql.Driver";
    
    public static final String CREATE_BOARDS_TABLE = """
        CREATE TABLE IF NOT EXISTS sudoku_boards (
            id SERIAL PRIMARY KEY,
            name VARCHAR(255) UNIQUE NOT NULL,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        )
    """;
    
    public static final String CREATE_FIELDS_TABLE = """
        CREATE TABLE IF NOT EXISTS sudoku_fields (
            id SERIAL PRIMARY KEY,
            board_id INTEGER REFERENCES sudoku_boards(id) ON DELETE CASCADE,
            row_index INTEGER NOT NULL,
            col_index INTEGER NOT NULL,
            field_value INTEGER NOT NULL,
            is_editable BOOLEAN DEFAULT TRUE,
            UNIQUE(board_id, row_index, col_index)
        )
    """;
    
    public static final String ADD_EDITABILITY_COLUMN = """
        ALTER TABLE sudoku_fields 
        ADD COLUMN IF NOT EXISTS is_editable BOOLEAN DEFAULT TRUE
    """;
    
    public static final String INSERT_BOARD = "INSERT INTO sudoku_boards (name) VALUES (?) RETURNING id";
    
    public static final String INSERT_FIELD = 
            "INSERT INTO sudoku_fields (board_id, row_index, col_index, "
            + "field_value, is_editable) VALUES (?, ?, ?, ?, ?)";
    
    public static final String SELECT_BOARD_ID = "SELECT id FROM sudoku_boards WHERE name = ?";
    
    public static final String SELECT_FIELDS = 
            "SELECT row_index, col_index, field_value, is_editable FROM sudoku_fields WHERE board_id = ? "
            + "ORDER BY row_index, col_index";
    
    public static final String SELECT_BOARD_NAMES = "SELECT name FROM sudoku_boards ORDER BY created_at DESC";
    public static final String DELETE_BOARD = "DELETE FROM sudoku_boards WHERE name = ?";
}
