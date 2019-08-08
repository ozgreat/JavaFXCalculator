package com.implemica.calculator;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class Main extends Application {

  private final static String TITLE = "Calculator";

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) throws IOException {
    Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("view/root.fxml"));
    primaryStage.setTitle("Calculator");
    primaryStage.setScene(new Scene(root));
    primaryStage.initStyle(StageStyle.UNDECORATED);
    primaryStage.setTitle(TITLE);
    primaryStage.show();
  }
}
