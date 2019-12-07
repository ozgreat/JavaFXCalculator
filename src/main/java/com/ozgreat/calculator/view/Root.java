package com.ozgreat.calculator.view;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Dimension2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.io.IOException;

/**
 * Launcher class of application. Set up stage from fxml,
 * listeners and implement resize and move of window, closing
 * and hiding of window.
 */
public class Root extends Application {
  /**
   * Path to fmxl file of stage
   */
  private static final String ROOT_FXML_PATH = "com/ozgreat/calculator/view/root.fxml";
  /**
   * Title text of window
   */
  private static final String TITLE = "Calculator";
  /**
   * Path to icon of application
   */
  private static final String ICON_PATH = "/com/ozgreat/calculator/view/icon.png";
  /**
   * Width from border of application after which text on display starts getting smaller
   */
  private static final double FONT_CHANGE_WIDTH_DOWN = 34.98;
  /**
   * Width from border of application after which text on display starts getting bigger
   */
  private static final double FONT_CHANGE_WIDTH_UP = 50d;
  /**
   * Maximum font size
   */
  private static final double MAX_FONT_SIZE = 74d;
  /**
   * Name of standard font
   */
  private static final String DEFAULT_FONT = "Segoe UI Semibold";
  /**
   * Delta of text on buttons width and width of scene, when button's font size starts growing
   */
  private static final double GROWING_HEIGHT_DELTA = 468.75;
  /**
   * Delta of text on buttons width and width of scene, when button's font size starts decrease
   */
  private static final double DECREASE_HEIGHT_DELTA = 516.76;
  /**
   * Current {@code Root} object
   */
  private static Root root;
  /**
   * Current scene
   */
  private static Scene scene;

  /**
   * Loader of fxml
   */
  private FXMLLoader loader = new FXMLLoader(Root.class.getClassLoader().getResource(ROOT_FXML_PATH));
  /**
   * Parent node
   */
  private Parent parent;
  /**
   * Current stage
   */
  private Stage stage;
  /**
   * Main pane of application
   */
  private BorderPane mainPane;
  /**
   * Display label
   */
  private Label display;

  /**
   * X coordinate of current position.
   */
  private static double xOffset = 0;
  /**
   * Y coordinate of current position.
   */
  private static double yOffset = 0;

  /**
   * X coordinate of moving
   */
  private double dx;
  /**
   * Y coordinate of moving
   */
  private double dy;
  /**
   * Padding for application in which resize is possible.
   */
  private final static double BORDER = 5;
  /**
   * True if application should be moved horizontally (when the cursor is on the left edge of the window).
   */
  private boolean moveH;
  /**
   * True if application should be moved vertically (when the cursor is on the top edge of the window).
   */
  private boolean moveV;
  /**
   * True if applying horizontal resizing.
   */
  private boolean resizeH = false;
  /**
   * True if applying vertical resizing.
   */
  private boolean resizeV = false;
  /**
   * True if application now is fullscreen
   */
  private boolean isFullscreen = false;

  /**
   * Current screen
   */
  private Screen screen;
  /**
   * Application window bounds
   */
  private Rectangle2D backupWindowBounds;

  /**
   * Application window min size
   */
  private Dimension2D minSize = new Dimension2D(325, 530);


  /**
   * Starting method
   *
   * @param primaryStage - the primary stage for this application, onto which the application scene can be set.
   * @throws IOException if didn't find and/or load fxml file
   */
  @Override
  public void start(Stage primaryStage) throws IOException {
    stage = primaryStage;
    initAll();
    setUpStage();
  }

  private void initAll() throws IOException {
    parent = loader.load();
    scene = new Scene(parent);
    initWindowsButtons();
    initWindowResizing();
    initWindowMoving();
    initListeners();
  }

  private void setUpStage() {
    stage.getIcons().add(new Image(getClass().getResource(ICON_PATH).toExternalForm()));
    stage.setTitle(TITLE);
    stage.setScene(scene);
    stage.initStyle(StageStyle.UNDECORATED);
    stage.setTitle(TITLE);
    stage.setMaxWidth(Screen.getPrimary().getBounds().getMaxX());
    stage.setMaxHeight(Screen.getPrimary().getBounds().getMaxY());
    stage.show();
  }

  private void initWindowsButtons() {
    Button closeBtn = (Button) parent.lookup("#closeButton");
    closeBtn.setOnMouseClicked(this::closeWindow);

    Button minBtn = (Button) parent.lookup("#minimizeWindow");
    minBtn.setOnMouseClicked(this::minimizeWindow);

    Button maximizeBtn = (Button) parent.lookup("#maximizeButton");
    maximizeBtn.setOnMouseClicked(this::maximizeWindow);
  }

  private void initWindowResizing() {
    mainPane = (BorderPane) parent.lookup("#bp");
    mainPane.setOnMouseDragged(this::dragResize);
    mainPane.setOnMouseMoved(this::moveResize);
    mainPane.setOnMousePressed(this::pressResize);
  }

  private void initWindowMoving() {
    AnchorPane windowBar = (AnchorPane) parent.lookup("#windowBar");
    windowBar.setOnMouseDragged(this::dragWindow);
    windowBar.setOnMousePressed(this::pressWindow);
  }

  private void initListeners() {
    display = (Label) parent.lookup("#display");
    display.textProperty().addListener(observable -> {
      Text text = new Text(display.getText());
      double fontSize = display.getFont().getSize();
      text.setFont(Font.font(DEFAULT_FONT, fontSize));
      double width = text.getLayoutBounds().getWidth();
      Scene scene = display.getScene();
      double sceneWidth = scene.getWidth();
      double textHeight = text.getLayoutBounds().getHeight();
      double sceneHeight = scene.getHeight();
      double heightDelta = sceneHeight - textHeight;


      while (FONT_CHANGE_WIDTH_DOWN > sceneWidth - width || heightDelta < DECREASE_HEIGHT_DELTA) {
        fontSize--;
        text.setFont(Font.font(DEFAULT_FONT, fontSize));
        width = text.getLayoutBounds().getWidth();
        textHeight = text.getLayoutBounds().getHeight();
        heightDelta = sceneHeight - textHeight;
      }


      while (sceneWidth - width > FONT_CHANGE_WIDTH_UP && fontSize <= MAX_FONT_SIZE
          && heightDelta > GROWING_HEIGHT_DELTA) {
        fontSize++;
        text.setFont(Font.font(DEFAULT_FONT, fontSize));
        width = text.getLayoutBounds().getWidth();
        textHeight = text.getLayoutBounds().getHeight();
        heightDelta = sceneHeight - textHeight;
      }

      display.setStyle(" -fx-font-size:" + fontSize + ";");
    });
  }

  public FXMLLoader getLoader() {
    return loader;
  }

  /**
   * Return scene, if scene is null init, them
   *
   * @return scene
   * @throws IOException if fxml not loaded
   */
  public Scene getScene() throws IOException {
    if (scene == null) {
      initAll();
    }
    return scene;
  }

  /**
   * return {@code root}. If root is null init new
   *
   * @return {@code root}
   */
  public static Root getRoot() {
    if (root == null) {
      root = new Root();
    }
    return root;
  }

  private void closeWindow(MouseEvent event) {
    stage.hide();
  }

  private void minimizeWindow(MouseEvent event) {
    ((Stage) scene.getWindow()).setIconified(true);
  }

  private void maximizeWindow(MouseEvent event) {
    Stage stage = (Stage) scene.getWindow();
    ObservableList<Screen> screens = Screen.getScreensForRectangle(stage.getX(), stage.getY(), 1, 1);

    if (screens.isEmpty()) {
      screen = Screen.getScreensForRectangle(0, 0, 1, 1).get(0);
    } else {
      screen = screens.get(0);
    }

    if (isFullscreen) {
      isFullscreen = false;
      Button btn = (Button) event.getSource();
      btn.setText("\uE922");

      if (backupWindowBounds != null) {
        stage.setX(backupWindowBounds.getMinX());
        stage.setY(backupWindowBounds.getMinY());
        stage.setWidth(backupWindowBounds.getWidth());
        stage.setHeight(backupWindowBounds.getHeight());
      }
      resizeButtonTextMinus();
    } else {
      isFullscreen = true;
      Button btn = (Button) event.getSource();
      btn.setText("\uE923");

      backupWindowBounds = new Rectangle2D(stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight());

      stage.setX(screen.getVisualBounds().getMinX());
      stage.setY(screen.getVisualBounds().getMinY());
      stage.setWidth(screen.getVisualBounds().getWidth());
      stage.setHeight(screen.getVisualBounds().getHeight());

      resizeButtonTextPlus();
    }
  }

  private void pressWindow(MouseEvent event) {
    Window stage = scene.getWindow();
    if (((AnchorPane) event.getSource()).getScene().getCursor().equals(Cursor.DEFAULT)) {
      xOffset = stage.getX() - event.getScreenX();
      yOffset = stage.getY() - event.getScreenY();
    }
  }

  private void dragWindow(MouseEvent event) {
    if (((AnchorPane) event.getSource()).getScene().getCursor().equals(Cursor.DEFAULT) && !isFullscreen) {
      Window stage = scene.getWindow();
      stage.setX(event.getScreenX() + xOffset);
      stage.setY(event.getScreenY() + yOffset);
    }
  }

  private void pressResize(MouseEvent event) {
    Stage stage = (Stage) mainPane.getScene().getWindow();
    dx = stage.getWidth() - event.getX();
    dy = stage.getHeight() - event.getY();
    display.setText(display.getText());

    ObservableList<Screen> screens = Screen.getScreensForRectangle(stage.getX(), stage.getY(), 1, 1);

    if (screens.isEmpty()) {
      screen = Screen.getScreensForRectangle(0, 0, 1, 1).get(0);
    } else {
      screen = screens.get(0);
    }

    if (stage.getHeight() >= screen.getVisualBounds().getHeight() && stage.getWidth() >= (screen.getVisualBounds().getWidth() / 2d)) {
      resizeButtonTextPlus();
    } else {
      resizeButtonTextMinus();
    }
  }

  private void dragResize(MouseEvent event) {
    Stage stage = (Stage) mainPane.getScene().getWindow();
    if (resizeH) {
      if (stage.getWidth() <= minSize.getWidth()) {
        if (moveH) {
          if (event.getX() < 0) {// if new > old, it's permitted
            changeWidthAndX(event, stage);
          }
        } else {
          if (event.getX() + dx - stage.getWidth() > 0) {
            stage.setWidth(event.getX() + dx);
          }
        }
      } else if (stage.getWidth() > minSize.getWidth()) {
        if (moveH) {
          changeWidthAndX(event, stage);
        } else {
          stage.setWidth(event.getX() + dx);
        }
      }
    }

    if (resizeV) {
      double deltaY;
      if (stage.getHeight() <= minSize.getHeight()) {
        if (moveV) {
          deltaY = stage.getY() - event.getScreenY() + 1;
          if (scene.getCursor().equals(Cursor.NE_RESIZE)) {
            deltaY--;
          }
          if (event.getY() < 0) {
            changeHeightAndY(event, stage, deltaY);
          }
        } else {
          if (event.getY() + dy - stage.getHeight() > 0) {
            stage.setHeight(event.getY() + dy);
          }
        }
      } else if (stage.getHeight() > minSize.getHeight()) {
        if (moveV) {
          deltaY = stage.getY() - event.getScreenY() + 1;
          if (scene.getCursor().equals(Cursor.NE_RESIZE)) {
            deltaY--;
          }
          changeHeightAndY(event, stage, deltaY);
        } else {
          stage.setHeight(event.getY() + dy);
        }
      }
    }
  }

  private void changeHeightAndY(MouseEvent event, Stage stage, double deltaY) {
    stage.setHeight(deltaY + stage.getHeight());
    stage.setY(event.getScreenY());
  }

  private void changeWidthAndX(MouseEvent event, Stage stage) {
    double deltaX;
    deltaX = stage.getX() - event.getScreenX() + 1;
    stage.setWidth(deltaX + stage.getWidth());
    stage.setX(event.getScreenX());
  }

  private void moveResize(MouseEvent t) {
    Scene scene = mainPane.getScene();
    if (t.getX() < BORDER && t.getY() < BORDER) {
      scene.setCursor(Cursor.NW_RESIZE);
      resizeH = true;
      resizeV = true;
      moveH = true;
      moveV = true;
    } else if (t.getX() < BORDER && t.getY() > scene.getHeight() - BORDER) {
      scene.setCursor(Cursor.SW_RESIZE);
      resizeH = true;
      resizeV = true;
      moveH = true;
      moveV = false;
    } else if (t.getX() > scene.getWidth() - BORDER && t.getY() < BORDER) {
      scene.setCursor(Cursor.NE_RESIZE);
      resizeH = true;
      resizeV = true;
      moveH = false;
      moveV = true;
    } else if (t.getX() > scene.getWidth() - BORDER && t.getY() > scene.getHeight() - BORDER) {
      scene.setCursor(Cursor.SE_RESIZE);
      resizeH = true;
      resizeV = true;
      moveH = false;
      moveV = false;
    } else if (t.getX() < BORDER || t.getX() > scene.getWidth() - BORDER) {
      scene.setCursor(Cursor.E_RESIZE);
      resizeH = true;
      resizeV = false;
      moveH = (t.getX() < BORDER);
      moveV = false;
    } else if (t.getY() < BORDER || t.getY() > scene.getHeight() - BORDER) {
      scene.setCursor(Cursor.N_RESIZE);
      resizeH = false;
      resizeV = true;
      moveH = false;
      moveV = (t.getY() < BORDER);
    } else {
      scene.setCursor(Cursor.DEFAULT);
      resizeH = false;
      resizeV = false;
      moveH = false;
      moveV = false;
    }
  }

  private void resizeButtonTextPlus() {
    GridPane numpad = (GridPane) parent.lookup("#numpad");
    for (Node node : numpad.getChildren()) {
      Button btn = (Button) node;
      btn.setStyle(" -fx-font-size:" + (btn.getFont().getSize() + 8d) + ";\n");
    }
  }

  private void resizeButtonTextMinus() {
    GridPane numpad = (GridPane) parent.lookup("#numpad");
    for (Node node : numpad.getChildren()) {
      Button btn = (Button) node;
      if (btn.getFont().getSize() < 24 || btn.getFont().getSize() == 24.5) {
        break;
      }
      btn.setStyle(" -fx-font-size:" + (btn.getFont().getSize() - 8d) + ";\n");
    }
  }
}
