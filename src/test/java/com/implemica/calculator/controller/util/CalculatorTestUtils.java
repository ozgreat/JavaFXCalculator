package com.implemica.calculator.controller.util;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import org.loadui.testfx.utils.FXTestUtils;
import org.testfx.api.FxAssert;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationTest;

import java.awt.*;
import java.awt.event.InputEvent;

import static org.testfx.matcher.control.LabeledMatchers.hasText;

public class CalculatorTestUtils extends ApplicationTest {
  protected FxRobot robot = new FxRobot();
  protected static Robot awtRobot;
  protected static final BiMap<String, String> operations = HashBiMap.create();
  protected static final BiMap<String, String> memoryOp = HashBiMap.create();

  static {
    operations.put("C", "C");
    operations.put("CE", "CE");
    operations.put("<-", "\uE94F");//Backspace
    operations.put("+", "\uE948");
    operations.put("-", "\uE949");
    operations.put("*", "\uE947");
    operations.put("/", "\uE94A");
    operations.put("1/x", "⅟\uD835\uDC65");
    operations.put("POW", "\uD835\uDC65²");
    operations.put("SQR", "\uE94B");
    operations.put("N", "\uE94D");
    operations.put("=", "\uE94E");
    operations.put("%", "\uE94C");

    memoryOp.put("M+", "\uF757");
    memoryOp.put("M-", "\uF758");
    memoryOp.put("MS", "\uF756");
    memoryOp.put("MC", "\uF754");
    memoryOp.put("MR", "\uF755");
  }


  protected void clicker(String pattern) {
    for (String s : pattern.split(" ")) {
      if (operations.containsKey(s)) {
        clickOn(operations.get(s));
      } else if (memoryOp.containsKey(s)) {
        clickOnMemory(memoryOp.get(s));
      } else {
        handleDigit(s);
      }
    }
  }

  protected void clickOn(String query) {
    Node node = robot.from(robot.lookup(".numpad").queryAll()).lookup(hasText(query)).queryButton();
    clickOn(node);
  }

  protected void clickOn(Node node){
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
