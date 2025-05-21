/*
 * The MIT License
 *
 * Copyright 2025 Daniyar Zhumatayev, Kuzma Martysiuk
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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.first.sudoku.sudokusolver.BacktrackingSudokuSolver;
import pl.first.sudoku.sudokusolver.SudokuBoard;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author zhuma
 */
public class FileSudokuBoardDaoTest {
    private final String TEST_DIR = "testSudoku";
    private final String TEST_FILE = "testBoard.sudoku";
    
    @BeforeEach
    public void setup() {
        File directory = new File(TEST_DIR);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }
    
    @AfterEach
    public void cleanup() throws Exception {
        Files.walk(Paths.get(TEST_DIR))
            .filter(Files::isRegularFile)
            .map(Path::toFile)
            .forEach(File::delete);
            
        File directory = new File(TEST_DIR);
        directory.delete();
    }
    
    @Test
    public void testWriteAndReadBoard() throws Exception {
        SudokuBoard originalBoard = new SudokuBoard(new BacktrackingSudokuSolver());
        originalBoard.solveGame();
        
        try (Dao<SudokuBoard> dao = SudokuBoardDaoFactory.getFileDao(TEST_DIR)) {
            dao.write(TEST_FILE, originalBoard);
        }
        
        SudokuBoard loadedBoard;
        try (Dao<SudokuBoard> dao = SudokuBoardDaoFactory.getFileDao(TEST_DIR)) {
            loadedBoard = dao.read(TEST_FILE);
        }
        
        assertEquals(originalBoard, loadedBoard, "Read board should equal the original board");
    }
    
    @Test
    public void testNames() throws Exception {
        SudokuBoard board = new SudokuBoard(new BacktrackingSudokuSolver());
        board.solveGame();
        
        try (Dao<SudokuBoard> dao = SudokuBoardDaoFactory.getFileDao(TEST_DIR)) {
            dao.write("board1.sudoku", board);
            dao.write("board2.sudoku", board);
            
            List<String> names = dao.names();
            
            assertEquals(2, names.size(), "Should have 2 files");
            assertTrue(names.contains("board1.sudoku"), "Should contain board1.sudoku");
            assertTrue(names.contains("board2.sudoku"), "Should contain board2.sudoku");
        }
    }
    
    @Test
    public void testReadNonExistentFile() {
        try (Dao<SudokuBoard> dao = SudokuBoardDaoFactory.getFileDao(TEST_DIR)) {
            assertThrows(DaoException.class, () -> {
                dao.read("nonexistent.sudoku");
            }, "Reading a non-existent file should throw DaoException");
        } catch (Exception e) {
            fail("Exception should not be thrown when closing DAO: " + e.getMessage());
        }
    }
    
    @Test
    public void testNamesIOException() {
        String invalidPath = "\\\\invalid\\path";
        try (Dao<SudokuBoard> dao = SudokuBoardDaoFactory.getFileDao(invalidPath)) {
            assertThrows(DaoException.class, () -> {
                dao.names();
            }, "Should throw DaoException when directory is invalid");
        } catch (Exception e) {
            fail("Exception should not be thrown when closing DAO: " + e.getMessage());
        }
    }
    
    @Test
    public void testWriteIOException() {
        SudokuBoard board = new SudokuBoard(new BacktrackingSudokuSolver());
        board.solveGame();
        
        String inaccessiblePath = "Z:\\nonexistent\\directory";

        try (Dao<SudokuBoard> dao = SudokuBoardDaoFactory.getFileDao(inaccessiblePath)) {
            assertThrows(DaoException.class, () -> {
                dao.write("test.sudoku", board);
            }, "Should throw DaoException when writing to inaccessible path");
        } catch (Exception e) {
            fail("Exception should not be thrown when closing DAO: " + e.getMessage());
        }
    }
    
    @Test
    public void testCreateExistingDirectory() {
        String existingDir = "existingDir";
        File directory = new File(existingDir);
        directory.mkdirs();

        try (Dao<SudokuBoard> dao = SudokuBoardDaoFactory.getFileDao(existingDir)) {
            assertTrue(directory.exists(), "Directory should still exist");
        } catch (Exception e) {
            fail("Exception should not be thrown: " + e.getMessage());
        } finally {
            directory.delete();
        }
    }
    
    @Test
    public void testSudokuBoardDaoFactory() {
        Dao<SudokuBoard> dao = SudokuBoardDaoFactory.getFileDao("testDir");
        assertNotNull(dao, "Factory should return a non-null DAO");
        assertTrue(dao instanceof FileSudokuBoardDao, "Factory should return a FileSudokuBoardDao instance");

        try {
            dao.close();
        } catch (Exception e) {
            fail("Exception should not be thrown when closing DAO: " + e.getMessage());
        }
    }
    
    @Test
    public void testDaoExceptionSimpleConstructor() {
        String errorMessage = "Test error message";
        DaoException exception = new DaoException(errorMessage);

        assertEquals(errorMessage, exception.getMessage(), 
                "Exception message should match the provided message");
        assertNull(exception.getCause(), "Cause should be null");
    }
    
    @Test
    public void testSudokuBoardDaoFactoryCreatesCorrectInstance() {
        String testDir = "factoryTestDir";
        Dao<SudokuBoard> dao = SudokuBoardDaoFactory.getFileDao(testDir);

        assertNotNull(dao, "Factory should return a non-null DAO");
        assertTrue(dao instanceof FileSudokuBoardDao, 
                "Factory should return a FileSudokuBoardDao instance");

        try {
            SudokuBoard board = new SudokuBoard(new BacktrackingSudokuSolver());
            board.solveGame();

            dao.write("factoryTest.sudoku", board);

            File directory = new File(testDir);
            assertTrue(directory.exists(), "Directory should exist");
            assertTrue(directory.isDirectory(), "Path should be a directory");

            dao.close();
            new File(testDir, "factoryTest.sudoku").delete();
            directory.delete();
        } catch (Exception e) {
            fail("Should not throw exception: " + e.getMessage());
        }
    }
    
    @Test
    public void testSudokuBoardDaoFactoryMethod() {
        assertThrows(NullPointerException.class, () -> {
            SudokuBoardDaoFactory.getFileDao(null);
        });

        Dao<SudokuBoard> daoWithEmptyDir = SudokuBoardDaoFactory.getFileDao("");
        assertNotNull(daoWithEmptyDir);

        String testDir = "factoryTestDir";
        Dao<SudokuBoard> dao = SudokuBoardDaoFactory.getFileDao(testDir);
        assertNotNull(dao);
        assertTrue(dao instanceof FileSudokuBoardDao);

        try {
            daoWithEmptyDir.close();
            dao.close();
        } catch (Exception e) {
            fail("Exception closing DAOs: " + e.getMessage());
        }
    }
    
    @Test
    public void testFactoryInstantiation() {
        SudokuBoardDaoFactory factory = new SudokuBoardDaoFactory();
        assertNotNull(factory, "Factory instance should not be null");
    }
    
    @Test
    public void testDaoExceptionConstructors() {
        Exception cause = new RuntimeException("Original cause");

        DaoException ex1 = new DaoException("Test message");
        DaoException ex2 = new DaoException("Test with cause", cause);
        DaoException ex3 = DaoException.createReadException("details", cause);
        DaoException ex4 = DaoException.createWriteException("details", cause);
        DaoException ex5 = DaoException.createNamesException("details", cause);

        assertEquals("Test message", ex1.getMessage());
        assertNull(ex1.getCause());

        assertEquals("Test with cause", ex2.getMessage());
        assertEquals(cause, ex2.getCause());

        assertNotNull(ex3.getMessage());
        assertEquals(cause, ex3.getCause());

        assertNotNull(ex4.getMessage());
        assertEquals(cause, ex4.getCause());

        assertNotNull(ex5.getMessage());
        assertEquals(cause, ex5.getCause());
    }
}
