package com.implemica.calculator.controller;

import com.implemica.calculator.controller.service.InputService;
import com.implemica.calculator.controller.util.NumberFormatter;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
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
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class RootController {
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
  @Getter
  private InputService inputService;

  /**
   * Status of sidebar
   */
  private boolean isSideBarOpened = false;

  /**
   * String, that contains full history of operations before equals
   */
  @Getter
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
    if (InputService.EXCEPTION_MESSAGES.contains(display.getText())) {
      setNormal();
    }
    try {
      display.setText(inputService.enterNumberOrComma(event, display.getText()));
    } catch (Throwable e) {
      handleError(e.getMessage());
    }
  }

  /**
   * Setting text in textArea to 0 and call clear in service
   */
  @FXML
  public void clearAction() { //button C
    if (InputService.EXCEPTION_MESSAGES.contains(display.getText())) {
      setNormal();
    }
    display.setText("0");
    inputService.clearDisplay();
    formula.setText("");
    formulaStr = "";
    leftFormulaButton.setVisible(false);
    rightFormulaButton.setVisible(false);
  }

  /**
   * Setting text in display to 0
   */
  @FXML
  public void clearEntryAction() {
    if (InputService.EXCEPTION_MESSAGES.contains(display.getText())) {
      setNormal();
    }
    display.setText("0");
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
    } catch (Throwable e) {
      handleError(e.getMessage());
    }
  }

  /**
   * Removing last symbol from display
   */
  @FXML
  public void backspaceButtonAction() {
    if (InputService.EXCEPTION_MESSAGES.contains(display.getText())) {
      setNormal();
    } else if (display.getText().length() == 1 && inputService.isBackspaceAvailable()) {
      clearEntryAction();
    } else if (inputService.isBackspaceAvailable()) {
      /*BigDecimal result;
      int scale = 0;
      try {
        result = NumberFormatter.parse(display.getText());
        scale = result.scale();

        if (!display.getText().endsWith(".")) {
          result = NumberFormatter.parse(display.getText().substring(0, display.getText().length() - 1));
        }
      } catch (ParseException e) {
        result = null;
        handleError(e.getMessage());
      }


      String value = NumberFormatter.format(result);

      if (scale == 1) {
        value += ".";
      }*/
      String value;
      if (display.getText().endsWith(".")) {
        try {
          BigDecimal result = NumberFormatter.parse(display.getText());
          int scale = result.scale();
          value = NumberFormatter.format(result);
          if (scale == 1) {
            value += ".";
          }
        } catch (Throwable e) {
          value = e.getMessage();
          handleError(e.getMessage());
        }
      } else if (display.getText().contains(".")) {
        String[] displayArr = display.getText().split("\\.");
        value = displayArr[0] + "." + displayArr[1].substring(0, displayArr[1].length() - 1);
      } else {
        try {
          BigDecimal result = NumberFormatter.parse(display.getText().substring(0, display.getText().length() - 1));
          value = NumberFormatter.format(result);
        } catch (Throwable e) {
          value = e.getMessage();
          handleError(e.getMessage());
        }
      }

      display.setText(value);
    }
  }

  /**
   * Calling a calculation and displaying results
   */
  @FXML
  public void equalAction() {
    if (InputService.EXCEPTION_MESSAGES.contains(display.getText())) {
      setNormal();
    }
    formula.setText("");
    formulaStr = "";
    leftFormulaButton.setVisible(false);
    rightFormulaButton.setVisible(false);
    try {
      String value = inputService.enterEqual(display.getText());
      display.setText(value);
    } catch (Throwable e) {
      handleError(e.getMessage());
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
    } catch (Throwable e) {
      handleError(e.getMessage());
    }
  }

  /**
   * Find the percent from number
   *
   * @param ae event, that call this method, is using for get a button, that pressed
   */
  @FXML
  public void percentAction(ActionEvent ae) {
    String value = display.getText();
    try {
      value = inputService.percentOp(display.getText());
    } catch (Throwable e) {
      handleError(e.getMessage());
    }
    display.setText(value);
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
      display.setText(inputService.recallFromMemory());
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
    } catch (Throwable e) {
      handleError(e.getMessage());
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
    } catch (Throwable e) {
      handleError(e.getMessage());
    }
    memoryDisableIfEmpty();
  }

  /**
   * Open history pane stub, disable buttons that have to
   */
  @FXML
  public void historyAction() {
    if (historyPane.isDisable()) {
      historyPane.setDisable(false);
      historyLabel.setVisible(true);
      historyPane.setBackground(BACKGROUND);
      historyUpperPane.setDisable(false);
      historyUpperPane.setVisible(true);
      memoryRecallButton.setDisable(true);
      memoryClearButton.setDisable(true);
      memoryPlusButton.setDisable(true);
      memoryMinusButton.setDisable(true);
      memoryShow.setDisable(true);
      memorySaveButton.setDisable(true);
    } else {
      historyPane.setDisable(true);
      historyLabel.setVisible(false);
      historyPane.setBackground(Background.EMPTY);
      historyUpperPane.setDisable(true);
      historyUpperPane.setVisible(false);
      memoryPlusButton.setDisable(false);
      memoryMinusButton.setDisable(false);
      memorySaveButton.setDisable(false);
      memoryDisableIfEmpty();
    }
  }

  /**
   * Open side bar stub
   */
  @FXML
  public void openSideBar() {
    Duration duration = Duration.millis(200);
    TranslateTransition transition = new TranslateTransition(duration, sideMenuBorderPane);
    if (isSideBarOpened) {
      transition.setByX(-272);
    } else {
      transition.setByX(272);
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
    leftFormulaButton.setVisible(true);
    if (formulaEndIndex + 40 < formulaStr.length()) {
      formulaBegIndex += 40;
      formulaEndIndex += 40;
      formula.setText(formulaStr.substring(formulaBegIndex, formulaEndIndex));
    } else {
      while (formula.getText().length() != formulaStr.length() - formulaBegIndex) {
        formulaBegIndex++;
      }
      formula.setText(formulaStr.substring(formulaBegIndex));
      formulaEndIndex = formulaStr.length();
      rightFormulaButton.setVisible(false);
    }
  }

  /**
   * Move text in formula label to left
   */
  @FXML
  public void leftFormulaButtonAction() {
    rightFormulaButton.setVisible(true);
    if (formulaBegIndex > 40) {
      formulaBegIndex -= 40;
      formulaEndIndex -= 40;
      formula.setText(formulaStr.substring(formulaBegIndex, formulaEndIndex));
    } else {
      formulaBegIndex = 0;
      formulaEndIndex = formula.getText().length();
      formula.setText(formulaStr.substring(formulaBegIndex, formulaEndIndex));
      leftFormulaButton.setVisible(false);
    }
  }

  private void formulaCalc(ActionEvent event) {
    formulaStr = inputService.highFormula(event, formulaStr, display.getText());
    Text text = new Text(formulaStr);
    text.setFont(new Font("Segoe UI", 14));
    if (text.getLayoutBounds().getWidth() > formula.getWidth()) {
      leftFormulaButton.setVisible(true);
      for (int i = 0; text.getLayoutBounds().getWidth() > formula.getWidth(); ++i) {
        text.setText(formulaStr.substring(i));
        formulaBegIndex = i;
      }
      formulaEndIndex = formulaStr.length();
    } else {
      leftFormulaButton.setVisible(false);
      rightFormulaButton.setVisible(false);
      text.setText(formulaStr);
    }
    formula.setText(text.getText());
  }

  private void handleError(String msg) {
    display.setText(msg);
    Stream.of(negateButton, addButton, subtractButton, sqrtButton, percentButton, pointButton, powButton, divideButton,
        multiplyButton, reverseButton, memoryClearButton, memoryMinusButton, memoryPlusButton, memoryRecallButton,
        memorySaveButton, memoryShow).collect(Collectors.toList()).forEach(button -> button.setDisable(true));
  }

  private void setNormal() {
    display.setText("0");
    formula.setText("");
    clearAction();

    Stream.of(negateButton, addButton, subtractButton, sqrtButton, percentButton, pointButton, powButton, divideButton,
        multiplyButton, reverseButton, memoryMinusButton, memoryPlusButton, memorySaveButton).
        collect(Collectors.toList()).forEach(button -> button.setDisable(false));

    memoryDisableIfEmpty();

  }

  private void memoryDisableIfEmpty() {
    if (inputService.isMemoryEmpty()) {
      memoryShow.setDisable(true);
      memoryClearButton.setDisable(true);
      memoryRecallButton.setDisable(true);
      memoryShow.setDisable(true);
    } else {
      memoryShow.setDisable(false);
      memoryClearButton.setDisable(false);
      memoryRecallButton.setDisable(false);
      memoryShow.setDisable(false);
    }
  }
}
