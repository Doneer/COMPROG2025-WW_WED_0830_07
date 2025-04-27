/*
 * The MIT License
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

package pl.first.sudoku.view;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import pl.first.sudoku.dao.Dao;
import pl.first.sudoku.dao.DaoException;
import pl.first.sudoku.dao.SudokuBoardDaoFactory;
import pl.first.sudoku.sudokusolver.BacktrackingSudokuSolver;
import pl.first.sudoku.sudokusolver.SudokuBoard;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller class for the Sudoku board view in JavaFX.
 * Handles user interactions and updates the board display in the JavaFX interface.
 * @author zhuma
 */
public class SudokuBoardController implements Initializable {

    @FXML
    private GridPane sudokuGrid;
    
    private SudokuBoard board;
    private TextField[][] fields;
    private static final String SAVE_DIRECTORY = "savedGames";
    private static final String DEFAULT_SAVE_NAME = "game.sudoku";
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        board = new SudokuBoard(new BacktrackingSudokuSolver());
        fields = new TextField[9][9];
        
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                TextField field = new TextField();
                field.setPrefSize(40, 40);
                field.setFont(Font.font(16));
                field.setStyle("-fx-alignment: center; -fx-border-color: lightgray;");
                
                if (row % 3 == 0 || col % 3 == 0) {
                    field.setStyle(field.getStyle() + " -fx-border-width: 2 0 0 2;");
                }
                
                sudokuGrid.add(field, col, row);
                fields[row][col] = field;
            }
        }
        newGame();
    }
    
    @FXML
    private void newGame() {
        board = new SudokuBoard(new BacktrackingSudokuSolver());
        boolean solved = board.solveGame();
        if (solved) {
            updateBoard();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to generate a solved board");
        }
    }
    
    @FXML
    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private void saveGame() {
        try (Dao<SudokuBoard> dao = SudokuBoardDaoFactory.getFileDao(SAVE_DIRECTORY)) {
            dao.write(DEFAULT_SAVE_NAME, board);
            showAlert(Alert.AlertType.INFORMATION, "Save Successful", 
                    "Game saved successfully to " + DEFAULT_SAVE_NAME);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Save Error", 
                    "Failed to save game: " + e.getMessage());
        }
    }
    
    @FXML
    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private void loadGame() {
        try (Dao<SudokuBoard> dao = SudokuBoardDaoFactory.getFileDao(SAVE_DIRECTORY)) {
            board = dao.read(DEFAULT_SAVE_NAME);
            updateBoard();
            showAlert(Alert.AlertType.INFORMATION, "Load Successful", 
                    "Game loaded successfully from " + DEFAULT_SAVE_NAME);
        } catch (DaoException e) {
            showAlert(Alert.AlertType.ERROR, "Load Error", 
                    "Failed to load game: " + e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", 
                    "An unexpected error occurred: " + e.getMessage());
        }
    }
    
    private void updateBoard() {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                int value = board.getValueAt(row, col);
                fields[row][col].setText(Integer.toString(value));
                fields[row][col].setEditable(false);

                if ((row / 3 + col / 3) % 2 == 0) {
                    fields[row][col].setStyle(fields[row][col].getStyle() + " -fx-background-color: #f0f0f0;");
                }
            }
        }
    }
    
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
