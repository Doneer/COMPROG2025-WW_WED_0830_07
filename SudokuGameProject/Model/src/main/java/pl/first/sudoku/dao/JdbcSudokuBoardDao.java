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

package pl.first.sudoku.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.first.sudoku.sudokusolver.BacktrackingSudokuSolver;
import pl.first.sudoku.sudokusolver.EditableSudokuBoardDecorator;
import pl.first.sudoku.sudokusolver.SudokuBoard;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC implementation of Dao interface for SudokuBoard objects.
 * Provides database persistence using PostgreSQL with transaction support.
 * @author zhuma
 */
public class JdbcSudokuBoardDao implements Dao<EditableSudokuBoardDecorator> {
    private static final Logger logger = LoggerFactory.getLogger(JdbcSudokuBoardDao.class);
    
    private Connection connection;
    
    public JdbcSudokuBoardDao() throws JdbcDaoException {
        this(DatabaseConfig.DB_URL, DatabaseConfig.DB_USER, DatabaseConfig.DB_PASSWORD);
    }
    
    public JdbcSudokuBoardDao(String url, String user, String password) throws JdbcDaoException {
        try {
            Class.forName(DatabaseConfig.DB_DRIVER);
            connection = DriverManager.getConnection(url, user, password);
            connection.setAutoCommit(false);
            initializeTables();
            logger.info("Database connection established successfully");
        } catch (ClassNotFoundException e) {
            logger.error("PostgreSQL driver not found", e);
            throw JdbcDaoException.createConnectionException("PostgreSQL driver not found", e);
        } catch (SQLException e) {
            logger.error("Failed to connect to database", e);
            throw JdbcDaoException.createConnectionException("Failed to connect to database", e);
        }
    }
    
    private void initializeTables() throws JdbcDaoException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(DatabaseConfig.CREATE_BOARDS_TABLE);
            stmt.execute(DatabaseConfig.CREATE_FIELDS_TABLE);
            
            try {
                stmt.execute(DatabaseConfig.ADD_EDITABILITY_COLUMN);
                logger.debug("Ensured editability column exists");
            } catch (SQLException e) {
                logger.debug("Editability column migration completed or already exists");
            }
            
            connection.commit();
            logger.debug("Database tables initialized with editability support");
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                logger.error("Failed to rollback transaction", rollbackEx);
            }
            throw JdbcDaoException.createSqlException("Failed to initialize database tables", e);
        }
    }
    
    @Override
    public EditableSudokuBoardDecorator read(String name) throws DaoException {
        logger.debug("Reading EditableSudokuBoardDecorator from database: {}", name);
        
        try {
            Integer boardId = getBoardId(name);
            if (boardId == null) {
                throw DaoException.createReadException(name, 
                        new SQLException("Board with name '" + name + "' not found"));
            }
            
            SudokuBoard board = new SudokuBoard(new BacktrackingSudokuSolver());
            EditableSudokuBoardDecorator decorator = new EditableSudokuBoardDecorator(board);
            
            try (PreparedStatement stmt = connection.prepareStatement(DatabaseConfig.SELECT_FIELDS)) {
                stmt.setInt(1, boardId);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        int row = rs.getInt("row_index");
                        int col = rs.getInt("col_index");
                        int value = rs.getInt("field_value");
                        boolean isEditable = rs.getBoolean("is_editable");
                        
                        board.setValueAt(row, col, value);
                        decorator.setFieldEditable(row, col, isEditable);
                    }
                }
            }
            
            logger.info("Successfully read EditableSudokuBoardDecorator from database: {}", name);
            return decorator;
            
        } catch (SQLException e) {
            logger.error("Error reading EditableSudokuBoardDecorator from database: {}", name, e);
            throw DaoException.createReadException(name, e);
        }
    }
    
    @Override
    public void write(String name, EditableSudokuBoardDecorator decorator) throws DaoException {
        logger.debug("Writing EditableSudokuBoardDecorator to database: {}", name);
        
        try {
            if (getBoardId(name) != null) {
                try (PreparedStatement stmt = connection.prepareStatement(DatabaseConfig.DELETE_BOARD)) {
                    stmt.setString(1, name);
                    stmt.executeUpdate();
                }
            }
            
            int boardId;
            try (PreparedStatement stmt = connection.prepareStatement(DatabaseConfig.INSERT_BOARD, 
                    Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, name);
                stmt.executeUpdate();
                
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        boardId = rs.getInt(1);
                    } else {
                        throw new SQLException("Failed to get generated board ID");
                    }
                }
            }
            
            try (PreparedStatement stmt = connection.prepareStatement(DatabaseConfig.INSERT_FIELD)) {
                SudokuBoard board = decorator.getSudokuBoard();
                
                for (int row = 0; row < 9; row++) {
                    for (int col = 0; col < 9; col++) {
                        stmt.setInt(1, boardId);
                        stmt.setInt(2, row);
                        stmt.setInt(3, col);
                        stmt.setInt(4, board.getValueAt(row, col));
                        stmt.setBoolean(5, decorator.isFieldEditable(row, col));
                        stmt.addBatch();
                    }
                }
                stmt.executeBatch();
            }
            
            connection.commit();
            logger.info("Successfully wrote EditableSudokuBoardDecorator to database: {}", name);
            
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                logger.error("Failed to rollback transaction", rollbackEx);
            }
            logger.error("Error writing EditableSudokuBoardDecorator to database: {}", name, e);
            throw DaoException.createWriteException(name, e);
        }
    }
    
    @Override
    public List<String> names() throws DaoException {
        logger.debug("Listing board names from database");
        
        try (PreparedStatement stmt = connection.prepareStatement(DatabaseConfig.SELECT_BOARD_NAMES);
             ResultSet rs = stmt.executeQuery()) {
            
            List<String> names = new ArrayList<>();
            while (rs.next()) {
                names.add(rs.getString("name"));
            }
            
            logger.info("Found {} boards in database", names.size());
            return names;
            
        } catch (SQLException e) {
            logger.error("Error listing board names from database", e);
            throw DaoException.createNamesException("database", e);
        }
    }
    
    private Integer getBoardId(String name) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(DatabaseConfig.SELECT_BOARD_ID)) {
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
                return null;
            }
        }
    }
    
    @Override
    public void close() throws Exception {
        logger.debug("Closing database connection");
        
        if (connection != null && !connection.isClosed()) {
            try {
                connection.close();
                logger.debug("Database connection closed successfully");
            } catch (SQLException e) {
                logger.error("Error closing database connection", e);
                throw new Exception("Error closing database connection", e);
            }
        }
    }
}
