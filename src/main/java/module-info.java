module calculator {
  requires javafx.fxml;
  requires javafx.graphics;
  requires javafx.base;
  requires javafx.controls;
  requires static lombok;

  opens com.implemica.calculator;
  opens com.implemica.calculator.controller;
  opens com.implemica.calculator.view;
  opens layout;
}