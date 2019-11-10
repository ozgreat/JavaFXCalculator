package com.implemica.calculator.controller;

import com.implemica.calculator.model.CalculatorException;
import com.implemica.calculator.model.CalculatorExceptionType;
import com.implemica.calculator.model.DigitBacspace;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.implemica.calculator.controller.InputService.DEFAULT_DISPLAY_NUMBER;
import static com.implemica.calculator.controller.NumberFormatter.*;

/**
 * FX Controller for {@link com.implemica.calculator.view.Root}
 *
 * @author ozgreat
 * @see InputService
 * @see com.implemica.calculator.view.Root
 */
public class RootController {
  /**
   * Background of history pane
   */
  private static final Background BACKGROUND = new Background(new BackgroundFill(Paint.valueOf("#f2f2f2"), CornerRadii.EMPTY, Insets.EMPTY));

  /**
   * Keyboard shortcut for sqrt button
   */
  private final static KeyCombination SQRT = new KeyCodeCombination(KeyCode.DIGIT2, KeyCombination.SHIFT_DOWN);

  /**
   * Keyboard shortcut for percent button
   */
  private static final KeyCombination PERCENT = new KeyCodeCombination(KeyCode.DIGIT5, KeyCombination.SHIFT_DOWN);

  /**
   * Keyboard shortcut fot MR button
   */
  private static final KeyCombination MEMORY_RECALL = new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN);

  /**
   * Keyboard shortcut for M+ button
   */
  private static final KeyCombination MEMORY_ADD = new KeyCodeCombination(KeyCode.P, KeyCombination.CONTROL_DOWN);

  /**
   * Keyboard shortcut for M- button
   */
  private static final KeyCombination MEMORY_SUB = new KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN);

  /**
   * Keyboard shortcut for MS button
   */
  private static final KeyCombination MEMORY_SAVE = new KeyCodeCombination(KeyCode.M, KeyCombination.CONTROL_DOWN);

  /**
   * Keyboard shortcut for MC button
   */
  private static final KeyCombination MEMORY_CLEAR = new KeyCodeCombination(KeyCode.L, KeyCombination.CONTROL_DOWN);

  /**
   * One of keyboard shortcuts for plus button
   */
  private static final KeyCombination PLUS = new KeyCodeCombination(KeyCode.EQUALS, KeyCombination.SHIFT_DOWN);

  /**
   * One of keyboard shortcuts for multiply button
   */
  private static final KeyCombination MULTIPLY = new KeyCodeCombination(KeyCode.DIGIT8, KeyCombination.SHIFT_DOWN);

  /**
   * Keyboard shortcut  button
   */
  private static final KeyCombination NEGATE_SHORTCUT = new KeyCodeCombination(KeyCode.F9);

  /**
   * Keyboard shortcut for C button
   */
  private static final KeyCombination CLEAR_SHORTCUT = new KeyCodeCombination(KeyCode.ESCAPE);

  /**
   * Keyboard shortcut for CE button
   */
  private static final KeyCombination CLEAR_ENTRY_SHORTCUT = new KeyCodeCombination(KeyCode.DELETE);

  /**
   * Keyboard shortcut for backspace button
   */
  private static final KeyCombination BACKSPACE_SHORTCUT = new KeyCodeCombination(KeyCode.BACK_SPACE);

  /**
   * Keyboard shortcut for equals button
   */
  private static final KeyCombination EQUALS_SHORTCUT = new KeyCodeCombination(KeyCode.EQUALS);

  /**
   * Keyboard shortcut for ⅟𝑥 button
   */
  private static final KeyCombination REVERSE_SHORTCUT = new KeyCodeCombination(KeyCode.R);

  /**
   * Duration of opening sidebar
   */
  private static final int OPEN_SIDEBAR_DURATION = 200;

  /**
   * Sidebar's opening shift value
   */
  private static final int SIDEBAR_SHIFT = 272;

  /**
   * Formula on display maximum length
   */
  private static final int FORMULA_MAX_SHIFT_LENGTH = 40;

  /**
   * Default display font
   */
  private static final Font DEFAULT_FONT = new Font("Segoe UI", 14);

  /**
   * List of shortcut for numpad buttons
   */
  private final static List<KeyCodeCombination> NUMPAD_AND_DIGITS = Arrays.asList(new KeyCodeCombination(KeyCode.DIGIT0),
      new KeyCodeCombination(KeyCode.DIGIT1), new KeyCodeCombination(KeyCode.DIGIT2), new KeyCodeCombination(KeyCode.DIGIT3),
      new KeyCodeCombination(KeyCode.DIGIT4), new KeyCodeCombination(KeyCode.DIGIT5), new KeyCodeCombination(KeyCode.DIGIT6),
      new KeyCodeCombination(KeyCode.DIGIT7), new KeyCodeCombination(KeyCode.DIGIT8), new KeyCodeCombination(KeyCode.DIGIT9),
      new KeyCodeCombination(KeyCode.NUMPAD0), new KeyCodeCombination(KeyCode.NUMPAD1), new KeyCodeCombination(KeyCode.NUMPAD2),
      new KeyCodeCombination(KeyCode.NUMPAD3), new KeyCodeCombination(KeyCode.NUMPAD4), new KeyCodeCombination(KeyCode.NUMPAD5),
      new KeyCodeCombination(KeyCode.NUMPAD6), new KeyCodeCombination(KeyCode.NUMPAD7), new KeyCodeCombination(KeyCode.NUMPAD8),
      new KeyCodeCombination(KeyCode.NUMPAD9), new KeyCodeCombination(KeyCode.PERIOD));


  /**
   * Map, that run action by shortcut
   */
  private final Map<KeyCombination, Runnable> COMBINATIONS = new HashMap<>();

  /**
   * Map, that contains messages to each type of {@link CalculatorExceptionType}
   */
  Map<CalculatorExceptionType, String> exceptionMessages = new HashMap<>();

  /**
   * Label with number, display of calculator
   */
  @FXML
  private Label display;

  /**
   * Button, that clear memory
   */
  @FXML
  private Button memoryClearButton;

  /**
   * Button, that call number from memory
   */
  @FXML
  private Button memoryRecallButton;

  /**
   * Button, that add current number to memory or save current number in memory, if memory isn't exist
   */
  @FXML
  private Button memoryPlusButton;

  /**
   * Button, that subtract current number to memory or save negate of current number in memory, if memory isn't exist
   */
  @FXML
  private Button memoryMinusButton;

  /**
   * Button, that save current number in memory
   */
  @FXML
  private Button memorySaveButton;

  /**
   * Button, that open memory pane
   */
  @FXML
  private Button memoryShow;

  /**
   * Pane, that close historyPane, if historyPane is opened.
   */
  @FXML
  private AnchorPane historyUpperPane;

  /**
   * Pane with stub, where original calculator has history of operations
   */
  @FXML
  private AnchorPane historyPane;

  /**
   * Label with stub text
   */
  @FXML
  private Label historyLabel;

  /**
   * Button, that call percent action
   */
  @FXML
  private Button percentButton;

  /**
   * Button, that call sqrt action
   */
  @FXML
  private Button sqrtButton;

  /**
   * Button, that call pow action
   */
  @FXML
  private Button powButton;

  /**
   * Button, that call divide action
   */
  @FXML
  private Button divideButton;

  /**
   * Button, that call reverse action
   */
  @FXML
  private Button reverseButton;

  /**
   * Button that call multiply action
   */
  @FXML
  private Button multiplyButton;

  /**
   * Button, that call add action
   */
  @FXML
  private Button addButton;

  /**
   * Button, that call subtract action
   */
  @FXML
  private Button subtractButton;

  /**
   * Button, that type point at display
   */
  @FXML
  private Button pointButton;

  /**
   * Button, that call negate action
   */
  @FXML
  private Button negateButton;

  /**
   * Button, that move formula label to right
   */
  @FXML
  private Button rightFormulaButton;

  /**
   * Button, that move formula label to left
   */
  @FXML
  private Button leftFormulaButton;

  /**
   * Label with history of operation until equals action
   */
  @FXML
  private Label formula;

  /**
   * Pane of side menu navigate bar
   */
  @FXML
  private BorderPane sideMenuBorderPane;

  /**
   * Pane , that turn off side bar, when you click on
   */
  @FXML
  private AnchorPane sideBarOffPane;

  /**
   * Label with text = "Standard", stub for mode of calculator
   */
  @FXML
  private Label standardLabel;

  /**
   * Service that connect controller with model
   */
  private InputService inputService;

  /**
   * Status of sidebar
   */
  private boolean isSideBarOpened = false;

  /**
   * String, that contains full history of operations before equals
   */
  private String formulaStr;

  /**
   * Index of first visible symbol in formulaStr on formula Label
   */
  private int formulaBegIndex;

  /**
   * Index of last visible symbol in formulaStr on formula Label
   */
  private int formulaEndIndex;

  /**
   * True when exception was caught
   */
  private boolean isException = false;

  /**
   * Init inputService, assigns empty string to formulaStr, put COMBINATIONS into map
   */
  public RootController() {
    inputService = new InputService();
    formulaStr = "";
  }

  @FXML
  public void initialize() {
    COMBINATIONS.put(CLEAR_SHORTCUT, this::clearAction);
    COMBINATIONS.put(CLEAR_ENTRY_SHORTCUT, this::clearEntryAction);
    COMBINATIONS.put(BACKSPACE_SHORTCUT, this::backspaceButtonAction);
    COMBINATIONS.put(EQUALS_SHORTCUT, this::equalAction);
    COMBINATIONS.put(MEMORY_ADD, memoryPlusButton::fire);
    COMBINATIONS.put(MEMORY_SUB, memoryMinusButton::fire);
    COMBINATIONS.put(MEMORY_CLEAR, memoryClearButton::fire);
    COMBINATIONS.put(MEMORY_SAVE, memorySaveButton::fire);
    COMBINATIONS.put(MEMORY_RECALL, memoryRecallButton::fire);
    COMBINATIONS.put(MULTIPLY, multiplyButton::fire);
    COMBINATIONS.put(PLUS, addButton::fire);
    COMBINATIONS.put(SQRT, sqrtButton::fire);
    COMBINATIONS.put(NEGATE_SHORTCUT, negateButton::fire);
    COMBINATIONS.put(PERCENT, percentButton::fire);
    COMBINATIONS.put(REVERSE_SHORTCUT, reverseButton::fire);
    COMBINATIONS.put(new KeyCodeCombination(KeyCode.MINUS), subtractButton::fire);
    COMBINATIONS.put(new KeyCodeCombination(KeyCode.SUBTRACT), subtractButton::fire);
    COMBINATIONS.put(new KeyCodeCombination(KeyCode.ADD), addButton::fire);
    COMBINATIONS.put(new KeyCodeCombination(KeyCode.MULTIPLY), multiplyButton::fire);
    COMBINATIONS.put(new KeyCodeCombination(KeyCode.DIVIDE), divideButton::fire);
    COMBINATIONS.put(new KeyCodeCombination(KeyCode.SLASH), divideButton::fire);

    exceptionMessages.put(CalculatorExceptionType.CANNOT_DIVIDE_BY_ZERO, "Cannot divide by zero");
    exceptionMessages.put(CalculatorExceptionType.DIVIDING_ZERO_BY_ZERO, "Result is undefined");
    exceptionMessages.put(CalculatorExceptionType.NEGATIVE_ROOT, "Invalid input");
    exceptionMessages.put(CalculatorExceptionType.OVERFLOW, "Overflow");
  }

  /**
   * Processing of keys typed
   *
   * @param event key, that user type
   */
  public void keyPressProcess(KeyEvent event) {
    Predicate<KeyCombination> keyCombinationMatcher = code -> code.match(event);

    NUMPAD_AND_DIGITS.stream().filter(keyCombinationMatcher).findFirst().
        ifPresent(comb -> addNumberOrDot(new ActionEvent(new Button(event.getText()), null)));

    COMBINATIONS.keySet().stream().filter(keyCombinationMatcher).findFirst().
        ifPresent(code -> COMBINATIONS.get(code).run());
  }

  /**
   * Typing of number or dot
   *
   * @param event event of button that we pressed
   */
  @FXML
  public void addNumberOrDot(ActionEvent event) { // buttons 0-9 and '.'
    if (isException) {
      setNormal();
    }
    try {
      display.setText(inputService.enterNumberOrComma(event, display.getText()));
    } catch (CalculatorException e) {
      handleException(e);
    } catch (Exception e) {
      e.printStackTrace();
      alertError(e);
    }
  }

  /**
   * Setting text in textArea to default value and call clear in service
   */
  @FXML
  public void clearAction() { //button C
    if (isException) {
      setNormal();
    }
    display.setText(DEFAULT_DISPLAY_NUMBER);
    inputService.clearDisplay();
    clearFormula();
  }

  /**
   * Setting text in display to 0
   */
  @FXML
  public void clearEntryAction() {
    if (isException) {
      setNormal();
    }
    display.setText(DEFAULT_DISPLAY_NUMBER);
    inputService.setMemoryRecall(false);
    inputService.setBackspacePossible(true);
  }

  /**
   * Entering binary operation
   *
   * @param event event of button that we pressed
   */
  @FXML
  public void operationButtonAction(ActionEvent event) {
    formulaCalc(event);
    try {
      display.setText(inputService.enterOperation(event, display.getText()));
    } catch (CalculatorException e) {
      handleException(e);
    } catch (Exception e) {
      e.printStackTrace();
      alertError(e);
    }
  }

  /**
   * Removing last symbol from display
   */
  @FXML
  public void backspaceButtonAction() {
    try {
      String displayText = display.getText();
      if (isException) {
        setNormal();
      } else if (inputService.isBackspaceAvailable()) {
        if (displayText.endsWith(String.valueOf(DECIMAL_SEPARATOR))) {
          display.setText(displayText.substring(0, displayText.length() - 1));
        } else {
          boolean saveDecimalSeparator = false;
          BigDecimal result = parse(displayText);

          if (result.scale() == 1) {
            saveDecimalSeparator = true;
          }

          displayText = format(DigitBacspace.deleteLastDigit(result));

          if (saveDecimalSeparator) {
            displayText += DECIMAL_SEPARATOR;
          }

          display.setText(displayText);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      alertError(e);
    }
  }

  /**
   * Calling a calculation and displaying results
   */
  @FXML
  public void equalAction() {
    if (isException) {
      setNormal();
    }
    clearFormula();
    try {
      String value = inputService.enterEqual(display.getText());
      display.setText(value);
    } catch (CalculatorException e) {
      handleException(e);
    } catch (Exception e) {
      e.printStackTrace();
      alertError(e);
    }
  }

  /**
   * Enter and calculate unary operation
   *
   * @param ae event, that call this method, is using for get a button, that pressed
   */
  @FXML
  public void unaryOperationAction(ActionEvent ae) {
    formulaCalc(ae);
    try {
      String value = inputService.unaryOp(ae, display.getText());
      display.setText(value);
    } catch (CalculatorException e) {
      handleException(e);
    } catch (Exception e) {
      e.printStackTrace();
      alertError(e);
    }
  }

  /**
   * Find the percent from number
   *
   * @param ae event, that call this method, is using for get a button, that pressed
   */
  @FXML
  public void percentAction(ActionEvent ae) {
    String displayText = display.getText();
    try {
      displayText = inputService.percentOp(display.getText());
    } catch (CalculatorException e) {
      handleException(e);
    } catch (Exception e) {
      e.printStackTrace();
      alertError(e);
    }
    display.setText(displayText);
    formulaCalc(ae);
  }

  /**
   * Send display to model to save in memory. Turn on memory buttons, if they is disabled
   */
  @FXML
  public void memorySaveAction() {
    inputService.saveToMemory(display.getText());
    memoryDisableIfEmpty();
  }

  /**
   * Set on display the last number from memory, if memory is not empty
   */
  @FXML
  public void memoryRecallAction() {
    if (!inputService.isMemoryEmpty()) {
      try {
        display.setText(inputService.recallFromMemory());
      } catch (CalculatorException e) {
        handleException(e);
      }
    }
  }

  /**
   * Delete memory number, if there is exists
   */
  @FXML
  public void memoryClearAction() {
    inputService.clearMemory();
    memoryDisableIfEmpty();
  }

  /**
   * Send number from display to memory and add to existing
   */
  @FXML
  public void memoryPlusAction() {
    try {
      inputService.addToMemory(display.getText());
    } catch (CalculatorException e) {
      handleException(e);
    } catch (Exception e) {
      e.printStackTrace();
      alertError(e);
    }
    memoryDisableIfEmpty();
  }

  /**
   * Send number from display to memory and subtract from existing
   */
  @FXML
  public void memoryMinusAction() {
    try {
      inputService.subToMemory(display.getText());
    } catch (CalculatorException e) {
      handleException(e);
    } catch (Exception e) {
      e.printStackTrace();
      alertError(e);
    }
    memoryDisableIfEmpty();
  }

  /**
   * Open history pane stub, disable buttons that have to
   */
  @FXML
  public void historyAction() {
    boolean flag = historyPane.isDisable();

    historyPane.setDisable(!flag);
    historyUpperPane.setDisable(!flag);
    historyLabel.setVisible(flag);
    historyUpperPane.setVisible(flag);
    memoryRecallButton.setDisable(flag);
    memoryClearButton.setDisable(flag);
    memoryPlusButton.setDisable(flag);
    memoryMinusButton.setDisable(flag);
    memoryShow.setDisable(flag);
    memorySaveButton.setDisable(flag);

    Background background;
    if (flag) {
      background = BACKGROUND;
    } else {
      background = Background.EMPTY;
      memoryDisableIfEmpty();
    }
    historyPane.setBackground(background);
  }


  /**
   * Open side bar stub
   */
  @FXML
  public void openSideBar() {
    Duration duration = Duration.millis(OPEN_SIDEBAR_DURATION);
    TranslateTransition transition = new TranslateTransition(duration, sideMenuBorderPane);
    if (isSideBarOpened) {
      transition.setByX(-SIDEBAR_SHIFT);
    } else {
      transition.setByX(SIDEBAR_SHIFT);
    }
    transition.play();
    standardLabel.setVisible(!standardLabel.isVisible());
    sideBarOffPane.setVisible(!sideBarOffPane.isVisible());
    isSideBarOpened = !isSideBarOpened;
  }

  /**
   * Open memory pane stub
   */
  @FXML
  public void memoryShowAction() {
    historyAction();
    historyLabel.setVisible(false);
    memoryShow.setDisable(inputService.isMemoryEmpty());
  }

  /**
   * Move text in formula label to right
   */
  @FXML
  public void rightFormulaButtonAction() {
    /*leftFormulaButton.setVisible(true);
    if (formulaEndIndex + FORMULA_MAX_SHIFT_LENGTH < formulaStr.length()) {
      formulaBegIndex += FORMULA_MAX_SHIFT_LENGTH;
      formulaEndIndex += FORMULA_MAX_SHIFT_LENGTH;
    } else {
      while (formula.getText().length() != formulaStr.length() - formulaBegIndex) {
        formulaBegIndex++;
      }
      formulaEndIndex = formulaStr.length();
      rightFormulaButton.setVisible(false);
    }*/
    boolean isMoveToEnd = formulaEndIndex + FORMULA_MAX_SHIFT_LENGTH >= formulaStr.length();
    int endBuff = formulaEndIndex;
    formulaEndIndex = isMoveToEnd ? formulaStr.length() : formulaEndIndex + FORMULA_MAX_SHIFT_LENGTH;
    formulaBegIndex += formulaEndIndex - endBuff;
    leftFormulaButton.setVisible(true);
    rightFormulaButton.setVisible(!isMoveToEnd);
    formula.setText(formulaStr.substring(formulaBegIndex, formulaEndIndex));
  }

  /**
   * Move text in formula label to left
   */
  @FXML
  public void leftFormulaButtonAction() {
    rightFormulaButton.setVisible(true);
    /*if (formulaBegIndex > FORMULA_MAX_SHIFT_LENGTH) {
      formulaBegIndex -= FORMULA_MAX_SHIFT_LENGTH;
      formulaEndIndex -= FORMULA_MAX_SHIFT_LENGTH;
    } else {
      formulaBegIndex = 0;
      formulaEndIndex = formula.getText().length();
      leftFormulaButton.setVisible(false);
    }*/
    boolean isBeginGreaterThanMax = formulaBegIndex > FORMULA_MAX_SHIFT_LENGTH;
    int beginBuff = formulaBegIndex;
    leftFormulaButton.setVisible(isBeginGreaterThanMax);
    formulaBegIndex = isBeginGreaterThanMax ? formulaBegIndex - FORMULA_MAX_SHIFT_LENGTH : 0;
    formulaEndIndex -= Math.abs(beginBuff - formulaBegIndex);
    formula.setText(formulaStr.substring(formulaBegIndex, formulaEndIndex));
  }

  private void formulaCalc(ActionEvent event) {
    formulaStr = inputService.highFormula(event, formulaStr, display.getText());
    Text text = new Text(formulaStr);
    text.setFont(DEFAULT_FONT);
//    if (text.getLayoutBounds().getWidth() > formula.getWidth()) {
    int i;
    leftFormulaButton.setVisible(text.getLayoutBounds().getWidth() > formula.getWidth());
    for (i = 0; text.getLayoutBounds().getWidth() > formula.getWidth(); ++i) {
      text.setText(formulaStr.substring(i));
    }
    formulaBegIndex = i;
    formulaEndIndex = formulaStr.length();
    /*} else {
      leftFormulaButton.setVisible(false);
      rightFormulaButton.setVisible(false);
    }*/
    rightFormulaButton.setVisible(false);
    formula.setText(text.getText());
  }

  private void handleException(CalculatorException e) {
    isException = true;

    if (exceptionMessages.containsKey(e.getType())) {
      display.setText(exceptionMessages.get(e.getType()));
      disableButtonIfError();
    } else {
      alertError(e);
    }
  }

  private void setNormal() {
    isException = false;
    clearAction();

    disableButtonIfError();

    memoryDisableIfEmpty();
  }

  private void disableButtonIfError() {
    Stream.of(negateButton, addButton, subtractButton, sqrtButton, percentButton, pointButton, powButton, divideButton,
        multiplyButton, reverseButton, memoryMinusButton, memoryPlusButton, memorySaveButton, memoryShow,
        memoryRecallButton, memoryClearButton).forEach(button -> button.setDisable(isException));
  }

  private void memoryDisableIfEmpty() {
    setMemoryFlags(inputService.isMemoryEmpty());
  }

  private void setMemoryFlags(boolean flag) {
    memoryShow.setDisable(flag);
    memoryClearButton.setDisable(flag);
    memoryRecallButton.setDisable(flag);
    memoryShow.setDisable(flag);
  }

  private void clearFormula() {
    formula.setText("");
    formulaStr = "";
    leftFormulaButton.setVisible(false);
    rightFormulaButton.setVisible(false);
  }

  public String getFormulaStr() {
    return formulaStr;
  }

  private void alertError(Exception e) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle("Unexpected error");
    alert.setHeaderText("Unexpected error");
    alert.setContentText("Unexpected error was thrown with message:\n" + e.getMessage() +
        "\nCalculator will be reset and you will be able to continue using");

    isException = true;
    memoryClearAction();
    setNormal();
  }
}
