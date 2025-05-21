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

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    
    private static final Logger logger = LoggerFactory.getLogger(MainMenuController.class);

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
        logger.debug("Initializing MainMenuController");

        languageManager = LanguageManager.getInstance();

        ObservableList<GameDifficulty> difficulties = FXCollections.observableArrayList(
                GameDifficulty.EASY,
                GameDifficulty.MEDIUM, 
                GameDifficulty.HARD);

        difficultyComboBox.setItems(difficulties);
        difficultyComboBox.getSelectionModel().selectFirst();
        
        difficultyComboBox.setCellFactory(param -> createDifficultyCell());
        difficultyComboBox.setButtonCell(createDifficultyCell());

        languageManager.localeProperty().addListener((observable, oldValue, newValue) -> {
            logger.debug("Language changed from {} to {}", oldValue, newValue);
            updateTexts();
            
            refreshComboBox(); 
            Platform.runLater(this::setStageTitle);
        });

        updateTexts();
        
        Platform.runLater(this::setStageTitle);

        logger.info("MainMenuController initialized successfully");
    }
    
    private ListCell<GameDifficulty> createDifficultyCell() {
        return new ListCell<GameDifficulty>() {
            @Override
            protected void updateItem(GameDifficulty item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(getDifficultyText(item));
                }
            }
        };
    }
    
    private String getDifficultyText(GameDifficulty difficulty) {
        if (difficulty == null) {
            return "";
        }

        ResourceBundle messages = languageManager.getMessagesBundle();
        switch (difficulty) {
            case EASY:
                return messages.getString("difficulty.easy");
            case MEDIUM:
                return messages.getString("difficulty.medium");
            case HARD:
                return messages.getString("difficulty.hard");
            default:
                return difficulty.toString();
        }
    }
    
    private void refreshComboBox() {
        final GameDifficulty currentSelection = difficultyComboBox.getValue();

        difficultyComboBox.setCellFactory(param -> new ListCell<GameDifficulty>() {
            @Override
            protected void updateItem(GameDifficulty item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(getDifficultyText(item));
                }
            }
        });

        difficultyComboBox.setButtonCell(new ListCell<GameDifficulty>() {
            @Override
            protected void updateItem(GameDifficulty item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(getDifficultyText(item));
                }
            }
        });

        Platform.runLater(() -> {
            if (currentSelection != null) {
                difficultyComboBox.setValue(currentSelection);
            }

            difficultyComboBox.show();
            difficultyComboBox.hide();
        });
    }
    
    private void setStageTitle() {
        if (titleLabel.getScene() != null && titleLabel.getScene().getWindow() != null) {
            Stage stage = (Stage) titleLabel.getScene().getWindow();
            ResourceBundle messages = languageManager.getMessagesBundle();
            stage.setTitle(messages.getString("title.mainMenu"));
        }
    }
    
    private void updateTexts() {
        ResourceBundle messages = languageManager.getMessagesBundle();

        titleLabel.setText(messages.getString("label.sudokuGame"));
        difficultyLabel.setText(messages.getString("label.selectDifficulty"));
        startGameButton.setText(messages.getString("button.startGame"));
        languageButton.setText(messages.getString("button.changeLanguage"));
        authorsButton.setText(messages.getString("button.showAuthors"));
        
        Platform.runLater(this::setStageTitle);
    }
    
    public void updateForCurrentLocale() {
        updateTexts();
        Platform.runLater(this::setStageTitle);
    }
    
    @FXML
    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private void startGame() {
        logger.debug("Starting new game");

        try {
            SudokuBoard solvedBoard = new SudokuBoard(new BacktrackingSudokuSolver());
            solvedBoard.solveGame();

            GameDifficulty selectedDifficulty = difficultyComboBox.getValue();

            logger.info("Creating new game with difficulty: {}", selectedDifficulty);

            SudokuBoard gameBoard = selectedDifficulty.prepareBoard(solvedBoard);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pl/first/sudoku/view/SudokuBoardView.fxml"));
            loader.setResources(languageManager.getMessagesBundle());
            Parent root = loader.load();

            SudokuBoardController controller = loader.getController();
            controller.setBoard(gameBoard);
            
            controller.updateForCurrentLocale();

            Scene scene = new Scene(root);
            Stage stage = (Stage) difficultyComboBox.getScene().getWindow();
            stage.setScene(scene);

            ResourceBundle messages = languageManager.getMessagesBundle();
            stage.setTitle(messages.getString("title.game") + " - " + selectedDifficulty);
            stage.show();

            logger.info("Game started successfully");
        } catch (IOException e) {
            logger.error("Error loading game board view", e);
        }
    }
    
    @FXML
    private Button authorsButton;

    @FXML
    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private void showAuthorsInfo() {
        ResourceBundle authors = languageManager.getAuthorsBundle();

        String title = authors.getString("authors.title");
        String names = authors.getString("authors.names");
        String university = authors.getString("authors.university");
        String email = authors.getString("authors.email");
        String year = authors.getString("authors.year");

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(names 
                + "\n" 
                + university 
                + "\n"
                + email 
                + "\n" 
                + year);
        alert.showAndWait();
    }
    
    @FXML
    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private void changeLanguage() {
        Locale currentLocale = languageManager.getLocale();

        if (currentLocale.equals(Locale.ENGLISH)) {
            languageManager.setLocale(new Locale("pl"));
        } else {
            languageManager.setLocale(Locale.ENGLISH);
        }

        updateTexts();
        refreshComboBox();
        Platform.runLater(this::setStageTitle);
    }
}
