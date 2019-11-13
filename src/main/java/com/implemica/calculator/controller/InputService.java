package com.implemica.calculator.controller;

import com.implemica.calculator.model.*;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.implemica.calculator.controller.NumberFormatter.*;
import static java.lang.Math.max;

/**
 * Service class, that is bridge between {@link com.implemica.calculator.controller.RootController} and
 * {@link CalculatorModel}. Calling from {@link com.implemica.calculator.controller.RootController} and
 * transmit query from controller to model.
 *
 * @author ozreat
 * @see com.implemica.calculator.controller.RootController
 * @see CalculatorModel
 * @see ArithmeticOperation
 * @see CalculatorState
 * @see NumberFormatter
 */
class InputService {
  /**
   * Map of binary {@link ArithmeticOperation} to get ArithmeticOperation from string
   */
  private final static Map<String, ArithmeticOperation> binaryOperationObject = new HashMap<>();

  /**
   * Map of unary {@link ArithmeticOperation} to get ArithmeticOperation from string
   */
  private final static Map<String, ArithmeticOperation> unaryOperationObject = new HashMap<>();

  /**
   * Map to get operation symbol from {@link ArithmeticOperation}
   */
  private final static Map<ArithmeticOperation, String> operationSymbols = new HashMap<>();

  /**
   * Default display number
   */
  static final String DEFAULT_DISPLAY_NUMBER = "0";

  /**
   * Bracket, that close unary operation
   */
  public static final String END_OF_UNARY_OPERATION = ")";
  /**
   * Bracket, that open unary operation
   */
  public static final String BEGIN_OF_UNARY_OPERATION = "(";

  /**
   * Plus symbol
   */
  public static final String PLUS_SYMBOL = "+";
  /**
   * Multiply symbol
   */
  public static final String MULTIPLY_SYMBOL = "×";
  /**
   * Divide symbol
   */
  public static final String DIVIDE_SYMBOL = "÷";
  /**
   * Minus symbol
   */
  public static final String MINUS_SYMBOL = "-";

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

  static {
    binaryOperationObject.put("\uE948", ArithmeticOperation.ADD);
    binaryOperationObject.put("\uE949", ArithmeticOperation.SUBTRACT);
    binaryOperationObject.put("\uE947", ArithmeticOperation.MULTIPLY);
    binaryOperationObject.put("\uE94A", ArithmeticOperation.DIVIDE);
    binaryOperationObject.put("/", ArithmeticOperation.DIVIDE);
    binaryOperationObject.put("*", ArithmeticOperation.MULTIPLY);
    binaryOperationObject.put(PLUS_SYMBOL, ArithmeticOperation.ADD);
    binaryOperationObject.put(MINUS_SYMBOL, ArithmeticOperation.SUBTRACT);
    binaryOperationObject.put(MULTIPLY_SYMBOL, ArithmeticOperation.MULTIPLY);
    binaryOperationObject.put(DIVIDE_SYMBOL, ArithmeticOperation.DIVIDE);

    unaryOperationObject.put("\uE94B", ArithmeticOperation.SQRT);
    unaryOperationObject.put("\uE94D", ArithmeticOperation.NEGATE);
    unaryOperationObject.put("⅟\uD835\uDC65", ArithmeticOperation.REVERSE);//⅟𝑥
    unaryOperationObject.put("\uD835\uDC65²", ArithmeticOperation.POW);//𝑥²
    unaryOperationObject.put("negate", ArithmeticOperation.NEGATE);
    unaryOperationObject.put("sqr", ArithmeticOperation.POW);
    unaryOperationObject.put("√", ArithmeticOperation.SQRT);
    unaryOperationObject.put("1/", ArithmeticOperation.REVERSE);

    operationSymbols.put(ArithmeticOperation.ADD, PLUS_SYMBOL);
    operationSymbols.put(ArithmeticOperation.DIVIDE, DIVIDE_SYMBOL);
    operationSymbols.put(ArithmeticOperation.MULTIPLY, MULTIPLY_SYMBOL);
    operationSymbols.put(ArithmeticOperation.SUBTRACT, MINUS_SYMBOL);
    operationSymbols.put(ArithmeticOperation.SQRT, "√");
    operationSymbols.put(ArithmeticOperation.NEGATE, "negate");
    operationSymbols.put(ArithmeticOperation.REVERSE, "1/");
    operationSymbols.put(ArithmeticOperation.POW, "sqr");
  }

  /**
   * Default constructor for {@code InputService} class
   */
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
  public String enterNumberOrComma(ActionEvent event, String display) throws ParseException, CalculatorException {
    Button btn = (Button) event.getSource();
    String buttonText = btn.getText();
    CalculatorState state = calc.getCalculatorState();

    String result;
    boolean isTooBig = isTooBigToInput(display + buttonText);
    boolean isStateLeftOrRight = state == CalculatorState.LEFT || state == CalculatorState.RIGHT;
    if (isTooBig && isStateLeftOrRight) {
      result = display;
    } else if (buttonText.equals(String.valueOf(DECIMAL_SEPARATOR))) {
      if (state == CalculatorState.AFTER) {
        calc.setCalculatorState(CalculatorState.LEFT);
        display = DEFAULT_DISPLAY_NUMBER;
      } else if (state == CalculatorState.TRANSIENT) {
        calc.setCalculatorState(CalculatorState.RIGHT);
        display = DEFAULT_DISPLAY_NUMBER;
      }

      if (!display.contains(String.valueOf(DECIMAL_SEPARATOR))) {
        display += DECIMAL_SEPARATOR;
      }

      result = display;
    } else {
      String valueToParse = buttonText;
      if (state == CalculatorState.AFTER) {
        state = CalculatorState.LEFT;
      } else if (state == CalculatorState.TRANSIENT) {
        state = CalculatorState.RIGHT;
      } else {
        valueToParse = removeGroupSeparator(display + buttonText);
      }

      result = format(parse(valueToParse));
      calc.setCalculatorState(state);
    }

    return result;
  }

  /**
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
  public String enterOperation(ActionEvent event, String display) throws ParseException, CalculatorException {
    ArithmeticOperation operation = getArithmeticOperationFromEvent(event);

    BigDecimal result;
    if (isMemoryRecall) {
      isMemoryRecall = false;
      result = calc.calculate(operation, calc.getMemory());
    } else {
      result = calc.calculate(operation, parse(display));
    }

    return format(result);
  }


  /**
   * Typing equal
   *
   * @param right right operand typed in calc
   * @return result of binary operation
   */
  public String enterEqual(String right) throws ParseException, CalculatorException {
    BigDecimal result;
    if (isMemoryRecall) {
      isMemoryRecall = false;
      result = calc.calculate(calc.getMemory());
    } else {
      result = calc.calculate(parse(right));
    }

    isBackspacePossible = false;

    return format(result);
  }

  /**
   * Typing the unary operation
   *
   * @param event   event info of button, that have been pressed
   * @param display numbers in textArea
   * @return result of operation
   */
  public String unaryOp(ActionEvent event, String display) throws ParseException, CalculatorException {
    ArithmeticOperation operation = getArithmeticOperationFromEvent(event);

    BigDecimal result;
    if (isMemoryRecall) {
      isMemoryRecall = false;
      result = calc.calculate(operation, calc.getMemory());
    } else {
      if (calc.getCalculatorState() == CalculatorState.AFTER) {
        result = calc.calculate(operation);
      } else {
        result = calc.calculate(operation, parse(display));
      }
    }

    isBackspacePossible = false;

    return format(result);
  }

  /**
   * Typing percent
   *
   * @param right right operand typed in calc
   * @return result of percent operation
   */
  public String percentOp(String right) throws ParseException, CalculatorException {
    ArithmeticOperation currentOperation = calc.getOperation();

    BigDecimal number;
    if (isMemoryRecall) {
      isMemoryRecall = false;
      number = calc.getMemory();
    } else {
      number = parse(right);
    }

    ArithmeticOperation percentOperation;
    BigDecimal result;
    if (currentOperation == null) {
      result = BigDecimal.ZERO;
    } else {
      if (currentOperation == ArithmeticOperation.ADD || currentOperation == ArithmeticOperation.SUBTRACT) {
        percentOperation = ArithmeticOperation.PERCENT_ADD_SUBTRACT;
      } else if (currentOperation == ArithmeticOperation.MULTIPLY || currentOperation == ArithmeticOperation.DIVIDE) {
        percentOperation = ArithmeticOperation.PERCENT_MUL_DIVIDE;
      } else {
        throw new IllegalStateException("Unsupported operation: " + currentOperation);
      }
      result = calc.calculate(percentOperation, number);
    }


    isBackspacePossible = false;

    return format(result);
  }

  /**
   * Save number from display to memory
   *
   * @param display number in textArea
   */
  public void memorySave(String display) throws ParseException {
    calc.memorySave(parse(display));
  }

  /**
   * Call last element from memory
   *
   * @return string with last element of memory
   */
  public String memoryRecall() throws CalculatorException {
    CalculatorState currentState = calc.getCalculatorState();

    if (currentState == CalculatorState.LEFT) {
      currentState = CalculatorState.TRANSIENT;
    } else if (currentState == CalculatorState.TRANSIENT) {
      currentState = CalculatorState.RIGHT;
    } else if (currentState == CalculatorState.AFTER) {
      currentState = CalculatorState.LEFT;
    }

    calc.setCalculatorState(currentState);

    isMemoryRecall = true;

    return format(calc.memoryRecall());
  }

  /**
   * Call clear memory in model
   */
  public void memoryClear() {
    calc.memoryClear();
  }

  /**
   * Call memory add in model and give number from display to method
   *
   * @param display text in display of calculator
   */
  public void memoryAdd(String display) throws ParseException, CalculatorException {
    calc.memoryAdd(parse(display));
  }

  /**
   * Call substract method from model and give them number from display
   *
   * @param display text in display of calculator
   */
  public void memorySub(String display) throws ParseException, CalculatorException {
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
    if (display.endsWith(String.valueOf(DECIMAL_SEPARATOR))) {
      display = display.substring(0, display.length() - 1);
    }

    CalculatorState state = calc.getCalculatorState();
    if (state == CalculatorState.TRANSIENT && oldFormula.isBlank()) {
      state = CalculatorState.LEFT;
      calc.setCalculatorState(state);
    }

    String result;
    if (state == CalculatorState.TRANSIENT) {
      result = transientHighFormula(event, oldFormula, display);
    } else if (state == CalculatorState.RIGHT) {
      result = rightHighFormula(event, oldFormula, display);
    } else if (state == CalculatorState.LEFT || state == CalculatorState.AFTER) {
      result = leftOrAfterHighFormula(event, oldFormula, display);
    } else {
      throw new IllegalStateException("Unsupported state: " + state);
    }

    return result;
  }

  /**
   * Check, that memory is empty
   *
   * @return true if is, false else
   */
  public boolean isMemoryEmpty() {
    return calc.getMemory() == null;
  }

  private String transientHighFormula(ActionEvent event, String oldFormula, String display) {
    ArithmeticOperation operation = getArithmeticOperationFromEvent(event);
    ArithmeticOperationType type = operation.getType();
    String operationSymbol = operationSymbols.get(operation);
    boolean isEndsWithBracket = oldFormula.endsWith(END_OF_UNARY_OPERATION);
    display = removeGroupSeparator(display);

    String result;
    if (type == ArithmeticOperationType.BINARY) {
      if (isEndsWithBracket) { // if there brackets, than we already have second operand at formula, so just add symbol
        result = oldFormula + " " + operationSymbol;
      } else {
        result = oldFormula.substring(0, oldFormula.length() - 1) + operationSymbol; // changing previous symbol of binary operation
      }
    } else if (type == ArithmeticOperationType.UNARY) {
      if (isEndsWithBracket) { // wrapping one unary operation in another
        String str = unaryOpSubStringFinder(oldFormula);
        result = oldFormula.substring(0, oldFormula.indexOf(str)) + operationSymbol + BEGIN_OF_UNARY_OPERATION + " "
            + str + " " + END_OF_UNARY_OPERATION;
      } else {
        if (operation == ArithmeticOperation.NEGATE && calc.getOperation() == null) { // if negate or no op, we dont change history
          result = oldFormula;
        } else {
          if (!oldFormula.isBlank()) {
            oldFormula += " ";
          }
          result = oldFormula + operationSymbol + BEGIN_OF_UNARY_OPERATION + " " + display + " " + END_OF_UNARY_OPERATION;
        }
      }
    } else if (type == ArithmeticOperationType.PERCENT) {
      if (oldFormula.isBlank()) { //if we don't have history, that means, that we don't have what to calc with percent
        result = "";
      } else {
        result = oldFormula + " " + display;
      }
    } else {
      throw new IllegalStateException("Unsupported type of operation: " + type);
    }
    return result;
  }

  private String leftOrAfterHighFormula(ActionEvent event, String oldFormula, String display) {
    ArithmeticOperation operation = getArithmeticOperationFromEvent(event);
    ArithmeticOperationType type = operation.getType();
    String operationSymbol = operationSymbols.get(operation);
    boolean isEndsWithBracket = oldFormula.endsWith(END_OF_UNARY_OPERATION);
    display = removeGroupSeparator(display);

    String result;
    if (type == ArithmeticOperationType.BINARY) {
      if (isEndsWithBracket) {
        // if there brackets, than we already have second operand at formula, so just add symbol
        result = oldFormula + " " + operationSymbol;
      } else {
        result = display + " " + operationSymbol;
      }
    } else if (type == ArithmeticOperationType.UNARY && operation != ArithmeticOperation.NEGATE) {
      if (isEndsWithBracket) { // wrapping one unary operation in another
        result = operationSymbol + BEGIN_OF_UNARY_OPERATION + " " + oldFormula + " " + END_OF_UNARY_OPERATION;
      } else {
        result = operationSymbol + BEGIN_OF_UNARY_OPERATION + " " + display + " " + END_OF_UNARY_OPERATION;
      }
    } else if (operation.getType() == ArithmeticOperationType.PERCENT || operation == ArithmeticOperation.NEGATE) {
      // if negate or no percent, formula have to be blank
      result = "";
    } else {
      throw new IllegalStateException("Unsupported type in this state: " + type);
    }

    return result;
  }

  private String rightHighFormula(ActionEvent event, String oldFormula, String display) {
    ArithmeticOperation operation = getArithmeticOperationFromEvent(event);
    ArithmeticOperationType type = operation.getType();
    String operationSymbol = operationSymbols.get(operation);
    boolean isEndsWithBracket = oldFormula.endsWith(END_OF_UNARY_OPERATION);
    display = removeGroupSeparator(display);

    String result;
    if (type == ArithmeticOperationType.BINARY) {
      if (Character.isDigit(oldFormula.charAt(oldFormula.length() - 1)) || isEndsWithBracket) {
        // if there brackets or digit, than we already have an operand at formula, so just add symbol
        result = oldFormula + " " + operationSymbol;
      } else {
        result = oldFormula + " " + display + " " + operationSymbol;
      }
    } else if (type == ArithmeticOperationType.UNARY) {
      if (isEndsWithBracket) {
        // wrapping one unary operation in another
        String str = unaryOpSubStringFinder(oldFormula);
        result = oldFormula.substring(0, oldFormula.indexOf(str)) + operationSymbol + BEGIN_OF_UNARY_OPERATION + str +
            " " + END_OF_UNARY_OPERATION;
      } else {
        result = oldFormula + " " + operationSymbol + BEGIN_OF_UNARY_OPERATION + " " + display + " " + END_OF_UNARY_OPERATION;
      }
    } else if (type == ArithmeticOperationType.PERCENT) {
      if (oldFormula.isBlank() || !containsBinaryOperator(oldFormula)) {
        // if we don't do binary op before, we don't have what to do
        result = "";
      } else {
        if (isEndsWithBracket) { //changing all unary on our number
          int index = indexOfLastBinary(oldFormula);
          oldFormula = oldFormula.substring(0, index + 1);
        }

        result = oldFormula + " " + display;
      }
    } else {
      throw new IllegalStateException("Unsupported type in this state: " + type);
    }

    return result;
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

  private ArithmeticOperation formatOperation(String op) {
    ArithmeticOperation formatted;

    if (binaryOperationObject.containsKey(op)) {
      formatted = binaryOperationObject.get(op);
    } else {
      formatted = unaryOperationObject.getOrDefault(op, ArithmeticOperation.PERCENT_ADD_SUBTRACT);
    }

    return formatted;
  }

  private int indexOfLastBinary(String formula) {
    int indexOfLastPlus = formula.lastIndexOf(PLUS_SYMBOL);
    int indexOfLastMinus = formula.lastIndexOf(MINUS_SYMBOL);
    int indexOfLastMultiply = formula.lastIndexOf(MULTIPLY_SYMBOL);
    int indexOfLastDivide = formula.lastIndexOf(DIVIDE_SYMBOL);

    return max(max(indexOfLastDivide, indexOfLastMinus), max(indexOfLastMultiply, indexOfLastPlus));
  }

  private boolean containsBinaryOperator(String formula) {
    return formula.contains(PLUS_SYMBOL) || formula.contains(MINUS_SYMBOL) || formula.contains(MULTIPLY_SYMBOL)
        || formula.contains(DIVIDE_SYMBOL);
  }

  private ArithmeticOperation getArithmeticOperationFromEvent(ActionEvent event) {
    Button btn = (Button) event.getSource();
    return formatOperation(btn.getText());
  }
}