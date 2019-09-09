package com.implemica.calculator.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Dimension2D;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

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
  private final static double border = 10;
  private boolean moveH;
  private boolean moveV;
  private boolean resizeH = false;
  private boolean resizeV = false;

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

  public Parent getFXML() throws IOException {
    if (parent == null) {
      initAll();
    }
    return parent;
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
    stage.setIconified(true);
  }

  private void maximizeWindow(MouseEvent event) {
    if (stage.isMaximized()) {
      stage.setMaximized(false);
      Button btn = (Button) event.getSource();
      btn.setText("\uE922");
    } else {
      stage.setMaximized(true);
      Button btn = (Button) event.getSource();
      btn.setText("\uE923");
    }
    String str = display.getText();
    display.setText("");
    display.setText(str);
  }

  private void pressWindow(MouseEvent event) {
    Stage stage = (Stage) (((AnchorPane) event.getSource()).getScene().getWindow());
    if (((AnchorPane) event.getSource()).getScene().getCursor().equals(Cursor.DEFAULT)) {
      xOffset = stage.getX() - event.getScreenX();
      yOffset = stage.getY() - event.getScreenY();
    }
  }

  private void dragWindow(MouseEvent event) {
    if (((AnchorPane) event.getSource()).getScene().getCursor().equals(Cursor.DEFAULT)) {
      Stage stage = (Stage) (((AnchorPane) event.getSource()).getScene().getWindow());
      stage.setX(event.getScreenX() + xOffset);
      stage.setY(event.getScreenY() + yOffset);
    }
  }

  private void pressResize(MouseEvent t) {
    Stage stage = (Stage) mainPane.getScene().getWindow();
    dx = stage.getWidth() - t.getX();
    dy = stage.getHeight() - t.getY();
    display.setText(display.getText());
  }

  private void dragResize(MouseEvent t) {
    Stage stage = (Stage) mainPane.getScene().getWindow();
    if (resizeH) {
      if (stage.getWidth() <= minSize.getWidth()) {
        if (moveH) {
          deltaX = stage.getX() - t.getScreenX() + 1;
          if (t.getX() < 0) {// if new > old, it's permitted
            stage.setWidth(deltaX + stage.getWidth());
            stage.setX(t.getScreenX());
          }
        } else {
          if (t.getX() + dx - stage.getWidth() > 0) {
            stage.setWidth(t.getX() + dx);
          }
        }
      } else if (stage.getWidth() > minSize.getWidth()) {
        if (moveH) {
          deltaX = stage.getX() - t.getScreenX() + 1;
          stage.setWidth(deltaX + stage.getWidth());
          stage.setX(t.getScreenX());
        } else {
          stage.setWidth(t.getX() + dx);
        }
      }
    }

    if (resizeV) {
      if (stage.getHeight() <= minSize.getHeight()) {
        if (moveV) {
          deltaY = stage.getY() - t.getScreenY() + 1;
          if (t.getY() < 0) {
            stage.setHeight(deltaY + stage.getHeight());
            stage.setY(t.getScreenY());
          }
        } else {
          if (t.getY() + dy - stage.getHeight() > 0) {
            stage.setHeight(t.getY() + dy);
          }
        }
      } else if (stage.getHeight() > minSize.getHeight()) {
        if (moveV) {
          deltaY = stage.getY() - t.getScreenY() + 1;
          stage.setHeight(deltaY + stage.getHeight());
          stage.setY(t.getScreenY());
        } else {
          stage.setHeight(t.getY() + dy);
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

}
