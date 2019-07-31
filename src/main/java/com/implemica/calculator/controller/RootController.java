package com.implemica.calculator.controller;

import com.implemica.calculator.service.InputService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

public class RootController {
  @FXML
  private TextArea textArea;

  private InputService inputService;

  public RootController() {
    inputService = new InputService();
  }

  @FXML
  public void addNumberOrComma(ActionEvent event) { // buttons 0-9 and ','
    String value = inputService.enterNumberOrComma(event, textArea.getText());
    textArea.setText(value);
  }

  @FXML
  public void clearAction() { //button C
    textArea.clear();
    inputService.clear();
  }

  @FXML
  public void clearEntryAction() {
    textArea.clear();
  }

  @FXML
  public void operationButtonAction(ActionEvent event) {
    textArea.setText(inputService.enterOperation(event, textArea.getText()));
  }

  @FXML
  public void backspaceButton() {
    if (!textArea.getText().isEmpty()) {
      textArea.setText(textArea.getText().substring(0, textArea.getText().length() - 1));
    }
  }

  @FXML
  public void equalAction() {
    textArea.setText(inputService.enterEqual(textArea.getText()));
  }

  @FXML
  public void unaryOperationAction(ActionEvent ae) {
    if (!textArea.getText().isEmpty()) {
      textArea.setText(inputService.unaryOp(ae, textArea.getText()));
    }
  }
}
