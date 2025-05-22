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

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests error handling paths in FileSudokuBoardDao.
 * @author zhuma
 */
public class FileSudokuBoardDaoErrorTest {
    
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
    
    private static class ThrowingObjectOutputStream extends ObjectOutputStream {
        public ThrowingObjectOutputStream() throws IOException {
            super(System.out); 
        }
        
        @Override
        public void close() throws IOException {
            throw new IOException("Simulated error closing output stream");
        }
    }
    
    @Test
    public void testCloseWithOutputStreamError() throws Exception {
        FileSudokuBoardDao dao = new FileSudokuBoardDao(testDirPath);
        
        Field outputStreamField = FileSudokuBoardDao.class.getDeclaredField("outputStream");
        outputStreamField.setAccessible(true);
        
        ObjectOutputStream problematicStream = new ThrowingObjectOutputStream();
        outputStreamField.set(dao, problematicStream);
        
        Exception exception = assertThrows(Exception.class, dao::close);
        
        assertTrue(exception.getMessage().contains("Error closing output stream") || 
                   (exception.getCause() != null && 
                    exception.getCause().getMessage().contains("Simulated error")), 
                "Exception message should indicate output stream closing error");
        
        assertNull(outputStreamField.get(dao), "OutputStream field should be null after close()");
    }
    
    @Test
    public void testCreateDirectoryIfNotExists() throws Exception {
        Path nonExistentDir = tempDir.resolve("non_existent_dir");
        
        FileSudokuBoardDao dao = new FileSudokuBoardDao(nonExistentDir.toString());
        
        assertTrue(java.nio.file.Files.exists(nonExistentDir), 
                "Directory should be created by the constructor");
        
        dao.close();
    }
}
