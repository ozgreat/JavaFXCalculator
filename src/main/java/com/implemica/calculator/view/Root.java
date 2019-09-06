package com.implemica.calculator.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import lombok.Getter;

import java.io.IOException;

@Getter
public class Root {
  private static final String ROOT_FXML_PATH = "view/root.fxml";
  private FXMLLoader loader;

  public Root() {
    loader = new FXMLLoader(getClass().getClassLoader().getResource(ROOT_FXML_PATH));
  }

  public Parent getFXML() throws IOException {
    return loader.load();
  }
}
