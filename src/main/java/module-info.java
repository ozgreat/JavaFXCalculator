module calculator {
  requires javafx.fxml;
  requires javafx.base;
  requires javafx.controls;

  opens com.implemica.calculator;
  opens com.implemica.calculator.controller;
  opens com.implemica.calculator.view;
}