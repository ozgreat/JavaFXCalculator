package com.implemica.calculator.controller;

import com.implemica.calculator.service.InputService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
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

  private InputService inputService;

  private static double xOffset = 0;

  private static double yOffset = 0;

  public RootController() {
    inputService = new InputService();
  }

  /**
   * Typing of number or comma
   *
   * @param event event of button that we pressed
   */
  @FXML
  public void addNumberOrComma(ActionEvent event) { // buttons 0-9 and ','
    String value = inputService.enterNumberOrComma(event, display.getText());
    display.setText(value);
    fontSizing();
  }

  /**
   * Setting text in textArea to 0 and call clear in service
   */
  @FXML
  public void clearAction() { //button C
    display.setText("0");
    inputService.clearDisplay();
    fontSizing();
  }

  /**
   * Setting text in textArea to 0
   */
  @FXML
  public void clearEntryAction() {
    display.setText("0");
  }

  /**
   * @param event event of button that we pressed
   */
  @FXML
  public void operationButtonAction(ActionEvent event) {
    display.setText(inputService.enterOperation(event, display.getText()));
    fontSizing();
  }

  /**
   * removing last symbol in textArea
   */
  @FXML
  public void backspaceButton() {
    if (!display.getText().isEmpty()) {
      String str = display.getText().substring(0, display.getText().length() - 1);
      display.setText(inputService.displayFormat(str));
      fontSizing();
    }
  }

  @FXML
  public void equalAction() {
    String value = inputService.enterEqual(display.getText());
    display.setText(inputService.displayFormat(value));
    fontSizing();
  }

  @FXML
  public void unaryOperationAction(ActionEvent ae) {
    if (!display.getText().isEmpty()) {
      String value = inputService.unaryOp(ae, display.getText());
      display.setText(inputService.displayFormat(value));
      fontSizing();
    }
  }

  @FXML
  public void percentAction() {
    String value = inputService.percentOp(display.getText());
    display.setText(inputService.displayFormat(value));
    fontSizing();
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

  private void fontSizing() {
    if (display.getText().length() > MAX_LENGTH_TO_STANDARD_FONT) {
      Font font = new Font(STANDARD_FONT_SIZE - (display.getText().length() + 1 - MAX_LENGTH_TO_STANDARD_FONT));
      display.setFont(font);
    }
  }
}
