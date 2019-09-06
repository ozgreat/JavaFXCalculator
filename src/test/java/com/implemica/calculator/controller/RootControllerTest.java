package com.implemica.calculator.controller;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.implemica.calculator.view.Root;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.loadui.testfx.utils.FXTestUtils;
import org.testfx.api.FxAssert;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.awt.*;
import java.awt.event.InputEvent;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.matcher.control.LabeledMatchers.hasText;

@ExtendWith(ApplicationExtension.class)
public class RootControllerTest {
  private FxRobot robot = new FxRobot();
  private static RootController controller;

  static Robot awtRobot;

  static final BiMap<String, String> operations = HashBiMap.create();
  static final BiMap<String, String> memoryOp = HashBiMap.create();

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


  @Start
  static void start(Stage stage) throws IOException, AWTException {
    awtRobot = new Robot();
    Root root = new Root();
    Scene scene = new Scene(root.getFXML());
    controller = root.getLoader().getController();
    stage.setScene(scene);
    stage.show();
  }


  @Test
  void simpleOp() {
    checkOperations("2 +", "2 +", "2");
    checkOperations("3 + + + + +", "3 +", "3");
    checkOperations("5 - - - - -", "5 -", "5");
    checkOperations("8 * * * * *", "8 ×", "8");
    checkOperations("12 / / / / / /", "12 ÷", "12");
    checkOperations("9 / * - + - * / * - + + - * +", "9 +", "9");
    checkOperations("52 - - + /  * - + - / * - + - /", "52 ÷", "52");
    checkOperations("12 + * / * - + - * / / * / * * - + - * /", "12 ÷", "12");
    checkOperations("2 + + + +  =", "", "4");
    checkOperations("4 - - - -  =", "", "0");
    checkOperations("8 * * * * *  =", "", "64");
    checkOperations("100 / / / /  =", "", "1");
  }

  @Test
  void plusTest() {
    checkOperations("2 + 2  =", "", "4");
    checkOperations("7 + 3  =", "", "10");
    checkOperations("1 + 8  =", "", "9");
    checkOperations("5 + 6  =", "", "11");
    checkOperations("3 + 9  =", "", "12");

    checkOperations("0 + 0  =", "", "0");
    checkOperations("1 + 0  =", "", "1");
    checkOperations("8 + 0  =", "", "8");
    checkOperations("0 + 5  =", "", "5");
    checkOperations("0 + 3  =", "", "3");

    checkOperations("2 + 5", "2 +", "5");
    checkOperations("5 + 3", "5 +", "3");
    checkOperations("2 + 3", "2 +", "3");
    checkOperations("2 + 8", "2 +", "8");
    checkOperations("1 + 2", "1 +", "2");

    checkOperations("0 + 0", "0 +", "0");
    checkOperations("1 + 0", "1 +", "0");
    checkOperations("5 + 0", "5 +", "0");
    checkOperations("0 + 2", "0 +", "2");
    checkOperations("0 + 6", "0 +", "6");

    checkOperations("0 +", "0 +", "0");
    checkOperations("2 +", "2 +", "2");
    checkOperations("3 +", "3 +", "3");
    checkOperations("9 +", "9 +", "9");
    checkOperations("8 +", "8 +", "8");

    checkOperations("+ 1", "0 +", "1");
    checkOperations("+ 5", "0 +", "5");
    checkOperations("+ 9", "0 +", "9");
    checkOperations("+ 7", "0 +", "7");
    checkOperations("+ 3", "0 +", "3");

    checkOperations("+", "0 +", "0");

    checkOperations("1 +  =", "", "2");
    checkOperations("5 +  =", "", "10");
    checkOperations("3 +  =", "", "6");
    checkOperations("2 +  =", "", "4");
    checkOperations("7 +  =", "", "14");

    checkOperations("+ 7  =", "", "7");
    checkOperations("+ 2  =", "", "2");
    checkOperations("+ 5  =", "", "5");
    checkOperations("+ 9  =", "", "9");
    checkOperations("+ 1  =", "", "1");

    checkOperations("1 + 5 +", "1 + 5 +", "6");
    checkOperations("2 + 9 +", "2 + 9 +", "11");
    checkOperations("3 + 8 +", "3 + 8 +", "11");
    checkOperations("4 + 7 +", "4 + 7 +", "11");
    checkOperations("5 + 6 +", "5 + 6 +", "11");

    checkOperations("2 + 3 +  =", "", "10");
    checkOperations("9 + 2 +  =", "", "22");
    checkOperations("5 + 7 +  =", "", "24");
    checkOperations("6 + 4 +  =", "", "20");
    checkOperations("7 + 6 +  =", "", "26");

    checkOperations("1 + 1 + 2  =", "", "4");
    checkOperations("6 + 2 + 2  =", "", "10");
    checkOperations("1 + 7 + 3  =", "", "11");
    checkOperations("0 + 2 + 8  =", "", "10");
    checkOperations("5 + 9 + 3  =", "", "17");

    checkOperations("2 + 4  = + 5", "6 +", "5");
    checkOperations("3 + 3  = + 7", "6 +", "7");
    checkOperations("5 + 9  = + 6", "14 +", "6");
    checkOperations("8 + 5  = + 4", "13 +", "4");
    checkOperations("7 + 1  = + 3", "8 +", "3");

    checkOperations("7 + 5  = +", "12 +", "12");
    checkOperations("4 + 3  = +", "7 +", "7");
    checkOperations("2 + 2  = +", "4 +", "4");
    checkOperations("8 + 8  = +", "16 +", "16");
    checkOperations("6 + 1  = +", "7 +", "7");

    checkOperations("3 + 1  = +  =", "", "8");
    checkOperations("5 + 5  = +  =", "", "20");
    checkOperations("8 + 3  = +  =", "", "22");
    checkOperations("4 + 8  = +  =", "", "24");
    checkOperations("1 + 4  = +  =", "", "10");

    checkOperations("5 N + 3  =", "", "-2");
    checkOperations("1 N + 4  =", "", "3");
    checkOperations("6 N + 2  =", "", "-4");
    checkOperations("8 N + 7  =", "", "-1");
    checkOperations("7 N + 6  =", "", "-1");

    checkOperations("4 + 4 N  =", "", "0");
    checkOperations("2 + 7 N  =", "", "-5");
    checkOperations("8 + 3 N  =", "", "5");
    checkOperations("2 + 6 N  =", "", "-4");
    checkOperations("4 + 7 N  =", "", "-3");

    checkOperations("0.1 + 1  =", "", "1.1");
    checkOperations("2.5 + 5  =", "", "7.5");
    checkOperations("8.2 + 9  =", "", "17.2");
    checkOperations("4.3 + 4  =", "", "8.3");
    checkOperations("7.5 + 6  =", "", "13.5");

    checkOperations("7 + 1.2  =", "", "8.2");
    checkOperations("2 + 3.5  =", "", "5.5");
    checkOperations("4 + 5.6  =", "", "9.6");
    checkOperations("5 + 7.8  =", "", "12.8");
    checkOperations("3 + 9.1  =", "", "12.1");
  }

  @Test
  void subTest() {
    checkOperations("2 - 2  =", "", "0");
    checkOperations("7 - 3  =", "", "4");
    checkOperations("1 - 8  =", "", "-7");
    checkOperations("5 - 6  =", "", "-1");
    checkOperations("3 - 9  =", "", "-6");

    checkOperations("0 - 0  =", "", "0");
    checkOperations("1 - 0  =", "", "1");
    checkOperations("8 - 0  =", "", "8");
    checkOperations("0 - 5  =", "", "-5");
    checkOperations("0 - 3  =", "", "-3");

    checkOperations("2 - 5", "2 -", "5");
    checkOperations("5 - 3", "5 -", "3");
    checkOperations("2 - 3", "2 -", "3");
    checkOperations("2 - 8", "2 -", "8");
    checkOperations("1 - 2", "1 -", "2");

    checkOperations("0 - 0", "0 -", "0");
    checkOperations("1 - 0", "1 -", "0");
    checkOperations("5 - 0", "5 -", "0");
    checkOperations("0 - 2", "0 -", "2");
    checkOperations("0 - 6", "0 -", "6");

    checkOperations("0 -", "0 -", "0");
    checkOperations("2 -", "2 -", "2");
    checkOperations("3 -", "3 -", "3");
    checkOperations("9 -", "9 -", "9");
    checkOperations("8 -", "8 -", "8");

    checkOperations("- 1", "0 -", "1");
    checkOperations("- 5", "0 -", "5");
    checkOperations("- 9", "0 -", "9");
    checkOperations("- 7", "0 -", "7");
    checkOperations("- 3", "0 -", "3");

    checkOperations("-", "0 -", "0");

    checkOperations("1 -  =", "", "0");
    checkOperations("5 -  =", "", "0");
    checkOperations("3 -  =", "", "0");
    checkOperations("2 -  =", "", "0");
    checkOperations("7 -  =", "", "0");

    checkOperations("- 7  =", "", "-7");
    checkOperations("- 2  =", "", "-2");
    checkOperations("- 5  =", "", "-5");
    checkOperations("- 9  =", "", "-9");
    checkOperations("- 1  =", "", "-1");

    checkOperations("1 - 5 -", "1 - 5 -", "-4");
    checkOperations("2 - 9 -", "2 - 9 -", "-7");
    checkOperations("3 - 8 -", "3 - 8 -", "-5");
    checkOperations("4 - 7 -", "4 - 7 -", "-3");
    checkOperations("5 - 6 -", "5 - 6 -", "-1");

    checkOperations("2 - 3 -  =", "", "0");
    checkOperations("9 - 2 -  =", "", "0");
    checkOperations("5 - 7 -  =", "", "0");
    checkOperations("6 - 4 -  =", "", "0");
    checkOperations("7 - 6 -  =", "", "0");

    checkOperations("1 - 1 - 2  =", "", "-2");
    checkOperations("6 - 2 - 2  =", "", "2");
    checkOperations("1 - 7 - 3  =", "", "-9");
    checkOperations("0 - 2 - 8  =", "", "-10");
    checkOperations("5 - 9 - 3  =", "", "-7");

    checkOperations("2 - 4  = - 5", "-2 -", "5");
    checkOperations("3 - 3  = - 7", "0 -", "7");
    checkOperations("5 - 9  = - 6", "-4 -", "6");
    checkOperations("8 - 5  = - 4", "3 -", "4");
    checkOperations("7 - 1  = - 3", "6 -", "3");

    checkOperations("7 - 5  = -", "2 -", "2");
    checkOperations("4 - 3  = -", "1 -", "1");
    checkOperations("2 - 2  = -", "0 -", "0");
    checkOperations("8 - 8  = -", "0 -", "0");
    checkOperations("6 - 1  = -", "5 -", "5");

    checkOperations("3 - 1  = -  =", "", "0");
    checkOperations("5 - 5  = -  =", "", "0");
    checkOperations("8 - 3  = -  =", "", "0");
    checkOperations("4 - 8  = -  =", "", "0");
    checkOperations("1 - 4  = -  =", "", "0");

    checkOperations("5 N - 3  =", "", "-8");
    checkOperations("1 N - 4  =", "", "-5");
    checkOperations("6 N - 2  =", "", "-8");
    checkOperations("8 N - 7  =", "", "-15");
    checkOperations("7 N - 6  =", "", "-13");

    checkOperations("4 - 4 N  =", "", "8");
    checkOperations("2 - 7 N  =", "", "9");
    checkOperations("8 - 3 N  =", "", "11");
    checkOperations("2 - 6 N  =", "", "8");
    checkOperations("4 - 7 N  =", "", "11");

    checkOperations("0.1 - 1  =", "", "-0.9");
    checkOperations("2.5 - 5  =", "", "-2.5");
    checkOperations("8.2 - 9  =", "", "-0.8");
    checkOperations("4.3 - 4  =", "", "0.3");
    checkOperations("7.5 - 6  =", "", "1.5");

    checkOperations("7 - 1.2  =", "", "5.8");
    checkOperations("2 - 3.5  =", "", "-1.5");
    checkOperations("4 - 5.6  =", "", "-1.6");
    checkOperations("5 - 7.8  =", "", "-2.8");
    checkOperations("3 - 9.1  =", "", "-6.1");
  }

  @Test
  void multiplyTest() {
    checkOperations("2 * 2 =", "", "4");
    checkOperations("7 * 3 =", "", "21");
    checkOperations("1 * 8 =", "", "8");
    checkOperations("5 * 6 =", "", "30");
    checkOperations("3 * 9 =", "", "27");

    checkOperations("0 * 0 =", "", "0");
    checkOperations("1 * 0 =", "", "0");
    checkOperations("8 * 0 =", "", "0");
    checkOperations("0 * 5 =", "", "0");
    checkOperations("0 * 3 =", "", "0");

    checkOperations("2 * 5", "2 ×", "5");
    checkOperations("5 * 3", "5 ×", "3");
    checkOperations("2 * 3", "2 ×", "3");
    checkOperations("2 * 8", "2 ×", "8");
    checkOperations("1 * 2", "1 ×", "2");

    checkOperations("0 * 0", "0 ×", "0");
    checkOperations("1 * 0", "1 ×", "0");
    checkOperations("5 * 0", "5 ×", "0");
    checkOperations("0 * 2", "0 ×", "2");
    checkOperations("0 * 6", "0 ×", "6");

    checkOperations("0 *", "0 ×", "0");
    checkOperations("2 *", "2 ×", "2");
    checkOperations("3 *", "3 ×", "3");
    checkOperations("9 *", "9 ×", "9");
    checkOperations("8 *", "8 ×", "8");

    checkOperations("* 1", "0 ×", "1");
    checkOperations("* 5", "0 ×", "5");
    checkOperations("* 9", "0 ×", "9");
    checkOperations("* 7", "0 ×", "7");
    checkOperations("* 3", "0 ×", "3");

    checkOperations("*", "0 ×", "0");

    checkOperations("1 * =", "", "1");
    checkOperations("5 * =", "", "25");
    checkOperations("3 * =", "", "9");
    checkOperations("2 * =", "", "4");
    checkOperations("7 * =", "", "49");

    checkOperations("* 7 =", "", "0");
    checkOperations("* 2 =", "", "0");
    checkOperations("* 5 =", "", "0");
    checkOperations("* 9 =", "", "0");
    checkOperations("* 1 =", "", "0");

    checkOperations("1 * 5 *", "1 × 5 ×", "5");
    checkOperations("2 * 9 *", "2 × 9 ×", "18");
    checkOperations("3 * 8 *", "3 × 8 ×", "24");
    checkOperations("4 * 7 *", "4 × 7 ×", "28");
    checkOperations("5 * 6 *", "5 × 6 ×", "30");

    checkOperations("2 * 3 * =", "", "36");
    checkOperations("9 * 2 * =", "", "324");
    checkOperations("5 * 7 * =", "", "1,225");
    checkOperations("6 * 4 * =", "", "576");
    checkOperations("7 * 6 * =", "", "1,764");

    checkOperations("1 * 1 * 2 =", "", "2");
    checkOperations("6 * 2 * 2 =", "", "24");
    checkOperations("1 * 7 * 3 =", "", "21");
    checkOperations("0 * 2 * 8 =", "", "0");
    checkOperations("5 * 9 * 3 =", "", "135");

    checkOperations("2 * 4 = * 5", "8 ×", "5");
    checkOperations("3 * 3 = * 7", "9 ×", "7");
    checkOperations("5 * 9 = * 6", "45 ×", "6");
    checkOperations("8 * 5 = * 4", "40 ×", "4");
    checkOperations("7 * 1 = * 3", "7 ×", "3");

    checkOperations("7 * 5 = *", "35 ×", "35");
    checkOperations("4 * 3 = *", "12 ×", "12");
    checkOperations("2 * 2 = *", "4 ×", "4");
    checkOperations("8 * 8 = *", "64 ×", "64");
    checkOperations("6 * 1 = *", "6 ×", "6");

    checkOperations("3 * 1 = * =", "", "9");
    checkOperations("5 * 5 = * =", "", "625");
    checkOperations("8 * 3 = * =", "", "576");
    checkOperations("4 * 8 = * =", "", "1,024");
    checkOperations("1 * 4 = * =", "", "16");

    checkOperations("5 N * 3 =", "", "-15");
    checkOperations("1 N * 4 =", "", "-4");
    checkOperations("6 N * 2 =", "", "-12");
    checkOperations("8 N * 7 =", "", "-56");
    checkOperations("7 N * 6 =", "", "-42");

    checkOperations("4 * 4 N =", "", "-16");
    checkOperations("2 * 7 N =", "", "-14");
    checkOperations("8 * 3 N =", "", "-24");
    checkOperations("2 * 6 N =", "", "-12");
    checkOperations("4 * 7 N =", "", "-28");

    checkOperations("0.1 * 1 =", "", "0.1");
    checkOperations("2.5 * 5 =", "", "12.5");
    checkOperations("8.2 * 9 =", "", "73.8");
    checkOperations("4.3 * 4 =", "", "17.2");
    checkOperations("7.5 * 6 =", "", "45");

    checkOperations("7 * 1.2 =", "", "8.4");
    checkOperations("2 * 3.5 =", "", "7");
    checkOperations("4 * 5.6 =", "", "22.4");
    checkOperations("5 * 7.8 =", "", "39");
    checkOperations("3 * 9.1 =", "", "27.3");
  }

  @Test
  void divideTest() {
    checkOperations("2 / 2 =", "", "1");
    checkOperations("7 / 3 =", "", "2.333333333333333");
    checkOperations("1 / 8 =", "", "0.125");
    checkOperations("5 / 6 =", "", "0.8333333333333333");
    checkOperations("3 / 9 =", "", "0.3333333333333333");

    checkOperations("0 / 5 =", "", "0");
    checkOperations("0 / 3 =", "", "0");

    checkOperations("2 / 5", "2 ÷", "5");
    checkOperations("5 / 3", "5 ÷", "3");
    checkOperations("2 / 3", "2 ÷", "3");
    checkOperations("2 / 8", "2 ÷", "8");
    checkOperations("1 / 2", "1 ÷", "2");

    checkOperations("0 / 0", "0 ÷", "0");
    checkOperations("1 / 0", "1 ÷", "0");
    checkOperations("5 / 0", "5 ÷", "0");
    checkOperations("0 / 2", "0 ÷", "2");
    checkOperations("0 / 6", "0 ÷", "6");

    checkOperations("0 /", "0 ÷", "0");
    checkOperations("2 /", "2 ÷", "2");
    checkOperations("3 /", "3 ÷", "3");
    checkOperations("9 /", "9 ÷", "9");
    checkOperations("8 /", "8 ÷", "8");

    checkOperations("/ 1", "0 ÷", "1");
    checkOperations("/ 5", "0 ÷", "5");
    checkOperations("/ 9", "0 ÷", "9");
    checkOperations("/ 7", "0 ÷", "7");
    checkOperations("/ 3", "0 ÷", "3");

    checkOperations("/", "0 ÷", "0");

    checkOperations("1 / =", "", "1");
    checkOperations("5 / =", "", "1");
    checkOperations("3 / =", "", "1");
    checkOperations("2 / =", "", "1");
    checkOperations("7 / =", "", "1");

    checkOperations("/ 7 =", "", "0");
    checkOperations("/ 2 =", "", "0");
    checkOperations("/ 5 =", "", "0");
    checkOperations("/ 9 =", "", "0");
    checkOperations("/ 1 =", "", "0");

    checkOperations("1 / 5 /", "1 ÷ 5 ÷", "0.2");
    checkOperations("2 / 9 /", "2 ÷ 9 ÷", "0.2222222222222222");
    checkOperations("3 / 8 /", "3 ÷ 8 ÷", "0.375");
    checkOperations("4 / 7 /", "4 ÷ 7 ÷", "0.5714285714285714");
    checkOperations("5 / 6 /", "5 ÷ 6 ÷", "0.8333333333333333");

    checkOperations("2 / 3 / =", "", "1");
    checkOperations("9 / 2 / =", "", "1");
    checkOperations("5 / 7 / =", "", "1");
    checkOperations("6 / 4 / =", "", "1");
    checkOperations("7 / 6 / =", "", "1");

    checkOperations("1 / 1 / 2 =", "", "0.5");
    checkOperations("6 / 2 / 2 =", "", "1.5");
    checkOperations("1 / 7 / 3 =", "", "0.0476190476190476");
    checkOperations("0 / 2 / 8 =", "", "0");
    checkOperations("5 / 9 / 3 =", "", "0.1851851851851852");

    checkOperations("2 /  4 = / 5", "0.5 ÷", "5");
    checkOperations("3 / 3 = / 7", "1 ÷", "7");
    checkOperations("5 / 9 = / 6", "0.5555555555555556 ÷", "6");
    checkOperations("8 / 5 = / 4", "1.6 ÷", "4");
    checkOperations("7 / 1 = / 3", "7 ÷", "3");

    checkOperations("7 / 5 = /", "1.4 ÷", "1.4");
    checkOperations("4 / 3 = /", "1.333333333333333 ÷", "1.333333333333333");
    checkOperations("2 / 2 = /", "1 ÷", "1");
    checkOperations("8 / 8 = /", "1 ÷", "1");
    checkOperations("6 / 1 = /", "6 ÷", "6");

    checkOperations("3 / 1 = / =", "", "1");
    checkOperations("5 / 5 = / =", "", "1");
    checkOperations("8 / 3 = / =", "", "1");
    checkOperations("4 / 8 = / =", "", "1");
    checkOperations("1 / 4 = / =", "", "1");

    checkOperations("5 N / 3 =", "", "-1.66666666666667");
    checkOperations("1 N / 4 =", "", "-0.25");
    checkOperations("6 N / 2 =", "", "-3");
    checkOperations("8 N / 7 =", "", "-1.14285714285714");
    checkOperations("7 N / 6 =", "", "-1.16666666666667");

    checkOperations("4 / 4 N =", "", "-1");
    checkOperations("2 / 7 N =", "", "-0.28571428571429");
    checkOperations("8 / 3 N =", "", "-2.66666666666667");
    checkOperations("2 / 6 N =", "", "-0.33333333333333");
    checkOperations("4 / 7 N =", "", "-0.57142857142857");

    checkOperations("0.1 / 1 =", "", "0.1");
    checkOperations("2.5 / 5 =", "", "0.5");
    checkOperations("8.2 / 9 =", "", "0.9111111111111111");
    checkOperations("4.3 / 4 =", "", "1.075");
    checkOperations("7.5 / 6 =", "", "1.25");

    checkOperations("7 / 1.2 =", "", "5.833333333333333");
    checkOperations("2 / 3.5 =", "", "0.5714285714285714");
    checkOperations("4 / 5.6 =", "", "0.7142857142857143");
    checkOperations("5 / 7.8 =", "", "0.641025641025641");
    checkOperations("3 / 9.1 =", "", "0.3296703296703297");
  }

  @Test
  void negateTest() {
    checkOperations("1 N", "", "-1");
    checkOperations("2 N", "", "-2");
    checkOperations("7 N", "", "-7");
    checkOperations("132 N", "", "-132");
    checkOperations("165 N", "", "-165");

    checkOperations("1.04 N", "", "-1.04");
    checkOperations("100.1 N", "", "-100.1");
    checkOperations("13.2 N", "", "-13.2");
    checkOperations("14.4 N", "", "-14.4");
    checkOperations("31.2 N", "", "-31.2");

    checkOperations("5 N N", "", "5");
    checkOperations("3 N N", "", "3");
    checkOperations("4 N N", "", "4");
    checkOperations("5 N N", "", "5");
    checkOperations("123 N N", "", "123");

    checkOperations("5 + N", "5 + negate( 5 )", "-5");
    checkOperations("21 + N", "21 + negate( 21 )", "-21");
    checkOperations("14 + N", "14 + negate( 14 )", "-14");
    checkOperations("3 + N", "3 + negate( 3 )", "-3");
    checkOperations("7 + N", "7 + negate( 7 )", "-7");

    checkOperations("43 + N =", "", "0");
    checkOperations("0.4 + N =", "", "0");
    checkOperations("3 + N =", "", "0");
    checkOperations("8 + N =", "", "0");
    checkOperations("7 + N =", "", "0");
  }

  @Test
  void powTest() {
    checkOperations("POW", "sqr( 0 )", "0");

    checkOperations("0 POW", "sqr( 0 )", "0");
    checkOperations("1 POW", "sqr( 1 )", "1");
    checkOperations("2 POW", "sqr( 2 )", "4");
    checkOperations("5 POW", "sqr( 5 )", "25");

    checkOperations("5 N POW", "sqr( -5 )", "25");
    checkOperations("2 N POW", "sqr( -2 )", "4");
    checkOperations("6 N POW", "sqr( -6 )", "36");
    checkOperations("4 N POW", "sqr( -4 )", "16");

    checkOperations("5 POW +", "sqr( 5 ) +", "25");
    checkOperations("2 POW -", "sqr( 2 ) -", "4");
    checkOperations("6 POW *", "sqr( 6 ) ×", "36");
    checkOperations("8 POW /", "sqr( 8 ) ÷", "64");

    checkOperations("5 N POW +", "sqr( -5 ) +", "25");
    checkOperations("1 N POW -", "sqr( -1 ) -", "1");
    checkOperations("7 N POW *", "sqr( -7 ) ×", "49");
    checkOperations("9 N POW /", "sqr( -9 ) ÷", "81");

    checkOperations("3 POW + 2", "sqr( 3 ) +", "2");
    checkOperations("4 POW - 5", "sqr( 4 ) -", "5");
    checkOperations("6 POW * 6", "sqr( 6 ) ×", "6");
    checkOperations("8 POW / 7", "sqr( 8 ) ÷", "7");

    checkOperations("2 POW POW", "sqr( sqr( 2 ) )", "16");
    checkOperations("2 POW POW POW", "sqr( sqr( sqr( 2 ) ) )", "256");
    checkOperations("2 SQR POW SQR POW SQR POW", "sqr( √( sqr( √( sqr( √( 2 ) ) ) ) ) )", "2");
    checkOperations("3 POW + 2 POW", "sqr( 3 ) + sqr( 2 )", "4");
    checkOperations("3 POW + 2 POW =", "", "13");
    checkOperations("3 POW + 7 =", "", "16");
    checkOperations("5 + POW POW", "5 + sqr( sqr( 5 ) )", "625");
    checkOperations("5 + POW POW =", "", "630");
    checkOperations("2 + 3 = POW", "sqr( 5 )", "25");


    checkOperations("0.000000000000001 POW", "sqr( 0.000000000000001 )", "1.E-30");
    checkOperations("0.000000000000001 POW POW", "sqr( sqr( 0.000000000000001 ) )", "1.E-60");
    checkOperations("9999999999999999 POW", "sqr( 9999999999999999 )", "9.999999999999998E+31");
    checkOperations("9999999999999999 POW POW", "sqr( sqr( 9999999999999999 ) )", "9.999999999999996E+63");
  }

  @Test
  void equalsTest() {
    checkOperations("=", "", "0");
    checkOperations("+ =", "", "0");
    checkOperations("3 =", "", "3");
    checkOperations("SQR =", "", "0");
    checkOperations("% =", "", "0");
    checkOperations("N =", "", "0");
    checkOperations("2 + 3 = + + +", "5 +", "5");
    checkOperations("239 * 4 = + - * /", "956 ÷", "956");
    checkOperations("10 + =", "", "20");
    checkOperations("10 + = + =", "", "40");
    checkOperations("10 + = + = + =", "", "80");
    checkOperations("7 + 3 = 1 +", "1 +", "1");
    checkOperations("2 + 3 = 4 = =", "", "13");
    checkOperations("1 + 2 = 4 =", "", "7");
    checkOperations("1 + 3 = = =", "", "10");
    checkOperations("289 - 102 = =", "", "85");
    checkOperations("2 * 3 = =", "", "18");
    checkOperations("188 / 2 = =", "", "47");
    checkOperations("5 + 3 =", "", "8");

    checkOperations("4 SQR =", "", "2");
    checkOperations("8 POW =", "", "64");
    checkOperations("2 1/x =", "", "0.5");
    checkOperations("5 + 3 = 1/x", "1/( 8 )", "0.125");
    checkOperations("3 = 6 +", "6 +", "6");
    checkOperations("2 = +", "2 +", "2");
  }

  @Test
  void sqrtTest() {
    checkOperations("0 SQR", "√( 0 )", "0");
    checkOperations("4 SQR", "√( 4 )", "2");
    checkOperations("4 SQR SQR", "√( √( 4 ) )", "1.414213562373095");
    checkOperations("4 SQR SQR SQR", "√( √( √( 4 ) ) )", "1.189207115002721");
    checkOperations("4 SQR SQR SQR =", "", "1.189207115002721");
    checkOperations("3 + 4 SQR =", "", "5");
    checkOperations("4 + SQR", "4 + √( 4 )", "2");
    checkOperations("4 + 1 + 1 + 1 + SQR", "4 + 1 + 1 + 1 + √( 7 )", "2.645751311064591");
    checkOperations("121 SQR", "√( 121 )", "11");
    checkOperations("456 SQR", "√( 456 )", "21.35415650406262");
    checkOperations("2875 SQR", "√( 2875 )", "53.61902647381804");
    checkOperations("1785 SQR", "√( 1785 )", "42.24926034855522");
    checkOperations("2134 SQR", "√( 2134 )", "46.19523784980439");
    checkOperations("4 + 5 = SQR", "√( 9 )", "3");

    checkOperations("0.0000000000000001 SQR", "√( 0.0000000000000001 )", "0.00000001");
    checkOperations("0.0000000000000001 SQR SQR", "√( √( 0.0000000000000001 ) )", "0.0001");

    checkOperations("9999999999999999 SQR", "√( 9999999999999999 )", "100,000,000");
  }

  @Test
  void reverseTest() {
    checkOperations("1 1/x", "1/( 1 )", "1");
    checkOperations("2 1/x", "1/( 2 )", "0.5");
    checkOperations("10 1/x", "1/( 10 )", "0.1");
    checkOperations("1.1 1/x", "1/( 1.1 )", "0.9090909090909091");

    checkOperations("5 1/x +", "1/( 5 ) +", "0.2");
    checkOperations("2 1/x -", "1/( 2 ) -", "0.5");
    checkOperations("6 1/x *", "1/( 6 ) ×", "0.1666666666666667");
    checkOperations("9 1/x /", "1/( 9 ) ÷", "0.1111111111111111");

    checkOperations("10 + 1/x ", "10 + 1/( 10 )", "0.1");
    checkOperations("21 - 1/x ", "21 - 1/( 21 )", "0.0476190476190476");
    checkOperations("32 * 1/x ", "32 × 1/( 32 )", "0.03125");
    checkOperations("42 / 1/x ", "42 ÷ 1/( 42 )", "0.0238095238095238");

    checkOperations("10 + 12 = 1/x ", "1/( 22 )", "0.0454545454545455");
    checkOperations("12 + 43 = 1/x ", "1/( 55 )", "0.0181818181818182");
    checkOperations("21 + 36 = 1/x ", "1/( 57 )", "0.0175438596491228");
    checkOperations("83 + 37 = 1/x ", "1/( 120 )", "0.0083333333333333");

    checkOperations("1000000000000000 - 1/x", "1000000000000000 - 1/( 1000000000000000 )", "0.000000000000001");
    checkOperations("1000000000000000 * = = = = = - 1/x", "1.E+90 - 1/( 1.E+90 )", "1.E-90");

    checkOperations("0.0000000000000001 1/x", "1/( 0.0000000000000001 )", "1.E+16");
    checkOperations("0.0000000000000001 1/x 1/x", "1/( 1/( 0.0000000000000001 ) )", "0.0000000000000001");

    checkOperations("9999999999999999 1/x ", "1/( 9999999999999999 )", "0.0000000000000001");
  }

  @Test
  void backspaceTest() {
    checkOperations("2 POW POW POW = <- <- <- <- <- <- <- <-", "", "256");
    checkOperations("70 / 7 = <- <- <-", "", "10");
    checkOperations("1234567890 = <- <- <- <- <- <- <- <- <- <- <- <- <- <- <- <- <- <- <- <- <-", "", "1,234,567,890");
    checkOperations("<- <- <- <- 2 SQR POW =", "", "2");
    checkOperations("2 + 3 = <- <- <- <-", "", "5");

    checkOperations("<-", "", "0");
    checkOperations(".", "", "0.");
    checkOperations(". . .", "", "0.");
    checkOperations(". <-", "", "0");
    checkOperations(". . . <- <- <-", "", "0");
    checkOperations(". . . <-", "", "0");
    checkOperations("<- .", "", "0.");
    checkOperations("<- . . .", "", "0.");

    checkOperations("<- 0", "", "0");
    checkOperations("0 <-", "", "0");
    checkOperations("0 0 0 0 0 0 <- <- <- <- <-", "", "0");
    checkOperations("0.0001 <-", "", "0.000");
    checkOperations("0.0 <-", "", "0.");
    checkOperations("1.0 <-", "", "1.");
    checkOperations("1.01 <-", "", "1.0");
    checkOperations("12.0 <-", "", "12.");
    checkOperations("12.0 <- <-", "", "12");
    checkOperations("12.0 <- <- <-", "", "1");
    checkOperations("12.0 <- <- <- <-", "", "0");
    checkOperations("<- <- <- 1234.12345", "", "1,234.12345");
    checkOperations("1234.12345 <- <- <-", "", "1,234.12");
    checkOperations("1234.12345 <- <- <- <-", "", "1,234.1");
    checkOperations("1234.12345 <- <- <- <- <- ", "", "1,234.");
    checkOperations("1234.12345 <- <- <- <- <- <-", "", "1,234");
    checkOperations("1234.12345 <- <- <- <- <- <- <-", "", "123");

    checkOperations("1000000000000000.", "", "1,000,000,000,000,000.");
    checkOperations("1000000000000000. <-", "", "1,000,000,000,000,000");
    checkOperations("1000000000000000. <- . 0 0", "", "1,000,000,000,000,000.");
    checkOperations("1000000000000000. <- . 10", "", "1,000,000,000,000,000.");
    checkOperations("1000000000000000. <- 0", "", "1,000,000,000,000,000");
  }

  @Test
  void clearEntryTest() {
    checkOperations("CE", "", "0");

    checkOperations("5 CE ", "", "0");
    checkOperations("85 CE", "", "0");
    checkOperations("894 CE", "", "0");
    checkOperations("1245 CE", "", "0");

    checkOperations("1245 CE 45", "", "45");
    checkOperations("9845 CE 23", "", "23");
    checkOperations("1889 CE 87", "", "87");
    checkOperations("4651 CE 62", "", "62");

    checkOperations("4651 CE 21 CE ", "", "0");
    checkOperations("7621 CE 45 CE ", "", "0");
    checkOperations("9276 CE 32 CE ", "", "0");
    checkOperations("4628 CE 70 CE ", "", "0");

    checkOperations("1 + 2 / 3 CE 9", "1 + 2 ÷", "9");
    checkOperations("1 - 2 * 3 CE 9", "1 - 2 ×", "9");
    checkOperations("1 * 2 - 3 CE 9", "1 × 2 -", "9");
    checkOperations("1 / 2 + 3 CE 9", "1 ÷ 2 +", "9");

    checkOperations("1 CE 5 CE 8 + 2 CE 6 / 1 CE 5", "8 + 6 ÷", "5");
    checkOperations("2 CE 6 CE 9 - 3 CE 7 * 2 CE 6", "9 - 7 ×", "6");
    checkOperations("3 CE 7 CE 0 * 4 CE 8 - 3 CE 7", "0 × 8 -", "7");
    checkOperations("4 CE 8 CE 1 / 5 CE 9 + 4 CE 8", "1 ÷ 9 +", "8");
  }

  @Test
  void percentTest() {
    checkOperations("%", "", "0");

    checkOperations("2 %", "", "0");
    checkOperations("168 %", "", "0");
    checkOperations("25 %", "", "0");
    checkOperations("7 % %", "", "0");

    checkOperations("6 N %", "", "0");
    checkOperations("2 N %", "", "0");
    checkOperations("4 N %", "", "0");
    checkOperations("5 N %", "", "0");

    checkOperations("10 % % % =", "", "0");
    checkOperations("32 % % % =", "", "0");
    checkOperations("41 % % % =", "", "0");
    checkOperations("12 % % % =", "", "0");

    checkOperations("200 + 2 %", "200 + 4", "4");
    checkOperations("152 + 3 %", "152 + 4.56", "4.56");
    checkOperations("234 + 4 %", "234 + 9.36", "9.36");
    checkOperations("543 + 5 %", "543 + 27.15", "27.15");

    checkOperations("5 + %", "5 + 0.25", "0.25");
    checkOperations("1 + %", "1 + 0.01", "0.01");
    checkOperations("3 + %", "3 + 0.09", "0.09");
    checkOperations("4 + %", "4 + 0.16", "0.16");

    checkOperations("200 + 2 % =", "", "204");
    checkOperations("152 + 3 % =", "", "156.56");
    checkOperations("234 + 4 % =", "", "243.36");
    checkOperations("543 + 5 % =", "", "570.15");

    checkOperations("200 + 2 % = %", "", "416.16");
    checkOperations("152 + 3 % = %", "", "245.110336");
    checkOperations("234 + 4 % = %", "", "592.240896");
    checkOperations("543 + 5 % = %", "", "3,250.710225");

    checkOperations("199 + 1 = %", "", "400");
    checkOperations("199 + 1 = % %", "", "800");
    checkOperations("199 + 1 = % % %", "", "1,600");
    checkOperations("199 + 1 = % % % %", "", "3,200");

    checkOperations("200 + 25 % % % % % % % % %", "", "12,800");
    checkOperations("200 + 25 % % % % % % % % % =", "", "13,000");
    checkOperations("300 N + 15 % =", "", "-345");
    checkOperations("245 + 63 N % =", "", "90.65");
  }

  @Test
  void displayFormatTest() {
    checkOperations("1", "", "1");
    checkOperations("2", "", "2");
    checkOperations("3", "", "3");
    checkOperations("4", "", "4");
    checkOperations("5", "", "5");
    checkOperations("6", "", "6");
    checkOperations("7", "", "7");
    checkOperations("8", "", "8");
    checkOperations("9", "", "9");

    checkOperations("0000000000000000000000000000000000000001", "", "1");
    checkOperations("01010111101010101000011", "", "1,010,111,101,010,101");
    checkOperations("123456789", "", "123,456,789");
    checkOperations("1000000000000000000", "", "1,000,000,000,000,000");
    checkOperations("1000000000000000000.", "", "1,000,000,000,000,000.");
    checkOperations("9999999999999999999.", "", "9,999,999,999,999,999.");
    checkOperations("1234567890123456789", "", "1,234,567,890,123,456");

    checkOperations("3.0", "", "3.0");
    checkOperations("3.00", "", "3.00");
    checkOperations("0.00000000000000000", "", "0.0000000000000000");
    checkOperations("3.00000000000000000", "", "3.000000000000000");
    checkOperations("3.000000000000000001", "", "3.000000000000000");
    checkOperations("10.000000000000000001", "", "10.00000000000000");
    checkOperations("100.000000000000000001", "", "100.0000000000000");
    checkOperations("1000.000000000000000001", "", "1,000.000000000000");
    checkOperations("100000000000000.000000000000000001", "", "100,000,000,000,000.0");
    checkOperations("1000000000000000.000000000000000001", "", "1,000,000,000,000,000.");
  }

  @Test
  void roundTest() {
    checkOperations("0.0000000000000001 + 1 =", "", "1");
    checkOperations("1 / 3 * 3 =", "", "1");
    checkOperations("1 / 3 * 3 - 1", "1 ÷ 3 × 3 -", "1");
    checkOperations("0.0111111111111111 * 0.1 =", "", "0.0011111111111111");
    /*checkOperations("0.0111111111111111 * 0.1 = =", "", "1.11111111111111E-4");
    checkOperations("0.0111111111111111 * 0.1 = = =", "", "1.11111111111111E-5");
    checkOperations("1 / 3 * 3 - 1 =", "", "0");*/
    checkOperations("2.0000000000000001 + 1 = = = = = = = =", "", "10");
    checkOperations("0.1 * = = = = = = = = = = = = = = = =", "", "1.E-17");
//    checkOperations("0.9 * = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =", "", "9,550049507968252e-4");
    checkOperations("9999999999999999 + 2 =", "", "1.E+16");
    checkOperations("9999999999999999 * 2 =", "", "2.E+16");
    checkOperations("9999999999999999 * 8 =", "", "7.999999999999999E+16");
    checkOperations("9999999999999999 * 8 = =", "", "6.399999999999999E+17");
    checkOperations("9999999999999999 * 8 = = =", "", "5.119999999999999E+18");
    checkOperations("9999999999999999 * 8 = = = =", "", "4.096E+19");
    checkOperations("9999999999999999 * 6 =", "", "5.999999999999999E+16");
    checkOperations("9999999999999999 * 6 = =", "", "3.6E+17");
    checkOperations("9999999999999999 * 6 = = =", "", "2.16E+18");
    checkOperations("9999999999999999 * 6 = =  = =", "", "1.296E+19");
    checkOperations("9999999999999999 * 6 = = = = =", "", "7.775999999999999E+19");
    checkOperations("9999999999999999 + 6 =", "", "1.000000000000001E+16");
    checkOperations("9999999999999999 + 7 =", "", "1.000000000000001E+16");
    checkOperations("9999999999999999 1/x + 1 =", "", "1");
    checkOperations("9999999999999999 1/x =", "", "0.0000000000000001");
    checkOperations("9999999999999999 1/x + =", "", "0.0000000000000002");
    checkOperations("9999999999999999 1/x + = =", "", "0.0000000000000003");
    checkOperations("9999999999999999 1/x + = = =", "", "0.0000000000000004");
    checkOperations("2.000000000000001 + 1 =", "", "3.000000000000001");
    checkOperations("2.000000000000001 + 2 =", "", "4.000000000000001");
    checkOperations("2.000000000000001 + 3 =", "", "5.000000000000001");
    checkOperations("2.000000000000001 + 4 =", "", "6.000000000000001");
    checkOperations("2.000000000000001 + 8 =", "", "10");

    checkOperations("0.0000000000000001 + =", "", "0.0000000000000002");
    checkOperations("0.0000000000000001 - =", "", "0");
//    checkOperations("1 / 7 * 7 - 1 =", "", "0");
//    checkOperations("1 / 7 * 1000000000000000 * 7 - 1000000000000000 =", "", "0");
//    checkOperations("1 / 3 / 7 / 11 / 13 / 17 * 1000000000000000 * 3 * 7 * 11 * 13 * 17 - 1000000000000000 =", "", "0");
    checkOperations("5 SQR SQR SQR SQR SQR  SQR SQR SQR SQR SQR SQR SQR SQR SQR SQR SQR SQR SQR SQR SQR SQR SQR SQR SQR SQR SQR SQR SQR SQR SQR SQR SQR SQR SQR SQR SQR SQR SQR SQR SQR SQR SQR SQR SQR SQR SQR SQR SQR SQR SQR =", "", "1.000000000000001");
//    checkOperations("1 / 3 * 0.0000000000000001 * 0.00000000001 * 1000000000000000 * 10000000000000 * 3 =", "", "10");
    checkOperations("0.9999999999999999 * 999999999999999.1 =", "", "999,999,999,999,999");
    checkOperations("0.9999999999999999 * 999999999999999.3 =", "", "999,999,999,999,999.2");
    checkOperations("0.9999999999999999 * 999999999999999.5 =", "", "999,999,999,999,999.4");
    checkOperations("0.9999999999999999 * 999999999999999.7 =", "", "999,999,999,999,999.6");
    checkOperations("0.9999999999999999 * 99999999999999.11 =", "", "99,999,999,999,999.1");

    checkOperations("10 / 3 = = * 1000000000000000 = = = = = = = =", "", "1.111111111111111E+120");
  }

  @Test
  void memoryTest() {
    checkOperations("MR", "", "0");
    checkOperations("M+ MR", "", "0");
    checkOperations("M- MR", "", "0");

    // takes from operand
    checkOperations("4 M+ MR ", "", "4");
    checkOperations("4 M+ M+ MR ", "", "8");
    checkOperations("4 N M+ MR ", "", "-4");
    checkOperations("4 N M+ M+ MR ", "", "-8");

    checkOperations("4 M- MR ", "", "-4");
    checkOperations("4 M- M- MR ", "", "-8");
    checkOperations("4 N M- MR ", "", "4");
    checkOperations("4 N M- M- MR ", "", "8");

    checkOperations("0 M- MR ", "", "0");
    checkOperations("0 M- M- MR ", "", "0");
    checkOperations("0 N M- MR ", "", "0");
    checkOperations("0 N M- M- MR ", "", "0");

    checkOperations("0 M+ MR ", "", "0");
    checkOperations("0 M+ M+ MR ", "", "0");
    checkOperations("0 N M+  MR ", "", "0");
    checkOperations("0 N M+ M+ MR ", "", "0");

    checkOperations(". M+ MR", "", "0");
    checkOperations("0. M+ MR", "", "0");
    checkOperations(". N M+ MR", "", "0");
    checkOperations("0. N M+ MR", "", "0");

    checkOperations(". M- MR", "", "0");
    checkOperations("0. M- MR", "", "0");
    checkOperations(". N M- MR", "", "0");
    checkOperations("0. N M- MR", "", "0");

    checkOperations("1 M+ M+ / MR =", "", "0.5");
    checkOperations("2 M+ M+ * MR =", "", "8");
    checkOperations("3 M+ M+ - MR =", "", "-3");
    checkOperations("4 M+ M+ + MR =", "", "12");

    checkOperations("1 M- M- / MR =", "", "-0.5");
    checkOperations("2 M- M- * MR =", "", "-8");
    checkOperations("3 M- M- - MR =", "", "9");
    checkOperations("4 M- M- + MR =", "", "-4");

    checkOperations("1 M+ M+ M+ M+ MR =", "", "4");
    checkOperations("2 M+ M+ M+ M- MR =", "", "4");
    checkOperations("3 M+ M+ M- M- MR =", "", "0");
    checkOperations("4 M+ M- M- M- MR =", "", "-8");

    // takes from result
    checkOperations("1 + 2 - 3 * 4 / 5 + M+ MR =", "", "0");
    checkOperations("1 - 2 * 3 / 4 + 5 + M+ MR =", "", "8.5");
    checkOperations("1 * 2 / 3 + 4 + 5 - M+ MR =", "", "0");
    checkOperations("1 / 2 + 3 + 4 - 5 * M+ MR =", "", "6.25");

    checkOperations("2 + 2 = M+ M+ M+ M+ MR + 0 =", "", "16");
    checkOperations("1 + 5 = M+ M+ M+ M+ MR + 0 =", "", "24");
    checkOperations("3 + 6 = M+ M+ M+ M+ MR + 0 =", "", "36");
    checkOperations("4 + 7 = M+ M+ M+ M+ MR + 0 =", "", "44");

    checkOperations("0 + 20 M- M- M- M- M- = - MR =", "", "120");
    checkOperations("1 + 21 M- M- M- M- M- = + MR =", "", "-83");
    checkOperations("2 + 22 M- M- M- M- M- = * MR =", "", "-2,640");
    checkOperations("3 + 23 M- M- M- M- M- = / MR =", "", "-0.22608695652174");
  }

  @Test
  void overflowTest() {
    checkForBigFormula("1000000000000000 POW POW POW POW POW POW POW POW POW", "sqr( sqr( sqr( sqr( sqr( sqr( sqr( sqr( sqr( 1000000000000000 ) ) ) ) ) ) ) ) )", "1.E+7680");
    checkErrorOp("1000000000000000 POW POW POW POW POW POW POW POW POW POW", "sqr( sqr( sqr( sqr( sqr( sqr( sqr( sqr( sqr( sqr( 1000000000000000 ) ) ) ) ) ) ) ) ) )", "Overflow");
    checkErrorOp("1000000000000000 POW POW POW POW POW POW POW POW POW POW POW", "sqr( sqr( sqr( sqr( sqr( sqr( sqr( sqr( sqr( sqr( 1000000000000000 ) ) ) ) ) ) ) ) ) )", "Overflow");
    checkErrorOp("1000000000000000 POW POW POW POW POW POW POW POW POW POW POW POW POW POW POW", "sqr( sqr( sqr( sqr( sqr( sqr( sqr( sqr( sqr( sqr( 1000000000000000 ) ) ) ) ) ) ) ) ) )", "Overflow");

    checkForBigFormula("0.0000000000000001 POW POW POW POW POW POW POW POW POW", "sqr( sqr( sqr( sqr( sqr( sqr( sqr( sqr( sqr( 0.0000000000000001 ) ) ) ) ) ) ) ) )", "1.E-8192");
    checkErrorOp("0.0000000000000001 POW POW POW POW POW POW POW POW POW POW", "sqr( sqr( sqr( sqr( sqr( sqr( sqr( sqr( sqr( sqr( 0.0000000000000001 ) ) ) ) ) ) ) ) ) )", "Overflow");
    checkErrorOp("0.0000000000000001 POW POW POW POW POW POW POW POW POW POW POW", "sqr( sqr( sqr( sqr( sqr( sqr( sqr( sqr( sqr( sqr( 0.0000000000000001 ) ) ) ) ) ) ) ) ) )", "Overflow");
    checkErrorOp("0.0000000000000001 POW POW POW POW POW POW POW POW POW POW POW POW POW POW POW", "sqr( sqr( sqr( sqr( sqr( sqr( sqr( sqr( sqr( sqr( 0.0000000000000001 ) ) ) ) ) ) ) ) ) )", "Overflow");

    checkOperations("9999999999999999 + 1 = * = * = * = * = * = * = * = * = * =", "", "1.E+8192");
    checkOperations("9999999999999999 + 1 = * = * = * = * = * = * = * = * = * = * =", "", "Overflow");
    checkOperations("9999999999999999 N - 1 = * = * = * = * = * = * = * = * = * = * =", "", "Overflow");
  }

  @Test
  void cannotDivideByZeroTest() {
    checkErrorOp("0 1/x", "1/( 0 )", "Cannot divide by zero");


    for (int i = 1; i <= 100; i++) {
      checkErrorOp(i + " / 0 =", "", "Cannot divide by zero");
      checkErrorOp(i + " N / 0 =", "", "Cannot divide by zero");
    }

    checkErrorOp("9999999999999998 / 0 =", "", "Cannot divide by zero");
    checkErrorOp("9999999999999999 / 0 =", "", "Cannot divide by zero");
    checkErrorOp("9999999999999998 N / 0 =", "", "Cannot divide by zero");
    checkErrorOp("9999999999999999 N / 0 =", "", "Cannot divide by zero");
    checkErrorOp("9999999999999999 + 1 = / 0 =", "", "Cannot divide by zero");
    checkErrorOp("9999999999999999 + 1 = N / 0 =", "", "Cannot divide by zero");
  }


  @Test
  void resultIsUndefinedTest() {
    checkErrorOp("0 / 0 =", "", "Result is undefined");

    for (int i = 1; i <= 100; i++) {
      checkErrorOp(i + " N SQR", "√( -" + i + " )", "Result is undefined");
    }
    checkErrorOp("9999999999999998 = N SQR", "√( -9999999999999998 )", "Result is undefined");
    checkErrorOp("9999999999999999 = N SQR", "√( -9999999999999999 )", "Result is undefined");
    checkErrorOp("9999999999999999 + 1 = N SQR", "√( -1.E+16 )", "Result is undefined");
  }

  @Test
  void setNormalTest() {
    checkSetNormal("0 1/x =", "", "0");
    checkSetNormal("0 1/x <-", "", "0");
    checkSetNormal("0 1/x C", "", "0");
    checkSetNormal("0 1/x CE", "", "0");
    checkSetNormal("0 1/x 9", "", "9");


    for (int i = 1; i <= 100; i++) {
      checkSetNormal(i + " / 0 = =", "", "0");
      checkSetNormal(i + " N / 0 = =", "", "0");
    }
    //todo: more cases
  }

  void checkOperations(String pattern, String formula, String res) {
    clicker(pattern);
    FxAssert.verifyThat("#display", hasText(res));
    FxAssert.verifyThat("#formula", hasText(formula));
    clear();
  }

  void checkSetNormal(String pattern, String formula, String res) {
    clicker(pattern);
    FxAssert.verifyThat("#display", hasText(res));
    assertEquals(formula, controller.getFormulaStr());
    assertFalse(controller.getAddButton().isDisabled());
    assertFalse(controller.getSubtractButton().isDisabled());
    assertFalse(controller.getMultiplyButton().isDisabled());
    assertFalse(controller.getDivideButton().isDisabled());
    assertFalse(controller.getSqrtButton().isDisabled());
    assertFalse(controller.getPowButton().isDisabled());
    assertFalse(controller.getPointButton().isDisabled());
    assertFalse(controller.getReverseButton().isDisabled());
    /*assertFalse(controller.getMemoryClearButton().isDisabled());
    assertFalse(controller.getMemoryMinusButton().isDisabled());
    assertFalse(controller.getMemoryPlusButton().isDisabled());
    assertFalse(controller.getMemoryRecallButton().isDisabled());
    assertFalse(controller.getMemorySaveButton().isDisabled());
    assertFalse(controller.getMemoryShow().isDisabled());*/
    clear();
  }

  void clicker(String pattern) {
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

  void checkForBigFormula(String pattern, String formula, String res) {
    clicker(pattern);
    FxAssert.verifyThat("#display", hasText(res));

    assertEquals(formula, controller.getFormulaStr());
    clear();
  }

  void checkErrorOp(String pattern, String formula, String res) {
    clicker(pattern);

    FxAssert.verifyThat("#display", hasText(res));
    assertEquals(formula, controller.getFormulaStr());
    assertTrue(controller.getAddButton().isDisabled());
    assertTrue(controller.getSubtractButton().isDisabled());
    assertTrue(controller.getMultiplyButton().isDisabled());
    assertTrue(controller.getDivideButton().isDisabled());
    assertTrue(controller.getSqrtButton().isDisabled());
    assertTrue(controller.getPowButton().isDisabled());
    assertTrue(controller.getPointButton().isDisabled());
    assertTrue(controller.getReverseButton().isDisabled());
    assertTrue(controller.getMemoryClearButton().isDisabled());
    assertTrue(controller.getMemoryMinusButton().isDisabled());
    assertTrue(controller.getMemoryPlusButton().isDisabled());
    assertTrue(controller.getMemoryRecallButton().isDisabled());
    assertTrue(controller.getMemorySaveButton().isDisabled());
    assertTrue(controller.getMemoryShow().isDisabled());
    clear();
  }

  void clickOn(String query) {
    Node node = robot.from(robot.lookup(".numpad").queryAll()).lookup(hasText(query)).queryButton();
    Bounds boundsInScreen = node.localToScreen(node.getBoundsInLocal());
    int x = (int) (boundsInScreen.getMinX() + (boundsInScreen.getMaxX() - boundsInScreen.getMinX()) / 2.0d);
    int y = (int) (boundsInScreen.getMinY() + (boundsInScreen.getMaxY() - boundsInScreen.getMinY()) / 2.0d);


    awtRobot.mouseMove(x, y);
    awtRobot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
    awtRobot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);


    FXTestUtils.awaitEvents();
  }

  void clickOnMemory(String query) {
    Node node = robot.lookup(query).queryButton();

    Bounds boundsInScreen = node.localToScreen(node.getBoundsInLocal());
    int x = (int) (boundsInScreen.getMinX() + (boundsInScreen.getMaxX() - boundsInScreen.getMinX()) / 2.0d);
    int y = (int) (boundsInScreen.getMinY() + (boundsInScreen.getMaxY() - boundsInScreen.getMinY()) / 2.0d);


    awtRobot.mouseMove(x, y);
    awtRobot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
    awtRobot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

    FXTestUtils.awaitEvents();
  }

  void handleDigit(String digit) {
    for (Character c : digit.toCharArray()) {
      clickOn(c.toString());
    }
  }

  void clear() {
    clickOn("C");
    FxAssert.verifyThat("#display", hasText("0"));
    FxAssert.verifyThat("#formula", hasText(""));
    clickOnMemory(memoryOp.get("MC"));
  }
}
