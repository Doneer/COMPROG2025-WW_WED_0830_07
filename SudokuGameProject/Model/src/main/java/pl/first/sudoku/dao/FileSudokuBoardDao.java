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

import pl.first.sudoku.sudokusolver.SudokuBoard;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of Dao interface for SudokuBoard objects that persists to the file system.
 * @author zhuma
 */
public class FileSudokuBoardDao implements Dao<SudokuBoard> {
    private final String directoryPath;
    private ObjectOutputStream outputStream = null;
    private ObjectInputStream inputStream = null;
    
    public FileSudokuBoardDao(String directoryPath) {
        this.directoryPath = directoryPath;
        createDirectoryIfNotExists();
    }
    
    private void createDirectoryIfNotExists() {
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }
    
    @Override
    public SudokuBoard read(String name) throws DaoException {
        Path filePath = Paths.get(directoryPath, name);
        try {
            inputStream = new ObjectInputStream(new FileInputStream(filePath.toFile()));
            SudokuBoard board = (SudokuBoard) inputStream.readObject();
            return board;
        } catch (IOException | ClassNotFoundException e) {
            throw new DaoException("Error reading SudokuBoard from file: " + name, e);
        }
    }
    
    @Override
    public void write(String name, SudokuBoard board) throws DaoException {
        Path filePath = Paths.get(directoryPath, name);
        try {
            outputStream = new ObjectOutputStream(new FileOutputStream(filePath.toFile()));
            outputStream.writeObject(board);
        } catch (IOException e) {
            throw new DaoException("Error writing SudokuBoard to file: " + name, e);
        }
    }
    
    @Override
    public List<String> names() throws DaoException {
        try {
            return Files.list(Paths.get(directoryPath))
                    .filter(Files::isRegularFile)
                    .map(path -> path.getFileName().toString())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new DaoException("Error listing files in directory: " + directoryPath, e);
        }
    }
    
    @Override
    public void close() throws Exception {
        if (outputStream != null) {
            outputStream.close();
        }
        if (inputStream != null) {
            inputStream.close();
        }
    }
}
