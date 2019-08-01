package com.implemica.calculator.service;

import com.implemica.calculator.model.CalculatorModel;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;

import java.math.BigDecimal;

enum CalcState {TypeLeft, TypeRight, DisplayResult}

public class InputService {
  /**
   * Current state of calculator
   */
  private CalcState calcState;

  /**
   * Calculator com.implemica.calculator.model
   */
  private CalculatorModel calc;

  public InputService() {
    calcState = CalcState.TypeLeft;
    calc = new CalculatorModel();
  }

  /**
   * @param event   event info of button, that have been pressed
   * @param display numbers in textArea
   * @return new numbers in textArea
   */
  public String enterNumberOrComma(ActionEvent event, String display) {
    Button btn = (Button) event.getSource();
    String value = btn.getText();

    if (calcState == CalcState.DisplayResult) {
      calcState = CalcState.TypeRight;
      return value;
    }

    if (Character.isDigit(value.charAt(0))) {
      if (display.startsWith("0") && !display.startsWith("0.")) {
        return value;
      }

      return display + value;
    } else if (value.charAt(0) == '.') {
      if (display.contains(".")) {
        return "";
      } else if (display.isEmpty()) {
        return "0.";
      } else return display + value;
    }
    return "";
  }

  /**
   * Set calcState to CalcState.TypeLeft to type new numbers like textArea is clear
   */
  public void clear() {
    calcState = CalcState.TypeLeft;
  }

  /**
   * Typing the binary operation
   *
   * @param event   event info of button, that have been pressed
   * @param display numbers in textArea
   * @return result of operation if two operands exists or display if not
   */
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

  /**
   * Typing equal
   *
   * @param right right operand typed in calc
   * @return result of binary operation
   */
  public String enterEqual(String right) {
    if (calc.getLeftOperand() != null) {
      calc.setRightOperand(new BigDecimal(right));
      String result = calc.getBinaryOperationResult();

      settingAfterResult(result);

      return result;
    } else {
      return right;
    }
  }


  /**
   * Typing the unary operation
   *
   * @param event   event info of button, that have been pressed
   * @param display numbers in textArea
   * @return result of operation
   */
  public String unaryOp(ActionEvent event, String display) {
    Button btn = (Button) event.getSource();
    String res = calc.getUnaryOperationResult(btn.getText(), display);

    if (calcState == CalcState.TypeRight) {
      calc.setRightOperand(new BigDecimal(res));
    } else if (calcState == CalcState.TypeLeft) {
      calc.setLeftOperand(new BigDecimal(res));
    }

    return res;
  }

  /**
   * Typing percent
   * @param right right operand typed in calc
   * @return result of percent operation
   */
  public String percentOp(String right) {
    if (calc.getLeftOperand() != null) {
      calc.setRightOperand(new BigDecimal(right));
      String result = calc.getPercentOperation();

      settingAfterResult(result);

      return result;
    } else {
      return right;
    }
  }


  private void settingAfterResult(String result) {
    calc.setOperation(null);
    calc.setLeftOperand(new BigDecimal(result));
    calc.setRightOperand(null);
    clear();
  }
}