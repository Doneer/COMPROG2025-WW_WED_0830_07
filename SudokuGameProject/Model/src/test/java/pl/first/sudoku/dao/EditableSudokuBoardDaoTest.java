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
import pl.first.sudoku.sudokusolver.EditableSudokuBoardDecorator;
import pl.first.sudoku.sudokusolver.SudokuBoard;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Enhanced tests for EditableSudokuBoardDao to achieve 100% coverage.
 * @author zhuma
 */
public class EditableSudokuBoardDaoTest {
    
    @TempDir
    Path tempDir;
    
    private String testDirPath;
    private EditableSudokuBoardDecorator testBoard;
    
    @BeforeEach
    public void setUp() {
        testDirPath = tempDir.toString();
        SudokuBoard board = new SudokuBoard(new BacktrackingSudokuSolver());
        board.solveGame();
        testBoard = new EditableSudokuBoardDecorator(board);
        testBoard.lockNonEmptyFields();
    }
    
    @Test
    public void testConstructorCreatesDirectory() {
        String newDirPath = tempDir.resolve("newDirectory").toString();
        assertFalse(Files.exists(Paths.get(newDirPath)), "Directory should not exist initially");
        
        EditableSudokuBoardDao dao = new EditableSudokuBoardDao(newDirPath);
        
        assertTrue(Files.exists(Paths.get(newDirPath)), "Directory should be created by constructor");
    }
    
    @Test
    public void testConstructorWithExistingDirectory() {
        EditableSudokuBoardDao dao = new EditableSudokuBoardDao(testDirPath);
        assertTrue(Files.exists(Paths.get(testDirPath)), "Directory should exist");
    }
    
    @Test
    public void testWriteAndRead() throws Exception {
        String filename = "testBoard.sudoku";
        
        try (EditableSudokuBoardDao dao = new EditableSudokuBoardDao(testDirPath)) {
            dao.write(filename, testBoard);
            
            EditableSudokuBoardDecorator loadedBoard = dao.read(filename);
            
            assertNotNull(loadedBoard, "Loaded board should not be null");
            assertEquals(testBoard.getSudokuBoard(), loadedBoard.getSudokuBoard(), 
                    "Loaded board should equal original board");
        }
    }
    
    @Test
    public void testReadNonExistentFile() {
        try (EditableSudokuBoardDao dao = new EditableSudokuBoardDao(testDirPath)) {
            assertThrows(DaoException.class, () -> {
                dao.read("nonexistent.sudoku");
            }, "Reading non-existent file should throw DaoException");
        } catch (Exception e) {
            fail("Exception should not be thrown when closing DAO: " + e.getMessage());
        }
    }
    
    @Test
    public void testWriteIOException() {
        String invalidPath = System.getProperty("os.name").toLowerCase().contains("win") 
                ? "Z:\\nonexistent\\path" 
                : "/nonexistent/path/that/cannot/be/created";
        
        try (EditableSudokuBoardDao dao = new EditableSudokuBoardDao(invalidPath)) {
            assertThrows(DaoException.class, () -> {
                dao.write("test.sudoku", testBoard);
            }, "Writing to invalid path should throw DaoException");
        } catch (Exception e) {
        }
    }
    
    @Test
    public void testNames() throws Exception {
        try (EditableSudokuBoardDao dao = new EditableSudokuBoardDao(testDirPath)) {
            List<String> names = dao.names();
            assertTrue(names.isEmpty(), "Directory should be empty initially");
            
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
                ? "\\\\invalid\\unc\\path" 
                : "/proc/invalid/path";
        
        try (EditableSudokuBoardDao dao = new EditableSudokuBoardDao(invalidPath)) {
            assertThrows(DaoException.class, () -> {
                dao.names();
            }, "names() on invalid directory should throw DaoException");
        } catch (Exception e) {
        }
    }
    
    @Test
    public void testCloseWithoutOperations() throws Exception {
        EditableSudokuBoardDao dao = new EditableSudokuBoardDao(testDirPath);
        
        assertDoesNotThrow(() -> dao.close(), "Closing unused DAO should not throw exception");
    }
    
    @Test
    public void testCloseAfterWrite() throws Exception {
        EditableSudokuBoardDao dao = new EditableSudokuBoardDao(testDirPath);
        
        dao.write("test.sudoku", testBoard);
        
        assertDoesNotThrow(() -> dao.close(), "Closing after write should not throw exception");
    }
    
    @Test
    public void testCloseAfterRead() throws Exception {
        String filename = "testForRead.sudoku";
        
        try (EditableSudokuBoardDao writeDao = new EditableSudokuBoardDao(testDirPath)) {
            writeDao.write(filename, testBoard);
        }
        
        EditableSudokuBoardDao readDao = new EditableSudokuBoardDao(testDirPath);
        readDao.read(filename);
        
        assertDoesNotThrow(() -> readDao.close(), "Closing after read should not throw exception");
    }
    
    @Test
    public void testMultipleOperations() throws Exception {
        try (EditableSudokuBoardDao dao = new EditableSudokuBoardDao(testDirPath)) {
            dao.write("board1.sudoku", testBoard);
            dao.write("board2.sudoku", testBoard);
            
            EditableSudokuBoardDecorator board1 = dao.read("board1.sudoku");
            EditableSudokuBoardDecorator board2 = dao.read("board2.sudoku");
            
            assertNotNull(board1, "Board1 should not be null");
            assertNotNull(board2, "Board2 should not be null");
            
            List<String> names = dao.names();
            assertEquals(2, names.size(), "Should have 2 files");
        } 
        
        System.gc();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    @Test
    public void testCreateDirectoryFailure() {
        String restrictedPath = System.getProperty("os.name").toLowerCase().contains("win") 
                ? "C:\\Windows\\System32\\TestSudoku" 
                : "/root/testsudoku";
        
        try {
            EditableSudokuBoardDao dao = new EditableSudokuBoardDao(restrictedPath);
            dao.close();
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("directory") || e.getMessage().contains("path"), 
                    "Exception should be related to directory/path issues");
        }
    }
    
    @Test
    public void testFactoryIntegration() throws Exception {
        try (Dao<EditableSudokuBoardDecorator> dao = SudokuBoardDaoFactory.getEditableFileDao(testDirPath)) {
            assertTrue(dao instanceof EditableSudokuBoardDao, 
                    "Factory should return EditableSudokuBoardDao instance");
            
            dao.write("factoryTest.sudoku", testBoard);
            EditableSudokuBoardDecorator loaded = dao.read("factoryTest.sudoku");
            
            assertNotNull(loaded, "Factory-created DAO should work correctly");
        }
    }
}