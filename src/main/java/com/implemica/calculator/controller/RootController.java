package com.implemica.calculator.controller;

import com.implemica.calculator.controller.service.InputService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Dimension2D;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;


@Getter
@Setter
public class RootController {
  @FXML
  private Label display;

  @FXML
  private Button memoryClearButton;

  @FXML
  private Button memoryRecallButton;

  @FXML
  private Button memoryPlusButton;

  @FXML
  private Button memoryMinusButton;

  @FXML
  private Button memorySaveButton;

  @FXML
  private Button memoryShow;

  @FXML
  private AnchorPane historyUpperPane;

  @FXML
  private AnchorPane historyPane;

  @FXML
  private Label historyLabel;

  @FXML
  private Button percentButton;

  @FXML
  private Button sqrtButton;

  @FXML
  private Button powButton;

  @FXML
  private Button divideButton;

  @FXML
  private Button reverseButton;

  @FXML
  private Button multiplyButton;

  @FXML
  private Button addButton;

  @FXML
  private Button subtractButton;

  @FXML
  private Button pointButton;

  @FXML
  private Button negateButton;

  @FXML
  private Button rightFormulaButton;

  @FXML
  private Button leftFormulaButton;

  @FXML
  private BorderPane bp;

  @FXML
  private Label formula;

  @FXML
  private BorderPane sideMenuBorderPane;

  @FXML
  private AnchorPane sideBarOffPane;

  @FXML
  private Label standardLabel;


  private InputService inputService;

  private static double xOffset = 0;

  private static double yOffset = 0;

  private double dx;
  private double dy;
  private double deltaX;
  private double deltaY;
  private final static double border = 10;
  private boolean moveH;
  private boolean moveV;
  private boolean resizeH = false;
  private boolean resizeV = false;

  private String formulaStr;
  private int formulaBegIndex;
  private int formulaEndIndex;


  private Dimension2D minSize = new Dimension2D(325, 530);

  private static final double FONT_CHANGE_WIDTH_DOWN = 34.98;
  private static final double FONT_CHANGE_WIDTH_UP = 50d;
  private static final double MAX_FONT_SIZE = 74d;
  private static final Background BACKGROUND = new Background(new BackgroundFill(Paint.valueOf("#f2f2f2"), CornerRadii.EMPTY, Insets.EMPTY));
  private static final String SEGOE_UI_SEMIBOLD = "Segoe UI Semibold";
  private static final double MIN_HEIGHT_DELTA = 468.75; // scene - text; debug
  private static final double MAX_HEIGHT_DELTA = 516.76;
  private final static KeyCombination SQRT = new KeyCodeCombination(KeyCode.DIGIT2, KeyCombination.SHIFT_DOWN);
  private final static KeyCombination PERCENT = new KeyCodeCombination(KeyCode.DIGIT5, KeyCombination.SHIFT_DOWN);
  private final static KeyCombination RECALL = new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN);
  private final static KeyCombination MEMORY_ADD = new KeyCodeCombination(KeyCode.P, KeyCombination.CONTROL_DOWN);
  private final static KeyCombination MEMORY_SUB = new KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN);
  private final static KeyCombination MEMORY_SAVE = new KeyCodeCombination(KeyCode.M, KeyCombination.CONTROL_DOWN);
  private final static KeyCombination MEMORY_CLEAR = new KeyCodeCombination(KeyCode.L, KeyCombination.CONTROL_DOWN);
  private final static KeyCombination PLUS = new KeyCodeCombination(KeyCode.EQUALS, KeyCombination.SHIFT_DOWN);
  private final static KeyCombination MULTIPLY = new KeyCodeCombination(KeyCode.DIGIT8, KeyCombination.SHIFT_DOWN);
  private final static List<KeyCode> NUMPAD_AND_DIGITS = Arrays.asList(KeyCode.DIGIT0, KeyCode.DIGIT1, KeyCode.DIGIT2, KeyCode.DIGIT3,
      KeyCode.DIGIT4, KeyCode.DIGIT5, KeyCode.DIGIT6, KeyCode.DIGIT7, KeyCode.DIGIT8, KeyCode.DIGIT9, KeyCode.NUMPAD0,
      KeyCode.NUMPAD1, KeyCode.NUMPAD2, KeyCode.NUMPAD3, KeyCode.NUMPAD4, KeyCode.NUMPAD5, KeyCode.NUMPAD6, KeyCode.NUMPAD7,
      KeyCode.NUMPAD8, KeyCode.NUMPAD9, KeyCode.PERIOD);
  private final static List<KeyCode> BINARY_OP = Arrays.asList(KeyCode.MINUS, KeyCode.SUBTRACT, KeyCode.ADD,
      KeyCode.MULTIPLY, KeyCode.DIVIDE, KeyCode.SLASH);

  public RootController() {
    inputService = new InputService();
    formulaStr = "";
  }

  @FXML
  public void initialize() {
    display.textProperty().addListener(observable -> {
      Text text = new Text(display.getText());
      double fontSize = display.getFont().getSize();
      text.setFont(new Font(SEGOE_UI_SEMIBOLD, fontSize));
      double width = text.getLayoutBounds().getWidth();
      Scene scene = display.getScene();
      double sceneWidth = scene.getWidth();
      double textHeight = text.getLayoutBounds().getHeight();
      double sceneHeight = scene.getHeight();
      double heightDelta = sceneHeight - textHeight;


      while (FONT_CHANGE_WIDTH_DOWN > sceneWidth - width || heightDelta < MAX_HEIGHT_DELTA) {
        fontSize--;
        text.setFont(new Font(SEGOE_UI_SEMIBOLD, fontSize));
        width = text.getLayoutBounds().getWidth();
        textHeight = text.getLayoutBounds().getHeight();
        heightDelta = sceneHeight - textHeight;
      }


      while (sceneWidth - width > FONT_CHANGE_WIDTH_UP && fontSize <= MAX_FONT_SIZE
          && heightDelta > MIN_HEIGHT_DELTA) {
        fontSize++;
        text.setFont(new Font(SEGOE_UI_SEMIBOLD, fontSize));
        width = text.getLayoutBounds().getWidth();
        textHeight = text.getLayoutBounds().getHeight();
        heightDelta = sceneHeight - textHeight;
      }


      display.setStyle(" -fx-font-size:" + fontSize + ";\n" +
          "  -fx-font-family: \"" + SEGOE_UI_SEMIBOLD + "\";\n" +
          "  -fx-text-alignment: right;");


    });


  }

  public void keyPressProcess(KeyEvent event) {
    Button btn = new Button();
    if (event.getCode() == KeyCode.ESCAPE) {
      clearAction();
    } else if (event.getCode() == KeyCode.DELETE) {
      clearEntryAction();
    } else if (event.getCode() == KeyCode.F9) {
      btn.setText("\uE94D"); // negate
      unaryOperationAction(new ActionEvent(btn, null));
    } else if (event.getCode() == KeyCode.BACK_SPACE) {
      backspaceButtonAction();
    } else if (SQRT.match(event)) {
      btn.setText("\uE94B");//sqrt
      unaryOperationAction(new ActionEvent(btn, null));
    } else if (PERCENT.match(event)) {
      btn.setText("\uE94C");//%
      percentAction(new ActionEvent(btn, null));
    } else if (RECALL.match(event)) {
      memoryRecallAction();
    } else if (MEMORY_ADD.match(event)) {
      memoryPlusAction();
    } else if (MEMORY_SUB.match(event)) {
      memoryMinusAction();
    } else if (MEMORY_SAVE.match(event)) {
      memorySaveAction();
    } else if (MEMORY_CLEAR.match(event)) {
      memoryClearAction();
    } else if (PLUS.match(event)) {
      btn.setText("+");
      operationButtonAction(new ActionEvent(btn, null));
    } else if (MULTIPLY.match(event)) {
      btn.setText("×");
      operationButtonAction(new ActionEvent(btn, null));
    } else if (NUMPAD_AND_DIGITS.contains(event.getCode())) {
      btn.setText(event.getText());
      addNumberOrComma(new ActionEvent(btn, null));
    } else if (BINARY_OP.contains(event.getCode())) {
      btn.setText(event.getText());
      operationButtonAction(new ActionEvent(btn, null));
    } else if (event.getCode() == KeyCode.R) {
      btn.setText("⅟\uD835\uDC65");
      unaryOperationAction(new ActionEvent(btn, null));
    } else if (event.getCode() == KeyCode.EQUALS) {
      equalAction();
    }

  }

  /**
   * Typing of number or comma
   *
   * @param event event of button that we pressed
   */
  @FXML
  public void addNumberOrComma(ActionEvent event) { // buttons 0-9 and ','
    if (InputService.EXCEPTION_MESSAGES.contains(display.getText())) {
      setNormal();
    }
    String value = inputService.enterNumberOrComma(event, display.getText());
    display.setText(value);
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
   * Setting text in textArea to 0
   */
  @FXML
  public void clearEntryAction() {
    if (InputService.EXCEPTION_MESSAGES.contains(display.getText())) {
      setNormal();
    }
    display.setText("0");
  }

  /**
   * @param event event of button that we pressed
   */
  @FXML
  public void operationButtonAction(ActionEvent event) {
    formulaCalc(event);
    try {
      display.setText(inputService.enterOperation(event, display.getText()));
    } catch (ArithmeticException e) {
      handleArithmetic(e.getMessage());
    }
  }

  /**
   * removing last symbol in textArea
   */
  @FXML
  public void backspaceButtonAction() {
    if (InputService.EXCEPTION_MESSAGES.contains(display.getText())) {
      setNormal();
    } else if (display.getText().length() == 1 && inputService.isBackspaceAvailable()) {
      clearEntryAction();
    } else if (inputService.isBackspaceAvailable()) {
      String str = display.getText().substring(0, display.getText().length() - 1);
      if (str.endsWith(".")) {
        display.setText(inputService.displayFormat(str));
      } else if (str.contains(".")) {
        String[] strArr = str.split("\\.");
        display.setText(inputService.displayFormat(strArr[0]) + "." + strArr[1]);
      } else {
        display.setText(inputService.displayFormat(str));
      }
    }
  }

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
    } catch (ArithmeticException e) {
      handleArithmetic(e.getMessage());
    }
  }

  @FXML
  public void unaryOperationAction(ActionEvent ae) {
    formulaCalc(ae);
    try {
      String value = inputService.unaryOp(ae, display.getText());
      display.setText(value);
    } catch (ArithmeticException e) {
      handleArithmetic(e.getMessage());
    }
  }

  @FXML
  public void percentAction(ActionEvent ae) {
    String value = inputService.percentOp(display.getText());
    display.setText(inputService.displayFormat(value));
    formulaCalc(ae);
  }

  @FXML
  public void memorySaveAction() {
    inputService.saveToMemory(display.getText());
    memoryDisableIfEmpty();
  }

  @FXML
  public void memoryRecallAction() {
    if (!inputService.isMemoryEmpty()) {
      display.setText(inputService.recallFromMemory());
    }
  }

  @FXML
  public void memoryClearAction() {
    inputService.clearMemory();
    memoryDisableIfEmpty();
  }

  @FXML
  public void memoryPlusAction() {
    inputService.addToMemory(display.getText());
    memoryDisableIfEmpty();
  }

  @FXML
  public void memoryMinusAction() {
    inputService.subToMemory(display.getText());
    memoryDisableIfEmpty();
  }

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

  @FXML
  public void closeWindow(MouseEvent event) {
    ((Button) event.getSource()).getScene().getWindow().hide();
  }

  @FXML
  public void minimizeWindow(MouseEvent event) {
    Stage stage = (Stage) (((Button) event.getSource()).getScene().getWindow());
    stage.setIconified(true);
  }

  @FXML
  public void pressWindow(MouseEvent event) {
    Stage stage = (Stage) (((AnchorPane) event.getSource()).getScene().getWindow());
    if (((AnchorPane) event.getSource()).getScene().getCursor().equals(Cursor.DEFAULT)) {
      xOffset = stage.getX() - event.getScreenX();
      yOffset = stage.getY() - event.getScreenY();
    }
  }

  @FXML
  public void dragWindow(MouseEvent event) {
    if (((AnchorPane) event.getSource()).getScene().getCursor().equals(Cursor.DEFAULT)) {
      Stage stage = (Stage) (((AnchorPane) event.getSource()).getScene().getWindow());
      stage.setX(event.getScreenX() + xOffset);
      stage.setY(event.getScreenY() + yOffset);
    }
  }

  @FXML
  public void pressResize(MouseEvent t) {
    Stage stage = (Stage) bp.getScene().getWindow();
    dx = stage.getWidth() - t.getX();
    dy = stage.getHeight() - t.getY();
    display.setText(display.getText());
  }

  @FXML
  public void dragResize(MouseEvent t) {
    Stage stage = (Stage) bp.getScene().getWindow();
    if (resizeH) {
      if (stage.getWidth() <= minSize.getWidth()) {
        if (moveH) {
          deltaX = stage.getX() - t.getScreenX();
          if (t.getX() < 0) {// if new > old, it's permitted
            stage.setWidth(deltaX + stage.getWidth());
            stage.setX(t.getScreenX());
          }
        } else {
          if (t.getX() + dx - stage.getWidth() > 0) {
            stage.setWidth(t.getX() + dx);
          }
        }
      } else if (stage.getWidth() > minSize.getWidth()) {
        if (moveH) {
          deltaX = stage.getX() - t.getScreenX();
          stage.setWidth(deltaX + stage.getWidth());
          stage.setX(t.getScreenX());
        } else {
          stage.setWidth(t.getX() + dx);
        }
      }
    }

    if (resizeV) {
      if (stage.getHeight() <= minSize.getHeight()) {
        if (moveV) {
          deltaY = stage.getY() - t.getScreenY();
          if (t.getY() < 0) {
            stage.setHeight(deltaY + stage.getHeight());
            stage.setY(t.getScreenY());
          }
        } else {
          if (t.getY() + dy - stage.getHeight() > 0) {
            stage.setHeight(t.getY() + dy);
          }
        }
      } else if (stage.getHeight() > minSize.getHeight()) {
        if (moveV) {
          deltaY = stage.getY() - t.getScreenY();
          stage.setHeight(deltaY + stage.getHeight());
          stage.setY(t.getScreenY());
        } else {
          stage.setHeight(t.getY() + dy);
        }
      }
    }
  }

  @FXML
  public void moveResize(MouseEvent t) {
    Scene scene = bp.getScene();
    if (t.getX() < border && t.getY() < border) {
      scene.setCursor(Cursor.NW_RESIZE);
      resizeH = true;
      resizeV = true;
      moveH = true;
      moveV = true;
    } else if (t.getX() < border && t.getY() > scene.getHeight() - border) {
      scene.setCursor(Cursor.SW_RESIZE);
      resizeH = true;
      resizeV = true;
      moveH = true;
      moveV = false;
    } else if (t.getX() > scene.getWidth() - border && t.getY() < border) {
      scene.setCursor(Cursor.NE_RESIZE);
      resizeH = true;
      resizeV = true;
      moveH = false;
      moveV = true;
    } else if (t.getX() > scene.getWidth() - border && t.getY() > scene.getHeight() - border) {
      scene.setCursor(Cursor.SE_RESIZE);
      resizeH = true;
      resizeV = true;
      moveH = false;
      moveV = false;
    } else if (t.getX() < border || t.getX() > scene.getWidth() - border) {
      scene.setCursor(Cursor.E_RESIZE);
      resizeH = true;
      resizeV = false;
      moveH = (t.getX() < border);
      moveV = false;
    } else if (t.getY() < border || t.getY() > scene.getHeight() - border) {
      scene.setCursor(Cursor.N_RESIZE);
      resizeH = false;
      resizeV = true;
      moveH = false;
      moveV = (t.getY() < border);
    } else {
      scene.setCursor(Cursor.DEFAULT);
      resizeH = false;
      resizeV = false;
      moveH = false;
      moveV = false;
    }
  }

  @FXML
  public void maximize() {
    Stage primaryStage = (Stage) display.getScene().getWindow();
    primaryStage.setMaximized(!primaryStage.isMaximized());
    String str = display.getText();
    display.setText("");
    display.setText(str);
  }

  @FXML
  public void openSideBar() {
    sideMenuBorderPane.setDisable(!sideMenuBorderPane.isDisabled());
    sideMenuBorderPane.setVisible(!sideMenuBorderPane.isVisible());
    standardLabel.setVisible(!standardLabel.isVisible());
    sideBarOffPane.setVisible(!sideBarOffPane.isVisible());
  }

  @FXML
  public void memoryShowAction() {
    historyAction();
    historyLabel.setVisible(false);
    memoryShow.setDisable(inputService.isMemoryEmpty());
  }

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


  private void handleArithmetic(String msg) {
    display.setText(msg);

    negateButton.setDisable(true);
    addButton.setDisable(true);
    subtractButton.setDisable(true);
    sqrtButton.setDisable(true);
    percentButton.setDisable(true);
    pointButton.setDisable(true);
    powButton.setDisable(true);
    divideButton.setDisable(true);
    multiplyButton.setDisable(true);
    reverseButton.setDisable(true);

    memoryClearButton.setDisable(true);
    memoryMinusButton.setDisable(true);
    memoryPlusButton.setDisable(true);
    memoryRecallButton.setDisable(true);
    memorySaveButton.setDisable(true);
    memoryShow.setDisable(true);
  }

  private void setNormal() {
    display.setText("0");
    formula.setText("");
    clearAction();

    negateButton.setDisable(false);
    addButton.setDisable(false);
    subtractButton.setDisable(false);
    sqrtButton.setDisable(false);
    percentButton.setDisable(false);
    pointButton.setDisable(false);
    powButton.setDisable(false);
    divideButton.setDisable(false);
    multiplyButton.setDisable(false);
    reverseButton.setDisable(false);
    memoryDisableIfEmpty();
    memoryMinusButton.setDisable(false);
    memoryPlusButton.setDisable(false);
    memorySaveButton.setDisable(false);
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
