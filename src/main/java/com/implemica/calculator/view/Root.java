package com.implemica.calculator.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;

public class Root {
  private static final String ROOT_FXML_PATH = "view/root.fxml";

  public Parent getRoot() throws IOException {
    return FXMLLoader.load(getClass().getClassLoader().getResource(ROOT_FXML_PATH));
  }
}
