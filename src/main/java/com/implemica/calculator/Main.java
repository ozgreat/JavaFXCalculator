package com.implemica.calculator;

import com.implemica.calculator.view.Root;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;

public class Main extends Application {

  private final static String TITLE = "Calculator";
  private static final String ICON_PATH = "icons/icon.png";

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) throws IOException {
    Parent root = new Root().getRoot();
//    primaryStage.getIcons().add(new Image(new File(ICON_PATH).toURI().toString()));
    primaryStage.setTitle("Calculator");
    primaryStage.getIcons().add(new Image("file:" + ICON_PATH));
    primaryStage.setScene(new Scene(root));
    primaryStage.initStyle(StageStyle.UNDECORATED);
    primaryStage.setTitle(TITLE);
    primaryStage.show();
  }
}

