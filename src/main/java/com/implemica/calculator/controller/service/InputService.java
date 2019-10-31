package com.implemica.calculator.controller.service;

import com.implemica.calculator.controller.util.NumberFormatter;
import com.implemica.calculator.model.CalculatorModel;
import com.implemica.calculator.model.util.CalcState;
import com.implemica.calculator.model.util.Operation;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.implemica.calculator.controller.util.NumberFormatter.removeGroupSeparator;
import static com.implemica.calculator.controller.util.NumberFormatter.parse;

/**
 * Service class, that is bridge between {@link com.implemica.calculator.controller.RootController} and
 * {@link CalculatorModel}. Calling from {@link com.implemica.calculator.controller.RootController} and
 * transmit query from controller to model.
 *
 * @author ozreat
 * @see com.implemica.calculator.controller.RootController
 * @see CalculatorModel
 * @see Operation
 * @see CalcState
 * @see NumberFormatter
 */
public class InputService {
  /**
   * Calculator model to do calculations
   */
  private CalculatorModel calc;

  /**
   * Flag, that confirm status of number on display
   */
  @Getter
  @Setter
  private boolean isMemoryRecall = false;

  @Getter
  @Setter
  private boolean isBackspacePossible = true;

  /**
   * Map of unicode symbols for binary operations
   */
  private final static Map<String, Operation> binaryOperationUnicode = new HashMap<>();

  /**
   * Map of unicode symbols for unary operations
   */
  private final static Map<String, Operation> unaryOperationUnicode = new HashMap<>();

  static {
    binaryOperationUnicode.put("\uE948", Operation.ADD);
    binaryOperationUnicode.put("\uE949", Operation.SUBTRACT);
    binaryOperationUnicode.put("\uE947", Operation.MULTIPLY);
    binaryOperationUnicode.put("\uE94A", Operation.DIVIDE);
    binaryOperationUnicode.put("/", Operation.DIVIDE);
    binaryOperationUnicode.put("*", Operation.MULTIPLY);
    binaryOperationUnicode.put("+", Operation.ADD);
    binaryOperationUnicode.put("-", Operation.SUBTRACT);
    binaryOperationUnicode.put("×", Operation.MULTIPLY);
    binaryOperationUnicode.put("÷", Operation.DIVIDE);

    unaryOperationUnicode.put("\uE94B", Operation.SQRT);
    unaryOperationUnicode.put("\uE94D", Operation.NEGATE);
    unaryOperationUnicode.put("⅟\uD835\uDC65", Operation.REVERSE);//⅟𝑥
    unaryOperationUnicode.put("\uD835\uDC65²", Operation.POW);//𝑥²
    unaryOperationUnicode.put("negate", Operation.NEGATE);
    unaryOperationUnicode.put("sqr", Operation.POW);
    unaryOperationUnicode.put("√", Operation.SQRT);
    unaryOperationUnicode.put("1/", Operation.REVERSE);
  }

  public InputService() {
    calc = new CalculatorModel();
  }

  /**
   * @param event   event info of button, that have been pressed
   * @param display numbers in textArea
   * @return new numbers in textArea
   */
  public String enterNumberOrComma(ActionEvent event, String display) throws ParseException {
    Button btn = (Button) event.getSource();
    String value = btn.getText();

    boolean isEditible = NumberFormatter.isTooBigToInput(display + value)
        && (calc.getCalcState() == CalcState.LEFT || calc.getCalcState() == CalcState.RIGHT);
    if (isEditible) {
      return display;
    }


    if (value.equals(".")) {
      if (calc.getCalcState() == CalcState.AFTER) {
        calc.setCalcState(CalcState.LEFT);
        display = "0";
      } else if (calc.getCalcState() == CalcState.TRANSIENT) {
        calc.setCalcState(CalcState.RIGHT);
        display = "0";
      }

      if (!display.contains(".")) {
        display += ".";
      }

      return display;
    }

    if (calc.getCalcState() == CalcState.AFTER) {
      calc.setCalcState(CalcState.LEFT);
      return NumberFormatter.format(new BigDecimal(value));
    } else if (calc.getCalcState() == CalcState.TRANSIENT) {
      calc.setCalcState(CalcState.RIGHT);
      return NumberFormatter.format(new BigDecimal(value));
    }

    return NumberFormatter.format(parse(removeGroupSeparator(display + value)));
  }

  /**
   * 1
   * Set calcState to CalcState.AFTER to type new numbers like textArea is clear
   */
  public void clearDisplay() {
    calc.setCalcState(CalcState.AFTER);
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
  public String enterOperation(ActionEvent event, String display) throws ArithmeticException, ParseException {
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
  public String enterEqual(String right) throws ArithmeticException, ParseException {
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
  public String unaryOp(ActionEvent event, String display) throws ArithmeticException, ParseException {
    Button btn = (Button) event.getSource();

    if (isMemoryRecall) {
      isMemoryRecall = false;
      return NumberFormatter.format(calc.doCalculate(formatOperation(btn.getText()), calc.getMemory()));
    }

    isBackspacePossible = false;

    if (calc.getCalcState() == CalcState.AFTER) {
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
  public String percentOp(String right) throws ParseException {
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
  public String recallFromMemory() {
    if (calc.getCalcState() == CalcState.LEFT) {
      calc.setCalcState(CalcState.TRANSIENT);
    } else if (calc.getCalcState() == CalcState.TRANSIENT) {
      calc.setCalcState(CalcState.RIGHT);
    } else if (calc.getCalcState() == CalcState.AFTER) {
      calc.setCalcState(CalcState.LEFT);
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
  public void addToMemory(String display) throws ParseException {
    calc.memoryAdd(parse(display));
  }

  /**
   * Call substract method from model and give them number from display
   *
   * @param display text in display of calculator
   */
  public void subToMemory(String display) throws ParseException {
    calc.memorySub(parse(display));
  }

  /**
   * Check, that user can use backspace
   *
   * @return true if can(calcState is not AFTER), else false
   */
  public boolean isBackspaceAvailable() {
    return calc.getCalcState() != CalcState.AFTER && isBackspacePossible;
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

    if (calc.getCalcState() == CalcState.TRANSIENT && oldFormula.isBlank()) {
      calc.setCalcState(CalcState.LEFT);
    }


    if (calc.getCalcState() == CalcState.TRANSIENT) {
      return transientHighFormula(btn, oldFormula, display);
    } else if (calc.getCalcState() == CalcState.RIGHT) {
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
    if (binaryOperationUnicode.containsKey(btn.getText())) {
      if (oldFormula.endsWith(")")) {
        return oldFormula + " " + binaryOperationUnicode.get(btn.getText()).getSymbol();
      }
      return oldFormula.substring(0, oldFormula.length() - 1) + binaryOperationUnicode.get(btn.getText()).getSymbol();
    } else if (unaryOperationUnicode.containsKey(btn.getText())) {
      if (oldFormula.endsWith(")")) {
        String str = unaryOpSubStringFinder(oldFormula);
        return oldFormula.substring(0, oldFormula.indexOf(str)) + unaryOperationUnicode.get(btn.getText()).getSymbol() + "( "
            + str + " )";
      }
      if (unaryOperationUnicode.get(btn.getText()) == Operation.NEGATE && calc.getOperation() == null) {
        return oldFormula;
      }
      if (!oldFormula.isBlank()) {
        oldFormula += " ";
      }
      return oldFormula + unaryOperationUnicode.get(btn.getText()).getSymbol() + "( " + display.replaceAll(",", "") + " )";
    } else if (btn.getText().equals("\uE94C")) { //%
      if (oldFormula.isBlank()) {
        return "";
      }
      return oldFormula + " " + display.replaceAll(",", "");
    }
    return "";
  }

  private String leftOrAfterHighFormula(Button btn, String oldFormula, String display) {
    if (binaryOperationUnicode.containsKey(btn.getText())) {
      if (oldFormula.endsWith(")")) {
        return oldFormula + " " + binaryOperationUnicode.get(btn.getText()).getSymbol();
      }
      return display.replaceAll(",", "") + " " + binaryOperationUnicode.get(btn.getText()).getSymbol();
    } else if (unaryOperationUnicode.containsKey(btn.getText()) && !btn.getText().equals("\uE94D")) {
      if (oldFormula.endsWith(")")) {
        return unaryOperationUnicode.get(btn.getText()).getSymbol() + "( " + oldFormula + " )";
      }

      return unaryOperationUnicode.get(btn.getText()).getSymbol() + "( " + display.replaceAll(",", "") + " )";
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
      return oldFormula + " " + display.replaceAll(",", "") + " "
          + binaryOperationUnicode.get(btn.getText()).getSymbol();
    } else if (unaryOperationUnicode.containsKey(btn.getText())) {
      if (oldFormula.endsWith(")")) {
        String str = unaryOpSubStringFinder(oldFormula);
        return oldFormula.substring(0, oldFormula.indexOf(str)) + unaryOperationUnicode.get(btn.getText()).getSymbol()
            + "(" + str + " )";
      }
      return oldFormula + " " + unaryOperationUnicode.get(btn.getText()).getSymbol() + "( "
          + display.replaceAll(",", "") + " )";
    } else if (btn.getText().equals("\uE94C")) { //%
      if (oldFormula.isBlank()) {
        return "";
      }
      return oldFormula + " " + display.replaceAll(",", "");
    }

    return "";
  }

  private String unaryOpSubStringFinder(String formula) {
    Pattern p;
    List<Integer> index = new ArrayList<>();
    List<String> list = new ArrayList<>(unaryOperationUnicode.keySet());
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
    Operation formatted = binaryOperationUnicode.get(op);
    if (formatted != null) {
      return formatted;
    }

    formatted = unaryOperationUnicode.get(op);
    return formatted;
  }

}