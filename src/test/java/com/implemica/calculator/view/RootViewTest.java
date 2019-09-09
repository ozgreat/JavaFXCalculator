package com.implemica.calculator.view;

import com.implemica.calculator.controller.RootController;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
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
  RIGHT_CENTER(0, 1),
  RIGHT_BOTTOM(1, 1),
  RIGHT_TOP(1, -1),
  CENTER_TOP(-1, 0),
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
  static Robot awtRobot;
  static RootController controller;
  private FxRobot fxRobot = new FxRobot();
  private Scene scene;

  private static Map<DragPoint, Function<Window, Point2D>> dragFunc = new HashMap<>();

  static {
    dragFunc.put(LEFT_CENTER, w -> new Point2D(w.getX() + 1.0d, w.getY() + w.getHeight() / 2));
    dragFunc.put(DragPoint.RIGHT_CENTER, w -> new Point2D(w.getX() + w.getWidth() - 1.0d, w.getY() + w.getHeight() / 2));
    dragFunc.put(LEFT_TOP, w -> new Point2D(w.getX() + 1.0d, w.getY() + 1.0d));
    dragFunc.put(DragPoint.RIGHT_TOP, w -> new Point2D(w.getX() + w.getWidth() - 1.0d, w.getY() + 1.0d));
    dragFunc.put(DragPoint.LEFT_BOTTOM, w -> new Point2D(w.getX() + 1.0d, w.getY() + w.getHeight() - 1.0d));
    dragFunc.put(RIGHT_BOTTOM, w -> new Point2D(w.getX() + w.getWidth() - 1.0d, w.getY() + w.getHeight() - 1.0d));
    dragFunc.put(CENTER_BOTTOM, w -> new Point2D(w.getX() + w.getWidth() / 2, w.getY() + w.getHeight() - 1.0d));
    dragFunc.put(CENTER_TOP, w -> new Point2D(w.getX() + w.getWidth() / 2, w.getY() + 1.0d));
  }

  @Override
  public void start(Stage stage) throws AWTException, IOException {
    awtRobot = new Robot();
    if (Window.getWindows().size() == 0) {
      Root root = Root.getRoot();
      scene = root.getScene();
      controller = root.getLoader().getController();
    }
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
    checkDrag(LEFT_TOP, -10, -10);
    checkDrag(LEFT_TOP, -10, 0);
    checkDrag(LEFT_TOP, 0, -10);

    //Left side
    checkDrag(LEFT_CENTER, -10, -10);
    checkDrag(LEFT_CENTER, -10, 0);
    checkDrag(LEFT_CENTER, 0, -10);

    //Left bottom corner
    checkDrag(LEFT_BOTTOM, -10, 10);
    checkDrag(LEFT_BOTTOM, -10, 0);
    checkDrag(LEFT_BOTTOM, 0, 10);

    //Right top corner
    checkDrag(RIGHT_TOP, 10, -10);
    checkDrag(RIGHT_TOP, 0, -10);
    checkDrag(RIGHT_TOP, 10, 0);

    //Right side
    checkDrag(RIGHT_CENTER, 10, 10);
    checkDrag(RIGHT_CENTER, 0, 10);
    checkDrag(RIGHT_CENTER, 10, 0);

    //Right bottom corner
    checkDrag(RIGHT_BOTTOM, 10, 10);
    checkDrag(RIGHT_BOTTOM, 0, 10);
    checkDrag(RIGHT_BOTTOM, 10, 0);
  }


  private void checkDrag(DragPoint dragPoint, int x, int y) {
    Window window = Window.getWindows().get(0);
    double prevHeight = window.getHeight();
    double prevWidth = window.getWidth();
    Point2D point = getPoint(dragPoint, window);
    drag(point.getX(), point.getY(), x, y);

    FXTestUtils.awaitEvents();

    assertEquals(prevWidth + x * dragPoint.getCoefficientX(), window.getWidth());
    assertEquals(prevHeight + y * dragPoint.getCoefficientY(), window.getHeight());

    //Back to normal
//    point = getPoint(dragPoint, window);
//    drag(point.getX(), point.getY(), -x, -y);
  }

  private void drag(double oldX, double oldY, int x, int y) {
    awtRobot.mouseMove((int) oldX, (int) oldY);
    awtRobot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
    awtRobot.mouseMove((int) (oldX + x), (int) (oldY + y));
    awtRobot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
  }

  private Point2D getPoint(DragPoint dragPoint, Window window) {
    return dragFunc.get(dragPoint).apply(window);
  }
}
