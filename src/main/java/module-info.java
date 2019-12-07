module calculator {
  requires javafx.fxml;
  requires javafx.base;
  requires javafx.controls;

  opens com.ozgreat.calculator;
  opens com.ozgreat.calculator.controller;
  opens com.ozgreat.calculator.view;
}