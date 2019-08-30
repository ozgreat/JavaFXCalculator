package com.implemica.calculator.controller.service;

import com.implemica.calculator.model.CalculatorModel;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
@Setter
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
  private final static Map<Integer, String> displayPattern = new HashMap<>();

  static {
    binaryOperationUnicode.put("\uE948", "+");
    binaryOperationUnicode.put("\uE949", "-");
    binaryOperationUnicode.put("\uE947", "×");
    binaryOperationUnicode.put("\uE94A", "÷");
    binaryOperationUnicode.put("/", "÷");
    binaryOperationUnicode.put("*", "×");

    unaryOperationUnicode.put("\uE94B", "√");
    unaryOperationUnicode.put("\uE94D", "negate");
    unaryOperationUnicode.put("⅟\uD835\uDC65", "1/");//⅟𝑥
    unaryOperationUnicode.put("\uD835\uDC65²", "sqr");//𝑥²

    displayPattern.put(0, "0.################");
    displayPattern.put(1, "0.###############");
    displayPattern.put(2, "#0.##############");
    displayPattern.put(3, "##0.#############");
    displayPattern.put(4, "#,##0.############");
    displayPattern.put(5, "#,##0.###########");
    displayPattern.put(6, "#,##0.##########");
    displayPattern.put(7, "#,##0.#########");
    displayPattern.put(8, "#,##0.########");
    displayPattern.put(9, "#,##0.#######");
    displayPattern.put(10, "#,##0.######");
    displayPattern.put(11, "#,##0.#####");
    displayPattern.put(12, "#,##0.####");
    displayPattern.put(13, "#,##0.###");
    displayPattern.put(14, "#,##0.##");
    displayPattern.put(15, "#,##0.#");
    displayPattern.put(16, "#,###.");
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

    if (value.equals(".")) {
      if (display.contains(".")) {
        return display;
      } else {
        if (CalcState.AFTER == calcState) {
          calcState = CalcState.LEFT;
        }
        return displayFormat(display + value);
      }
    }

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
      return displayFormat(display + value);
    }

    return "";
  }

  /**
   * Set calcState to CalcState.AFTER to type new numbers like textArea is clear
   */
  public void clearDisplay() {
    calcState = CalcState.AFTER;
    calc.setOperation(null);
    calc.setLeftOperand(BigDecimal.ZERO);
    calc.setRightOperand(BigDecimal.ZERO);
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
      if (calcState == CalcState.TRANSIENT && calc.getOperation().equals("÷")) {
        calc.setRightOperand(calc.getLeftOperand());
      } else if (calcState != CalcState.AFTER) {
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
      calcState = CalcState.AFTER;

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
//      calcState = CalcState.TRANSIENT;
    } else if (calcState == CalcState.LEFT) {
      calc.setLeftOperand(new BigDecimal(res));
      if (!formatOperation(btn.getText()).equals("negate")) {
        calcState = CalcState.TRANSIENT;
      }
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

      if (calcState == CalcState.TRANSIENT) {
        calcState = CalcState.RIGHT;
      } else if (calcState == CalcState.LEFT) {
        calcState = CalcState.TRANSIENT;
        calc.setLeftOperand(calc.getRightOperand());
      } else if (calcState == CalcState.RIGHT) {
        calcState = CalcState.AFTER;
      }

      return displayFormat(result);
    } else {
      return displayFormat("0");
    }
  }

  /**
   * Save number from display to memory
   *
   * @param display number in textArea
   */
  public void saveToMemory(String display) {
    calc.setMemory(new BigDecimal(display.replaceAll(",", "")));
  }

  /**
   * Call last element from memory
   *
   * @return string with last element of memory
   */
  public String recallFromMemory() {
    return displayFormat(calc.getMemory().toString());
  }

  /**
   * Clear memory
   */
  public void clearMemory() {
    calc.clearMemory();
  }

  public void addToMemory(String display) {
    calc.memoryAdd(new BigDecimal(display.replaceAll(",", "")));
  }

  public void subToMemory(String display) {
    calc.memorySub(new BigDecimal(display.replaceAll(",", "")));
  }

  public String displayFormat(String display) {
    DecimalFormat df;
    if (display.contains(",")) {
      display = display.replaceAll(",", "");
    }


    BigDecimal big = new BigDecimal(display);
    String displayBuf = CalculatorModel.getRounded16IfItsPossible(big).toPlainString();

    if (displayBuf.replace(".", "").length() <= 16 && !display.contains(".")) {
      if ((calcState == CalcState.TRANSIENT || calcState == CalcState.AFTER) && displayBuf.contains(".")) {
        String[] displayArr = displayBuf.split("\\.");
        String pattern = displayPattern.get(displayArr[0].length());
        df = new DecimalFormat(pattern, new DecimalFormatSymbols(Locale.ENGLISH));
      } else {
        df = new DecimalFormat("#,##0", new DecimalFormatSymbols(Locale.ENGLISH));
      }

      return df.format(big);
    } else if (displayBuf.startsWith("0") && displayBuf.replace(".", "").length() >= 17 && display.contains(".")) {
      if (display.endsWith(".")) {
        df = new DecimalFormat("#,##0", new DecimalFormatSymbols(Locale.ENGLISH));

        return df.format(big) + ".";
      }

      String pattern = displayPattern.get(0);
      if (calcState == CalcState.LEFT && pattern.contains(".")) {
        pattern = getFracPattern(display, pattern);
      }

      df = new DecimalFormat(pattern, new DecimalFormatSymbols(Locale.ENGLISH));

      return df.format(big);
    } else if (display.contains(".")) {
      if (display.endsWith(".")) {
        df = new DecimalFormat("#,##0", new DecimalFormatSymbols(Locale.ENGLISH));

        return df.format(big) + ".";
      }
      String[] displayArr = displayBuf.split("\\.");
      String pattern = displayPattern.get(displayArr[0].length());
      if (pattern != null) {
        if (calcState == CalcState.LEFT && pattern.contains(".") && !pattern.endsWith(".")) {
          pattern = getFracPattern(display, pattern);
        }
        df = new DecimalFormat(pattern, new DecimalFormatSymbols(Locale.ENGLISH));

        return df.format(big);
      }
    }

    if (display.contains("E")) {
      BigDecimal tmp = new BigDecimal(display);
      if (tmp.toPlainString().replace(".", "").replace("-", "").length() <= 16) {
        display = tmp.toPlainString();
      } else {
        return formatLong(tmp);
      }
    }

    if (!display.contains(".") &&
        display.replace(",", "").replace("-", "").replace(".", "").length() > 17) {
      return formatLong(big);
    }
    return null;
  }

  private String getFracPattern(String display, String pattern) {
    String[] patternArr = pattern.split("\\.");
    for (int i = 0; i < (display.length() - display.indexOf(".") - 1); i++) {
      patternArr[1] = patternArr[1].replaceFirst("#", "0");
    }
    pattern = patternArr[0] + "." + patternArr[1];
    return pattern;
  }

  public boolean isBackspaceAvailable() {
    return calcState != CalcState.AFTER;
  }


  private void settingAfterResult(String result) {
    calc.setLeftOperand(new BigDecimal(result));
    calcState = CalcState.AFTER;
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

    if (!oldFormula.equals("") && Character.isDigit(oldFormula.charAt(oldFormula.length() - 1))) {
      if (binaryOperationUnicode.containsKey(btn.getText())) {
        return oldFormula + binaryOperationUnicode.get(btn.getText());
      } else if (unaryOperationUnicode.containsKey(btn.getText())) {
        return oldFormula + binaryOperationUnicode.get(btn.getText());
      }
    }


    if (calcState == CalcState.TRANSIENT) {
      return transientHighFormula(btn, oldFormula, display);
    } else if (calcState == CalcState.RIGHT) {
      return rightHighFormula(btn, oldFormula, display);
    } else {
      return leftOrAfterHighFormula(btn, oldFormula, display);
    }
  }

  public boolean isMemoryEmpty() {
    return calc.getMemory() == null;
  }

  private String transientHighFormula(Button btn, String oldFormula, String display) {
    if (binaryOperationUnicode.containsKey(btn.getText())) {
      if (oldFormula.endsWith(")")) {
        return oldFormula + " " + binaryOperationUnicode.get(btn.getText());
      }
      return oldFormula.substring(0, oldFormula.length() - 1) + binaryOperationUnicode.get(btn.getText());
    } else if (unaryOperationUnicode.containsKey(btn.getText())) {
      if (oldFormula.endsWith(")")) {
        String str = unaryOpSubStringFinder(oldFormula);
        return oldFormula.substring(0, oldFormula.indexOf(str)) + unaryOperationUnicode.get(btn.getText()) + "( "
            + str + " )";
      }
      if (unaryOperationUnicode.get(btn.getText()).equals("negate") && calc.getOperation() == null) {
        return oldFormula;
      }
      if (!oldFormula.isBlank()) {
        oldFormula += " ";
      }
      return oldFormula + unaryOperationUnicode.get(btn.getText()) + "( " + display.replaceAll(",", "") + " )";
    } else if (btn.getText().equals("\uE94C")) { //%
      if (oldFormula.isBlank()) {
        return "";
      }
      return oldFormula + " " + display.replaceAll(",", "");
    } else if (binaryOperationUnicode.containsValue(btn.getText())) {
      if (oldFormula.endsWith(")")) {
        return oldFormula + " " + btn.getText();
      }
      return oldFormula.substring(0, oldFormula.length() - 1) + btn.getText();
    } else if (unaryOperationUnicode.containsValue(btn.getText())) {
      if (oldFormula.endsWith(")")) {
        String str = unaryOpSubStringFinder(oldFormula);
        return oldFormula.substring(0, oldFormula.indexOf(str)) + btn.getText() + "( " + str + " )";
      }
      if (btn.getText().equals("negate")) {
        return oldFormula;
      }
      if (!oldFormula.isBlank()) {
        oldFormula += " ";
      }
      return oldFormula + btn.getText() + "( " + display.replaceAll(",", "") + " )";
    }
    return "";
  }

  private String leftOrAfterHighFormula(Button btn, String oldFormula, String display) {
    if (binaryOperationUnicode.containsKey(btn.getText())) {
      if (oldFormula.endsWith(")")) {
        return oldFormula + " " + binaryOperationUnicode.get(btn.getText());
      }
      return display.replaceAll(",", "") + " " + binaryOperationUnicode.get(btn.getText());
    } else if (unaryOperationUnicode.containsKey(btn.getText()) && !btn.getText().equals("\uE94D")) {
      if (oldFormula.endsWith(")")) {
        return unaryOperationUnicode.get(btn.getText()) + "( " + oldFormula + " )";
      }

      return unaryOperationUnicode.get(btn.getText()) + "( " + display.replaceAll(",", "") + " )";
    } else if (binaryOperationUnicode.containsValue(btn.getText())) {
      if (oldFormula.endsWith(")")) {
        return oldFormula + " " + btn.getText();
      }
      return display.replaceAll(",", "") + " " + btn.getText();
    } else if (unaryOperationUnicode.containsValue(btn.getText())) {
      if (oldFormula.endsWith(")")) {
        return btn.getText() + "( " + oldFormula + " )";
      }

      return btn.getText() + "( " + display.replaceAll(",", "") + " )";
    } else if (btn.getText().equals("\uE94C")) { //%
      if (oldFormula.isBlank()) {
        return "";
      } else if (Character.isDigit(oldFormula.charAt(oldFormula.length() - 1))) {
        return "";
      }
      return oldFormula + " " + display.replaceAll(",", "");
    }
    return "";
  }

  private String rightHighFormula(Button btn, String oldFormula, String display) {
    if (binaryOperationUnicode.containsKey(btn.getText())) {
      return oldFormula + " " + display.replaceAll(",", "") + " " + binaryOperationUnicode.get(btn.getText());
    } else if (unaryOperationUnicode.containsKey(btn.getText())) {
      if (oldFormula.endsWith(")")) {
        String str = unaryOpSubStringFinder(oldFormula);
        return oldFormula.substring(0, oldFormula.indexOf(str)) + unaryOperationUnicode.get(btn.getText()) + "(" + str +
            " )";
      }
      return oldFormula + " " + unaryOperationUnicode.get(btn.getText()) + "( " + display.replaceAll(",", "") + " )";
    } else if (btn.getText().equals("\uE94C")) { //%
      if (oldFormula.isBlank()) {
        return "";
      }
      return oldFormula + " " + display.replaceAll(",", "");
    } else if (binaryOperationUnicode.containsValue(btn.getText())) {
      return oldFormula + " " + display.replaceAll(",", "") + " " + btn.getText();
    } else if (unaryOperationUnicode.containsValue(btn.getText())) {
      if (oldFormula.endsWith(")")) {
        String str = unaryOpSubStringFinder(oldFormula);
        return oldFormula.substring(0, oldFormula.indexOf(str)) + btn.getText() + "(" + str + " )";
      }

      return oldFormula + " " + btn.getText() + "( " + display.replaceAll(",", "") + " )";
    }

    return "";
  }

  private String unaryOpSubStringFinder(String formula) {
    Pattern p;
    List<Integer> index = new ArrayList<>();
    List<String> list = new ArrayList<>(unaryOperationUnicode.values());
    for (String op : list) {
      p = Pattern.compile("\\b" + op + "\\b");
      Matcher matcher = p.matcher(formula);
      if (matcher.find()) {
        index.add(matcher.start());
      }
    }

    if (formula.contains("√")) {
      index.add(formula.indexOf("√"));
    }

    if (formula.contains("1/")) {
      index.add((formula.indexOf("1/")));
    }

    int min = index.indexOf(Collections.min(index));
    return formula.substring(index.get(min));
  }

  private String formatLongN(BigDecimal big) {
    big = big.round(CalculatorModel.mc32);
    DecimalFormat df = new DecimalFormat("#,##0.###############E0###", new DecimalFormatSymbols(Locale.ENGLISH));
    String res = df.format(big);
    return res;
  }

  private String formatLong(BigDecimal big) {
    String display = big.round(CalculatorModel.mc32).toEngineeringString();
    if (!display.contains("E")) {
      display = formatLongN(big);
    }
    String[] displayArr = display.split("E");
    displayArr[0] = CalculatorModel.getRounded16IfItsPossible(new BigDecimal(displayArr[0])).toString();

    if (displayArr[0].contains(".")) {
      formatEngineer(displayArr);
    } else {
      while (displayArr[0].endsWith("0")) {
        displayArr[1] = incrementEPart(displayArr, displayArr[1]);
      }
      displayArr[0] += ".";
    }

    display = displayArr[0] + "E" + displayArr[1];

    return display;
  }

  private void formatEngineer(String[] displayArr) {
    String[] displayNumParts = displayArr[0].split("\\.");
    if (displayNumParts[0].endsWith("0")) {
      displayArr[1] = incrementEPart(displayNumParts, displayArr[1]);

      displayArr[0] = displayNumParts[0] + "." + displayNumParts[1];
      displayArr[0] = deleteLastZeroInFrac(displayArr[0]);
    }
  }

  private String deleteLastZeroInFrac(String num) {
    if (num.endsWith("0")) {
      return deleteLastZeroInFrac(num.substring(0, num.length() - 1));
    } else {
      return num;
    }
  }

  private String incrementEPart(String[] displayArr, String ePart) {
    displayArr[0] = displayArr[0].substring(0, displayArr[0].length() - 1);
    int lastNum = Integer.parseInt(/*Character.toString(ePart.charAt(ePart.length() - 1))*/ePart);

    if (lastNum + 1 >= 0) {
      return "+" + (lastNum + 1);
    } else {
      return String.valueOf(lastNum + 1);
    }
  }
}