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

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.first.sudoku.dao.Dao;
import pl.first.sudoku.dao.DaoException;
import pl.first.sudoku.dao.SudokuBoardDaoFactory;
import pl.first.sudoku.sudokusolver.BacktrackingSudokuSolver;
import pl.first.sudoku.sudokusolver.SudokuBoard;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

public class SudokuBoardController implements Initializable {
    
    private static final Logger logger = LoggerFactory.getLogger(SudokuBoardController.class);

    @FXML
    private GridPane sudokuGrid;
    
    @FXML
    private Label titleLabel;
    
    @FXML
    private Button newGameButton;
    
    @FXML
    private Button checkSolutionButton;
    
    @FXML
    private Button saveGameButton;
    
    @FXML
    private Button loadGameButton;
    
    @FXML
    private Button languageButton;
    
    private SudokuBoard board;
    private TextField[][] fields;
    private static final String SAVE_DIRECTORY = "savedGames";
    private static final String DEFAULT_SAVE_NAME = "game.sudoku";
    private LanguageManager languageManager;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        languageManager = LanguageManager.getInstance();
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
                
                sudokuGrid.add(field, col, row);
                fields[row][col] = field;
            }
        }
        
        if (board == null) {
            board = new SudokuBoard(new BacktrackingSudokuSolver());
            board.solveGame();
        }
        
        languageManager.localeProperty().addListener((observable, oldValue, newValue) -> {
            updateTexts();
        });
        
        updateTexts();
        updateBoard();
        
        Platform.runLater(this::setStageTitle);
    }
    
    private void updateTexts() {
        ResourceBundle messages = languageManager.getMessagesBundle();

        titleLabel.setText(messages.getString("label.sudokuGame"));
        newGameButton.setText(messages.getString("button.newGame"));
        checkSolutionButton.setText(messages.getString("button.checkSolution"));
        saveGameButton.setText(messages.getString("button.saveGame"));
        loadGameButton.setText(messages.getString("button.loadGame"));
        languageButton.setText(messages.getString("button.changeLanguage"));
        
        Platform.runLater(this::setStageTitle);
    }
    
    public void updateForCurrentLocale() {
        updateTexts();
        Platform.runLater(this::setStageTitle);
    }
    
    public void setBoard(SudokuBoard board) {
        this.board = board;
        if (fields != null) {
            updateBoard();
        }
    }
    
    private void setStageTitle() {
        if (titleLabel.getScene() != null && titleLabel.getScene().getWindow() != null) {
            Stage stage = (Stage) titleLabel.getScene().getWindow();
            ResourceBundle messages = languageManager.getMessagesBundle();
            stage.setTitle(messages.getString("title.game"));
        }
    }
    
    @FXML
    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private void newGame() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pl/first/sudoku/view/MainMenuView.fxml"));
            loader.setResources(languageManager.getMessagesBundle());
            Parent root = loader.load();
            MainMenuController controller = loader.getController();
            controller.updateForCurrentLocale();
            Scene scene = new Scene(root);
            Stage stage = (Stage) sudokuGrid.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle(languageManager.getMessagesBundle().getString("title.mainMenu"));

            logger.info("Returned to main menu");
        } catch (IOException e) {
            logger.error("Error loading main menu view", e);

            showAlert(Alert.AlertType.ERROR, languageManager.getMessagesBundle().getString("alert.error"), 
                    languageManager.getMessagesBundle().getString("alert.error.content") + e.getMessage());
        }
    }
    
    @FXML
    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private void checkSolution() {
        ResourceBundle messages = languageManager.getMessagesBundle();
        
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
                showAlert(Alert.AlertType.INFORMATION, messages.getString("result.success"), 
                        messages.getString("result.congratulations"));
            } else {
                showAlert(Alert.AlertType.INFORMATION, 
                        messages.getString("result.validSoFar"), 
                        messages.getString("result.validButIncomplete"));
            }
        } else {
            showAlert(Alert.AlertType.WARNING, messages.getString("result.invalidSolution"), 
                    messages.getString("result.invalidSolution.content"));
        }
    }
    
    @FXML
    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private void saveGame() {
        ResourceBundle messages = languageManager.getMessagesBundle();

        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle(messages.getString("dialog.save.title"));
        dialog.setHeaderText(messages.getString("dialog.save.header"));

        ButtonType okButtonType = new ButtonType(messages.getString("button.ok"), ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType(messages.getString("button.cancel"), 
                ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, cancelButtonType);

        TextField textField = new TextField(DEFAULT_SAVE_NAME);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        grid.add(new Label(messages.getString("dialog.save.content")), 0, 0);
        grid.add(textField, 1, 0);

        dialog.getDialogPane().setContent(grid);

        Platform.runLater(textField::requestFocus);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                return textField.getText();
            }
            return null;
        });

        Optional<String> result = dialog.showAndWait();

        result.ifPresent(filename -> {
            if (!filename.endsWith(".sudoku")) {
                filename += ".sudoku";
            }

            logger.debug("Saving game to file: {}", filename);

            try (Dao<SudokuBoard> dao = SudokuBoardDaoFactory.getFileDao(SAVE_DIRECTORY)) {
                dao.write(filename, board);

                logger.info("Game saved successfully to {}", filename);

                showAlert(Alert.AlertType.INFORMATION, 
                        messages.getString("alert.saveSuccess"), 
                        messages.getString("alert.saveSuccess.content") + filename);
            } catch (Exception e) {
                logger.error("Failed to save game: {}", e.getMessage(), e);

                showAlert(Alert.AlertType.ERROR, 
                        messages.getString("alert.saveError"), 
                        messages.getString("alert.saveError.content") + e.getMessage());
            }
        });
    }
    
    @FXML
    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private void loadGame() {
        ResourceBundle messages = languageManager.getMessagesBundle();

        logger.debug("Attempting to load game");

        try (Dao<SudokuBoard> dao = SudokuBoardDaoFactory.getFileDao(SAVE_DIRECTORY)) {
            List<String> savedGames = dao.names();

            if (savedGames.isEmpty()) {
                logger.info("No saved games found");

                showAlert(Alert.AlertType.INFORMATION, 
                        messages.getString("dialog.noSavedGames"), 
                        messages.getString("dialog.noSavedGames.content"));
                return;
            }

            logger.debug("Found {} saved games", savedGames.size());

            Dialog<String> dialog = new Dialog<>();
            dialog.setTitle(messages.getString("dialog.load.title"));
            dialog.setHeaderText(messages.getString("dialog.load.header"));

            ButtonType okButtonType = new ButtonType(messages.getString("button.ok"), 
                    ButtonBar.ButtonData.OK_DONE);
            ButtonType cancelButtonType = new ButtonType(messages.getString("button.cancel"), 
                    ButtonBar.ButtonData.CANCEL_CLOSE);
            dialog.getDialogPane().getButtonTypes().addAll(okButtonType, cancelButtonType);

            ComboBox<String> comboBox = new ComboBox<>();
            comboBox.getItems().addAll(savedGames);
            comboBox.setValue(savedGames.get(0));

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));
            grid.add(new Label(messages.getString("dialog.load.content")), 0, 0);
            grid.add(comboBox, 1, 0);

            dialog.getDialogPane().setContent(grid);

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == okButtonType) {
                    return comboBox.getValue();
                }
                return null;
            });

            Optional<String> result = dialog.showAndWait();

            result.ifPresent(filename -> {
                try {
                    logger.debug("Loading game from file: {}", filename);

                    board = dao.read(filename);
                    updateBoard();

                    logger.info("Game loaded successfully from {}", filename);

                    showAlert(Alert.AlertType.INFORMATION, 
                            messages.getString("alert.loadSuccess"), 
                            messages.getString("alert.loadSuccess.content") + filename);
                } catch (DaoException e) {
                    logger.error("Failed to load game: {}", e.getMessage(), e);

                    showAlert(Alert.AlertType.ERROR, 
                            messages.getString("alert.loadError"), 
                            messages.getString("alert.loadError.content") + e.getMessage());
                }
            });
        } catch (Exception e) {
            logger.error("Unexpected error while loading game", e);

            showAlert(Alert.AlertType.ERROR, 
                    messages.getString("alert.error"), 
                    messages.getString("alert.error.content") + e.getMessage());
        }
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
        Platform.runLater(this::setStageTitle); 
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
                
                field.textProperty().addListener((observable, oldValue, newValue) -> {
                    if (field.isEditable()) {
                        try {
                            int val = newValue.isEmpty() ? 0 : Integer.parseInt(newValue);
                            if (val >= 0 && val <= 9) {
                                board.setValueAt(finalRow, finalCol, val);
                            }
                        } catch (NumberFormatException e) {
                            logger.debug("Invalid input format ignored: {}", e.getMessage());
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
