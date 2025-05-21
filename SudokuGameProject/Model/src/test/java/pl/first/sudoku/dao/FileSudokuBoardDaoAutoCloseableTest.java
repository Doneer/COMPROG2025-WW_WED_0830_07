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
import org.junit.jupiter.api.io.TempDir;
import pl.first.sudoku.sudokusolver.BacktrackingSudokuSolver;
import pl.first.sudoku.sudokusolver.SudokuBoard;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author zhuma
 */
public class FileSudokuBoardDaoAutoCloseableTest {
    
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
    
    @AfterEach
    public void tearDown() throws IOException {
        Files.walk(tempDir)
            .filter(Files::isRegularFile)
            .map(Path::toFile)
            .forEach(File::delete);
    }
    
    @Test
    public void testTryWithResourcesPattern() throws Exception {
        String filename = "testAutoClose.sudoku";
        
        try (Dao<SudokuBoard> writeDao = SudokuBoardDaoFactory.getFileDao(testDirPath)) {
            writeDao.write(filename, testBoard);
        }
        
        SudokuBoard loadedBoard;
        try (Dao<SudokuBoard> readDao = SudokuBoardDaoFactory.getFileDao(testDirPath)) {
            loadedBoard = readDao.read(filename);
        }
        
        assertEquals(testBoard, loadedBoard, "Board read with try-with-resources should equal original board");
    }
    
    @Test
    public void testAutoCloseableCorrectlyCloses() throws Exception {
        class TrackingFileSudokuBoardDao extends FileSudokuBoardDao {
            private boolean closed = false;
            
            public TrackingFileSudokuBoardDao(String directoryPath) {
                super(directoryPath);
            }
            
            @Override
            public void close() throws Exception {
                super.close();
                closed = true;
            }
            
            public boolean isClosed() {
                return closed;
            }
        }
        
        TrackingFileSudokuBoardDao dao = new TrackingFileSudokuBoardDao(testDirPath);
        
        try (dao) {
            dao.write("testClosing.sudoku", testBoard);
        }
        
        assertTrue(dao.isClosed(), "DAO should be closed after try-with-resources block");
    }
    
    @Test
    public void testExceptionHandlingInClose() {
        class ExceptionThrowingDao extends FileSudokuBoardDao {
            public ExceptionThrowingDao(String directoryPath) {
                super(directoryPath);
            }
            
            @Override
            public void close() throws Exception {
                super.close();
                throw new IOException("Test exception in close");
            }
        }
        
        ExceptionThrowingDao dao = new ExceptionThrowingDao(testDirPath);
        
        Exception exception = assertThrows(Exception.class, () -> {
            try (dao) {
                //Just acquire the resource
            }
        });
        
        assertTrue(exception.getMessage().contains("Test exception in close"), 
                "Exception from close should be propagated");
    }
    
    @Test
    public void testAutoClosingWithExceptionInOperation() {
        class FailingWriteDao extends FileSudokuBoardDao {
            private boolean closed = false;
            
            public FailingWriteDao(String directoryPath) {
                super(directoryPath);
            }
            
            @Override
            public void write(String name, SudokuBoard board) throws DaoException {
                throw new DaoException("Simulated write failure");
            }
            
            @Override
            public void close() throws Exception {
                super.close();
                closed = true;
            }
            
            public boolean isClosed() {
                return closed;
            }
        }
        
        FailingWriteDao dao = new FailingWriteDao(testDirPath);
        
        assertThrows(DaoException.class, () -> {
            try (dao) {
                dao.write("doesnt-matter.sudoku", testBoard);
            }
        });
        
        assertTrue(dao.isClosed(), "DAO should be closed even when operation throws exception");
    }
    
    @Test
    public void testMultipleOperationsInSingleTryWithResources() throws Exception {
        String filename1 = "board1.sudoku";
        String filename2 = "board2.sudoku";

        try (Dao<SudokuBoard> dao = SudokuBoardDaoFactory.getFileDao(testDirPath)) {
            dao.write(filename1, testBoard);

            SudokuBoard anotherBoard = new SudokuBoard(new BacktrackingSudokuSolver());
            anotherBoard.solveGame();
            dao.write(filename2, anotherBoard);

            List<String> savedBoards = dao.names();

            assertEquals(2, savedBoards.size(), "Should have saved 2 boards");
            assertTrue(savedBoards.contains(filename1), "First filename should be in the list");
            assertTrue(savedBoards.contains(filename2), "Second filename should be in the list");

            SudokuBoard loadedBoard1 = dao.read(filename1);
            SudokuBoard loadedBoard2 = dao.read(filename2);

            assertEquals(testBoard, loadedBoard1, "First loaded board should match first saved board");
            assertNotEquals(testBoard, loadedBoard2, "Second loaded board should not match first saved board");
        }

        System.gc(); 
        Thread.sleep(100); 
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

   private static class MockObjectInputStream {
       public void close() throws IOException {
           throw new IOException("Simulated error closing input stream");
       }
   }

   @Test
   public void testCloseWithOutputStreamError() throws Exception {
       FileSudokuBoardDao dao = new FileSudokuBoardDao(testDirPath);

       Field outputStreamField = FileSudokuBoardDao.class.getDeclaredField("outputStream");
       outputStreamField.setAccessible(true);
       outputStreamField.set(dao, new ThrowingObjectOutputStream());

       Field inputStreamField = FileSudokuBoardDao.class.getDeclaredField("inputStream");
       inputStreamField.setAccessible(true);
       inputStreamField.set(dao, null);

       Exception exception = assertThrows(Exception.class, dao::close);

       assertTrue(exception.getMessage().contains("Error closing output stream") || 
                  (exception.getCause() != null && 
                   exception.getCause().getMessage().contains("Simulated error")), 
               "Exception message should indicate output stream closing error");
   }

    @Test
    public void testCloseWithInputStreamError() throws Exception {
        class TestDao extends FileSudokuBoardDao {
            public TestDao(String directoryPath) {
                super(directoryPath);
            }

            @Override
            public void close() throws Exception {
                Field outputStreamField = FileSudokuBoardDao.class.getDeclaredField("outputStream");
                outputStreamField.setAccessible(true);
                outputStreamField.set(this, null);

                throw new Exception("Error closing input stream", new IOException("Simulated error"));
            }
        }

        TestDao dao = new TestDao(testDirPath);

        Exception exception = assertThrows(Exception.class, dao::close);

        assertTrue(exception.getMessage().contains("Error closing input stream"), 
                "Exception message should indicate input stream closing error");
    }
}
