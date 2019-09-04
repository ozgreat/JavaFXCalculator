package com.implemica.calculator.controller;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;
import org.testfx.api.FxAssert;
import org.testfx.framework.junit5.Start;
import org.testfx.matcher.control.LabeledMatchers;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class RootControllerKeyboardTest extends RootControllerTest {
  private static final BiMap<String, KeyCodeCombination> operationsKeyCode = HashBiMap.create();

  private static final List<KeyCode> digits = Arrays.asList(KeyCode.DIGIT0, KeyCode.DIGIT1, KeyCode.DIGIT2, KeyCode.DIGIT3,
      KeyCode.DIGIT4, KeyCode.DIGIT5, KeyCode.DIGIT6, KeyCode.DIGIT7, KeyCode.DIGIT8, KeyCode.DIGIT9);

  static {
    operationsKeyCode.put("C", new KeyCodeCombination(KeyCode.ESCAPE));
    operationsKeyCode.put("CE", new KeyCodeCombination(KeyCode.DELETE));
    operationsKeyCode.put("<-", new KeyCodeCombination(KeyCode.BACK_SPACE));//Backspace
    operationsKeyCode.put("+", new KeyCodeCombination(KeyCode.EQUALS, KeyCombination.SHIFT_DOWN));
    operationsKeyCode.put("-", new KeyCodeCombination(KeyCode.SUBTRACT));
    operationsKeyCode.put("*", new KeyCodeCombination(KeyCode.DIGIT8, KeyCombination.SHIFT_DOWN));//multiply
    operationsKeyCode.put("/", new KeyCodeCombination(KeyCode.DIVIDE));
    operationsKeyCode.put("1/x", new KeyCodeCombination(KeyCode.R));
    operationsKeyCode.put("SQR", new KeyCodeCombination(KeyCode.DIGIT2, KeyCombination.SHIFT_DOWN));
    operationsKeyCode.put("N", new KeyCodeCombination(KeyCode.F9));
    operationsKeyCode.put("=", new KeyCodeCombination(KeyCode.EQUALS));
    operationsKeyCode.put("%", new KeyCodeCombination(KeyCode.DIGIT5, KeyCombination.SHIFT_DOWN));
  }

  @Start
  static void start(Stage stage) throws IOException {
    RootControllerTest.start(stage);
  }
  @Override

  void checkOperations(String pattern, String formula, String res) {
    for (String s : pattern.split(" ")) {
      if (operationsKeyCode.containsKey(s)) {
        pressOn(s);
      } else if (s.equals("POW")) {
        clickOn(operations.get(s));
      } else {
        handleDigit(s);
      }
    }
    FxAssert.verifyThat("#display", LabeledMatchers.hasText(res));
    FxAssert.verifyThat("#formula", LabeledMatchers.hasText(formula));
    clear();
  }

  private void pressOn(String query) {
    robot.push(operationsKeyCode.get(query));
  }

  @Override
  void handleDigit(String digit) {
    for (char ch : digit.toCharArray()) {
      if (ch == '.') {
        robot.push(KeyCode.PERIOD);
      } else {
        robot.push(digits.get(Character.getNumericValue(ch)));
      }
    }
  }

  @Override
  void clear() {
    pressOn("C");
  }
}
