package com.implemica.calculator.service;

import com.implemica.calculator.model.CalculatorModel;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;

enum CalcState {TypeLeft, TypeRight, DisplayResult}

public class InputService {
  private CalcState calcState;
  private CalculatorModel calc = new CalculatorModel();

  public String enterNumberOrComma(ActionEvent event, String area) {
    Button btn = (Button) event.getSource();
    String value = btn.getText();

    if (calcState == CalcState.DisplayResult) {
      calcState = CalcState.TypeLeft;
      return "";
    }

    if (Character.isDigit(value.charAt(0))) {
      return area + value;
    } else if (value.charAt(0) == ',') {
      if (area.contains(",")) {
        return "";
      } else if (area.isEmpty()) {
        return "0,";
      } else return area + value;
    }
    return "";
  }

  public void clear() {
    calcState = CalcState.TypeLeft;
  }

  public void enterOperation(ActionEvent event) {
    if (calc.getOperation() != null) {
      Button btn = (Button) event.getSource();
      calc.setOperation(btn.getText());
    }
  }
}
