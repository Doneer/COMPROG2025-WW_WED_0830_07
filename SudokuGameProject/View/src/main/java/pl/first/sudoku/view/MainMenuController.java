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

package pl.first.sudoku.view;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import pl.first.sudoku.sudokusolver.BacktrackingSudokuSolver;
import pl.first.sudoku.sudokusolver.GameDifficulty;
import pl.first.sudoku.sudokusolver.SudokuBoard;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the main menu view.
 * Handles difficulty selection and game start.
 * @author zhuma
 */
public class MainMenuController implements Initializable {

    @FXML
    private ComboBox<GameDifficulty> difficultyComboBox;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        difficultyComboBox.setItems(FXCollections.observableArrayList(GameDifficulty.values()));
        difficultyComboBox.getSelectionModel().selectFirst();
    }
    
    @FXML
    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private void startGame() {
        try {
            SudokuBoard solvedBoard = new SudokuBoard(new BacktrackingSudokuSolver());
            solvedBoard.solveGame();
            
            GameDifficulty selectedDifficulty = difficultyComboBox.getValue();
            SudokuBoard gameBoard = selectedDifficulty.prepareBoard(solvedBoard);
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pl/first/sudoku/view/SudokuBoardView.fxml"));
            Parent root = loader.load();
            
            SudokuBoardController controller = loader.getController();
            controller.setBoard(gameBoard);
            
            Scene scene = new Scene(root);
            Stage stage = (Stage) difficultyComboBox.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Sudoku Game - " + selectedDifficulty);
            stage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
