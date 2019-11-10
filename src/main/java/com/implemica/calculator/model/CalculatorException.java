package com.implemica.calculator.model;

public class CalculatorException extends Exception {
  private final CalculatorExceptionType type;

  public CalculatorException(CalculatorExceptionType type) {
    super();
    this.type = type;
  }

  public CalculatorExceptionType getType() {
    return type;
  }
}
