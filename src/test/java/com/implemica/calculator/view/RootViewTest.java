package com.implemica.calculator.view;

import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.junit.jupiter.api.Test;
import org.loadui.testfx.utils.FXTestUtils;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationTest;

import java.awt.*;
import java.awt.event.InputEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static com.implemica.calculator.view.DragPoint.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

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

public class RootViewTest extends ApplicationTest {
  private Robot awtRobot;
  private static FxRobot fxRobot = new FxRobot();

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

  @Override
  public void start(Stage stage) throws AWTException, IOException {
    awtRobot = new Robot();
    Root root = Root.getRoot();
    Scene scene = root.getScene();
    stage.setScene(scene);
    stage.show();
  }


  @Test
  void dragTest() {
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

  @Test
  void moveTest() {
    final double MAX_X = Screen.getPrimary().getBounds().getMaxX() - 50.0d;
    final double MAX_Y = Screen.getPrimary().getBounds().getMaxY() - 50.0d;

    moveCheck(0, 0);
    moveCheck(0, 1);
    moveCheck(0, MAX_Y);
    moveCheck(0, MAX_Y / 2);
    moveCheck(0, MAX_Y / 4);
    moveCheck(0, MAX_Y * 0.75);
    moveCheck(1, 0);
    moveCheck(1, 1);
    moveCheck(1, MAX_Y);
    moveCheck(1, MAX_Y / 2);
    moveCheck(1, MAX_Y / 4);
    moveCheck(1, MAX_Y * 0.75);
    moveCheck(MAX_X, 0);
    moveCheck(MAX_X, 1);
    moveCheck(MAX_X, MAX_Y);
    moveCheck(MAX_X, MAX_Y / 2);
    moveCheck(MAX_X, MAX_Y / 4);
    moveCheck(MAX_X, MAX_Y * 0.75);
    moveCheck(MAX_X / 2, 0);
    moveCheck(MAX_X / 2, 1);
    moveCheck(MAX_X / 2, MAX_Y);
    moveCheck(MAX_X / 2, MAX_Y / 2);
    moveCheck(MAX_X / 2, MAX_Y / 4);
    moveCheck(MAX_X / 2, MAX_Y * 0.75);
    moveCheck(MAX_X / 4, 0);
    moveCheck(MAX_X / 4, 1);
    moveCheck(MAX_X / 4, MAX_Y);
    moveCheck(MAX_X / 4, MAX_Y / 2);
    moveCheck(MAX_X / 4, MAX_Y / 4);
    moveCheck(MAX_X / 4, MAX_Y * 0.75);
    moveCheck(MAX_X * 0.75, 0);
    moveCheck(MAX_X * 0.75, 1);
    moveCheck(MAX_X * 0.75, MAX_Y);
    moveCheck(MAX_X * 0.75, MAX_Y / 2);
    moveCheck(MAX_X * 0.75, MAX_Y / 4);
    moveCheck(MAX_X * 0.75, MAX_Y * 0.75);
  }


  private void checkDrag(DragPoint dragPoint, int x, int y) {
    awtRobot.mouseMove(0, 0);
    Window window = Window.getWindows().get(0);
    int prevHeight = (int) window.getHeight();
    int prevWidth = (int) window.getWidth();
    Point2D point = getPoint(dragPoint, window);
    drag(point.getX(), point.getY(), x, y);

    FXTestUtils.awaitEvents();

    assertEquals(prevWidth + x * dragPoint.getCoefficientX(), window.getWidth());
    assertEquals(prevHeight + y * dragPoint.getCoefficientY(), window.getHeight());
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
    Window window = Window.getWindows().get(0);

    double initialX = window.getX() + window.getWidth() / 2.0;
    double initialY = window.getY() + 10.0;

    fxRobot.drag(initialX, initialY);
    fxRobot.moveTo(x, y);
    fxRobot.release(MouseButton.PRIMARY);


    double expectedX = Math.round(x - window.getWidth() / 2.0);
    double expectedY = y - 10.0;

    assertEquals((int) expectedX, window.getX());
    assertEquals((int) expectedY, window.getY());
    fxRobot.drop();
  }
}
