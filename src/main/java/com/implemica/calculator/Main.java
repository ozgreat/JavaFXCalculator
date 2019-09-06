package com.implemica.calculator;

import com.implemica.calculator.view.Root;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class Main extends Application {

  private final static String TITLE = "Calculator";
  private static final String ICON_PATH = "/icons/icon.png";

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) throws IOException {
    Parent root = new Root().getFXML();
    primaryStage.getIcons().add(new Image(getClass().getResource(ICON_PATH).toExternalForm()));
    primaryStage.setTitle("Calculator");
    primaryStage.setScene(new Scene(root));
    primaryStage.initStyle(StageStyle.UNDECORATED);
    primaryStage.setTitle(TITLE);
    primaryStage.show();
  }
}

