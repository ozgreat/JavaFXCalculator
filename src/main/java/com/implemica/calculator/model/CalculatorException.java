package com.implemica.calculator.model;

/**
 * Exception class, that get signal to exceptional situations in the calculator like diving on zero
 *
 * @author ozgreat
 */
public class CalculatorException extends Exception {
  /**
   * Type of calculator exception
   */
  private final CalculatorExceptionType type;

  /**
   * Constructor with parameter to set type of exception
   *
   * @param type type of exception
   */
  public CalculatorException(CalculatorExceptionType type) {
    this.type = type;
  }

  public CalculatorExceptionType getType() {
    return type;
  }
}
