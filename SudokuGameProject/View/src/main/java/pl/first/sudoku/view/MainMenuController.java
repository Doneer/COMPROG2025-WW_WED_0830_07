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
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import pl.first.sudoku.sudokusolver.BacktrackingSudokuSolver;
import pl.first.sudoku.sudokusolver.GameDifficulty;
import pl.first.sudoku.sudokusolver.SudokuBoard;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Controller for the main menu view.
 * Handles difficulty selection and game start.
 * @author zhuma
 */
public class MainMenuController implements Initializable {

    @FXML
    private ComboBox<GameDifficulty> difficultyComboBox;
    
    @FXML
    private Label titleLabel;
    
    @FXML
    private Label difficultyLabel;
    
    @FXML
    private Button startGameButton;
    
    @FXML
    private Button languageButton;
    
    private LanguageManager languageManager;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        languageManager = LanguageManager.getInstance();
        
        // Initialize difficulty options
        ObservableList<GameDifficulty> difficulties = FXCollections.observableArrayList(
                GameDifficulty.EASY,
                GameDifficulty.MEDIUM, 
                GameDifficulty.HARD);
                
        difficultyComboBox.setItems(difficulties);
        difficultyComboBox.getSelectionModel().selectFirst();
        
        // Set up language change listener
        languageManager.localeProperty().addListener((observable, oldValue, newValue) -> {
            updateTexts();
        });
        
        updateTexts();
    }
    
    private void updateTexts() {
        ResourceBundle messages = languageManager.getMessagesBundle();
        
        titleLabel.setText(messages.getString("label.sudokuGame"));
        difficultyLabel.setText(messages.getString("label.selectDifficulty"));
        startGameButton.setText(messages.getString("button.startGame"));
        languageButton.setText(messages.getString("button.changeLanguage"));
        
        // Update stage title
        Stage stage = (Stage) titleLabel.getScene().getWindow();
        if (stage != null) {
            stage.setTitle(messages.getString("title.mainMenu"));
        }
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
            loader.setResources(languageManager.getMessagesBundle());
            Parent root = loader.load();
            
            SudokuBoardController controller = loader.getController();
            controller.setBoard(gameBoard);
            
            Scene scene = new Scene(root);
            Stage stage = (Stage) difficultyComboBox.getScene().getWindow();
            stage.setScene(scene);
            
            ResourceBundle messages = languageManager.getMessagesBundle();
            stage.setTitle(messages.getString("title.game") + " - " + selectedDifficulty);
            stage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private void changeLanguage() {
        Locale currentLocale = languageManager.getLocale();
        
        // Toggle between English and Polish (or your native language)
        if (currentLocale.equals(Locale.ENGLISH)) {
            languageManager.setLocale(new Locale("pl"));
        } else {
            languageManager.setLocale(Locale.ENGLISH);
        }
    }
}
