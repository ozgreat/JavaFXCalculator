package com.implemica.calculator.controller;

import com.implemica.calculator.service.InputService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;

public class RootController {
  @FXML
  private TextArea textArea;

  @FXML
  private Button memoryClearButton;

  @FXML
  private Button memoryRecallButton;

  @FXML
  private Button memoryPlusButton;

  @FXML
  private Button memoryMinusButton;

  @FXML
  private Button memorySaveButton;

  @FXML
  private AnchorPane historyPane;

  @FXML
  private Label historyLabel;

  private InputService inputService;

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
    String value = inputService.enterNumberOrComma(event, textArea.getText());
    textArea.setText(value);
  }

  /**
   * Setting text in textArea to 0 and call clear in service
   */
  @FXML
  public void clearAction() { //button C
    textArea.setText("0");
    inputService.clearDisplay();
  }

  /**
   * Setting text in textArea to 0
   */
  @FXML
  public void clearEntryAction() {
    textArea.setText("0");
  }

  /**
   * @param event event of button that we pressed
   */
  @FXML
  public void operationButtonAction(ActionEvent event) {
    textArea.setText(inputService.enterOperation(event, textArea.getText()));
  }

  /**
   * removing last symbol in textArea
   */
  @FXML
  public void backspaceButton() {
    if (!textArea.getText().isEmpty()) {
      String str = textArea.getText().substring(0, textArea.getText().length() - 1);
      textArea.setText(inputService.displayFormat(str));
    }
  }

  @FXML
  public void equalAction() {
    String value = inputService.enterEqual(textArea.getText());
    textArea.setText(inputService.displayFormat(value));
  }

  @FXML
  public void unaryOperationAction(ActionEvent ae) {
    if (!textArea.getText().isEmpty()) {
      String value = inputService.unaryOp(ae, textArea.getText());
      textArea.setText(inputService.displayFormat(value));
    }
  }

  @FXML
  public void percentAction() {
    String value = inputService.percentOp(textArea.getText());
    textArea.setText(inputService.displayFormat(value));
  }

  @FXML
  public void memorySaveAction() {
    inputService.saveToMemory(textArea.getText());

    if (memoryClearButton.isDisable() && memoryRecallButton.isDisable()) {
      memoryRecallButton.setDisable(false);
      memoryClearButton.setDisable(false);
    }
  }

  @FXML
  public void memoryRecallAction() {
    textArea.setText(inputService.recallFromMemory());
  }

  @FXML
  public void memoryClearAction() {
    inputService.clearMemory();

    memoryClearButton.setDisable(true);
    memoryRecallButton.setDisable(true);
  }

  @FXML
  public void memoryPlusAction() {
    inputService.addToMemory(textArea.getText());

    if (memoryClearButton.isDisable() && memoryRecallButton.isDisable()) {
      memoryRecallButton.setDisable(false);
      memoryClearButton.setDisable(false);
    }
  }

  @FXML
  public void memoryMinusAction() {
    inputService.subToMemory(textArea.getText());

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

      memoryClearButton.setDisable(true);
      memoryRecallButton.setDisable(true);
      memoryMinusButton.setDisable(true);
      memoryPlusButton.setDisable(true);
      memorySaveButton.setDisable(true);
    } else {
      historyPane.setDisable(true);
      historyLabel.setVisible(false);

      memoryClearButton.setDisable(false);
      memoryRecallButton.setDisable(false);
      memoryMinusButton.setDisable(false);
      memoryPlusButton.setDisable(false);
      memorySaveButton.setDisable(false);
    }
  }
}
