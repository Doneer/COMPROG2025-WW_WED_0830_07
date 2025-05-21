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

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SudokuGameApp extends Application {
    private static final Logger logger = LoggerFactory.getLogger(SudokuGameApp.class);
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        logger.info("Starting Sudoku Game application");

        LanguageManager languageManager = LanguageManager.getInstance();

        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/pl/first/sudoku/view/MainMenuView.fxml"));

        loader.setResources(languageManager.getMessagesBundle());

        logger.debug("Loading main menu view");

        Parent root = loader.load();
        Scene scene = new Scene(root);

        primaryStage.setTitle(languageManager.getMessagesBundle().getString("title.mainMenu"));
        primaryStage.setScene(scene);
        primaryStage.show();

        logger.info("Application initialized and displayed");
    }
    
    public static void main(String[] args) {
        logger.info("Sudoku Game application launched");

        launch(args);
    }
}

