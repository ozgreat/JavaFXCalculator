package com.implemica.calculator.service;

import com.implemica.calculator.model.CalculatorModel;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;

import java.math.BigDecimal;

enum CalcState {TypeLeft, TypeRight, DisplayResult}

public class InputService {
  private CalcState calcState;
  private CalculatorModel calc;

  public InputService() {
    calcState = CalcState.TypeLeft;
    calc = new CalculatorModel();
  }

  public String enterNumberOrComma(ActionEvent event, String area) {
    Button btn = (Button) event.getSource();
    String value = btn.getText();

    if (calcState == CalcState.DisplayResult) {
      calcState = CalcState.TypeRight;
      return value;
    }

    if (Character.isDigit(value.charAt(0))) {
      return area + value;
    } else if (value.charAt(0) == '.') {
      if (area.contains(".")) {
        return "";
      } else if (area.isEmpty()) {
        return "0.";
      } else return area + value;
    }
    return "";
  }

  public void clear() {
    calcState = CalcState.TypeLeft;
  }

  public String enterOperation(ActionEvent event, String display) {
    if (calcState == CalcState.TypeLeft) {
      calc.setLeftOperand(new BigDecimal(display));
      calcState = CalcState.DisplayResult;
      Button btn = (Button) event.getSource();
      calc.setOperation(btn.getText());
      return display;
    } else if (calcState == CalcState.TypeRight) {
      calc.setRightOperand(new BigDecimal(display));
      calcState = CalcState.DisplayResult;
      String res = calc.getBinaryOperationResult();
      Button btn = (Button) event.getSource();
      calc.setOperation(btn.getText());
      return res;
    }
    return display;
  }

  public String enterEqual(String right) {
    if (calc.getLeftOperand() != null) {
      calc.setRightOperand(new BigDecimal(right));
      String result = calc.getBinaryOperationResult();
      calc.setOperation(null);
      calc.setLeftOperand(new BigDecimal(result));
      calc.setRightOperand(null);
      clear();
      return result;
    } else {
      return right;
    }
  }

  public String unaryOp(ActionEvent ae, String display) {
    Button btn = (Button) ae.getSource();
    String res = calc.getUnaryOperationResult(btn.getText(), display);
    if (calcState == CalcState.TypeRight) {
      calc.setRightOperand(new BigDecimal(res));
    } else if (calcState == CalcState.TypeLeft) {
      calc.setLeftOperand(new BigDecimal(res));
    }
    return res;
  }
}