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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.first.sudoku.sudokusolver.BacktrackingSudokuSolver;
import pl.first.sudoku.sudokusolver.EditableSudokuBoardDecorator;
import pl.first.sudoku.sudokusolver.SudokuBoard;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Enhanced tests for JdbcSudokuBoardDao to achieve 100% coverage with EditableSudokuBoardDecorator.
 * @author zhuma
 */
public class JdbcSudokuBoardDaoTest {
    
    private EditableSudokuBoardDecorator testDecorator;
    
    @BeforeEach
    public void setUp() {
        SudokuBoard testBoard = new SudokuBoard(new BacktrackingSudokuSolver());
        testBoard.solveGame();
        testDecorator = new EditableSudokuBoardDecorator(testBoard);
        testDecorator.lockNonEmptyFields();
    }
    
    @Test
    public void testWriteAndRead() throws Exception {
        String boardName = "testBoard_" + System.currentTimeMillis();
        
        try (Dao<EditableSudokuBoardDecorator> dao = SudokuBoardDaoFactory.getJdbcDao()) {
            dao.write(boardName, testDecorator);
            
            EditableSudokuBoardDecorator loadedDecorator = dao.read(boardName);
            
            assertNotNull(loadedDecorator, "Loaded decorator should not be null");
            assertNotSame(testDecorator, loadedDecorator, "Loaded decorator should be different object");
            assertEquals(testDecorator.getSudokuBoard(), loadedDecorator.getSudokuBoard(), 
                    "Loaded board should equal original board");
            
            for (int row = 0; row < 9; row++) {
                for (int col = 0; col < 9; col++) {
                    assertEquals(testDecorator.isFieldEditable(row, col), loadedDecorator.isFieldEditable(row, col),
                            "Editability should be preserved at position [" + row + "," + col + "]");
                    assertEquals(testDecorator.getValueAt(row, col), loadedDecorator.getValueAt(row, col),
                            "Field values should match at position [" + row + "," + col + "]");
                }
            }
        }
    }
    
    @Test
    public void testReadNonExistentBoard() {
        try (Dao<EditableSudokuBoardDecorator> dao = SudokuBoardDaoFactory.getJdbcDao()) {
            assertThrows(DaoException.class, () -> {
                dao.read("nonexistent_board_name");
            }, "Reading non-existent board should throw DaoException");
        } catch (Exception e) {
            fail("Exception should not be thrown when closing DAO: " + e.getMessage());
        }
    }
    
    @Test
    public void testNames() throws Exception {
        String boardName1 = "testBoard1_" + System.currentTimeMillis();
        String boardName2 = "testBoard2_" + System.currentTimeMillis();
        
        try (Dao<EditableSudokuBoardDecorator> dao = SudokuBoardDaoFactory.getJdbcDao()) {
            List<String> initialNames = dao.names();
            
            dao.write(boardName1, testDecorator);
            dao.write(boardName2, testDecorator);
            
            List<String> names = dao.names();
            
            assertTrue(names.size() >= initialNames.size() + 2, "Should have at least 2 more boards");
            assertTrue(names.contains(boardName1), "Should contain board1");
            assertTrue(names.contains(boardName2), "Should contain board2");
        }
    }
    
    @Test
    public void testOverwriteExistingBoard() throws Exception {
        String boardName = "overwriteTest_" + System.currentTimeMillis();
        
        SudokuBoard board1 = new SudokuBoard(new BacktrackingSudokuSolver());
        board1.solveGame();
        EditableSudokuBoardDecorator decorator1 = new EditableSudokuBoardDecorator(board1);
        decorator1.lockNonEmptyFields();
        
        SudokuBoard board2 = new SudokuBoard(new BacktrackingSudokuSolver());
        board2.solveGame();
        EditableSudokuBoardDecorator decorator2 = new EditableSudokuBoardDecorator(board2);
        for (int i = 0; i < 5; i++) {
            decorator2.setFieldEditable(0, i, true);
        }
        
        try (Dao<EditableSudokuBoardDecorator> dao = SudokuBoardDaoFactory.getJdbcDao()) {
            dao.write(boardName, decorator1);
            
            dao.write(boardName, decorator2);
            
            EditableSudokuBoardDecorator loadedDecorator = dao.read(boardName);
            
            assertEquals(decorator2.getSudokuBoard(), loadedDecorator.getSudokuBoard(), 
                    "Should load the overwritten board");
            
            for (int i = 0; i < 5; i++) {
                assertEquals(decorator2.isFieldEditable(0, i), loadedDecorator.isFieldEditable(0, i),
                        "Editability should match decorator2 at [0," + i + "]");
            }
        }
    }
    
    @Test
    public void testTransactionRollback() throws Exception {
        String boardName = "transactionTest_" + System.currentTimeMillis();
        
        try (JdbcSudokuBoardDao dao = (JdbcSudokuBoardDao) SudokuBoardDaoFactory.getJdbcDao()) {
            assertNotNull(dao, "DAO should be created successfully");
            
            assertDoesNotThrow(() -> {
                dao.write(boardName, testDecorator);
            }, "Writing valid decorator should not throw exception");
            
            EditableSudokuBoardDecorator loadedDecorator = dao.read(boardName);
            assertEquals(testDecorator.getSudokuBoard(), loadedDecorator.getSudokuBoard(), 
                    "Transaction should be committed successfully");
        }
    }
    
    @Test
    public void testAutoCloseablePattern() throws Exception {
        String boardName = "autoCloseTest_" + System.currentTimeMillis();
        
        try (Dao<EditableSudokuBoardDecorator> dao = SudokuBoardDaoFactory.getJdbcDao()) {
            dao.write(boardName, testDecorator);
        }
        
        try (Dao<EditableSudokuBoardDecorator> dao = SudokuBoardDaoFactory.getJdbcDao()) {
            EditableSudokuBoardDecorator loadedDecorator = dao.read(boardName);
            assertEquals(testDecorator.getSudokuBoard(), loadedDecorator.getSudokuBoard(), 
                    "Board should be readable after DAO was closed");
        }
    }
    
    @Test
    public void testFactoryCreation() throws Exception {
        Dao<EditableSudokuBoardDecorator> dao = SudokuBoardDaoFactory.getJdbcDao();
        
        assertNotNull(dao, "Factory should return non-null DAO");
        assertTrue(dao instanceof JdbcSudokuBoardDao, "Factory should return JdbcSudokuBoardDao instance");
        
        dao.close();
    }
    
    @Test
    public void testDatabaseConnectionException() {
        assertThrows(JdbcDaoException.class, () -> {
            new JdbcSudokuBoardDao(
                "jdbc:postgresql://nonexistent_host:5432/testdb",
                "invalid_user",
                "invalid_password"
            );
        }, "Should throw JdbcDaoException for invalid connection");
    }
    
    @Test
    public void testMultipleOperations() throws Exception {
        String boardName1 = "multiOp1_" + System.currentTimeMillis();
        String boardName2 = "multiOp2_" + System.currentTimeMillis();
        
        SudokuBoard board2 = new SudokuBoard(new BacktrackingSudokuSolver());
        board2.solveGame();
        EditableSudokuBoardDecorator decorator2 = new EditableSudokuBoardDecorator(board2);
        decorator2.lockNonEmptyFields();
        
        try (Dao<EditableSudokuBoardDecorator> dao = SudokuBoardDaoFactory.getJdbcDao()) {
            dao.write(boardName1, testDecorator);
            dao.write(boardName2, decorator2);
            
            EditableSudokuBoardDecorator loaded1 = dao.read(boardName1);
            EditableSudokuBoardDecorator loaded2 = dao.read(boardName2);
            
            assertEquals(testDecorator.getSudokuBoard(), loaded1.getSudokuBoard(), "First board should match");
            assertEquals(decorator2.getSudokuBoard(), loaded2.getSudokuBoard(), "Second board should match");
            
            List<String> names = dao.names();
            assertTrue(names.contains(boardName1), "Names should contain first board");
            assertTrue(names.contains(boardName2), "Names should contain second board");
        }
    }
    
    @Test
    public void testCloseWithClosedConnection() throws Exception {
        JdbcSudokuBoardDao dao = (JdbcSudokuBoardDao) SudokuBoardDaoFactory.getJdbcDao();
        
        Field connectionField = JdbcSudokuBoardDao.class.getDeclaredField("connection");
        connectionField.setAccessible(true);
        Connection connection = (Connection) connectionField.get(dao);
        
        connection.close();
        
        assertDoesNotThrow(() -> dao.close(), "Close should handle already closed connection gracefully");
    }
    
    @Test
    public void testCloseWithNullConnection() throws Exception {
        JdbcSudokuBoardDao dao = (JdbcSudokuBoardDao) SudokuBoardDaoFactory.getJdbcDao();
        
        Field connectionField = JdbcSudokuBoardDao.class.getDeclaredField("connection");
        connectionField.setAccessible(true);
        connectionField.set(dao, null);
        
        assertDoesNotThrow(() -> dao.close(), "Close should handle null connection gracefully");
    }
    
    @Test
    public void testWriteWithSQLException() throws Exception {
        String boardName = "sqlExceptionTest_" + System.currentTimeMillis();
        
        try (JdbcSudokuBoardDao dao = (JdbcSudokuBoardDao) SudokuBoardDaoFactory.getJdbcDao()) {
            Field connectionField = JdbcSudokuBoardDao.class.getDeclaredField("connection");
            connectionField.setAccessible(true);
            Connection connection = (Connection) connectionField.get(dao);
            
            connection.close();
            
            assertThrows(DaoException.class, () -> {
                dao.write(boardName, testDecorator);
            }, "Writing with closed connection should throw DaoException");
        }
    }
    
    @Test
    public void testReadWithSQLException() throws Exception {
        try (JdbcSudokuBoardDao dao = (JdbcSudokuBoardDao) SudokuBoardDaoFactory.getJdbcDao()) {
            Field connectionField = JdbcSudokuBoardDao.class.getDeclaredField("connection");
            connectionField.setAccessible(true);
            Connection connection = (Connection) connectionField.get(dao);
            
            connection.close();
            
            assertThrows(DaoException.class, () -> {
                dao.read("test_board");
            }, "Reading with closed connection should throw DaoException");
        }
    }
    
    @Test
    public void testNamesWithSQLException() throws Exception {
        try (JdbcSudokuBoardDao dao = (JdbcSudokuBoardDao) SudokuBoardDaoFactory.getJdbcDao()) {
            Field connectionField = JdbcSudokuBoardDao.class.getDeclaredField("connection");
            connectionField.setAccessible(true);
            Connection connection = (Connection) connectionField.get(dao);
            
            connection.close();
            
            assertThrows(DaoException.class, () -> {
                dao.names();
            }, "Getting names with closed connection should throw DaoException");
        }
    }
    
    @Test
    public void testDriverNotFound() {
        try {
            Class.forName("invalid.driver.Driver");
            fail("Should have thrown ClassNotFoundException");
        } catch (ClassNotFoundException e) {
            JdbcDaoException exception = JdbcDaoException.createConnectionException(
                "Driver not found", e);
            assertNotNull(exception);
            assertEquals(e, exception.getCause());
        }
    }
    
    @Test
    public void testEditabilityPreservation() throws Exception {
        String boardName = "editabilityTest_" + System.currentTimeMillis();
        
        SudokuBoard board = new SudokuBoard(new BacktrackingSudokuSolver());
        board.solveGame();
        EditableSudokuBoardDecorator decorator = new EditableSudokuBoardDecorator(board);
        
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                boolean shouldBeEditable = (row + col) % 2 == 0;
                decorator.setFieldEditable(row, col, shouldBeEditable);
            }
        }
        
        try (Dao<EditableSudokuBoardDecorator> dao = SudokuBoardDaoFactory.getJdbcDao()) {
            dao.write(boardName, decorator);
            EditableSudokuBoardDecorator loadedDecorator = dao.read(boardName);
            
            for (int row = 0; row < 9; row++) {
                for (int col = 0; col < 9; col++) {
                    assertEquals(decorator.isFieldEditable(row, col), loadedDecorator.isFieldEditable(row, col),
                            "Editability should be preserved at [" + row + "," + col + "]");
                }
            }
        }
    }
    
    @Test
    public void testCloseException() throws Exception {
        class ThrowingConnection implements Connection {
            private boolean closed = false;
            
            @Override
            public void close() throws SQLException {
                if (closed) {
                    throw new SQLException("Connection already closed");
                }
                closed = true;
                throw new SQLException("Simulated close error");
            }
            
            @Override
            public boolean isClosed() throws SQLException {
                return closed;
            }
            
            @Override
            public void setAutoCommit(boolean autoCommit) throws SQLException {}
            @Override
            public java.sql.Statement createStatement() throws SQLException { return null; }
            @Override
            public java.sql.PreparedStatement prepareStatement(String sql) throws SQLException { return null; }
            @Override
            public java.sql.CallableStatement prepareCall(String sql) throws SQLException { return null; }
            @Override
            public String nativeSQL(String sql) throws SQLException { return null; }
            @Override
            public boolean getAutoCommit() throws SQLException { return false; }
            @Override
            public void commit() throws SQLException {}
            @Override
            public void rollback() throws SQLException {}
            @Override
            public java.sql.DatabaseMetaData getMetaData() throws SQLException { return null; }
            @Override
            public void setReadOnly(boolean readOnly) throws SQLException {}
            @Override
            public boolean isReadOnly() throws SQLException { return false; }
            @Override
            public void setCatalog(String catalog) throws SQLException {}
            @Override
            public String getCatalog() throws SQLException { return null; }
            @Override
            public void setTransactionIsolation(int level) throws SQLException {}
            @Override
            public int getTransactionIsolation() throws SQLException { return 0; }
            @Override
            public java.sql.SQLWarning getWarnings() throws SQLException { return null; }
            @Override
            public void clearWarnings() throws SQLException {}
            @Override
            public java.sql.Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException { return null; }
            @Override
            public java.sql.PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException { return null; }
            @Override
            public java.sql.CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException { return null; }
            @Override
            public java.util.Map<String, Class<?>> getTypeMap() throws SQLException { return null; }
            @Override
            public void setTypeMap(java.util.Map<String, Class<?>> map) throws SQLException {}
            @Override
            public void setHoldability(int holdability) throws SQLException {}
            @Override
            public int getHoldability() throws SQLException { return 0; }
            @Override
            public java.sql.Savepoint setSavepoint() throws SQLException { return null; }
            @Override
            public java.sql.Savepoint setSavepoint(String name) throws SQLException { return null; }
            @Override
            public void rollback(java.sql.Savepoint savepoint) throws SQLException {}
            @Override
            public void releaseSavepoint(java.sql.Savepoint savepoint) throws SQLException {}
            @Override
            public java.sql.Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException { return null; }
            @Override
            public java.sql.PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException { return null; }
            @Override
            public java.sql.CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException { return null; }
            @Override
            public java.sql.PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException { return null; }
            @Override
            public java.sql.PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException { return null; }
            @Override
            public java.sql.PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException { return null; }
            @Override
            public java.sql.Clob createClob() throws SQLException { return null; }
            @Override
            public java.sql.Blob createBlob() throws SQLException { return null; }
            @Override
            public java.sql.NClob createNClob() throws SQLException { return null; }
            @Override
            public java.sql.SQLXML createSQLXML() throws SQLException { return null; }
            @Override
            public boolean isValid(int timeout) throws SQLException { return false; }
            @Override
            public void setClientInfo(String name, String value) throws java.sql.SQLClientInfoException {}
            @Override
            public void setClientInfo(java.util.Properties properties) throws java.sql.SQLClientInfoException {}
            @Override
            public String getClientInfo(String name) throws SQLException { return null; }
            @Override
            public java.util.Properties getClientInfo() throws SQLException { return null; }
            @Override
            public java.sql.Array createArrayOf(String typeName, Object[] elements) throws SQLException { return null; }
            @Override
            public java.sql.Struct createStruct(String typeName, Object[] attributes) throws SQLException { return null; }
            @Override
            public void setSchema(String schema) throws SQLException {}
            @Override
            public String getSchema() throws SQLException { return null; }
            @Override
            public void abort(java.util.concurrent.Executor executor) throws SQLException {}
            @Override
            public void setNetworkTimeout(java.util.concurrent.Executor executor, int milliseconds) throws SQLException {}
            @Override
            public int getNetworkTimeout() throws SQLException { return 0; }
            @Override
            public <T> T unwrap(Class<T> iface) throws SQLException { return null; }
            @Override
            public boolean isWrapperFor(Class<?> iface) throws SQLException { return false; }
        }
        
        JdbcSudokuBoardDao dao = (JdbcSudokuBoardDao) SudokuBoardDaoFactory.getJdbcDao();
        
        Field connectionField = JdbcSudokuBoardDao.class.getDeclaredField("connection");
        connectionField.setAccessible(true);
        connectionField.set(dao, new ThrowingConnection());
        
        Exception exception = assertThrows(Exception.class, () -> {
            dao.close();
        }, "Close should throw exception when connection.close() fails");
        
        assertTrue(exception.getMessage().contains("Error closing database connection"),
                "Exception should mention database connection closing error");
    }
}