package com.ozgreat.calculator.controller;

import com.ozgreat.calculator.controller.util.ControllerTestUtils;
import com.ozgreat.calculator.view.Root;
import javafx.geometry.Bounds;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.loadui.testfx.utils.FXTestUtils;
import org.testfx.api.FxAssert;
import org.testfx.framework.junit5.ApplicationExtension;

import java.awt.*;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.matcher.control.LabeledMatchers.hasText;

@ExtendWith(ApplicationExtension.class)
public class RootControllerMouseTest extends ControllerTestUtils {
  private RootController controller;

  @Override
  public void start(Stage stage) throws IOException, AWTException {
    awtRobot = new Robot();
    Root root = Root.getRoot();
    Scene scene = root.getScene();
    stage.setScene(scene);
    controller = root.getLoader().getController();
    stage.show();
  }

  @Test
  void memoryTest() {
    clickOn(lookup("#memoryClearButton").queryButton());
    memoryCheck("MR", "0");
    memoryCheck("M+ MR", "0");
    memoryCheck("M- MR", "0");

    // takes from operand
    memoryCheck("4 M+ MR ", "4");
    memoryCheck("4 M+ M+ MR ", "8");

    memoryCheck("4 M- MR ", "-4");
    memoryCheck("4 M- M- MR ", "-8");

    memoryCheck("0 M- MR ", "0");
    memoryCheck("0 M- M- MR ", "0");

    memoryCheck("0 M+ MR ", "0");
    memoryCheck("0 M+ M+ MR ", "0");

    memoryCheck(". M+ MR", "0");
    memoryCheck("0. M+ MR", "0");

    memoryCheck(". M- MR", "0");
    memoryCheck("0. M- MR", "0");

    memoryCheck("2 M+ M+ * MR=", "8");
    memoryCheck("3 M+ M+ - MR=", "-3");
    memoryCheck("4 M+ M+ + MR=", "12");

    memoryCheck("1 M- M- / MR=", "-0.5");
    memoryCheck("2 M- M- * MR=", "-8");
    memoryCheck("3 M- M- - MR=", "9");
    memoryCheck("4 M- M- + MR=", "-4");

    memoryCheck("1 M+ M+ M+ M+ MR=", "4");
    memoryCheck("2 M+ M+ M+ M- MR=", "4");
    memoryCheck("3 M+ M+ M- M- MR=", "0");
    memoryCheck("4 M+ M- M- M- MR=", "-8");

    // takes from result
    memoryCheck("1+ 2- 3* 4/5+ M+ MR=", "0");
    memoryCheck("1- 2* 3/4+ 5+ M+ MR=", "8.5");
    memoryCheck("1/2+ 3+ 4- 5* M+ MR=", "6.25");

    memoryCheck("2+ 2= M+ M+ M+ M+ MR + 0=", "16");
    memoryCheck("1+ 5= M+ M+ M+ M+ MR+ 0=", "24");
    memoryCheck("3+ 6= M+ M+ M+ M+ MR+ 0=", "36");
    memoryCheck("4+ 7= M+ M+ M+ M+ MR+ 0=", "44");

    memoryCheck("0+ 20 M- M- M- M- M-=- MR=", "120");
    memoryCheck("1+ 21 M- M- M- M- M-=+ MR=", "-83");
    memoryCheck("2+ 22 M- M- M- M- M-=* MR=", "-2,640");
    memoryCheck("3+ 23 M- M- M- M- M-=/MR=", "-0.2260869565217391");

    //Memory save
    memoryCheck("1234567890 MS + 20= M+ MR", "2,469,135,800");
    memoryCheck("9999999999999999 MS M+ MR", "2.E+16");
  }

  @Test
  void mixOfOperations() {
    checkForBigFormula("300+5=%√-1000000000000000= ^ + 9999999999999999 R * 1000000000000000 / 50", "sqr( -999999999999982.5 ) + 1/( 9999999999999999 ) × 1000000000000000 ÷", "50");
    checkOperations("400 + % * 123.3 √ R / 55 * 12253 + 124± - 73 CE 81", "81");
    checkOperations("0.01 * 123 + % = √ ^ + 20 R / 7.1 - % + 876 CE 765± ", "-765");
    checkOperations("82 + 1 - 23 C 210 + ± * 67.0987 ^ R / 8", "8");
    checkOperations("1 + 34 = C 123 ± + 3 % * -100 + 123.4 + 6 ^ - 90 R", "-123 + -3.69 - 100 + 123.4 + sqr( 6 ) - 1/( 90 )", "0.0111111111111111");

    checkOperations("12 - 52 C 123 <-<-<- + 125.4 R + 23 ^ - 89 √ * 71 - 1± %", "-368.8975352782175");
    checkOperations("25 * 12 C 234 + 34 <- 5 R + 32 ^ * 250 √ + / % %", "1.989117823641056");
    checkOperations("234 / 2 C 8765 - <-<- 76 = ^ ^ + R - 234 √ + * 123", "123");
    checkOperations("12 + C 234 CE 324 - R + 876 ^ * 637 √ %", "0.2523885892824793");
    checkOperations("83 + 2 = C 21 - √ * 90 ^ + 8 R - %", "289,340,525.2501563");

    checkOperations("200+4% R R ^ √", "200 + 8 √(sqr(1/(1/( 8 ) ) ) )", "8");
    checkOperations("1/3= R ", "1/( 0.3333333333333333 )", "3");
  }

  @Test
  void historyOpenTest() {
    clickOn(robot.lookup("\uF738").query());

    assertTrue(robot.lookup("#historyLabel").queryLabeled().isVisible());
    assertFalse(robot.lookup("#historyPane").query().isDisabled());
    FxAssert.verifyThat(robot.lookup("#historyLabel").queryLabeled(), hasText("There's no history yet"));

    clickOn(robot.lookup("\uF738").query());

    assertFalse(robot.lookup("#historyLabel").queryLabeled().isVisible());
    assertTrue(robot.lookup("#historyPane").query().isDisabled());
  }

  @Test
  synchronized void openSideBarTest() throws InterruptedException {
    BorderPane sideMenu = robot.lookup("#sideMenuBorderPane").queryAs(BorderPane.class);
    Bounds b = sideMenu.localToScreen(sideMenu.getBoundsInLocal());
    int oldX = (int) (b.getMinX() + (b.getMaxX() - b.getMinX()) / 2.0d);
    clickOn(robot.lookup("\uE700").query());
    wait(300);
    b = sideMenu.localToScreen(sideMenu.getBoundsInLocal());
    int newX = (int) (b.getMinX() + (b.getMaxX() - b.getMinX()) / 2.0d);
    assertEquals(oldX + 272, newX);
    assertTrue(robot.lookup("#sideBarOffPane").query().isVisible());
    assertFalse(robot.lookup(hasText("Standard")).queryLabeled().isVisible());

    clickOn(robot.lookup("\uF738").query());
    wait(300);
    b = sideMenu.localToScreen(sideMenu.getBoundsInLocal());
    newX = (int) (b.getMinX() + (b.getMaxX() - b.getMinX()) / 2.0d);
    assertEquals(oldX, newX);
    assertFalse(robot.lookup("#sideBarOffPane").query().isVisible());
    assertTrue(robot.lookup(hasText("Standard")).queryLabeled().isVisible());
  }

  @Test
  void memoryShowTest() {
    clickOnMemory("MS");
    clickOn(robot.lookup("#memoryShow").queryButton());

    assertFalse(robot.lookup("#historyPane").query().isDisabled());
    assertFalse(robot.lookup("#historyLabel").queryLabeled().isVisible());


    clickOn(robot.lookup("#memoryShow").queryButton());

    assertTrue(robot.lookup("#historyPane").query().isDisabled());
  }

  @Test
  void arrowFormulaTest() {
    formulaButtonVisibleCheck("9999999999999999+9999999999999999+9999999999999999+", "9999999999999999 + 9999999999999999 + 9999999999999999 +");
    formulaButtonVisibleCheck("123456789012345+123456789012345+123456789012345+", "123456789012345 + 123456789012345 + 123456789012345 +");

    formulaMoveCheck("9999999999999999+ 9999999999999999+ 9999999999999999+ <", "9999999999999999 + 9999999999999999 + 9999999999999999 +", "9999999999999999 + 9999999999999999 + ");
    formulaMoveCheck("9999999999999999+ 9999999999999999+ 9999999999999999+ < >", "9999999999999999 + 9999999999999999 + 9999999999999999 +", " 9999999999999999 + 9999999999999999 +");
    formulaMoveCheck("9999999999999999+ 9999999999999999+ 9999999999999999+ 9999999999999999+ 9999999999999999+ <", "9999999999999999 + 9999999999999999 + 9999999999999999 + 9999999999999999 + 9999999999999999 +", " + 9999999999999999 + 9999999999999999");
    formulaMoveCheck("9999999999999999+ 9999999999999999+ 9999999999999999+ 9999999999999999+ 9999999999999999+ < < >", "9999999999999999 + 9999999999999999 + 9999999999999999 + 9999999999999999 + 9999999999999999 +", "99999999999999 + 9999999999999999 + 99");
  }


  @BeforeEach
  void before() {
    clear();
  }

  void checkOperations(String pattern, String res) {
    clicker(pattern);
    FxAssert.verifyThat("#display", hasText(res));
    clear();
  }

  void checkOperations(String pattern, String formula, String res) {
    clicker(pattern);
    FxAssert.verifyThat("#display", hasText(res));
    FxAssert.verifyThat("#formula", hasText(formula));
    clear();
  }

  void checkSetNormal(String pattern, String res) {
    clicker(pattern);
    FxAssert.verifyThat("#display", hasText(res));
    assertTrue(controller.getFormulaStr().isBlank());
    assertFalse(robot.from(robot.lookup(".numpad").queryAll()).lookup(operations.get("+")).queryButton().isDisabled());
    assertFalse(robot.from(robot.lookup(".numpad").queryAll()).lookup(operations.get("-")).queryButton().isDisabled());
    assertFalse(robot.from(robot.lookup(".numpad").queryAll()).lookup(operations.get("*")).queryButton().isDisabled());
    assertFalse(robot.from(robot.lookup(".numpad").queryAll()).lookup(operations.get("/")).queryButton().isDisabled());
    assertFalse(robot.from(robot.lookup(".numpad").queryAll()).lookup(operations.get("√")).queryButton().isDisabled());
    assertFalse(robot.from(robot.lookup(".numpad").queryAll()).lookup(operations.get("^")).queryButton().isDisabled());
    assertFalse(robot.from(robot.lookup(".numpad").queryAll()).lookup(hasText(".")).queryButton().isDisabled());
    assertFalse(robot.from(robot.lookup(".numpad").queryAll()).lookup(operations.get("R")).queryButton().isDisabled());
    clear();
  }

  void checkForBigFormula(String pattern, String formula, String res) {
    clicker(pattern);
    FxAssert.verifyThat("#display", hasText(res));

    assertEquals(formula, controller.getFormulaStr());
    clear();
  }

  void checkErrorOp(String pattern, String res) {
    clicker(pattern);
    FXTestUtils.awaitEvents();
    FxAssert.verifyThat("#display", hasText(res));
    assertTrue(robot.from(robot.lookup(".numpad").queryAll()).lookup(operations.get("+")).queryButton().isDisabled());
    assertTrue(robot.from(robot.lookup(".numpad").queryAll()).lookup(operations.get("-")).queryButton().isDisabled());
    assertTrue(robot.from(robot.lookup(".numpad").queryAll()).lookup(operations.get("*")).queryButton().isDisabled());
    assertTrue(robot.from(robot.lookup(".numpad").queryAll()).lookup(operations.get("/")).queryButton().isDisabled());
    assertTrue(robot.from(robot.lookup(".numpad").queryAll()).lookup(operations.get("√")).queryButton().isDisabled());
    assertTrue(robot.from(robot.lookup(".numpad").queryAll()).lookup(operations.get("^")).queryButton().isDisabled());
    assertTrue(robot.from(robot.lookup(".numpad").queryAll()).lookup(hasText(".")).queryButton().isDisabled());
    assertTrue(robot.from(robot.lookup(".numpad").queryAll()).lookup(operations.get("R")).queryButton().isDisabled());
    assertTrue(robot.lookup(hasText("M")).queryButton().isDisabled());
    assertTrue(robot.lookup(hasText(memoryOp.get("M-"))).queryButton().isDisabled());
    assertTrue(robot.lookup(hasText(memoryOp.get("M+"))).queryButton().isDisabled());
    assertTrue(robot.lookup(hasText(memoryOp.get("MR"))).queryButton().isDisabled());
    assertTrue(robot.lookup(hasText(memoryOp.get("MS"))).queryButton().isDisabled());
    assertTrue(robot.lookup(hasText(memoryOp.get("MC"))).queryButton().isDisabled());
    clear();
  }

  void checkErrorOp(String pattern, String formula, String res) {
    clicker(pattern);
    FXTestUtils.awaitEvents();
    FxAssert.verifyThat("#display", hasText(res));
    assertEquals(formula, controller.getFormulaStr());
    assertTrue(robot.from(robot.lookup(".numpad").queryAll()).lookup(operations.get("+")).queryButton().isDisabled());
    assertTrue(robot.from(robot.lookup(".numpad").queryAll()).lookup(operations.get("-")).queryButton().isDisabled());
    assertTrue(robot.from(robot.lookup(".numpad").queryAll()).lookup(operations.get("*")).queryButton().isDisabled());
    assertTrue(robot.from(robot.lookup(".numpad").queryAll()).lookup(operations.get("/")).queryButton().isDisabled());
    assertTrue(robot.from(robot.lookup(".numpad").queryAll()).lookup(operations.get("√")).queryButton().isDisabled());
    assertTrue(robot.from(robot.lookup(".numpad").queryAll()).lookup(operations.get("^")).queryButton().isDisabled());
    assertTrue(robot.from(robot.lookup(".numpad").queryAll()).lookup(hasText(".")).queryButton().isDisabled());
    assertTrue(robot.from(robot.lookup(".numpad").queryAll()).lookup(operations.get("R")).queryButton().isDisabled());
    assertTrue(robot.lookup(hasText("M")).queryButton().isDisabled());
    assertTrue(robot.lookup(hasText(memoryOp.get("M-"))).queryButton().isDisabled());
    assertTrue(robot.lookup(hasText(memoryOp.get("M+"))).queryButton().isDisabled());
    assertTrue(robot.lookup(hasText(memoryOp.get("MR"))).queryButton().isDisabled());
    assertTrue(robot.lookup(hasText(memoryOp.get("MS"))).queryButton().isDisabled());
    assertTrue(robot.lookup(hasText(memoryOp.get("MC"))).queryButton().isDisabled());
    clear();
  }

  void memoryCheck(String pattern, String res) {
    memoryCheck(pattern, "", res);
    clickOnMemory(memoryOp.get("MC"));
  }

  void memoryCheck(String pattern, String formula, String res) {
    checkOperations(pattern, formula, res);
    clickOnMemory(memoryOp.get("MC"));
  }

  void formulaButtonVisibleCheck(String pattern, String formula) {
    clicker(pattern);
    assertEquals(formula, controller.getFormulaStr());
    Button leftArrow = lookup("#leftFormulaButton").queryButton();
    assertTrue(leftArrow.isVisible());
    clickOn(leftArrow);
    assertTrue(lookup("#rightFormulaButton").queryButton().isVisible());

    clear();
  }

  void formulaMoveCheck(String pattern, String formulaStr, String formulaOnDisplay) {
    clicker(pattern);
    assertEquals(formulaStr, controller.getFormulaStr());
    assertEquals(formulaOnDisplay, lookup("#formula").queryLabeled().getText());

    clear();
  }
}
