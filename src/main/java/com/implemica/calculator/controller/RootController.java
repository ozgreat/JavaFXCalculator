package com.implemica.calculator.controller;

import com.implemica.calculator.service.InputService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;


public class RootController {
  @FXML
  private TextArea textArea;

  @FXML
  private Button clearButton;

  @FXML
  private Button fiveButton;

  private InputService inputService = new InputService();


  @FXML
  private void addNumberOrComma(ActionEvent event){ // buttons 0-9 and ','
    String value = inputService.enterNumberOrComma(event, textArea.getText());
    textArea.setText(value);
  }

  @FXML
  private void clearAction(){ //button C
    textArea.clear();
    inputService.clear();
  }

  @FXML
  private void operationButtonAction(ActionEvent event){
      textArea.clear();
      inputService.enterOperation(event);
  }
}
