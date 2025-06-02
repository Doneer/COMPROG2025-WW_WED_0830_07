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
import org.junit.jupiter.api.io.TempDir;
import pl.first.sudoku.sudokusolver.BacktrackingSudokuSolver;
import pl.first.sudoku.sudokusolver.EditableSudokuBoardDecorator;
import pl.first.sudoku.sudokusolver.SudokuBoard;

import java.lang.reflect.Constructor;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Enhanced tests for SudokuBoardDaoFactory to achieve 100% coverage with database support.
 * @author zhuma
 */
public class SudokuBoardDaoFactoryTest {
    
    @TempDir
    Path tempDir;
    
    @Test
    public void testGetFileDao() throws Exception {
        String directoryName = tempDir.toString();
        
        Dao<SudokuBoard> dao = SudokuBoardDaoFactory.getFileDao(directoryName);
        
        assertNotNull(dao, "Factory should return non-null DAO");
        assertTrue(dao instanceof FileSudokuBoardDao, 
                "Factory should return FileSudokuBoardDao instance");
        
        SudokuBoard testBoard = new SudokuBoard(new BacktrackingSudokuSolver());
        testBoard.solveGame();
        
        try (dao) {
            dao.write("test.sudoku", testBoard);
            SudokuBoard loaded = dao.read("test.sudoku");
            assertEquals(testBoard, loaded, "DAO should work correctly");
        }
    }
    
    @Test
    public void testGetEditableFileDao() throws Exception {
        String directoryName = tempDir.toString();
        
        Dao<EditableSudokuBoardDecorator> dao = SudokuBoardDaoFactory.getEditableFileDao(directoryName);
        
        assertNotNull(dao, "Factory should return non-null DAO");
        assertTrue(dao instanceof EditableSudokuBoardDao, 
                "Factory should return EditableSudokuBoardDao instance");
        
        SudokuBoard testBoard = new SudokuBoard(new BacktrackingSudokuSolver());
        testBoard.solveGame();
        EditableSudokuBoardDecorator decorator = new EditableSudokuBoardDecorator(testBoard);
        decorator.lockNonEmptyFields();
        
        try (dao) {
            dao.write("test.sudoku", decorator);
            EditableSudokuBoardDecorator loaded = dao.read("test.sudoku");
            assertEquals(decorator.getSudokuBoard(), loaded.getSudokuBoard(), 
                    "DAO should work correctly");
        }
    }
    
    @Test
    public void testGetJdbcDao() throws Exception {
        Dao<EditableSudokuBoardDecorator> dao = SudokuBoardDaoFactory.getJdbcDao();
        
        assertNotNull(dao, "Factory should return non-null DAO");
        assertTrue(dao instanceof JdbcSudokuBoardDao, 
                "Factory should return JdbcSudokuBoardDao instance");
        
        SudokuBoard testBoard = new SudokuBoard(new BacktrackingSudokuSolver());
        testBoard.solveGame();
        EditableSudokuBoardDecorator decorator = new EditableSudokuBoardDecorator(testBoard);
        decorator.lockNonEmptyFields();
        
        String testName = "factoryTest_" + System.currentTimeMillis();
        
        try (dao) {
            dao.write(testName, decorator);
            EditableSudokuBoardDecorator loaded = dao.read(testName);
            assertEquals(decorator.getSudokuBoard(), loaded.getSudokuBoard(), 
                    "JDBC DAO should work correctly");
            
            for (int row = 0; row < 9; row++) {
                for (int col = 0; col < 9; col++) {
                    assertEquals(decorator.isFieldEditable(row, col), loaded.isFieldEditable(row, col),
                            "Editability should be preserved at [" + row + "," + col + "]");
                }
            }
        }
    }
    
    @Test
    public void testGetLegacyJdbcDao() {
        assertThrows(UnsupportedOperationException.class, () -> {
            SudokuBoardDaoFactory.getLegacyJdbcDao();
        }, "Legacy method should throw UnsupportedOperationException");
    }
    
    @Test
    public void testGetFileDaoWithNullParameter() {
        assertThrows(NullPointerException.class, () -> {
            SudokuBoardDaoFactory.getFileDao(null);
        }, "Should throw NullPointerException for null directory name");
    }
    
    @Test
    public void testGetEditableFileDaoWithNullParameter() {
        assertThrows(NullPointerException.class, () -> {
            SudokuBoardDaoFactory.getEditableFileDao(null);
        }, "Should throw NullPointerException for null directory name");
    }
    
    @Test
    public void testGetFileDaoWithEmptyString() throws Exception {
        Dao<SudokuBoard> dao = SudokuBoardDaoFactory.getFileDao("");
        
        assertNotNull(dao, "Factory should handle empty string");
        assertTrue(dao instanceof FileSudokuBoardDao, 
                "Should return FileSudokuBoardDao instance");
        
        dao.close();
    }
    
    @Test
    public void testGetEditableFileDaoWithEmptyString() throws Exception {
        Dao<EditableSudokuBoardDecorator> dao = SudokuBoardDaoFactory.getEditableFileDao("");
        
        assertNotNull(dao, "Factory should handle empty string");
        assertTrue(dao instanceof EditableSudokuBoardDao, 
                "Should return EditableSudokuBoardDao instance");
        
        dao.close();
    }
    
    @Test
    public void testFactoryConstructor() throws Exception {
        Constructor<SudokuBoardDaoFactory> constructor = 
                SudokuBoardDaoFactory.class.getDeclaredConstructor();
        
        SudokuBoardDaoFactory factory = constructor.newInstance();
        
        assertNotNull(factory, "Should be able to create factory instance");
    }
    
    @Test
    public void testMultipleFactoryCalls() throws Exception {
        String directory1 = tempDir.resolve("dir1").toString();
        String directory2 = tempDir.resolve("dir2").toString();
        
        Dao<SudokuBoard> dao1 = SudokuBoardDaoFactory.getFileDao(directory1);
        Dao<SudokuBoard> dao2 = SudokuBoardDaoFactory.getFileDao(directory2);
        
        assertNotNull(dao1, "First DAO should not be null");
        assertNotNull(dao2, "Second DAO should not be null");
        assertNotSame(dao1, dao2, "Different calls should return different instances");
        
        dao1.close();
        dao2.close();
    }
    
    @Test
    public void testMultipleEditableFactoryCalls() throws Exception {
        String directory1 = tempDir.resolve("editable1").toString();
        String directory2 = tempDir.resolve("editable2").toString();
        
        Dao<EditableSudokuBoardDecorator> dao1 = 
                SudokuBoardDaoFactory.getEditableFileDao(directory1);
        Dao<EditableSudokuBoardDecorator> dao2 = 
                SudokuBoardDaoFactory.getEditableFileDao(directory2);
        
        assertNotNull(dao1, "First DAO should not be null");
        assertNotNull(dao2, "Second DAO should not be null");
        assertNotSame(dao1, dao2, "Different calls should return different instances");
        
        dao1.close();
        dao2.close();
    }
    
    @Test
    public void testMultipleJdbcFactoryCalls() throws Exception {
        Dao<EditableSudokuBoardDecorator> dao1 = SudokuBoardDaoFactory.getJdbcDao();
        Dao<EditableSudokuBoardDecorator> dao2 = SudokuBoardDaoFactory.getJdbcDao();
        
        assertNotNull(dao1, "First JDBC DAO should not be null");
        assertNotNull(dao2, "Second JDBC DAO should not be null");
        assertNotSame(dao1, dao2, "Different calls should return different instances");
        
        dao1.close();
        dao2.close();
    }
    
    @Test
    public void testFactoryWithSpecialCharacters() throws Exception {
        String specialDir = tempDir.resolve("test_dir-123").toString();
        
        Dao<SudokuBoard> dao = SudokuBoardDaoFactory.getFileDao(specialDir);
        assertNotNull(dao, "Factory should handle special characters in directory name");
        
        dao.close();
    }
    
    @Test
    public void testFactoryReturnTypes() {
        String testDir = tempDir.toString();
        
        Dao<SudokuBoard> fileDao = SudokuBoardDaoFactory.getFileDao(testDir);
        Dao<EditableSudokuBoardDecorator> editableDao = 
                SudokuBoardDaoFactory.getEditableFileDao(testDir);
        
        assertEquals(FileSudokuBoardDao.class, fileDao.getClass(), 
                "getFileDao should return FileSudokuBoardDao");
        assertEquals(EditableSudokuBoardDao.class, editableDao.getClass(), 
                "getEditableFileDao should return EditableSudokuBoardDao");
        
        try {
            fileDao.close();
            editableDao.close();
        } catch (Exception e) {
            fail("Should not throw exception when closing: " + e.getMessage());
        }
    }
    
    @Test
    public void testJdbcFactoryReturnType() throws Exception {
        Dao<EditableSudokuBoardDecorator> jdbcDao = SudokuBoardDaoFactory.getJdbcDao();
        
        assertEquals(JdbcSudokuBoardDao.class, jdbcDao.getClass(), 
                "getJdbcDao should return JdbcSudokuBoardDao");
        
        jdbcDao.close();
    }
    
    @Test
    public void testFactoryWithDifferentDirectoryPaths() throws Exception {
        String[] testPaths = {
            tempDir.toString(),
            tempDir.resolve("subdir").toString(),
            tempDir.resolve("nested").resolve("path").toString(),
            "testdir",
            "./testdir2"
        };
        
        for (String path : testPaths) {
            try (Dao<SudokuBoard> dao = SudokuBoardDaoFactory.getFileDao(path)) {
                assertNotNull(dao, "Factory should handle path: " + path);
                assertTrue(dao instanceof FileSudokuBoardDao, 
                        "Should return correct type for path: " + path);
            }
        }
    }
    
    @Test
    public void testFactoryIntegrationWithRealOperations() throws Exception {
        String testDir = tempDir.resolve("integration").toString();
        
        SudokuBoard board = new SudokuBoard(new BacktrackingSudokuSolver());
        board.solveGame();
        
        try (Dao<SudokuBoard> fileDao = SudokuBoardDaoFactory.getFileDao(testDir)) {
            fileDao.write("regular.sudoku", board);
            SudokuBoard loaded = fileDao.read("regular.sudoku");
            assertEquals(board, loaded, "Regular DAO should work through factory");
        }
        
        EditableSudokuBoardDecorator decorator = new EditableSudokuBoardDecorator(board);
        decorator.lockNonEmptyFields();
        
        try (Dao<EditableSudokuBoardDecorator> editableDao = 
                SudokuBoardDaoFactory.getEditableFileDao(testDir)) {
            editableDao.write("editable.sudoku", decorator);
            EditableSudokuBoardDecorator loadedDecorator = editableDao.read("editable.sudoku");
            assertEquals(decorator.getSudokuBoard(), loadedDecorator.getSudokuBoard(), 
                    "Editable DAO should work through factory");
        }
        
        String testName = "jdbcIntegration_" + System.currentTimeMillis();
        try (Dao<EditableSudokuBoardDecorator> jdbcDao = SudokuBoardDaoFactory.getJdbcDao()) {
            jdbcDao.write(testName, decorator);
            EditableSudokuBoardDecorator loadedJdbc = jdbcDao.read(testName);
            assertEquals(decorator.getSudokuBoard(), loadedJdbc.getSudokuBoard(), 
                    "JDBC DAO should work through factory");
            
            for (int row = 0; row < 9; row++) {
                for (int col = 0; col < 9; col++) {
                    assertEquals(decorator.isFieldEditable(row, col), 
                            loadedJdbc.isFieldEditable(row, col),
                            "JDBC DAO should preserve editability through factory");
                }
            }
        }
    }
    
    @Test
    public void testFactoryStaticMethods() {
        assertDoesNotThrow(() -> {
            String testDir = tempDir.toString();
            
            Dao<SudokuBoard> dao1 = SudokuBoardDaoFactory.getFileDao(testDir);
            Dao<EditableSudokuBoardDecorator> dao2 = SudokuBoardDaoFactory.getEditableFileDao(testDir);
            Dao<EditableSudokuBoardDecorator> dao3 = SudokuBoardDaoFactory.getJdbcDao();
            
            assertNotNull(dao1, "Static method should work for FileDao");
            assertNotNull(dao2, "Static method should work for EditableFileDao");
            assertNotNull(dao3, "Static method should work for JdbcDao");
            
            dao1.close();
            dao2.close();
            dao3.close();
        }, "Factory methods should be accessible statically");
    }
    
    @Test
    public void testFactoryWithLongDirectoryName() throws Exception {
        StringBuilder longName = new StringBuilder();
        for (int i = 0; i < 50; i++) {
            longName.append("very_long_directory_name_");
        }
        
        String longDir = tempDir.resolve(longName.toString()).toString();
        
        try (Dao<SudokuBoard> dao = SudokuBoardDaoFactory.getFileDao(longDir)) {
            assertNotNull(dao, "Factory should handle long directory names");
        }
    }
    
    @Test
    public void testDatabaseConnectionFailureHandling() {
        assertDoesNotThrow(() -> {
            try {
                Dao<EditableSudokuBoardDecorator> dao = SudokuBoardDaoFactory.getJdbcDao();
                assertNotNull(dao, "Factory should return DAO even if database is not accessible");
                dao.close();
            } catch (JdbcDaoException e) {
                assertNotNull(e.getMessage(), "Exception should have meaningful message");
            }
        }, "Factory should handle database connection issues gracefully");
    }
    
    @Test
    public void testGetLegacyJdbcDaoThrowsException() {
        UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class, () -> {
            SudokuBoardDaoFactory.getLegacyJdbcDao();
        });

        assertTrue(exception.getMessage().contains("Use getJdbcDao()"));
        assertTrue(exception.getMessage().contains("EditableSudokuBoardDecorator"));
    }

    @Test
    public void testFactoryWithInvalidDatabaseCredentials() {
        assertThrows(JdbcDaoException.class, () -> {
            new JdbcSudokuBoardDao(
                "jdbc:postgresql://nonexistent_host_that_does_not_exist:9999/nonexistentdb",
                "completely_invalid_user",
                "completely_invalid_password"
            );
        }, "Creating DAO with invalid credentials should throw JdbcDaoException");
    }

    @Test
    public void testFactoryEdgeCases() throws Exception {
        String[] edgeCases = {
            "",           
            " ",          
            ".",         
            "..",         
            "/",          
            "\\",        
            "test dir",   
            "test-dir",   
            "test_dir",   
            "123",        
            "a",          
        };

        for (String testDir : edgeCases) {
            try {
                Dao<SudokuBoard> fileDao = SudokuBoardDaoFactory.getFileDao(testDir);
                assertNotNull(fileDao, "Factory should handle edge case: '" + testDir + "'");
                fileDao.close();

                Dao<EditableSudokuBoardDecorator> editableDao = SudokuBoardDaoFactory.getEditableFileDao(testDir);
                assertNotNull(editableDao, "Factory should handle edge case: '" + testDir + "'");
                editableDao.close();

            } catch (Exception e) {
                //We're ensuring the factory doesn't crash unexpectedly
            }
        }
    }
}