package com.implemica.calculator.controller.util;

import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Button;
import org.loadui.testfx.utils.FXTestUtils;
import org.testfx.api.FxAssert;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationTest;

import java.awt.*;
import java.awt.event.InputEvent;
import java.util.List;
import java.util.*;
import java.util.function.Supplier;

import static org.testfx.matcher.control.LabeledMatchers.hasText;

public class ControllerTestUtils extends ApplicationTest {
  protected static FxRobot robot = new FxRobot();
  protected static Robot awtRobot;
  protected static final Map<String, String> operations = new HashMap<>();
  protected static final Map<String, String> memoryOp = new HashMap<>();
  private static final Map<String, Supplier<Button>> arrows = new HashMap<>();
  private static final List<String> opList = new ArrayList<>();

  static {
    operations.put("C", "C");
    operations.put("CE", "CE");
    operations.put("<-", "\uE94F");//Backspace
    operations.put("+", "\uE948");
    operations.put("-", "\uE949");
    operations.put("*", "\uE947");
    operations.put("/", "\uE94A");
    operations.put("R", "⅟\uD835\uDC65");
    operations.put("POW", "\uD835\uDC65²");
    operations.put("√", "\uE94B");
    operations.put("±", "\uE94D");
    operations.put("=", "\uE94E");
    operations.put("%", "\uE94C");

    memoryOp.put("M+", "M+");
    memoryOp.put("M-", "M-");
    memoryOp.put("MS", "MS");
    memoryOp.put("MC", "MC");
    memoryOp.put("MR", "MR");

    arrows.put("<", () -> robot.lookup("#leftFormulaButton").queryButton());
    arrows.put(">", () -> robot.lookup("#rightFormulaButton").query());

    opList.addAll(operations.keySet());
    opList.addAll(memoryOp.keySet());
    opList.addAll(arrows.keySet());
  }


  protected void clicker(String pattern) {
    pattern = translatePattern(pattern);
    for (String s : pattern.split(" ")) {
      if (operations.containsKey(s)) {
        clickOn(operations.get(s));
      } else if (memoryOp.containsKey(s)) {
        clickOnMemory(memoryOp.get(s));
      } else if (arrows.containsKey(s)) {
        clickOn(arrows.get(s).get());
      } else {
        handleDigit(s);
      }
    }
  }

  private String translatePattern(String pattern) {
    StringBuilder patternBuilder = new StringBuilder(pattern);
    for (int i = pattern.length(); i >= 0; --i) {
      String patternBuf = pattern.substring(0, i);
      opList.stream().filter(patternBuf::endsWith).max(Comparator.comparing(String::length)).
          ifPresent((String x) -> {
            int index = patternBuf.lastIndexOf(x);
            patternBuilder.insert(index, " ");
            patternBuilder.insert(index + x.length() + 1, " ");
          });
    }
    return patternBuilder.toString();
  }

  protected void clickOn(String query) {
    Node node = robot.from(robot.lookup(".numpad").queryAll()).lookup(hasText(query)).queryButton();
    clickOn(node);
  }

  protected void clickOn(Node node) {
    Bounds boundsInScreen = node.localToScreen(node.getBoundsInLocal());
    int x = (int) (boundsInScreen.getMinX() + (boundsInScreen.getMaxX() - boundsInScreen.getMinX()) / 2.0d);
    int y = (int) (boundsInScreen.getMinY() + (boundsInScreen.getMaxY() - boundsInScreen.getMinY()) / 2.0d);


    awtRobot.mouseMove(x, y);
    awtRobot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
    awtRobot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);


    FXTestUtils.awaitEvents();
  }

  protected void handleDigit(String digit) {
    for (Character c : digit.toCharArray()) {
      clickOn(c.toString());
    }
  }

  protected void clear() {
    clickOn("C");
    FxAssert.verifyThat("#display", hasText("0"));
    FxAssert.verifyThat("#formula", hasText(""));
  }

  protected void clickOnMemory(String query) {
    Node node = robot.lookup(query).queryButton();

    clickOn(node);
  }
}
