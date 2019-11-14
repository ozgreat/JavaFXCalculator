package com.implemica.calculator.view;

import com.implemica.calculator.controller.util.ControllerTestUtils;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.control.Labeled;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.junit.jupiter.api.Test;
import org.loadui.testfx.utils.FXTestUtils;
import org.testfx.api.FxAssert;

import java.awt.*;
import java.awt.event.InputEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static com.implemica.calculator.view.DragPoint.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.matcher.control.LabeledMatchers.hasText;

/**
 * Enum of coefficient for coordinate in most common case(Edges and corners of stage)
 */
enum DragPoint {
  LEFT_CENTER(-1, 0),
  LEFT_BOTTOM(-1, 1),
  LEFT_TOP(-1, -1),
  RIGHT_CENTER(1, 0),
  RIGHT_BOTTOM(1, 1),
  RIGHT_TOP(1, -1),
  CENTER_TOP(0, -1),
  CENTER_BOTTOM(0, 1);

  private int coefficientX;
  private int coefficientY;

  DragPoint(int coefficientX, int coefficientY) {
    this.coefficientX = coefficientX;
    this.coefficientY = coefficientY;
  }

  public int getCoefficientX() {
    return coefficientX;
  }

  public int getCoefficientY() {
    return coefficientY;
  }
}

/**
 * Test class for {@link Root} class, that tests view elements like resize, move window, color of buttons.
 * Also tests window buttons
 */
public class RootViewTest extends ControllerTestUtils {
  /**
   * Default button's color
   */
  private static final Color ACTION_BUTTON_STANDARD_COLOR = Color.valueOf("#f0f0f0");
  /**
   * Common color for hovered buttons
   */
  private static final Color STANDARD_BUTTON_HOVER_COLOR = Color.valueOf("#e6e6e6");
  /**
   * Digit buttons color
   */
  private static final Color DIGIT_BUTTON_COLOR = Color.valueOf("#fafafa");
  /**
   * Arithmetic buttons color
   */
  private static final Color ARITHMETIC_BUTTON_COLOR = Color.valueOf("#4c4a48");
  /**
   * Color of sidebar
   */
  private static final Color SIDEBAR_COLOR = Color.valueOf("#cfcfcf");
  /**
   * Height of windows panel
   */
  private static final double HEIGHT_OF_WINDOWS_PANEL = 40.0d;

  /**
   * Map for function to drag for edges and corners of stage {@link DragPoint}
   */
  private static Map<DragPoint, Function<Window, Point2D>> dragFunc = new HashMap<>();

  static {
    dragFunc.put(LEFT_CENTER, w -> new Point2D(w.getX() + 1.0d, w.getY() + w.getHeight() / 2));
    dragFunc.put(RIGHT_CENTER, w -> new Point2D(w.getX() + w.getWidth() - 1.0d, w.getY() + w.getHeight() / 2));
    dragFunc.put(LEFT_TOP, w -> new Point2D(w.getX() + 1.0d, w.getY() + 1.0d));
    dragFunc.put(RIGHT_TOP, w -> new Point2D(w.getX() + w.getWidth() - 1.0d, w.getY()));
    dragFunc.put(LEFT_BOTTOM, w -> new Point2D(w.getX() + 1.0d, w.getY() + w.getHeight() - 1.0d));
    dragFunc.put(RIGHT_BOTTOM, w -> new Point2D(w.getX() + w.getWidth() - 1.0d, w.getY() + w.getHeight() - 1.0d));
    dragFunc.put(CENTER_BOTTOM, w -> new Point2D(w.getX() + w.getWidth() / 2, w.getY() + w.getHeight() - 1.0d));
    dragFunc.put(CENTER_TOP, w -> new Point2D(w.getX() + w.getWidth() / 2, w.getY() + 1.0d));
  }

  /**
   * Stage in work
   */
  private Stage testStage;

  @Override
  public void start(Stage stage) throws AWTException, IOException {
    awtRobot = new Robot();
    Root root = Root.getRoot();
    testStage = stage;
    root.start(stage);
  }

  @Test
  void allTests() {
    dragTest();
    moveTest();
    colorChangeTest();
    checkButtons();
    testFullScreen();
    minimizeTest();
  }

  void dragTest() {
    //don't move
    checkDrag(LEFT_CENTER, 0, 0);
    checkDrag(RIGHT_BOTTOM, 0, 0);
    checkDrag(LEFT_TOP, 0, 0);
    checkDrag(CENTER_BOTTOM, 0, 0);
    checkDrag(CENTER_TOP, 0, 0);

    //Left top corner
    checkDrag(LEFT_TOP, -50, -50);
    checkDrag(LEFT_TOP, -50, 0);
    checkDrag(LEFT_TOP, 0, -50);
    checkDrag(LEFT_TOP, 50, 50);
    checkDrag(LEFT_TOP, 50, 0);
    checkDrag(LEFT_TOP, 0, 50);

    //Left side
    checkDrag(LEFT_CENTER, -50, -50);
    checkDrag(LEFT_CENTER, -50, 0);
    checkDrag(LEFT_CENTER, 0, -50);
    checkDrag(LEFT_CENTER, 50, 50);
    checkDrag(LEFT_CENTER, 50, 0);
    checkDrag(LEFT_CENTER, 0, 50);

    //Left bottom corner
    checkDrag(LEFT_BOTTOM, -50, 50);
    checkDrag(LEFT_BOTTOM, -50, 0);
    checkDrag(LEFT_BOTTOM, 0, 50);
    checkDrag(LEFT_BOTTOM, 50, -50);
    checkDrag(LEFT_BOTTOM, 50, 0);
    checkDrag(LEFT_BOTTOM, 0, -50);

    //Right top corner
    checkDrag(RIGHT_TOP, 50, -50);
    checkDrag(RIGHT_TOP, 0, -50);
    checkDrag(RIGHT_TOP, 50, 0);
    checkDrag(RIGHT_TOP, -50, 50);
    checkDrag(RIGHT_TOP, 0, 50);
    checkDrag(RIGHT_TOP, -50, 0);

    //Right side
    checkDrag(RIGHT_CENTER, 50, 50);
    checkDrag(RIGHT_CENTER, 0, 50);
    checkDrag(RIGHT_CENTER, 50, 0);
    checkDrag(RIGHT_CENTER, -50, -50);
    checkDrag(RIGHT_CENTER, 0, -50);
    checkDrag(RIGHT_CENTER, -50, 0);

    //Right bottom corner
    checkDrag(RIGHT_BOTTOM, 50, 50);
    checkDrag(RIGHT_BOTTOM, 0, 50);
    checkDrag(RIGHT_BOTTOM, 50, 0);
    checkDrag(RIGHT_BOTTOM, -50, -50);
    checkDrag(RIGHT_BOTTOM, 0, -50);
    checkDrag(RIGHT_BOTTOM, -50, 0);

    //Center top
    checkDrag(CENTER_TOP, 50, -50);
    checkDrag(CENTER_TOP, 0, -50);
    checkDrag(CENTER_TOP, -50, 0);
    checkDrag(CENTER_TOP, -50, 50);
    checkDrag(CENTER_TOP, 0, 50);
    checkDrag(CENTER_TOP, 50, 0);


    //Center bottom
    checkDrag(CENTER_BOTTOM, 50, 50);
    checkDrag(CENTER_BOTTOM, 0, 50);
    checkDrag(CENTER_BOTTOM, 50, 0);
    checkDrag(CENTER_BOTTOM, -50, -50);
    checkDrag(CENTER_BOTTOM, 0, -50);
    checkDrag(CENTER_BOTTOM, -50, 0);
  }

  void moveTest() {
    final double MAX_X = Screen.getPrimary().getBounds().getMaxX() - 100.0d;
    final double MAX_Y = Screen.getPrimary().getBounds().getMaxY() - 100.0d;
    final Window window = getCurrentWindow();
    final double INITIAL_X = window.getX();
    final double INITIAL_Y = window.getY();

    //Zero by x in all y
    moveCheck(0, 0);
    moveCheck(0, 1);
    moveCheck(0, MAX_Y);
    moveCheck(0, MAX_Y / 2);
    moveCheck(0, MAX_Y / 4);
    moveCheck(0, MAX_Y * 0.75);

    //1 by x in all y
    moveCheck(1, 0);
    moveCheck(1, 1);
    moveCheck(1, MAX_Y);
    moveCheck(1, MAX_Y / 2);
    moveCheck(1, MAX_Y / 4);
    moveCheck(1, MAX_Y * 0.75);

    //max x in all  y
    moveCheck(MAX_X, 0);
    moveCheck(MAX_X, 1);
    moveCheck(MAX_X, MAX_Y);
    moveCheck(MAX_X, MAX_Y / 2);
    moveCheck(MAX_X, MAX_Y / 4);
    moveCheck(MAX_X, MAX_Y * 0.75);

    //half of max x in all y
    moveCheck(MAX_X / 2, 0);
    moveCheck(MAX_X / 2, 1);
    moveCheck(MAX_X / 2, MAX_Y);
    moveCheck(MAX_X / 2, MAX_Y / 2);
    moveCheck(MAX_X / 2, MAX_Y / 4);
    moveCheck(MAX_X / 2, MAX_Y * 0.75);

    //Quarter of max in all y
    moveCheck(MAX_X / 4, 0);
    moveCheck(MAX_X / 4, 1);
    moveCheck(MAX_X / 4, MAX_Y);
    moveCheck(MAX_X / 4, MAX_Y / 2);
    moveCheck(MAX_X / 4, MAX_Y / 4);
    moveCheck(MAX_X / 4, MAX_Y * 0.75);

    //3/4 of max in all y
    moveCheck(MAX_X * 0.75, 0);
    moveCheck(MAX_X * 0.75, 1);
    moveCheck(MAX_X * 0.75, MAX_Y);
    moveCheck(MAX_X * 0.75, MAX_Y / 2);
    moveCheck(MAX_X * 0.75, MAX_Y / 4);
    moveCheck(MAX_X * 0.75, MAX_Y * 0.75);

    //move to initial x and y
    moveCheck(INITIAL_X, INITIAL_Y);
  }

  void colorChangeTest() {
    //checking action buttons
    checkColor("C", ACTION_BUTTON_STANDARD_COLOR, STANDARD_BUTTON_HOVER_COLOR);
    checkColor("CE", ACTION_BUTTON_STANDARD_COLOR, STANDARD_BUTTON_HOVER_COLOR);
    checkColor("\uE94B", ACTION_BUTTON_STANDARD_COLOR, STANDARD_BUTTON_HOVER_COLOR);
    checkColor("\uD835\uDC65²", ACTION_BUTTON_STANDARD_COLOR, STANDARD_BUTTON_HOVER_COLOR);
    checkColor("⅟\uD835\uDC65", ACTION_BUTTON_STANDARD_COLOR, STANDARD_BUTTON_HOVER_COLOR);
    checkColor("\uE94F", ACTION_BUTTON_STANDARD_COLOR, STANDARD_BUTTON_HOVER_COLOR);
    checkColor(".", ACTION_BUTTON_STANDARD_COLOR, STANDARD_BUTTON_HOVER_COLOR);
    checkColor("\uE94D", ACTION_BUTTON_STANDARD_COLOR, STANDARD_BUTTON_HOVER_COLOR);

    //Check menu buttons
    checkColor(robot.lookup("#sideMenuButton").queryButton(), Color.TRANSPARENT, SIDEBAR_COLOR);
    checkColor(robot.lookup("#history").queryButton(), Color.TRANSPARENT, Color.TRANSPARENT);

    //check all digit buttons
    checkColor(robot.lookup(".digitButton").queryAllAs(Labeled.class), DIGIT_BUTTON_COLOR, STANDARD_BUTTON_HOVER_COLOR);

    //check all arithmetic buttons(+,-,etc)
    checkColor(robot.lookup(".arithmeticButton").queryAllAs(Labeled.class), ACTION_BUTTON_STANDARD_COLOR, ARITHMETIC_BUTTON_COLOR);
  }

  void testFullScreen() {
    Window window = getCurrentWindow();
    double height = window.getHeight();
    double width = window.getWidth();
    Button fullScreen = robot.lookup("#maximizeButton").queryButton();

    //make fullscreen
    robot.clickOn(fullScreen);
    assertEquals(Screen.getPrimary().getBounds().getWidth(), window.getWidth());
    assertEquals(Screen.getPrimary().getBounds().getHeight() - HEIGHT_OF_WINDOWS_PANEL, window.getHeight());

    //turn off fullscreen and back to previous size
    robot.clickOn(fullScreen);
    FXTestUtils.awaitEvents();
    assertEquals(width, window.getWidth());
    assertEquals(height, window.getHeight());
  }

  void checkButtons() {
    clickOn("C");
    clickOn("1");
    clickOn("2");
    clickOn("3");
    clickOn("4");
    clickOn("5");
    clickOn("6");
    clickOn("7");
    clickOn("8");
    clickOn("9");
    clickOn("0");
    FxAssert.verifyThat(robot.lookup("#display"), hasText("1,234,567,890"));

    clickOn("C");
    FxAssert.verifyThat(robot.lookup("#display"), hasText("0"));
  }

  void minimizeTest() {
    assertFalse(testStage.isIconified());
    clickOn(lookup("#minimizeWindow").queryButton());
    assertTrue(testStage.isIconified());
  }


  private void checkDrag(DragPoint dragPoint, int x, int y) {
    awtRobot.mouseMove(0, 0);
    Window window = getCurrentWindow();
    int prevHeight = (int) window.getHeight();
    int prevWidth = (int) window.getWidth();
    Point2D point = getPoint(dragPoint, window);
    drag(point.getX(), point.getY(), x, y);

    FXTestUtils.awaitEvents();

    assertEquals(Math.round(prevWidth + x * dragPoint.getCoefficientX()), Math.round(window.getWidth()));
    assertEquals(Math.round(prevHeight + y * dragPoint.getCoefficientY()), Math.round(window.getHeight()));
  }

  private void drag(double oldX, double oldY, int x, int y) {
    awtRobot.mouseMove((int) oldX, (int) oldY);
    awtRobot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
    awtRobot.mouseMove((int) Math.round(oldX + x), (int) Math.round(oldY + y));
    awtRobot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
  }

  private Point2D getPoint(DragPoint dragPoint, Window window) {
    return dragFunc.get(dragPoint).apply(window);
  }

  private void moveCheck(double x, double y) {
    Window window = getCurrentWindow();

    double initialX = window.getX() + window.getWidth() / 2.0;
    double initialY = window.getY() + 10.0;

    awtRobot.mouseMove((int) initialX, (int) initialY);
    robot.drag(initialX, initialY);
    awtRobot.mouseMove((int) x, (int) y);
    robot.release(MouseButton.PRIMARY);


    double expectedX = Math.round(x - window.getWidth() / 2.0);
    double expectedY = y - 10.0;

    assertEquals((int) expectedX, (int) Math.round(window.getX()));
    assertEquals((int) expectedY, (int) Math.round(window.getY()));
    robot.drop();
  }

  private void checkColor(Labeled btn, Color expectedBefore, Color expectedAfter) {
    assertEquals(btn.getBackground().getFills().get(0).getFill(), expectedBefore);
    Bounds bounds = btn.localToScreen(btn.getBoundsInLocal());
    awtRobot.mouseMove((int) (bounds.getCenterX()), (int) (bounds.getCenterY()));
    FXTestUtils.awaitEvents();
    assertEquals(btn.getBackground().getFills().get(0).getFill(), expectedAfter);
  }

  private void checkColor(Set<Labeled> nodes, Color expectedBefore, Color expectedAfter) {
    for (Labeled node : nodes) {
      checkColor(node, expectedBefore, expectedAfter);
    }
  }

  private void checkColor(String nodeName, Color expectedBefore, Color expectedAfter) {
    Button btn = robot.from(lookup(".numpad")).lookup(hasText(nodeName)).queryButton();
    checkColor(btn, expectedBefore, expectedAfter);
  }

  private Window getCurrentWindow() {
    return Window.getWindows().get(0);
  }
}