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
import org.junit.jupiter.api.io.TempDir;
import pl.first.sudoku.sudokusolver.BacktrackingSudokuSolver;
import pl.first.sudoku.sudokusolver.SudokuBoard;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Enhanced tests for FileSudokuBoardDao to achieve 100% coverage.
 * @author zhuma
 */
public class FileSudokuBoardDaoEnhancedTest {
    
    @TempDir
    Path tempDir;
    
    private String testDirPath;
    private SudokuBoard testBoard;
    
    @BeforeEach
    public void setUp() {
        testDirPath = tempDir.toString();
        testBoard = new SudokuBoard(new BacktrackingSudokuSolver());
        testBoard.solveGame();
    }
    
    @Test
    public void testCreateDirectoryIfNotExistsSuccess() {
        String newDirPath = tempDir.resolve("newTestDirectory").toString();
        assertFalse(Files.exists(Paths.get(newDirPath)), "Directory should not exist initially");
        
        FileSudokuBoardDao dao = new FileSudokuBoardDao(newDirPath);
        
        assertTrue(Files.exists(Paths.get(newDirPath)), "Directory should be created");
        assertTrue(Files.isDirectory(Paths.get(newDirPath)), "Created path should be a directory");
        
        try {
            dao.close();
        } catch (Exception e) {
            fail("Should not throw exception when closing: " + e.getMessage());
        }
    }
    
    @Test
    public void testCreateDirectoryIfNotExistsFailure() {
        String restrictedPath;
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            restrictedPath = "C:\\Windows\\System32\\TestDirectory";
        } else {
            restrictedPath = "/root/testdirectory";
        }
        
        assertDoesNotThrow(() -> {
            FileSudokuBoardDao dao = new FileSudokuBoardDao(restrictedPath);
            dao.close();
        }, "Constructor should handle directory creation failure gracefully");
    }
    
    @Test
    public void testConstructorWithExistingDirectory() {
        FileSudokuBoardDao dao = new FileSudokuBoardDao(testDirPath);
        assertTrue(Files.exists(Paths.get(testDirPath)), "Directory should exist");
        
        try {
            dao.close();
        } catch (Exception e) {
            fail("Should not throw exception when closing: " + e.getMessage());
        }
    }
    
    @Test
    public void testWriteAndReadSuccess() throws Exception {
        String filename = "testBoard.sudoku";
        
        try (FileSudokuBoardDao dao = new FileSudokuBoardDao(testDirPath)) {
            dao.write(filename, testBoard);
            
            SudokuBoard loadedBoard = dao.read(filename);
            
            assertNotNull(loadedBoard, "Loaded board should not be null");
            assertEquals(testBoard, loadedBoard, "Loaded board should equal original");
        }
    }
    
    @Test
    public void testReadNonExistentFile() {
        try (FileSudokuBoardDao dao = new FileSudokuBoardDao(testDirPath)) {
            DaoException exception = assertThrows(DaoException.class, () -> {
                dao.read("nonexistent.sudoku");
            });
            
            assertNotNull(exception.getCause(), "Exception should have a cause");
            assertNotNull(exception.getMessage(), "Exception should have a message");
            assertFalse(exception.getMessage().isEmpty(), "Message should not be empty");
        } catch (Exception e) {
            fail("Unexpected exception when closing DAO: " + e.getMessage());
        }
    }
    
    @Test
    public void testWriteToInvalidPath() {
        String invalidPath = System.getProperty("os.name").toLowerCase().contains("win") 
                ? "Z:\\invalid\\path" 
                : "/invalid/path/that/does/not/exist";
        
        try (FileSudokuBoardDao dao = new FileSudokuBoardDao(invalidPath)) {
            DaoException exception = assertThrows(DaoException.class, () -> {
                dao.write("test.sudoku", testBoard);
            });
            
            assertNotNull(exception.getCause(), "Exception should have a cause");
            assertNotNull(exception.getMessage(), "Exception should have a message");
            assertFalse(exception.getMessage().isEmpty(), "Message should not be empty");
        } catch (Exception e) {
        }
    }
    
    @Test
    public void testNamesSuccess() throws Exception {
        try (FileSudokuBoardDao dao = new FileSudokuBoardDao(testDirPath)) {
            List<String> names = dao.names();
            assertTrue(names.isEmpty() || names.size() == 0, "Directory should be empty initially");
            
            dao.write("board1.sudoku", testBoard);
            dao.write("board2.sudoku", testBoard);
            
            names = dao.names();
            assertEquals(2, names.size(), "Should have 2 files");
            assertTrue(names.contains("board1.sudoku"), "Should contain board1.sudoku");
            assertTrue(names.contains("board2.sudoku"), "Should contain board2.sudoku");
        } 
        
        System.gc();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    @Test
    public void testNamesIOException() {
        String invalidPath = System.getProperty("os.name").toLowerCase().contains("win") 
                ? "\\\\invalid\\unc\\path\\that\\does\\not\\exist" 
                : "/proc/sys/invalid/path";
        
        try (FileSudokuBoardDao dao = new FileSudokuBoardDao(invalidPath)) {
            DaoException exception = assertThrows(DaoException.class, () -> {
                dao.names();
            });
            
            assertNotNull(exception.getCause(), "Exception should have a cause");
            assertNotNull(exception.getMessage(), "Exception should have a message");
            assertFalse(exception.getMessage().isEmpty(), "Message should not be empty");
        } catch (Exception e) {
        }
    }
    
    @Test
    public void testCloseWithoutStreams() throws Exception {
        FileSudokuBoardDao dao = new FileSudokuBoardDao(testDirPath);
        
        assertDoesNotThrow(() -> dao.close(), "Closing without operations should not throw");
    }
    
    @Test
    public void testCloseAfterWrite() throws Exception {
        FileSudokuBoardDao dao = new FileSudokuBoardDao(testDirPath);
        
        dao.write("test.sudoku", testBoard);
        
        assertDoesNotThrow(() -> dao.close(), "Closing after write should not throw");
    }
    
    @Test
    public void testCloseAfterRead() throws Exception {
        String filename = "testRead.sudoku";
        
        try (FileSudokuBoardDao writeDao = new FileSudokuBoardDao(testDirPath)) {
            writeDao.write(filename, testBoard);
        }
        
        FileSudokuBoardDao readDao = new FileSudokuBoardDao(testDirPath);
        readDao.read(filename);
        
        assertDoesNotThrow(() -> readDao.close(), "Closing after read should not throw");
    }
    
    @Test
    public void testMultipleWrites() throws Exception {
        try (FileSudokuBoardDao dao = new FileSudokuBoardDao(testDirPath)) {
            dao.write("board1.sudoku", testBoard);
            dao.write("board2.sudoku", testBoard);
            dao.write("board3.sudoku", testBoard);
            
            List<String> names = dao.names();
            assertEquals(3, names.size(), "Should have 3 files");
        } 
        
        System.gc();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    @Test
    public void testMultipleReads() throws Exception {
        String filename = "multiRead.sudoku";
        
        try (FileSudokuBoardDao dao = new FileSudokuBoardDao(testDirPath)) {
            dao.write(filename, testBoard);
        } 
        
        System.gc();
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        try (FileSudokuBoardDao dao = new FileSudokuBoardDao(testDirPath)) {
            SudokuBoard board1 = dao.read(filename);
            SudokuBoard board2 = dao.read(filename);
            SudokuBoard board3 = dao.read(filename);
            
            assertNotNull(board1, "First read should succeed");
            assertNotNull(board2, "Second read should succeed");
            assertNotNull(board3, "Third read should succeed");
            
            assertEquals(board1, board2, "Multiple reads should return equal boards");
            assertEquals(board2, board3, "Multiple reads should return equal boards");
        } 
        
        System.gc();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    @Test
    public void testFactoryCreation() throws Exception {
        try (Dao<SudokuBoard> dao = SudokuBoardDaoFactory.getFileDao(testDirPath)) {
            assertTrue(dao instanceof FileSudokuBoardDao, 
                    "Factory should return FileSudokuBoardDao instance");
            
            dao.write("factory.sudoku", testBoard);
            SudokuBoard loaded = dao.read("factory.sudoku");
            
            assertEquals(testBoard, loaded, "Factory-created DAO should work correctly");
        }
    }
    
    @Test
    public void testEmptyDirectory() throws Exception {
        try (FileSudokuBoardDao dao = new FileSudokuBoardDao(testDirPath)) {
            List<String> names = dao.names();
            assertTrue(names.isEmpty(), "Empty directory should return empty list");
        }
    }
}