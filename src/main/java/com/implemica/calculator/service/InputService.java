package com.implemica.calculator.service;

import com.implemica.calculator.model.CalculatorModel;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

enum CalcState {
  /**
   * CalcState when user is typing left operand
   */
  LEFT,
  /**
   * CalcState when user is typing right operand
   */
  RIGHT,
  /**
   * CalcState when user has just typed left operand and will type right operand
   */
  TRANSIENT,
  /**
   * CalcState after operation
   */
  AFTER
}

public class InputService {
  public static final String CANNOT_DIVIDE_BY_ZERO = "Cannot divide by zero";
  public static final String OVERFLOW = "Overflow";
  /**
   * Current state of calculator
   */
  private CalcState calcState;

  /**
   * Calculator com.implemica.calculator.model
   */
  private CalculatorModel calc;

  private final static int MAX_LENGTH = 21;

  private final static Map<String, String> binaryOperationUnicode = new HashMap<>();

  private final static Map<String, String> unaryOperationUnicode = new HashMap<>();

  static {
    binaryOperationUnicode.put("\uE948", "+");
    binaryOperationUnicode.put("\uE949", "-");
    binaryOperationUnicode.put("\uE947", "×");
    binaryOperationUnicode.put("\uE94A", "÷");

    unaryOperationUnicode.put("\uE94B", "√");
    unaryOperationUnicode.put("\uE94D", "negate");
    unaryOperationUnicode.put("⅟\uD835\uDC65", "1/");//⅟𝑥
    unaryOperationUnicode.put("\uD835\uDC65²", "sqr");//𝑥²
  }

  public InputService() {
    calcState = CalcState.LEFT;
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

    if (calcState == CalcState.AFTER) {
      calcState = CalcState.LEFT;
      return displayFormat(value);
    } else if (calcState == CalcState.TRANSIENT) {
      calcState = CalcState.RIGHT;
      return displayFormat(value);
    }

    if (display.length() == MAX_LENGTH) {
      return displayFormat(display);
    }


    if (Character.isDigit(value.charAt(0))) {
      if (display.startsWith("0") && !display.startsWith("0.")) {
        return displayFormat(value);
      }
      return displayFormat(display + value);
    }

    if (value.equals(".")) {
      if (display.contains(".")) {
        return display;
      } else if (display.isEmpty()) {
        return "0.";
      } else {
        return displayFormat(display + value);
      }
    }

    return "";
  }

  /**
   * Set calcState to CalcState.AFTER to type new numbers like textArea is clear
   */
  public void clearDisplay() {
    calcState = CalcState.AFTER;
    calc.setOperation(null);
  }

  /**
   * Typing the binary operation
   *
   * @param event   event info of button, that have been pressed
   * @param display numbers in textArea
   * @return result of operation if two operands exists or display if not
   */
  public String enterOperation(ActionEvent event, String display) throws ArithmeticException {
    display = display.replaceAll(",", "");

    if (calcState == CalcState.LEFT) {
      calc.setLeftOperand(new BigDecimal(display));

      calcState = CalcState.TRANSIENT;

      Button btn = (Button) event.getSource();
      calc.setOperation(formatOperation(btn.getText()));

      return displayFormat(display);
    } else if (calcState == CalcState.RIGHT) {
      calc.setRightOperand(new BigDecimal(display));

      calcState = CalcState.TRANSIENT;

      String res = calc.getBinaryOperationResult();

      Button btn = (Button) event.getSource();
      calc.setOperation(formatOperation(btn.getText()));
      calc.setLeftOperand(new BigDecimal(res));
      return displayFormat(res);
    } else if (calcState == CalcState.AFTER) {
      calcState = CalcState.TRANSIENT;

      Button btn = (Button) event.getSource();
      calc.setOperation(formatOperation(btn.getText()));

      return displayFormat(display);
    }

    Button btn = (Button) event.getSource();
    calc.setOperation(formatOperation(btn.getText()));
    return displayFormat(display);
  }

  /**
   * Typing equal
   *
   * @param right right operand typed in calc
   * @return result of binary operation
   */
  public String enterEqual(String right) throws ArithmeticException {
    if (calc.getLeftOperand() != null && calc.getOperation() != null) {
      if (calcState != CalcState.AFTER) {
        right = right.replaceAll(",", "");
        calc.setRightOperand(new BigDecimal(right));
      }

      String result = calc.getBinaryOperationResult();


      if (result.equals(CANNOT_DIVIDE_BY_ZERO) || result.equals(OVERFLOW)) {
        return result;
      }

      settingAfterResult(result);

      return displayFormat(result);
    } else {
      return displayFormat(right);
    }
  }


  /**
   * Typing the unary operation
   *
   * @param event   event info of button, that have been pressed
   * @param display numbers in textArea
   * @return result of operation
   */
  public String unaryOp(ActionEvent event, String display) throws ArithmeticException {
    Button btn = (Button) event.getSource();
    display = display.replaceAll(",", "");

    String res = calc.getUnaryOperationResult(formatOperation(btn.getText()), display);

    if (calcState == CalcState.RIGHT) {
      calc.setRightOperand(new BigDecimal(res));
    } else if (calcState == CalcState.LEFT) {
      calc.setLeftOperand(new BigDecimal(res));
    }

    return displayFormat(res);
  }

  /**
   * Typing percent
   *
   * @param right right operand typed in calc
   * @return result of percent operation
   */
  public String percentOp(String right) {
    if (calc.getLeftOperand() != null) {
      right = right.replaceAll(",", "");
      calc.setRightOperand(new BigDecimal(right));

      String result = calc.getPercentOperation();

      return displayFormat(result);
    } else {
      return displayFormat(right);
    }
  }

  /**
   * Save number from display to memory
   *
   * @param display number in textArea
   */
  public void saveToMemory(String display) {
    calc.getMemory().add(new BigDecimal(display));
  }

  /**
   * Call last element from memory
   *
   * @return string with last element of memory
   */
  public String recallFromMemory() {
    return calc.recallMemory().toString();
  }

  /**
   * Clear memory
   */
  public void clearMemory() {
    calc.clearMemory();
  }

  public void addToMemory(String display) {
    calc.memoryAdd(new BigDecimal(display));
  }

  public void subToMemory(String display) {
    calc.memorySub(new BigDecimal(display));
  }

  public String displayFormat(String display) {
    display = display.replaceAll(",", "");


    if (display.contains("E")) {
      display = formatLongNums(display);
      return display;
    }


    if (display.endsWith(".")) {
      return displayFormat(display.substring(0, display.indexOf('.'))) + ".";
    }

    BigDecimal big = new BigDecimal(display);
    big = CalculatorModel.getRounded16IfItsPossible(big);
    display = big.toString();

    if (display.contains(".")) {
      String[] partsOfFrac = display.split("\\.");
      if (partsOfFrac.length == 2) {
        return deleteLastZeroInFrac(displayFormat(partsOfFrac[0]) + "." + partsOfFrac[1]); // display format for int part
      }
    }


    StringBuilder displayBuilder = new StringBuilder();
    for (int i = 0; i < display.length(); i++) {
      displayBuilder.append(display.charAt(i));

      if ((display.length() - i - 1) % 3 == 0) { // comma after every 3d element from the end
        displayBuilder.append(",");
      }
    }
    display = displayBuilder.toString();


    if (display.endsWith(",")) {
      display = display.substring(0, display.length() - 1);
    }


    return display;
  }


  private String formatLongNums(String displayBuf) {
    String display = new BigDecimal(displayBuf, CalculatorModel.mc32).toEngineeringString();
    if (!display.contains("E")) {
      return display;
    }
    String[] displayArr = display.split("E");
    displayArr[0] = CalculatorModel.getRounded16IfItsPossible(new BigDecimal(displayArr[0])).toString();

    if (displayArr[0].contains(".")) {
      formatEngineer(displayArr);
    } else {
      if (displayArr[0].endsWith("0")) {
        displayArr[1] = incrementEPart(displayArr, displayArr[1]);
      }

      displayArr[0] += ".";
    }

    display = displayArr[0] + "E" + displayArr[1];
    return display;
  }

  private void formatEngineer(String[] displayArr) { //todo: maybe return String[]
    String[] displayNumParts = displayArr[0].split("\\.");
    if (displayNumParts[0].endsWith("0")) {
      displayArr[1] = incrementEPart(displayNumParts, displayArr[1]);

      displayArr[0] = displayNumParts[0] + "." + displayNumParts[1];
      displayArr[0] = deleteLastZeroInFrac(displayArr[0]);
    }
  }

  private String incrementEPart(String[] displayArr, String ePart) {
    displayArr[0] = displayArr[0].substring(0, displayArr[0].length() - 1);
    int lastNum = Integer.parseInt(Character.toString(ePart.charAt(ePart.length() - 1)));

    return ePart.substring(0, ePart.length() - 1) + (lastNum + 1);
  }

  private void settingAfterResult(String result) {
    calc.setLeftOperand(new BigDecimal(result));
    calcState = CalcState.AFTER;
  }

  private String deleteLastZeroInFrac(String num) {
    if (num.endsWith("0")) {
      return deleteLastZeroInFrac(num.substring(0, num.length() - 1));
    } else {
      return num;
    }
  }

  private String formatOperation(String op) {
    String formated = binaryOperationUnicode.get(op);
    if (formated != null) {
      return formated;
    }

    formated = unaryOperationUnicode.get(op);
    if (formated != null) {
      return formated;
    }

    return op;
  }

  public String highFormula(ActionEvent event, String oldFormula, String display) {
    Button btn = (Button) event.getSource();

    if (calcState == CalcState.TRANSIENT) {
      if (binaryOperationUnicode.containsKey(btn.getText())) {
        return oldFormula.substring(oldFormula.length() - 1) + btn.getText();
      } else if (unaryOperationUnicode.containsKey(btn.getText())) {
        return oldFormula + unaryOperationUnicode.get(btn.getText()) + "(" + display + ")";
      }
    }

    if (calcState == CalcState.LEFT || calcState == CalcState.AFTER) {
      if (binaryOperationUnicode.containsKey(btn.getText())) {
        return display + btn.getText();
      } else if (unaryOperationUnicode.containsKey(btn.getText())) {
        return unaryOperationUnicode.get(btn.getText()) + "(" + display + ")";
      }
    }

    if (calcState == CalcState.RIGHT) {
      if (binaryOperationUnicode.containsKey(btn.getText())) {
        return oldFormula + display + btn.getText();
      } else if (unaryOperationUnicode.containsKey(btn.getText())) {
        return oldFormula + unaryOperationUnicode.get(btn.getText()) + "(" + display + ")";
      }
    }

    //todo: percent
    return "";
  }

  public boolean isMemoryEmpty() {
    return calc.getMemory().isEmpty();
  }
}