package com.implemica.calculator.controller;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import javafx.scene.input.KeyCode;
import org.loadui.testfx.utils.FXTestUtils;

import java.util.Arrays;
import java.util.List;

public class RootControllerKeyboardTest extends RootControllerTest {
  private static final BiMap<String, KeyCode[]> operationsKeyCode = HashBiMap.create();

  private static final List<KeyCode> digits = Arrays.asList(KeyCode.DIGIT0, KeyCode.DIGIT1, KeyCode.DIGIT2, KeyCode.DIGIT3,
      KeyCode.DIGIT4, KeyCode.DIGIT5, KeyCode.DIGIT6, KeyCode.DIGIT7, KeyCode.DIGIT8, KeyCode.DIGIT9);


  static {
    operationsKeyCode.put("C", new KeyCode[]{KeyCode.ESCAPE});
    operationsKeyCode.put("CE", new KeyCode[]{KeyCode.DELETE});
    operationsKeyCode.put("<-", new KeyCode[]{KeyCode.BACK_SPACE});
    operationsKeyCode.put("+", new KeyCode[]{KeyCode.SHIFT, KeyCode.EQUALS});
    operationsKeyCode.put("-", new KeyCode[]{KeyCode.SUBTRACT});
    operationsKeyCode.put("*", new KeyCode[]{KeyCode.SHIFT, KeyCode.DIGIT8});//multiply
    operationsKeyCode.put("/", new KeyCode[]{KeyCode.DIVIDE});
    operationsKeyCode.put("1/x", new KeyCode[]{KeyCode.R});
    operationsKeyCode.put("SQR", new KeyCode[]{KeyCode.SHIFT, KeyCode.DIGIT2});
    operationsKeyCode.put("N", new KeyCode[]{KeyCode.F9});
    operationsKeyCode.put("=", new KeyCode[]{KeyCode.EQUALS});
    operationsKeyCode.put("%", new KeyCode[]{KeyCode.SHIFT, KeyCode.DIGIT5});
    operationsKeyCode.put("MC", new KeyCode[]{KeyCode.CONTROL, KeyCode.L});
    operationsKeyCode.put("MS", new KeyCode[]{KeyCode.CONTROL, KeyCode.M});
    operationsKeyCode.put("MR", new KeyCode[]{KeyCode.CONTROL, KeyCode.R});
    operationsKeyCode.put("M+", new KeyCode[]{KeyCode.CONTROL, KeyCode.P});
    operationsKeyCode.put("M-", new KeyCode[]{KeyCode.CONTROL, KeyCode.Q});
  }

  private void pressOn(String query) {
    KeyCode[] comb = operationsKeyCode.get(query);
    for (KeyCode k : comb) {
      awtRobot.keyPress(k.getCode());
    }
    for (KeyCode k : comb) {
      awtRobot.keyRelease(k.getCode());
    }

    FXTestUtils.awaitEvents();
  }

  @Override
  void handleDigit(String digit) {
    for (char ch : digit.toCharArray()) {
      if (ch == '.') {
        awtRobot.keyPress(KeyCode.PERIOD.getCode());
        awtRobot.keyRelease(KeyCode.PERIOD.getCode());
      } else {
        awtRobot.keyPress(digits.get(Character.getNumericValue(ch)).getCode());
        awtRobot.keyRelease(digits.get(Character.getNumericValue(ch)).getCode());
      }
      FXTestUtils.awaitEvents();
    }
  }

  @Override
  void clear() {
    pressOn("C");
    pressOn("MC");
  }

  @Override
  void clicker(String pattern) {
    for (String s : pattern.split(" ")) {
      if (operationsKeyCode.containsKey(s)) {
        pressOn(s);
      } else if (s.equals("POW")) {
        clickOn(operations.get(s));
      } else {
        handleDigit(s);
      }
    }
    FXTestUtils.awaitEvents();
  }

}
