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
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TextInputDialog;
import pl.first.sudoku.dao.Dao;
import pl.first.sudoku.dao.DaoException;
import pl.first.sudoku.dao.SudokuBoardDaoFactory;
import pl.first.sudoku.sudokusolver.BacktrackingSudokuSolver;
import pl.first.sudoku.sudokusolver.SudokuBoard;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.List;
import java.util.Optional;

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
        SudokuFieldConverter converter = new SudokuFieldConverter();

        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                TextField field = new TextField();
                field.setPrefSize(40, 40);
                field.setFont(Font.font(16));
                field.setStyle("-fx-alignment: center; -fx-border-color: lightgray;");

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
                field.setTextFormatter(SudokuTextFormatter.createFormatter(converter));

                final int finalRow = row;
                final int finalCol = col;

                sudokuGrid.add(field, col, row);
                fields[row][col] = field;
            }
        }

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
    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private void newGame() {
        try {
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
    @SuppressWarnings("PMD.UnusedPrivateMethod")
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
                showAlert(Alert.AlertType.INFORMATION, 
                        "Valid so far", 
                        "Your solution is valid so far, but the board is not complete.");
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Invalid Solution", "There are errors in your solution.");
        }
    }
    
    @FXML
    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private void saveGame() {
        TextInputDialog dialog = new TextInputDialog(DEFAULT_SAVE_NAME);
        dialog.setTitle("Save Game");
        dialog.setHeaderText("Save your current game");
        dialog.setContentText("Enter a filename:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String filename = result.get();
            if (!filename.endsWith(".sudoku")) {
                filename += ".sudoku";
            }

            try (Dao<SudokuBoard> dao = SudokuBoardDaoFactory.getFileDao(SAVE_DIRECTORY)) {
                dao.write(filename, board);
                showAlert(Alert.AlertType.INFORMATION, "Save Successful", 
                        "Game saved successfully to " + filename);
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Save Error", 
                        "Failed to save game: " + e.getMessage());
            }
        }
    }
    
    @FXML
    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private void loadGame() {
        try (Dao<SudokuBoard> dao = SudokuBoardDaoFactory.getFileDao(SAVE_DIRECTORY)) {
            List<String> savedGames = dao.names();

            if (savedGames.isEmpty()) {
                showAlert(Alert.AlertType.INFORMATION, "No Saved Games", 
                        "There are no saved games to load.");
                return;
            }

            ChoiceDialog<String> dialog = new ChoiceDialog<>(savedGames.get(0), savedGames);
            dialog.setTitle("Load Game");
            dialog.setHeaderText("Load a saved game");
            dialog.setContentText("Choose a saved game:");

            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()) {
                String filename = result.get();
                board = dao.read(filename);
                updateBoard();
                showAlert(Alert.AlertType.INFORMATION, "Load Successful", 
                        "Game loaded successfully from " + filename);
            }
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

                final int finalRow = row;
                final int finalCol = col;

                field.textProperty().removeListener(changeListener -> {});

                field.textProperty().addListener((observable, oldValue, newValue) -> {
                    if (field.isEditable()) {
                        try {
                            int val = newValue.isEmpty() ? 0 : Integer.parseInt(newValue);
                            if (val >= 0 && val <= 9) {
                                board.setValueAt(finalRow, finalCol, val);
                            }
                        } catch (NumberFormatException e) {
                            //Invalid input, TextFormatter should prevent this
                        }
                    }
                });
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
