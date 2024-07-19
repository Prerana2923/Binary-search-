

import javafx.animation.*;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Arrays;
import java.util.Optional;
import java.util.Random;

public class BinarySearch extends Application {
    private int[] array;
    private HBox arrayBox;
    private TextField targetField;
    private Button searchButton;
    private Button inputButton;
    private Button resetButton;
    private Button randomButton;
    private TextArea resultArea;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Binary Search Visualization");

        // Background image
        BackgroundImage backgroundImage = new BackgroundImage(new Image("images/image.png", 2000, 1000, false, true),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT);

        arrayBox = new HBox();
        arrayBox.setSpacing(10);
        arrayBox.setAlignment(Pos.CENTER);

        targetField = new TextField();
        targetField.setPromptText("Enter target value");

        searchButton = new Button("Search");
        searchButton.setTooltip(new Tooltip("Search for the target value in the array"));
        searchButton.setOnAction(e -> startBinarySearch());

        inputButton = new Button("Input Array Data");
        inputButton.setTooltip(new Tooltip("Input array elements separated by commas"));
        inputButton.setOnAction(e -> doAction(e));

        resetButton = new Button("Reset");
        resetButton.setTooltip(new Tooltip("Reset the array and input fields"));
        resetButton.setOnAction(e -> resetFields());

        randomButton = new Button("Generate Random Data");
        randomButton.setTooltip(new Tooltip("Generate random array data"));
        randomButton.setOnAction(e -> generateRandomData());

        resultArea = new TextArea();
        resultArea.setEditable(false);
        resultArea.setPrefHeight(100);

        VBox vBox = new VBox(10, arrayBox, targetField, inputButton, randomButton, searchButton, resetButton, resultArea);
        vBox.setPadding(new Insets(10));
        vBox.setAlignment(Pos.CENTER);
        vBox.setBackground(new Background(backgroundImage));  // Set background image

        Scene scene = new Scene(vBox, 2000, 1000);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void doAction(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Input Array Data");
        dialog.setHeaderText("Enter array elements separated by commas");
        dialog.setContentText("Array:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(arrayString -> {
            String[] elements = arrayString.split(",");
            array = Arrays.stream(elements).mapToInt(Integer::parseInt).toArray();
            Arrays.sort(array);
            arrayBox.getChildren().clear();
            createArrayBox(array);
        });
    }

    private void generateRandomData() {
        TextInputDialog sizeDialog = new TextInputDialog();
        sizeDialog.setTitle("Generate Random Data");
        sizeDialog.setHeaderText("Enter the size of the array");
        sizeDialog.setContentText("Size:");

        Optional<String> result = sizeDialog.showAndWait();
        result.ifPresent(sizeString -> {
            int size;
            try {
                size = Integer.parseInt(sizeString);
            } catch (NumberFormatException e) {
                resultArea.setText("Invalid size value. Please enter a valid integer.");
                return;
            }

            array = new Random().ints(size, 1, 100).sorted().toArray();
            arrayBox.getChildren().clear();
            createArrayBox(array);
        });
    }

    private void createArrayBox(int[] arr) {
        for (int value : arr) {
            Circle circle = new Circle(20, Color.LIGHTBLUE);
            Label label = new Label(String.valueOf(value));
            StackPane stackPane = new StackPane(circle, label);
            arrayBox.getChildren().add(stackPane);
        }
    }

    private void startBinarySearch() {
        if (array == null || array.length == 0) {
            resultArea.setText("Array is empty. Please input array data.");
            return;
        }

        int target;
        try {
            target = Integer.parseInt(targetField.getText());
        } catch (NumberFormatException e) {
            resultArea.setText("Invalid target value. Please enter a valid integer.");
            return;
        }

        resultArea.setText("");
        new Thread(() -> {
            int index = binarySearch(target, 0, array.length - 1);
            if (index == -1) {
                resultArea.setText("Target value not found.");
            } else {
                resultArea.setText("Target value found at index " + index);
                animateSearch(index);
            }
        }).start();
    }

    private int binarySearch(int target, int left, int right) {
        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (array[mid] == target) {
                return mid;
            }
            if (array[mid] < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        return -1;
    }

    private void animateSearch(int index) {
        Circle circle = (Circle) ((StackPane) arrayBox.getChildren().get(index)).getChildren().get(0);

        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.5), circle);
        translateTransition.setByY(-20);
        translateTransition.setAutoReverse(true);
        translateTransition.setCycleCount(2);

        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(0.5), circle);
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.3);
        fadeTransition.setAutoReverse(true);
        fadeTransition.setCycleCount(2);

        FillTransition fillTransition = new FillTransition(Duration.seconds(1), circle, Color.LIGHTBLUE, Color.GREEN);
        fillTransition.setAutoReverse(true);
        fillTransition.setCycleCount(2);

        SequentialTransition sequentialTransition = new SequentialTransition(translateTransition, fadeTransition, fillTransition);
        sequentialTransition.play();
    }

    private void resetFields() {
        arrayBox.getChildren().clear();
        targetField.clear();
        resultArea.clear();
        array = null;
    }
}
