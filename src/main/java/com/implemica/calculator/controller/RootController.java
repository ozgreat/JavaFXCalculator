package com.implemica.calculator.controller;

import com.implemica.calculator.service.InputService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class RootController {

  private static final int MAX_LENGTH_TO_STANDARD_FONT = 13;
  private static final int STANDARD_FONT_SIZE = 46;
  public static final Background BACKGROUND = new Background(new BackgroundFill(Paint.valueOf("#f2f2f2"), CornerRadii.EMPTY, Insets.EMPTY));

  @FXML
  private Label display;

  @FXML
  private Button memoryClearButton;

  @FXML
  private Button memoryRecallButton;

  @FXML
  private AnchorPane historyUpperPane;

  @FXML
  private AnchorPane historyPane;

  @FXML
  private Label historyLabel;

  @FXML
  private Button percentButton;

  @FXML
  private Button sqrtButton;

  @FXML
  private Button powButton;

  @FXML
  private Button divideButton;

  @FXML
  private Button reverseButton;

  @FXML
  private Button multiplyButton;

  @FXML
  private Button addButton;

  @FXML
  private Button subtractButton;

  @FXML
  private Button pointButton;

  @FXML
  private Button negateButton;


  private InputService inputService;

  private static double xOffset = 0;

  private static double yOffset = 0;

  private static final double FONT_CHANGE_WIDTH_DOWN = 31.98;
  private static final double FONT_CHANGE_WIDTH_UP = 50d;

  public RootController() {
    inputService = new InputService();
  }

  @FXML
  public void initialize() {
    display.textProperty().addListener(observable -> {
      Text text = new Text(display.getText());
      double fontSize = display.getFont().getSize();
      text.setFont(new Font("Segoe UI Semibold", fontSize));
      double width = text.getLayoutBounds().getWidth();
      Scene scene = display.getScene();
      double sceneWidth = scene.getWidth();

      while (FONT_CHANGE_WIDTH_DOWN > sceneWidth - width) {
        fontSize--;
        text.setFont(new Font("Segoe UI Semibold", fontSize));
        width = text.getLayoutBounds().getWidth();
      }

      while (sceneWidth - width > FONT_CHANGE_WIDTH_UP && text.getText().length() > 12) {
        fontSize++;
        text.setFont(new Font("Segoe UI Semibold", fontSize));
        width = text.getLayoutBounds().getWidth();
      }

      display.setStyle(
          "-fx-font-size:" + fontSize + ";\n" +
              "  -fx-font-family: \"Segoe UI Semibold\";"
      );

    });
  }

  /**
   * Typing of number or comma
   *
   * @param event event of button that we pressed
   */
  @FXML
  public void addNumberOrComma(ActionEvent event) { // buttons 0-9 and ','
    if (display.getText().equals(InputService.CANNOT_DIVIDE_BY_ZERO) || display.getText().equals(InputService.OVERFLOW)) {
      setNormal();
    }
    String value = inputService.enterNumberOrComma(event, display.getText());
    display.setText(value);
  }

  /**
   * Setting text in textArea to 0 and call clear in service
   */
  @FXML
  public void clearAction() { //button C
    if (display.getText().equals(InputService.CANNOT_DIVIDE_BY_ZERO) || display.getText().equals(InputService.OVERFLOW)) {
      setNormal();
    }
    display.setText("0");
    inputService.clearDisplay();
  }

  /**
   * Setting text in textArea to 0
   */
  @FXML
  public void clearEntryAction() {
    display.setText("0");
    if (display.getText().equals(InputService.CANNOT_DIVIDE_BY_ZERO) || display.getText().equals(InputService.OVERFLOW)) {
      setNormal();
    }
  }

  /**
   * @param event event of button that we pressed
   */
  @FXML
  public void operationButtonAction(ActionEvent event) {
    try {
      display.setText(inputService.enterOperation(event, display.getText()));
    } catch (ArithmeticException e) {
      handleArithmetic(e.getMessage());
    }
  }

  /**
   * removing last symbol in textArea
   */
  @FXML
  public void backspaceButton() {
    if (display.getText().equals(InputService.CANNOT_DIVIDE_BY_ZERO) || display.getText().equals(InputService.OVERFLOW)) {
      setNormal();
    } else if (display.getText().length() == 1) {
      clearEntryAction();
    } else if (!display.getText().isEmpty()) {
      String str = display.getText().substring(0, display.getText().length() - 1);
      display.setText(inputService.displayFormat(str));
    }
  }

  @FXML
  public void equalAction() {
    if (display.getText().equals(InputService.CANNOT_DIVIDE_BY_ZERO) || display.getText().equals(InputService.OVERFLOW)) {
      setNormal();
    }
    try {
      String value = inputService.enterEqual(display.getText());
      display.setText(value);
    } catch (ArithmeticException e) {
      handleArithmetic(e.getMessage());
    }
  }

  @FXML
  public void unaryOperationAction(ActionEvent ae) {
    if (!display.getText().isEmpty()) {
      try {
        String value = inputService.unaryOp(ae, display.getText());
        display.setText(inputService.displayFormat(value));
      } catch (ArithmeticException e) {
        handleArithmetic(e.getMessage());
      }

    }
  }

  @FXML
  public void percentAction() {
    String value = inputService.percentOp(display.getText());
    display.setText(inputService.displayFormat(value));
  }

  @FXML
  public void memorySaveAction() {
    inputService.saveToMemory(display.getText());

    if (memoryClearButton.isDisable() && memoryRecallButton.isDisable()) {
      memoryRecallButton.setDisable(false);
      memoryClearButton.setDisable(false);
    }
  }

  @FXML
  public void memoryRecallAction() {
    display.setText(inputService.recallFromMemory());
  }

  @FXML
  public void memoryClearAction() {
    inputService.clearMemory();

    memoryClearButton.setDisable(true);
    memoryRecallButton.setDisable(true);
  }

  @FXML
  public void memoryPlusAction() {
    inputService.addToMemory(display.getText());

    if (memoryClearButton.isDisable() && memoryRecallButton.isDisable()) {
      memoryRecallButton.setDisable(false);
      memoryClearButton.setDisable(false);
    }
  }

  @FXML
  public void memoryMinusAction() {
    inputService.subToMemory(display.getText());

    if (memoryClearButton.isDisable() && memoryRecallButton.isDisable()) {
      memoryRecallButton.setDisable(false);
      memoryClearButton.setDisable(false);
    }
  }

  @FXML
  public void historyAction() {
    if (historyPane.isDisable()) {
      historyPane.setDisable(false);
      historyLabel.setVisible(true);
      historyPane.setBackground(BACKGROUND);
      historyUpperPane.setDisable(false);
      historyUpperPane.setVisible(true);
    } else {
      historyPane.setDisable(true);
      historyLabel.setVisible(false);
      historyPane.setBackground(Background.EMPTY);
      historyUpperPane.setDisable(true);
      historyUpperPane.setVisible(false);
    }
  }

  @FXML
  public void closeWindow(MouseEvent event) {
    ((Button) event.getSource()).getScene().getWindow().hide();
  }

  @FXML
  public void minimizeWindow(MouseEvent event) {
    Stage stage = (Stage) (((Button) event.getSource()).getScene().getWindow());
    stage.setIconified(true);
  }

  @FXML
  public void pressWindow(MouseEvent event) {
    Stage stage = (Stage) (((AnchorPane) event.getSource()).getScene().getWindow());
    xOffset = stage.getX() - event.getScreenX();
    yOffset = stage.getY() - event.getScreenY();
  }

  @FXML
  public void dragWindow(MouseEvent event) {
    Stage stage = (Stage) (((AnchorPane) event.getSource()).getScene().getWindow());
    stage.setX(event.getScreenX() + xOffset);
    stage.setY(event.getScreenY() + yOffset);
  }


  private void handleArithmetic(String msg) {
    display.setText(msg);

    negateButton.setDisable(true);
    addButton.setDisable(true);
    subtractButton.setDisable(true);
    sqrtButton.setDisable(true);
    percentButton.setDisable(true);
    pointButton.setDisable(true);
    powButton.setDisable(true);
    divideButton.setDisable(true);
    multiplyButton.setDisable(true);
    reverseButton.setDisable(true);
  }

  private void setNormal() {
    display.setText("0");
    clearAction();

    negateButton.setDisable(false);
    addButton.setDisable(false);
    subtractButton.setDisable(false);
    sqrtButton.setDisable(false);
    percentButton.setDisable(false);
    pointButton.setDisable(false);
    powButton.setDisable(false);
    divideButton.setDisable(false);
    multiplyButton.setDisable(false);
    reverseButton.setDisable(false);
  }


}
