package com.project.connectfour;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Controller implements Initializable {

        private static final int COLUMNS = 7;
        private static final int ROWS = 6;
        private static final int CIRCLE_DIAMETER = 80;
        private static final String discColor1 = "#24303E";
        private static final String discColor2 = "#4CAA88";

        private static String PLAYER_ONE = "Player One";
        private static String PLAYER_TWO = "Player Two";

        private boolean isPlayerOneTurn = true;

        private Disc[][] insertedDiscsArray = new Disc[ROWS][COLUMNS]; // For Structural changes

        @FXML
        public GridPane rootGridPane;
        @FXML
        public Pane insertedDiscsPane;
        @FXML
        public Label playerNameLabel;
        @FXML
        public TextField player_1;
        @FXML
        public TextField player_2;
        @FXML
        public Button setName;


        private boolean allowed_toInsert = true;

        public void createPlayground() {
                Shape rectangleWithHoles = createGameStructuralGrid(); // create a rectangle for playground
                rootGridPane.add(rectangleWithHoles, 0, 1); // add the rectangle onto the grid pane
                List<Rectangle> rectangleList = createClickableColumns();

                for (Rectangle rectangle: rectangleList) {
                        rootGridPane.add(rectangle, 0, 1);
                }
        }

        private Shape createGameStructuralGrid() {

                Shape rectangleWithHoles = new Rectangle((COLUMNS + 1) * CIRCLE_DIAMETER, (ROWS + 1) * CIRCLE_DIAMETER);

                for (int row = 0; row < ROWS; row++) {
                        for (int col = 0; col < COLUMNS; col++) {
                                Circle circle = new Circle();
                                circle.setRadius(CIRCLE_DIAMETER / 2);
                                circle.setCenterX(CIRCLE_DIAMETER / 2);
                                circle.setCenterY(CIRCLE_DIAMETER / 2);
                                circle.setSmooth(true);

                                circle.setTranslateX(col * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4);
                                circle.setTranslateY(row * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4);

                                rectangleWithHoles = Shape.subtract(rectangleWithHoles, circle);
                        }
                }

                rectangleWithHoles.setFill(Color.WHITE);

                return rectangleWithHoles;
        }

        // Set rectangles for every column
        private List<Rectangle> createClickableColumns() {

                List<Rectangle> rectangleList = new ArrayList<>();

                for (int col = 0; col < COLUMNS; col++) {

                        Rectangle rectangle = new Rectangle(CIRCLE_DIAMETER, (ROWS + 1) * CIRCLE_DIAMETER);
                        rectangle.setFill(Color.TRANSPARENT);
                        rectangle.setTranslateX(col * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4);

                        rectangle.setOnMouseEntered(event -> rectangle.setFill(Color.valueOf("#eeeeee26"))); // color when mouse pointer pointed on that particular position
                        rectangle.setOnMouseExited(event -> rectangle.setFill(Color.TRANSPARENT));

                        final int column = col;
                        rectangle.setOnMouseClicked(event -> { // insert disc while clicked on the particular column
                                if (allowed_toInsert) {
                                        allowed_toInsert = false;
                                        insertDisc(new Disc(isPlayerOneTurn), column);
                                }
                        });

                        rectangleList.add(rectangle);
                }

                return rectangleList;
        }

        // insert disc at a particular column and set animation
        private void insertDisc(Disc disc, int column) { // for structural changes
                int row = ROWS - 1;
                while (row >= 0) { // maintaining the row th position
                        if (row < 0 || row >= ROWS || column < 0 || column >= COLUMNS || insertedDiscsArray[row][column] == null)
                                break;
                        --row;
                }

                if (row < 0) // return if all rows are filled
                        return;

                insertedDiscsArray[row][column] = disc; // For structural
                insertedDiscsPane.getChildren().add(disc); // For visual
                disc.setTranslateX(column * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4);
                TranslateTransition transition = new TranslateTransition(Duration.seconds(0.5), disc);
                transition.setToY(row * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4);

                // toggle between players
                int currentRow = row;
                transition.setOnFinished(actionEvent -> {
                        allowed_toInsert = true;
                        if (gameEnded(currentRow, column)) {
                                gameOver(); // we should end the game when we have a winner
                                return;
                        }

                        isPlayerOneTurn = !isPlayerOneTurn;
                        playerNameLabel.setText(isPlayerOneTurn ? PLAYER_ONE : PLAYER_TWO);
                });
                transition.play();
        }

        /**
         *      There are 4 winning criteria -> Horizontal, Vertical, Diagonal, Anti-Diagonal
         *      We have to check for every possible criteria
         **/
        private boolean gameEnded(int row, int col) {
                // Vertical Criteria i.e. only row will change
                // So we have to check the entire col and row ranges are {0,1,2,3,4,5} for 4 disc combination

                List<Point2D> verticalPoints =  IntStream.rangeClosed(row - 3, row + 3)  // range of row values = 0,1,2,3,4,5
                        .mapToObj(r -> new Point2D(r, col)) // Assume col = 3, then (0, 3) (1, 3) (2, 3) (3, 3) (4, 3) (5, 3) -> Point2D row, col
                        .collect(Collectors.toList());

                // Horizontal Criteria i.e. only col will change
                List<Point2D> horizontalPoints =  IntStream.rangeClosed(col - 3, col + 3)  // range of row values = 0,1,2,3,4,5
                        .mapToObj(c -> new Point2D(row, c)) // Assume row = 3, then (3, 0) (3, 1) (3, 2) (3, 3) (3, 4) (3, 5) -> Point2D row, col
                        .collect(Collectors.toList());


                // Diagonal Criteria
                Point2D startPoint1 = new Point2D(row - 3, col + 3);
                List<Point2D> diagonal = IntStream.rangeClosed(0, 6)
                        .mapToObj(i -> startPoint1.add(i, -i))
                        .collect(Collectors.toList());

                // Anti Diagonal
                Point2D startPoint2 = new Point2D(row - 3, col - 3);
                List<Point2D> anti_diagonal = IntStream.rangeClosed(0, 6)
                        .mapToObj(i -> startPoint2.add(i, i))
                        .collect(Collectors.toList());

                boolean found_combination = checkCombination(verticalPoints) || checkCombination(horizontalPoints)
                        || checkCombination(diagonal) || checkCombination(anti_diagonal);

                return found_combination;
        }

        private boolean checkCombination(List<Point2D> points) {
                int chain = 0;
                for (Point2D point : points) {
                        int rowIdx = (int) point.getX();
                        int colIdx = (int) point.getY();

                        Disc disc = rowIdx >= 0 && rowIdx < ROWS && colIdx >= 0 && colIdx < COLUMNS ? insertedDiscsArray[rowIdx][colIdx] : null; // check out of bound
                        if (disc != null && disc.isPlayerOneMove == isPlayerOneTurn) {
                                chain++;
                                if (chain == 4)
                                        return true;
                        } else {
                                chain = 0;
                        }
                }
                return false;
        }

        private void gameOver() {
                String winner = isPlayerOneTurn ? PLAYER_ONE : PLAYER_TWO;
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Connect Four");
                alert.setHeaderText("The Winner is " + winner);
                alert.setContentText("Want to play again ?");

                ButtonType yes = new ButtonType("Yes");
                ButtonType no = new ButtonType("No, Exit");
                alert.getButtonTypes().setAll(yes, no);

                Platform.runLater(() -> {
                        Optional<ButtonType> clickedButton = alert.showAndWait();
                        if (clickedButton.isPresent() && clickedButton.get() == yes) {
                                resetGame();
                        } else {
                                Platform.exit();
                                System.exit(0);
                        }
                });
//                alert.show();
        }

        public void resetGame() {
//                remove all the disc from the pane
                insertedDiscsPane.getChildren().clear();

//                remove all the disc from the array and set a new play ground
                for (int r = 0; r < insertedDiscsArray.length; r++)
                        for (int c = 0; c < insertedDiscsArray[r].length; c++)
                                insertedDiscsArray[r][c] = null;

                isPlayerOneTurn = true;
                playerNameLabel.setText(PLAYER_TWO);
                createPlayground(); // create fresh new play ground
        }

        private static class Disc extends Circle {

                private final boolean isPlayerOneMove;

                public Disc(boolean isPlayerOneMove) {

                        this.isPlayerOneMove = isPlayerOneMove;
                        setRadius(CIRCLE_DIAMETER / 2);
                        setFill(isPlayerOneMove ? Color.valueOf(discColor1) : Color.valueOf(discColor2));
                        setCenterX(CIRCLE_DIAMETER/2);
                        setCenterY(CIRCLE_DIAMETER/2);
                }
        }

        @Override
        public void initialize(URL location, ResourceBundle resources) {
                setName.setOnAction(actionEvent -> {
                        PLAYER_ONE = player_1.getText();
                        PLAYER_TWO = player_2.getText();
                        playerNameLabel.setText(PLAYER_ONE);
                });
        }
}
