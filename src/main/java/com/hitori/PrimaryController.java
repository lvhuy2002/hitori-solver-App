package com.hitori;

import java.io.File;

import com.controller.GenTest;
import com.controller.HitoriController;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class PrimaryController {

    private int gridSize = 5;
    private double cellSizePercent = 4.0;
    private int[][] matrix;
    private int[][] userMatrix;
    private FileChooser fileChooser = new FileChooser();
    @FXML
    private ChoiceBox<String> encodingMethodChoiceBox;

    @FXML
    private TextArea informationTextArea;

    @FXML
    private Spinner<Integer> matrixSizeSpinner;

    @FXML
    private Button randomButton;

    @FXML
    private Button runSolverButton;

    @FXML
    private HBox sceneHBox;

    @FXML
    private Button selectFileButton;

    @FXML
    private ScrollPane matrixScrollPane;

    @FXML
    void initialize() {
        fileChooser.setInitialDirectory(new File("D:\\Formal method\\hitori\\input"));
        informationTextArea.setScrollTop(Double.MAX_VALUE);
        InitMatrixSizeSpinner();
        InitEncodingMethodChoiceBox();
        InitMatrixScrollPane();
        matrix = GenTest.genSingleTestForApp(gridSize);
        userMatrix = matrix;
        AddNewMatrixToScrollPane();
    }

    void InitMatrixSizeSpinner() {
        int minValue = 1; // Giá trị tối thiểu
        int maxValue = 50; // Giá trị tối đa
        int defaultValue = gridSize; // Giá trị mặc định

        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(minValue,
                maxValue, defaultValue);
        matrixSizeSpinner.setValueFactory(valueFactory);
    }

    void InitEncodingMethodChoiceBox() {

        encodingMethodChoiceBox.getItems().addAll("CE", "CE+");
        encodingMethodChoiceBox.setValue("CE");

    }

    void InitMatrixScrollPane() {
        matrixScrollPane.widthProperty().addListener((observable, oldValue, newValue) -> {
            // Xử lý sự kiện thay đổi kích thước chiều ngang của ScrollPane
            double newWidth = newValue.doubleValue();
            if (matrixScrollPane.getContent() instanceof GridPane && gridSize > 20) {
                GridPane gridPane = (GridPane) matrixScrollPane.getContent();
                matrixScrollPane.setFitToWidth(false);
                gridPane.setPrefWidth((cellSizePercent * newWidth / 100.0) * gridSize);

            }
            // ...
        });

        matrixScrollPane.heightProperty().addListener((observable, oldValue, newValue) -> {
            // Xử lý sự kiện thay đổi kích thước chiều cao của ScrollPane
            double newHeight = newValue.doubleValue();
            if (matrixScrollPane.getContent() instanceof GridPane && gridSize > 20) {
                GridPane gridPane = (GridPane) matrixScrollPane.getContent();
                matrixScrollPane.setFitToWidth(false);
                gridPane.setPrefHeight((cellSizePercent * newHeight / 100.0) * gridSize);

            }
            // ...
        });
    }

    void AddNewMatrixToScrollPane() {

        matrixScrollPane.setContent(null);
        GridPane gridPane = new GridPane();
        if (gridSize > 20) {
            matrixScrollPane.setFitToWidth(false);
            matrixScrollPane.setFitToHeight(false);
            gridPane.setPrefWidth((cellSizePercent * matrixScrollPane.getWidth() / 100.0) * gridSize);
            gridPane.setPrefHeight((cellSizePercent * matrixScrollPane.getHeight() / 100.0) * gridSize);

        } else {
            matrixScrollPane.setFitToWidth(true);
            matrixScrollPane.setFitToHeight(true);
        }
        gridPane.setStyle("-fx-grid-lines-visible: true; -fx-grid-line-color: black;");

        // Tạo các constraints cho cột và hàng để có độ dài và chiều rộng bằng nhau
        for (int i = 0; i < gridSize; i++) {
            ColumnConstraints columnConstraints = new ColumnConstraints();
            columnConstraints.setPercentWidth(100.0 / gridSize);
            gridPane.getColumnConstraints().add(columnConstraints);

            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setPercentHeight(100.0 / gridSize);
            gridPane.getRowConstraints().add(rowConstraints);
        }

        // Tạo các ô trong GridPane
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                Label label = new Label(String.valueOf(matrix[row][col]));
                label.setAlignment(Pos.CENTER);
                label.setStyle("-fx-font-size: 16px;"); 
                HBox cellContainer = new HBox(label);
                cellContainer.setAlignment(Pos.CENTER);
                cellContainer.setStyle("-fx-background-color: white;");
                // Biến trạng thái để theo dõi màu của ô
                cellContainer.setOnMouseClicked(e -> {
                    if (e.getButton() == MouseButton.PRIMARY) {
                        String currentColor = cellContainer.getStyle();

                        if (currentColor.equals("-fx-background-color: black;")) {
                            cellContainer.setStyle("-fx-background-color: white;");
                            label.setStyle("-fx-font-size: 16px; -fx-text-fill: black;");
                        } else {

                            cellContainer.setStyle("-fx-background-color: black;");
                            label.setStyle("-fx-font-size: 16px; -fx-text-fill: white;");
                        }
                    } else if (e.getButton() == MouseButton.SECONDARY) {
                        // Xử lý sự kiện click chuột phải
                        // Tạo một TextField mới với giá trị ban đầu là giá trị hiện tại của ô
                        TextField textField = new TextField(label.getText());
                        textField.setAlignment(Pos.CENTER); // Căn giữa nội dung của TextField

                        // Đặt TextField vào cellContainer thay thế Label
                        cellContainer.getChildren().clear();
                        cellContainer.getChildren().add(textField);

                        // Giới hạn độ dài của TextField là 2 kí tự
                        textField.setOnKeyTyped(ev -> {
                            if (textField.getText().length() > 2) {
                                textField.setText(textField.getText().substring(0, 2));
                            }
                        });

                        // Chỉ cho phép nhập kí tự số trong TextField
                        textField.textProperty().addListener((observable, oldValue, newValue) -> {
                            if (!newValue.matches("\\d*")) {
                                textField.setText(newValue.replaceAll("[^\\d]", "")); // Xóa bỏ các kí tự không phải số
                            }
                        });

                        // Tự động chọn toàn bộ nội dung của TextField
                        textField.selectAll();

                        // Xử lý sự kiện khi người dùng kết thúc chỉnh sửa trong TextField (nhấn Enter
                        // hoặc mất focus)
                        textField.setOnAction(ev -> {
                            // Lấy giá trị mới từ TextField
                            String newValue = textField.getText();

                            // Đặt giá trị mới vào Label và đặt lại Label vào cellContainer
                            label.setText(newValue);
                            cellContainer.getChildren().clear();
                            cellContainer.getChildren().add(label);
                        });

                        // Xử lý sự kiện khi người dùng mất focus khỏi TextField
                        textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
                            // Nếu TextField mất focus, đặt giá trị hiện tại của TextField vào Label và đặt
                            // lại Label vào cellContainer
                            if (!newValue) {
                                String currentValue = textField.getText();
                                label.setText(currentValue);
                                cellContainer.getChildren().clear();
                                cellContainer.getChildren().add(label);
                            }
                        });
                    }

                });
                gridPane.add(cellContainer, col, row);
            }
        }
        matrixScrollPane.setContent(gridPane);
    }

    void AddNewContentToTextArea(String content) {
        informationTextArea.setText(
                informationTextArea.getText() + "----------------------------------------\n" + content);
        informationTextArea.positionCaret(informationTextArea.getLength());
    }

    @FXML
    void handleRandomButtonOnClick(MouseEvent event) {
        gridSize = matrixSizeSpinner.getValue();
        matrix = GenTest.genSingleTestForApp(gridSize);
        userMatrix = matrix;
        AddNewMatrixToScrollPane();
    }

    @FXML
    void handleRunSolverButtonOnClick(MouseEvent event) {
        if (matrixScrollPane.getContent() instanceof GridPane) {
            int typeEncodingMethod = 1;
            String encodingMethod = encodingMethodChoiceBox.getSelectionModel().getSelectedItem();
            if (encodingMethod == "CE+") {
                typeEncodingMethod = 2;
            }
            GridPane gridPane = (GridPane) matrixScrollPane.getContent();
            for (int row = 0; row < gridSize; row++) {
                for (int col = 0; col < gridSize; col++) {
                    // Lấy ra thành phần tại vị trí (row, col)
                    HBox cell = (HBox) gridPane.getChildren().get(row * gridSize + col);
                    Label label = (Label) cell.getChildren().get(0);
                    matrix[row][col] = Integer.parseInt(label.getText());
                }
            }
            int[][] result = HitoriController.runSolver(matrix, typeEncodingMethod);
            long time = HitoriController.recentTime;
            int clause = HitoriController.recentClause;
            int variable = HitoriController.recentVariable;
            if (result[0][0] == 0) {
                AddNewContentToTextArea("Action: Run solver"
                        + "\nMatrix: " + Integer.toString(gridSize) + "x" + Integer.toString(gridSize)
                        + "\nEncoding method: " + encodingMethod
                        + "\nTime: " + Long.toString(time)
                        + "ms\nVariables: " + Integer.toString(variable)
                        + "\nClause: " + Integer.toString(clause)
                        + "\nResult: UNSAT\n");
                // AddNewContentToTextArea();
            } else {
                AddNewContentToTextArea("Action: Run solver"
                        + "\nMatrix: " + Integer.toString(gridSize) + "x" + Integer.toString(gridSize)
                        + "\nEncoding method: " + encodingMethod
                        + "\nTime: " + Long.toString(time)
                        + "ms\nVariables: " + Integer.toString(variable)
                        + "\nClause: " + Integer.toString(clause)
                        + "\nResult: SAT\n");
                for (int row = 0; row < gridSize; row++) {
                    for (int col = 0; col < gridSize; col++) {
                        // Lấy ra thành phần tại vị trí (row, col)
                        HBox cell = (HBox) gridPane.getChildren().get(row * gridSize + col);
                        Label label = (Label) cell.getChildren().get(0);
                        if (result[row][col] < 0) {
                            cell.setStyle("-fx-background-color: black;");
                            label.setStyle("-fx-font-size: 16px; -fx-text-fill: white;");
                        } else {
                            cell.setStyle("-fx-background-color: white;");
                            label.setStyle("-fx-font-size: 16px;-fx-text-fill: black;");
                        }
                    }
                }
            }

        }

    }

    @FXML
    void handleCheckButtonOnClick(MouseEvent event) {
        if (matrixScrollPane.getContent() instanceof GridPane) {
            GridPane gridPane = (GridPane) matrixScrollPane.getContent();
            for (int row = 0; row < gridSize; row++) {
                for (int col = 0; col < gridSize; col++) {
                    // Lấy ra thành phần tại vị trí (row, col)
                    HBox cell = (HBox) gridPane.getChildren().get(row * gridSize + col);
                    Label label = (Label) cell.getChildren().get(0);
                    if (cell.getStyle().equals("-fx-background-color: black;")) {
                        userMatrix[row][col] = -Integer.parseInt(label.getText());
                        System.out.println(userMatrix[row][col]);
                    } else if (cell.getStyle().equals("-fx-background-color: white;")) {
                        userMatrix[row][col] = Integer.parseInt(label.getText());
                        System.out.println(userMatrix[row][col]);
                    }
                }
            }
            boolean isValid = HitoriController.validateHitori(userMatrix);
            if (isValid) {
                AddNewContentToTextArea("Action: Check is matrix valid"
                    + "\nMatrix: " + Integer.toString(gridSize) + "x" + Integer.toString(gridSize)
                    + "\nResult: valid\n");
            } else {
                AddNewContentToTextArea("Action: Check is matrix valid"
                    + "\nMatrix: " + Integer.toString(gridSize) + "x" + Integer.toString(gridSize)
                    + "\nResult: invalid\n");
            }
            // render isValid

            
        }
    }

    @FXML
    void handleSelectFileButtonOnClick(MouseEvent event) {
        File file = fileChooser.showOpenDialog(new Stage());
        matrix = HitoriController.readFileInput(file);
        userMatrix = matrix;
        gridSize = matrix.length;
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
        matrixSizeSpinner.getValueFactory().setValue(gridSize);
        ;
        // System.out.println(matrix.length);
        AddNewMatrixToScrollPane();
    }

    @FXML
    void touchThem(MouseEvent event) {
        System.out.println("laa");
    }

}

// sceneHBox.setPadding(new Insets(10));
// int NUM_ROWS = 20; // Số hàng
// int NUM_COLS = 20; // Số cột
// GridPane gridPane = new GridPane();

// // Thiết lập ràng buộc cho GridPane
// HBox.setHgrow(gridPane, javafx.scene.layout.Priority.ALWAYS);
// //sceneHBox.setVgrow(gridPane, javafx.scene.layout.Priority.ALWAYS);
// gridPane.setStyle("-fx-grid-lines-visible: true; -fx-grid-line-color:
// black;");
// // Thiết lập percent width cho các cột trong GridPane
// for (int col = 0; col < NUM_COLS; col++) {
// ColumnConstraints columnConstraints = new ColumnConstraints();
// columnConstraints.setPercentWidth(100.0/NUM_COLS);
// gridPane.getColumnConstraints().add(columnConstraints);
// }

// // Thêm nội dung vào các ô của GridPane
// for (int row = 0; row < NUM_ROWS; row++) {
// for (int col = 0; col < NUM_COLS; col++) {
// Text cellText = new Text("00");
// gridPane.add(cellText, col, row);
// GridPane.setVgrow(cellText, javafx.scene.layout.Priority.ALWAYS);
// }
// }
// sceneHBox.getChildren().add(gridPane);
