package com.implemica.calculator.view;

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

public class Root extends Application {
  private static final String ROOT_FXML_PATH = "layout/root.fxml";
  private static final String TITLE = "Calculator";
  private static final String ICON_PATH = "/icons/icon.png";
  private static final double FONT_CHANGE_WIDTH_DOWN = 34.98;
  private static final double FONT_CHANGE_WIDTH_UP = 50d;
  private static final double MAX_FONT_SIZE = 74d;
  private static final String SEGOE_UI_SEMIBOLD = "Segoe UI Semibold";
  private static final double MIN_HEIGHT_DELTA = 468.75; // scene - text; debug
  private static final double MAX_HEIGHT_DELTA = 516.76;
  private static Root root;

  private FXMLLoader loader = new FXMLLoader(Root.class.getClassLoader().getResource(ROOT_FXML_PATH));
  private Parent parent;
  private Stage stage;
  private BorderPane mainPane;
  private Label display;
  private static Scene scene;

  private static double xOffset = 0;

  private static double yOffset = 0;

  private double dx;
  private double dy;
  private double deltaX;
  private double deltaY;
  private final static double border = 5;
  private boolean moveH;
  private boolean moveV;
  private boolean resizeH = false;
  private boolean resizeV = false;
  private boolean isFullscreen = false;

  private Screen screen;
  private Rectangle2D backupWindowBounds;


  private Dimension2D minSize = new Dimension2D(325, 530);

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
      text.setFont(new Font(SEGOE_UI_SEMIBOLD, fontSize));
      double width = text.getLayoutBounds().getWidth();
      Scene scene = display.getScene();
      double sceneWidth = scene.getWidth();
      double textHeight = text.getLayoutBounds().getHeight();
      double sceneHeight = scene.getHeight();
      double heightDelta = sceneHeight - textHeight;


      while (FONT_CHANGE_WIDTH_DOWN > sceneWidth - width || heightDelta < MAX_HEIGHT_DELTA) {
        fontSize--;
        text.setFont(new Font(SEGOE_UI_SEMIBOLD, fontSize));
        width = text.getLayoutBounds().getWidth();
        textHeight = text.getLayoutBounds().getHeight();
        heightDelta = sceneHeight - textHeight;
      }


      while (sceneWidth - width > FONT_CHANGE_WIDTH_UP && fontSize <= MAX_FONT_SIZE
          && heightDelta > MIN_HEIGHT_DELTA) {
        fontSize++;
        text.setFont(new Font(SEGOE_UI_SEMIBOLD, fontSize));
        width = text.getLayoutBounds().getWidth();
        textHeight = text.getLayoutBounds().getHeight();
        heightDelta = sceneHeight - textHeight;
      }


      display.setStyle(" -fx-font-size:" + fontSize + ";\n" +
          "  -fx-font-family: \"" + SEGOE_UI_SEMIBOLD + "\";\n" +
          "  -fx-text-alignment: right;");


    });
  }

  public FXMLLoader getLoader() {
    return loader;
  }

  public Scene getScene() throws IOException {
    if (scene == null) {
      initAll();
    }
    return scene;
  }

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
    }else{
      resizeButtonTextMinus();
    }
  }


  private void dragResize(MouseEvent event) {
    Stage stage = (Stage) mainPane.getScene().getWindow();
    if (resizeH) {
      if (stage.getWidth() <= minSize.getWidth()) {
        if (moveH) {
          deltaX = stage.getX() - event.getScreenX() + 1;
          if (event.getX() < 0) {// if new > old, it's permitted
            stage.setWidth(deltaX + stage.getWidth());
            stage.setX(event.getScreenX());
          }
        } else {
          if (event.getX() + dx - stage.getWidth() > 0) {
            stage.setWidth(event.getX() + dx);
          }
        }
      } else if (stage.getWidth() > minSize.getWidth()) {
        if (moveH) {
          deltaX = stage.getX() - event.getScreenX() + 1;
          stage.setWidth(deltaX + stage.getWidth());
          stage.setX(event.getScreenX());
        } else {
          stage.setWidth(event.getX() + dx);
        }
      }
    }

    if (resizeV) {
      if (stage.getHeight() <= minSize.getHeight()) {
        if (moveV) {
          deltaY = stage.getY() - event.getScreenY() + 1;
          if (scene.getCursor().equals(Cursor.NE_RESIZE)) {
            deltaY--;
          }
          if (event.getY() < 0) {
            stage.setHeight(deltaY + stage.getHeight());
            stage.setY(event.getScreenY());
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
          stage.setHeight(deltaY + stage.getHeight());
          stage.setY(event.getScreenY());
        } else {
          stage.setHeight(event.getY() + dy);
        }
      }
    }
  }

  private void moveResize(MouseEvent t) {
    Scene scene = mainPane.getScene();
    if (t.getX() < border && t.getY() < border) {
      scene.setCursor(Cursor.NW_RESIZE);
      resizeH = true;
      resizeV = true;
      moveH = true;
      moveV = true;
    } else if (t.getX() < border && t.getY() > scene.getHeight() - border) {
      scene.setCursor(Cursor.SW_RESIZE);
      resizeH = true;
      resizeV = true;
      moveH = true;
      moveV = false;
    } else if (t.getX() > scene.getWidth() - border && t.getY() < border) {
      scene.setCursor(Cursor.NE_RESIZE);
      resizeH = true;
      resizeV = true;
      moveH = false;
      moveV = true;
    } else if (t.getX() > scene.getWidth() - border && t.getY() > scene.getHeight() - border) {
      scene.setCursor(Cursor.SE_RESIZE);
      resizeH = true;
      resizeV = true;
      moveH = false;
      moveV = false;
    } else if (t.getX() < border || t.getX() > scene.getWidth() - border) {
      scene.setCursor(Cursor.E_RESIZE);
      resizeH = true;
      resizeV = false;
      moveH = (t.getX() < border);
      moveV = false;
    } else if (t.getY() < border || t.getY() > scene.getHeight() - border) {
      scene.setCursor(Cursor.N_RESIZE);
      resizeH = false;
      resizeV = true;
      moveH = false;
      moveV = (t.getY() < border);
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
