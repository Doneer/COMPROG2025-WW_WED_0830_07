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
import javafx.beans.binding.Bindings;
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
import javafx.util.StringConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.first.sudoku.dao.Dao;
import pl.first.sudoku.dao.DaoException;
import pl.first.sudoku.dao.SudokuBoardDaoFactory;
import pl.first.sudoku.sudokusolver.BacktrackingSudokuSolver;
import pl.first.sudoku.sudokusolver.EditableSudokuBoardDecorator;
import pl.first.sudoku.sudokusolver.SudokuBoard;
import pl.first.sudoku.sudokusolver.SudokuField;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controller for the sudoku board view with JavaFX Property binding support.
 * @author zhuma
 */
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
    private EditableSudokuBoardDecorator decoratedBoard;
    private TextField[][] fields;
    private static final String SAVE_DIRECTORY = "savedGames";
    private static final String DEFAULT_SAVE_NAME = "game.sudoku";
    private LanguageManager languageManager;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        languageManager = LanguageManager.getInstance();
        fields = new TextField[9][9];
        
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
                field.setTextFormatter(SudokuTextFormatter.createFormatter(new SudokuFieldConverter()));
                
                sudokuGrid.add(field, col, row);
                fields[row][col] = field;
            }
        }
        
        if (board == null) {
            board = new SudokuBoard(new BacktrackingSudokuSolver());
            board.solveGame();
            decoratedBoard = new EditableSudokuBoardDecorator(board);
        }
        
        languageManager.localeProperty().addListener((observable, oldValue, newValue) -> {
            updateTexts();
        });
        
        updateTexts();
        updateBoard();
        
        Platform.runLater(this::setStageTitle);
    }
    
    private static class SudokuFieldStringConverter extends StringConverter<Number> {
        
        @Override
        public String toString(Number value) {
            if (value == null || value.intValue() == 0) {
                return "";
            }
            return value.toString();
        }
        
        @Override
        public Number fromString(String string) {
            if (string == null || string.trim().isEmpty()) {
                return 0;
            }
            try {
                int value = Integer.parseInt(string.trim());
                if (value >= 1 && value <= 9) {
                    return value;
                } else {
                    return 0;
                }
            } catch (NumberFormatException e) {
                return 0;
            }
        }
    }
    
    private void setupFieldBinding(TextField field, int row, int col) {
        if (board == null) {
            logger.warn("Cannot setup binding: board is null");
            return;
        }
        
        SudokuField sudokuField = board.getSudokuField(row, col);
        
        Bindings.bindBidirectional(
            field.textProperty(), 
            sudokuField.valueProperty(), 
            new SudokuFieldStringConverter()
        );
        
        logger.debug("Set up bidirectional binding for field at [{},{}]", row, col);
    }
    
    private void removeFieldBinding(TextField field, int row, int col) {
        if (board == null) {
            return;
        }
        
        SudokuField sudokuField = board.getSudokuField(row, col);
        
        Bindings.unbindBidirectional(field.textProperty(), sudokuField.valueProperty());
        
        logger.debug("Removed binding for field at [{},{}]", row, col);
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
        this.decoratedBoard = new EditableSudokuBoardDecorator(board);
        this.decoratedBoard.lockNonEmptyFields();
        if (fields != null) {
            updateBoard();
        }
    }
    
    public void setDecoratedBoard(EditableSudokuBoardDecorator decoratedBoard) {
        this.decoratedBoard = decoratedBoard;
        this.board = decoratedBoard.getSudokuBoard();
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
        logger.debug("Starting new game");

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pl/first/sudoku/view/MainMenuView.fxml"));
            loader.setResources(languageManager.getMessagesBundle());
            Parent root = loader.load();

            MainMenuController controller = loader.getController();
            controller.updateForCurrentLocale();

            Scene scene = new Scene(root);
            Stage stage = (Stage) sudokuGrid.getScene().getWindow();
            stage.setScene(scene);

            ResourceBundle messages = languageManager.getMessagesBundle();
            stage.setTitle(messages.getString("title.mainMenu"));
            stage.show();

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

            try (Dao<EditableSudokuBoardDecorator> dao = SudokuBoardDaoFactory.getEditableFileDao(SAVE_DIRECTORY)) {
                dao.write(filename, decoratedBoard);

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

        try (Dao<EditableSudokuBoardDecorator> dao = SudokuBoardDaoFactory.getEditableFileDao(SAVE_DIRECTORY)) {
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

                    decoratedBoard = dao.read(filename);
                    
                    board = decoratedBoard.getSudokuBoard();
                    
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
        if (decoratedBoard == null && board != null) {
            decoratedBoard = new EditableSudokuBoardDecorator(board);
            decoratedBoard.lockNonEmptyFields();
        }
        
        if (decoratedBoard == null) {
            logger.warn("Both board and decoratedBoard are null in updateBoard");
            return;
        }
        
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                removeFieldBinding(fields[row][col], row, col);
            }
        }
        
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                TextField field = fields[row][col];
                boolean isEditable = decoratedBoard.isFieldEditable(row, col);
                
                setupFieldBinding(field, row, col);
                
                field.setEditable(isEditable);
                if (isEditable) {
                    field.setStyle(field.getStyle() + " -fx-text-fill: blue;");
                } else {
                    field.setStyle(field.getStyle() + " -fx-text-fill: black; -fx-background-color: #f0f0f0;");
                }
            }
        }
        
        logger.debug("Board updated with JavaFX Property bindings");
    }
    
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}