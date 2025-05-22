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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.first.sudoku.sudokusolver.EditableSudokuBoardDecorator;

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
 * Implementation of Dao interface for EditableSudokuBoardDecorator objects.
 * Provides methods to save and load decorated Sudoku boards to/from the file system.
 * @author zhuma
 */
public class EditableSudokuBoardDao implements Dao<EditableSudokuBoardDecorator> {
    private static final Logger logger = LoggerFactory.getLogger(EditableSudokuBoardDao.class);
    
    private final String directoryPath;
    private ObjectOutputStream outputStream = null;
    private ObjectInputStream inputStream = null;
    
    public EditableSudokuBoardDao(String directoryPath) {
        this.directoryPath = directoryPath;
        createDirectoryIfNotExists();
    }
    
    private void createDirectoryIfNotExists() {
        Path directory = Paths.get(directoryPath);
        if (!Files.exists(directory)) {
            try {
                Files.createDirectories(directory);
                logger.info("Created directory: {}", directoryPath);
            } catch (IOException e) {
                logger.warn("Failed to create directory: {}", directoryPath, e);
            }
        }
    }
    
    @Override
    public EditableSudokuBoardDecorator read(String name) throws DaoException {
        Path filePath = Paths.get(directoryPath, name);
        logger.debug("Reading EditableSudokuBoardDecorator from file: {}", filePath);
        
        try {
            inputStream = new ObjectInputStream(new FileInputStream(filePath.toFile()));
            EditableSudokuBoardDecorator board = (EditableSudokuBoardDecorator) inputStream.readObject();
            logger.info("Successfully read EditableSudokuBoardDecorator from file: {}", name);
            return board;
        } catch (IOException | ClassNotFoundException e) {
            logger.error("Error reading EditableSudokuBoardDecorator from file: {}", name, e);
            throw DaoException.createReadException(name, e);
        }
    }
    
    @Override
    public void write(String name, EditableSudokuBoardDecorator board) throws DaoException {
        Path filePath = Paths.get(directoryPath, name);
        logger.debug("Writing EditableSudokuBoardDecorator to file: {}", filePath);
        
        try {
            outputStream = new ObjectOutputStream(new FileOutputStream(filePath.toFile()));
            outputStream.writeObject(board);
            outputStream.flush();
            logger.info("Successfully wrote EditableSudokuBoardDecorator to file: {}", name);
        } catch (IOException e) {
            logger.error("Error writing EditableSudokuBoardDecorator to file: {}", name, e);
            throw DaoException.createWriteException(name, e);
        }
    }
    
    @Override
    public List<String> names() throws DaoException {
        logger.debug("Listing files in directory: {}", directoryPath);
        
        try {
            List<String> fileNames = Files.list(Paths.get(directoryPath))
                    .filter(Files::isRegularFile)
                    .map(path -> path.getFileName().toString())
                    .collect(Collectors.toList());
            
            logger.info("Found {} files in directory: {}", fileNames.size(), directoryPath);
            return fileNames;
        } catch (IOException e) {
            logger.error("Error listing files in directory: {}", directoryPath, e);
            throw DaoException.createNamesException(directoryPath, e);
        }
    }
    
    @Override
    public void close() throws Exception {
        logger.debug("Closing DAO resources");
        
        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (IOException e) {
                logger.error("Error closing output stream", e);
                throw new Exception("Error closing output stream", e);
            } finally {
                outputStream = null;
            }
        }
        
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                logger.error("Error closing input stream", e);
                throw new Exception("Error closing input stream", e);
            } finally {
                inputStream = null;
            }
        }
        
        logger.debug("DAO resources closed successfully");
    }
}
