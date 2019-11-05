package com.implemica.calculator.controller;

import com.implemica.calculator.model.*;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.implemica.calculator.controller.NumberFormatter.parse;
import static com.implemica.calculator.controller.NumberFormatter.removeGroupSeparator;
import static java.lang.Math.max;

/**
 * Service class, that is bridge between {@link com.implemica.calculator.controller.RootController} and
 * {@link CalculatorModel}. Calling from {@link com.implemica.calculator.controller.RootController} and
 * transmit query from controller to model.
 *
 * @author ozreat
 * @see com.implemica.calculator.controller.RootController
 * @see CalculatorModel
 * @see Operation
 * @see CalculatorState
 * @see NumberFormatter
 */
class InputService {
  /**
   * Calculator model to do calculations
   */
  private CalculatorModel calc;

  /**
   * Flag, that confirm status of number on display
   */
  private boolean isMemoryRecall = false;

  /**
   * Flag that confirm, that user can do backspace
   */
  private boolean isBackspacePossible = true;

  /**
   * Map of binary {@link Operation} to get Operation from string
   */
  private final static Map<String, Operation> binaryOperationObject = new HashMap<>();

  /**
   * Map of unary {@link Operation} to get Operation from string
   */
  private final static Map<String, Operation> unaryOperationObject = new HashMap<>();

  /**
   * Map to get operation symbol from {@link Operation}
   */
  private final static Map<Operation, String> operationSymbols = new HashMap<>();

  static {
    binaryOperationObject.put("\uE948", Operation.ADD);
    binaryOperationObject.put("\uE949", Operation.SUBTRACT);
    binaryOperationObject.put("\uE947", Operation.MULTIPLY);
    binaryOperationObject.put("\uE94A", Operation.DIVIDE);
    binaryOperationObject.put("/", Operation.DIVIDE);
    binaryOperationObject.put("*", Operation.MULTIPLY);
    binaryOperationObject.put("+", Operation.ADD);
    binaryOperationObject.put("-", Operation.SUBTRACT);
    binaryOperationObject.put("×", Operation.MULTIPLY);
    binaryOperationObject.put("÷", Operation.DIVIDE);

    unaryOperationObject.put("\uE94B", Operation.SQRT);
    unaryOperationObject.put("\uE94D", Operation.NEGATE);
    unaryOperationObject.put("⅟\uD835\uDC65", Operation.REVERSE);//⅟𝑥
    unaryOperationObject.put("\uD835\uDC65²", Operation.POW);//𝑥²
    unaryOperationObject.put("negate", Operation.NEGATE);
    unaryOperationObject.put("sqr", Operation.POW);
    unaryOperationObject.put("√", Operation.SQRT);
    unaryOperationObject.put("1/", Operation.REVERSE);

    operationSymbols.put(Operation.ADD, "+");
    operationSymbols.put(Operation.DIVIDE, "÷");
    operationSymbols.put(Operation.MULTIPLY, "×");
    operationSymbols.put(Operation.SUBTRACT, "-");
    operationSymbols.put(Operation.SQRT, "√");
    operationSymbols.put(Operation.NEGATE, "negate");
    operationSymbols.put(Operation.REVERSE, "1/");
    operationSymbols.put(Operation.POW, "sqr");
  }

  public InputService() {
    calc = new CalculatorModel();
  }

  public void setMemoryRecall(boolean memoryRecall) {
    isMemoryRecall = memoryRecall;
  }

  public void setBackspacePossible(boolean backspacePossible) {
    isBackspacePossible = backspacePossible;
  }

  /**
   * @param event   event info of button, that have been pressed
   * @param display numbers in textArea
   * @return new numbers in textArea
   */
  public String enterNumberOrComma(ActionEvent event, String display) throws ParseException, OverflowException {
    Button btn = (Button) event.getSource();
    String value = btn.getText();

    boolean isEditible = NumberFormatter.isTooBigToInput(display + value)
        && (calc.getCalculatorState() == CalculatorState.LEFT || calc.getCalculatorState() == CalculatorState.RIGHT);
    if (isEditible) {
      return display;
    }


    if (value.equals(".")) {
      if (calc.getCalculatorState() == CalculatorState.AFTER) {
        calc.setCalculatorState(CalculatorState.LEFT);
        display = "0";
      } else if (calc.getCalculatorState() == CalculatorState.TRANSIENT) {
        calc.setCalculatorState(CalculatorState.RIGHT);
        display = "0";
      }

      if (!display.contains(".")) {
        display += ".";
      }

      return display;
    }

    if (calc.getCalculatorState() == CalculatorState.AFTER) {
      calc.setCalculatorState(CalculatorState.LEFT);
      return NumberFormatter.format(new BigDecimal(value));
    } else if (calc.getCalculatorState() == CalculatorState.TRANSIENT) {
      calc.setCalculatorState(CalculatorState.RIGHT);
      return NumberFormatter.format(new BigDecimal(value));
    }

    return NumberFormatter.format(parse(removeGroupSeparator(display + value)));
  }

  /**
   * 1
   * Set calculatorState to CalculatorState.AFTER to type new numbers like textArea is clear
   */
  public void clearDisplay() {
    calc.setCalculatorState(CalculatorState.AFTER);
    calc.setOperation(null);
    calc.setLeftOperand(null);
    calc.setRightOperand(null);
    isMemoryRecall = false;
    isBackspacePossible = true;
  }

  /**
   * Typing the binary operation
   *
   * @param event   event info of button, that have been pressed
   * @param display numbers in textArea
   * @return result of operation if two operands exists or display if not
   */
  public String enterOperation(ActionEvent event, String display) throws ParseException, CalculationException {
    Button btn = (Button) event.getSource();

    if (isMemoryRecall) {
      isMemoryRecall = false;
      return NumberFormatter.format(calc.doCalculate(formatOperation(btn.getText()), calc.getMemory()));
    }

    return NumberFormatter.format(calc.doCalculate(formatOperation(btn.getText()), parse(display)));
  }

  /**
   * Typing equal
   *
   * @param right right operand typed in calc
   * @return result of binary operation
   */
  public String enterEqual(String right) throws ParseException, CalculationException {
    if (isMemoryRecall) {
      isMemoryRecall = false;

      return NumberFormatter.format(calc.doCalculate(calc.getMemory()));
    }

    isBackspacePossible = false;

    return NumberFormatter.format(calc.doCalculate(parse(right)));
  }

  /**
   * Typing the unary operation
   *
   * @param event   event info of button, that have been pressed
   * @param display numbers in textArea
   * @return result of operation
   */
  public String unaryOp(ActionEvent event, String display) throws ParseException, CalculationException {
    Button btn = (Button) event.getSource();

    if (isMemoryRecall) {
      isMemoryRecall = false;
      return NumberFormatter.format(calc.doCalculate(formatOperation(btn.getText()), calc.getMemory()));
    }

    isBackspacePossible = false;

    if (calc.getCalculatorState() == CalculatorState.AFTER) {
      return NumberFormatter.format(calc.doCalculate(formatOperation(btn.getText())));
    }


    BigDecimal res = calc.doCalculate(formatOperation(btn.getText()), parse(display));
    return NumberFormatter.format(res);
  }

  /**
   * Typing percent
   *
   * @param right right operand typed in calc
   * @return result of percent operation
   */
  public String percentOp(String right) throws ParseException, CalculationException {
    Operation op;
    if (calc.getOperation() == Operation.ADD || calc.getOperation() == Operation.SUBTRACT) {
      op = Operation.PERCENT_ADD_SUBTRACT;
    } else if (calc.getOperation() == Operation.MULTIPLY || calc.getOperation() == Operation.DIVIDE) {
      op = Operation.PERCENT_MUL_DIVIDE;
    } else {
      return "0";
    }

    if (isMemoryRecall) {
      isMemoryRecall = false;
      return NumberFormatter.format(calc.doCalculate(op, calc.getMemory()));
    }

    isBackspacePossible = false;

    return NumberFormatter.format(calc.doCalculate(op, parse(right)));
  }

  /**
   * Save number from display to memory
   *
   * @param display number in textArea
   */
  public void saveToMemory(String display) {
    calc.memorySave(new BigDecimal(display.replaceAll(",", "")));
  }

  /**
   * Call last element from memory
   *
   * @return string with last element of memory
   */
  public String recallFromMemory() throws OverflowException {
    if (calc.getCalculatorState() == CalculatorState.LEFT) {
      calc.setCalculatorState(CalculatorState.TRANSIENT);
    } else if (calc.getCalculatorState() == CalculatorState.TRANSIENT) {
      calc.setCalculatorState(CalculatorState.RIGHT);
    } else if (calc.getCalculatorState() == CalculatorState.AFTER) {
      calc.setCalculatorState(CalculatorState.LEFT);
    }

    isMemoryRecall = true;

    return NumberFormatter.format(calc.recallMemory());
  }

  /**
   * Call clear memory in model
   */
  public void clearMemory() {
    calc.clearMemory();
  }

  /**
   * Call memory add in model and give number from display to method
   *
   * @param display text in display of calculator
   */
  public void addToMemory(String display) throws ParseException, OverflowException {
    calc.memoryAdd(parse(display));
  }

  /**
   * Call substract method from model and give them number from display
   *
   * @param display text in display of calculator
   */
  public void subToMemory(String display) throws ParseException, OverflowException {
    calc.memorySub(parse(display));
  }

  /**
   * Check, that user can use backspace
   *
   * @return true if can(calculatorState is not AFTER), else false
   */
  public boolean isBackspaceAvailable() {
    return calc.getCalculatorState() != CalculatorState.AFTER && isBackspacePossible;
  }

  /**
   * Create string to formula label. History of operation, that user do before pressing "="
   *
   * @param event      Event that call method, who call this method. This param is using to get text of button, that call them
   * @param oldFormula old string, that we create before
   * @param display    text from display of calculator
   * @return new formula or old formula if there is nothing to change
   */
  public String highFormula(ActionEvent event, String oldFormula, String display) {
    Button btn = (Button) event.getSource();

    if (display.endsWith(".")) {
      display = display.substring(0, display.length() - 1);
    }

    if (calc.getCalculatorState() == CalculatorState.TRANSIENT && oldFormula.isBlank()) {
      calc.setCalculatorState(CalculatorState.LEFT);
    }


    if (calc.getCalculatorState() == CalculatorState.TRANSIENT) {
      return transientHighFormula(btn, oldFormula, display);
    } else if (calc.getCalculatorState() == CalculatorState.RIGHT) {
      return rightHighFormula(btn, oldFormula, display);
    } else {
      return leftOrAfterHighFormula(btn, oldFormula, display);
    }
  }

  /**
   * Check, that memory is empty
   *
   * @return true if is, false else
   */
  public boolean isMemoryEmpty() {
    return calc.getMemory() == null;
  }

  private String transientHighFormula(Button btn, String oldFormula, String display) {
    if (binaryOperationObject.containsKey(btn.getText())) {
      if (oldFormula.endsWith(")")) {
        return oldFormula + " " + operationSymbols.get(binaryOperationObject.get(btn.getText()));
      }
      return oldFormula.substring(0, oldFormula.length() - 1) + operationSymbols.get(binaryOperationObject.get(btn.getText()));
    } else if (unaryOperationObject.containsKey(btn.getText())) {
      if (oldFormula.endsWith(")")) {
        String str = unaryOpSubStringFinder(oldFormula);
        return oldFormula.substring(0, oldFormula.indexOf(str)) + operationSymbols.get(unaryOperationObject.get(btn.getText())) + "( "
            + str + " )";
      }
      if (unaryOperationObject.get(btn.getText()) == Operation.NEGATE && calc.getOperation() == null) {
        return oldFormula;
      }
      if (!oldFormula.isBlank()) {
        oldFormula += " ";
      }
      return oldFormula + operationSymbols.get(unaryOperationObject.get(btn.getText())) + "( " + display.replaceAll(",", "") + " )";
    } else if (btn.getText().equals("\uE94C")) { //%
      if (oldFormula.isBlank()) {
        return "";
      }
      return oldFormula + " " + display.replaceAll(",", "");
    }
    return "";
  }

  private String leftOrAfterHighFormula(Button btn, String oldFormula, String display) {
    if (binaryOperationObject.containsKey(btn.getText())) {
      if (oldFormula.endsWith(")")) {
        return oldFormula + " " + operationSymbols.get(binaryOperationObject.get(btn.getText()));
      }
      return display.replaceAll(",", "") + " " + operationSymbols.get(binaryOperationObject.get(btn.getText()));
    } else if (unaryOperationObject.containsKey(btn.getText()) && !btn.getText().equals("\uE94D")) {
      if (oldFormula.endsWith(")")) {
        return operationSymbols.get(unaryOperationObject.get(btn.getText())) + "( " + oldFormula + " )";
      }

      return operationSymbols.get(unaryOperationObject.get(btn.getText())) + "( " + display.replaceAll(",", "") + " )";
    } else {
      return "";
    }
  }

  private String rightHighFormula(Button btn, String oldFormula, String display) {
    if (binaryOperationObject.containsKey(btn.getText())) {
      if (Character.isDigit(oldFormula.charAt(oldFormula.length() - 1)) || oldFormula.endsWith(")")) {
        return oldFormula + " " + operationSymbols.get(binaryOperationObject.get(btn.getText()));
      } else {
        return oldFormula + " " + display.replaceAll(",", "") + " "
            + operationSymbols.get(binaryOperationObject.get(btn.getText()));
      }
    } else if (unaryOperationObject.containsKey(btn.getText())) {
      if (oldFormula.endsWith(")")) {
        String str = unaryOpSubStringFinder(oldFormula);
        return oldFormula.substring(0, oldFormula.indexOf(str)) +
            operationSymbols.get(unaryOperationObject.get(btn.getText())) + "(" + str + " )";
      }
      return oldFormula + " " + operationSymbols.get(unaryOperationObject.get(btn.getText())) + "( "
          + display.replaceAll(",", "") + " )";
    } else if (btn.getText().equals("\uE94C")) { //%
      if (oldFormula.isBlank() || !containsBinaryOperator(oldFormula)) {
        return "";
      } else if (oldFormula.endsWith(")")) {
        int index = indexOfLastBinary(oldFormula);
        oldFormula = oldFormula.substring(0, index + 1);
      }

      return oldFormula + " " + display.replaceAll(",", "");
    }

    return "";
  }

  private String unaryOpSubStringFinder(String formula) {
    Pattern p;
    List<Integer> index = new ArrayList<>();
    List<String> list = new ArrayList<>(unaryOperationObject.keySet());
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

  private Operation formatOperation(String op) {
    Operation formatted = binaryOperationObject.get(op);
    if (formatted != null) {
      return formatted;
    }

    formatted = unaryOperationObject.get(op);
    return formatted;
  }

  private int indexOfLastBinary(String formula) {
    int indexOfLastPlus = formula.lastIndexOf("+"),
        indexOfLastMinus = formula.lastIndexOf("-"),
        indexOfLastMultiply = formula.lastIndexOf("×"),
        indexOfLastDivide = formula.lastIndexOf("÷");

    return max(max(indexOfLastDivide, indexOfLastMinus), max(indexOfLastMultiply, indexOfLastPlus));
  }

  private boolean containsBinaryOperator(String formula) {
    return formula.contains("+") || formula.contains("-") || formula.contains("×") || formula.contains("÷");
  }

//
}