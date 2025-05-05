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
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import pl.first.sudoku.dao.Dao;
import pl.first.sudoku.dao.DaoException;
import pl.first.sudoku.dao.SudokuBoardDaoFactory;
import pl.first.sudoku.sudokusolver.BacktrackingSudokuSolver;
import pl.first.sudoku.sudokusolver.SudokuBoard;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SudokuBoardController implements Initializable {

    @FXML
    private GridPane sudokuGrid;
    
    private SudokuBoard board;
    private TextField[][] fields;
    private static final String SAVE_DIRECTORY = "savedGames";
    private static final String DEFAULT_SAVE_NAME = "game.sudoku";
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        fields = new TextField[9][9];
        
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                TextField field = new TextField();
                field.setPrefSize(40, 40);
                field.setFont(Font.font(16));
                field.setStyle("-fx-alignment: center; -fx-border-color: lightgray;");
                
                // Apply thicker borders for 3x3 box separation
                StringBuilder style = new StringBuilder(field.getStyle());
                if (row % 3 == 0) {
                    style.append(" -fx-border-width: 2 0 0 0;");
                }
                if (col % 3 == 0) {
                    style.append(" -fx-border-width: 0 0 0 2;");
                }
                if (row % 3 == 0 && col % 3 == 0) {
                    style.append(" -fx-border-width: 2 0 0 2;");
                }
                
                field.setStyle(style.toString());
                
                final int finalRow = row;
                final int finalCol = col;
                field.textProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue.matches("[1-9]")) {
                        try {
                            int value = Integer.parseInt(newValue);
                            board.setValueAt(finalRow, finalCol, value);
                        } catch (NumberFormatException e) {
                            field.setText(oldValue);
                        }
                    } else if (newValue.isEmpty()) {
                        board.setValueAt(finalRow, finalCol, 0);
                    } else {
                        field.setText(oldValue);
                    }
                });
                
                sudokuGrid.add(field, col, row);
                fields[row][col] = field;
            }
        }
        
        // If no board is provided, create a default one
        if (board == null) {
            board = new SudokuBoard(new BacktrackingSudokuSolver());
            board.solveGame();
        }
        
        updateBoard();
    }
    
    public void setBoard(SudokuBoard board) {
        this.board = board;
        if (fields != null) {
            updateBoard();
        }
    }
    
    @FXML
    private void newGame() {
        try {
            // Go back to the main menu
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pl/first/sudoku/view/MainMenuView.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) sudokuGrid.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Sudoku Game - Main Menu");
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load main menu: " + e.getMessage());
        }
    }
    
    @FXML
    private void checkSolution() {
        if (board.isValid()) {
            boolean complete = true;
            for (int row = 0; row < 9; row++) {
                for (int col = 0; col < 9; col++) {
                    if (board.getValueAt(row, col) == 0) {
                        complete = false;
                        break;
                    }
                }
                if (!complete) {
                    break;
                }
            }
            
            if (complete) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Congratulations! You solved the puzzle correctly!");
            } else {
                showAlert(Alert.AlertType.INFORMATION, "Valid so far", "Your solution is valid so far, but the board is not complete.");
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Invalid Solution", "There are errors in your solution.");
        }
    }
    
    @FXML
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
                TextField field = fields[row][col];
                
                if (value == 0) {
                    field.setText("");
                    field.setEditable(true);
                    field.setStyle(field.getStyle() + " -fx-text-fill: blue;");
                } else {
                    field.setText(Integer.toString(value));
                    field.setEditable(false);
                    field.setStyle(field.getStyle() + " -fx-text-fill: black; -fx-background-color: #f0f0f0;");
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
