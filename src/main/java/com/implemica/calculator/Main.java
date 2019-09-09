package com.implemica.calculator;

import com.implemica.calculator.view.Root;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

import static javafx.application.Application.launch;

public class Main {



  public static void main(String[] args) {
    launch(Root.class);
  }

  /*@Override
  public void start(Stage primaryStage) throws IOException {
    Parent root = Root.getFXML();
    primaryStage.getIcons().add(new Image(getClass().getResource(ICON_PATH).toExternalForm()));
    primaryStage.setTitle("Calculator");
    primaryStage.setScene(new Scene(root));
    primaryStage.initStyle(StageStyle.UNDECORATED);
    primaryStage.setTitle(TITLE);
    primaryStage.show();
  }*/
}

