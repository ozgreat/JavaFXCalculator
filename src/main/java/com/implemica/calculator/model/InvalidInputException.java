package com.implemica.calculator.model;

/**
 * Custom made exception which shows that input is invalid (root operation for negative number was performed).
 *
 * @author ozgreat
 */
public class InvalidInputException extends CalculationException {
  /**
   * Message of exception
   */
  private static final String MESSAGE = "Invalid input";

  /**
   * Constructor of exception
   */
  InvalidInputException() {
    super(MESSAGE);
  }
}
