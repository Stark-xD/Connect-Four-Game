package com.project.connectfour;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class connectFour extends Application {

        private Controller controller;

        public static void main(String[] args) {
                launch(args);
        }

        @Override
        public void start(Stage primaryStage) throws Exception {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("game.fxml"));
                GridPane rootGridPane = loader.load();

                controller = loader.getController();
                controller.createPlayground();

                MenuBar menuBar = createMenu();
                menuBar.prefWidthProperty().bind(primaryStage.widthProperty()); // menu bar width = primaryStage width

                Pane menuPane = (Pane) rootGridPane.getChildren().get(0); // get the first child of GridPane which is nothing but a pane in a fxml file
                menuPane.getChildren().add(menuBar); // add the menu bar to the pane

                Scene scene = new Scene(rootGridPane);
                primaryStage.setScene(scene);
                primaryStage.setResizable(false);
                primaryStage.show();
        }

        public MenuBar createMenu() {
                /* File Menu */
                Menu fileMenu = new Menu("File");

                // New Game
                MenuItem newGame = new Menu("New Game");
                newGame.setOnAction(actionEvent -> controller.resetGame()); // set action event on New Game

                // Reset Game
                MenuItem resetGame = new Menu("Reset Game");
                resetGame.setOnAction(actionEvent -> controller.resetGame());

                SeparatorMenuItem separator = new SeparatorMenuItem();

                // Exit Game
                MenuItem exitGame = new Menu("Exit Game");
                exitGame.setOnAction(actionEvent -> exitGame());

                // add all menu items to File Menu
                fileMenu.getItems().addAll(newGame, resetGame, separator, exitGame);

                /* Help Menu */
                Menu helpMenu = new Menu("Help");

                // About Connect Four
                MenuItem abtGame = new Menu("About Connect4");
                abtGame.setOnAction(actionEvent -> about_game());

                // About Me
                MenuItem abtMe = new Menu("About Me");
                abtMe.setOnAction(actionEvent -> about_me());

                // add all menu items to the Help Menu
                helpMenu.getItems().addAll(abtGame, abtMe);

                // add all menu to the Menu Bar
                MenuBar menuBar = new MenuBar();
                menuBar.getMenus().addAll(fileMenu, helpMenu);
                return menuBar;
        }

        // Method for About Game
        private void about_me() {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("About The Developer");
                alert.setHeaderText("Amit Ghosh");
                alert.setContentText("I love to play online computer games such as Dota 2," +
                                        " CS:GO, Modern Warfare etc");
                alert.show();
        }

        // Method for About Game
        private void about_game() {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("About Connect Four");
                alert.setHeaderText("How To Play");
                alert.setContentText("Connect Four is a two-player connection game in which the players first choose a color " +
                        "and then take turns dropping colored discs from the top into a seven-column, six-row vertically suspended grid. " +
                        "The pieces fall straight down, occupying the next available space within the column. The objective of the game is to be " +
                        "the first to form a horizontal, vertical, or diagonal line of four of one's own discs. Connect Four is a solved game. " +
                        "The first player can always win by playing the right moves.");
                alert.show();
        }

        // Method for Exit Game
        private void exitGame() {
                Platform.exit();
                System.exit(0);
        }
}
