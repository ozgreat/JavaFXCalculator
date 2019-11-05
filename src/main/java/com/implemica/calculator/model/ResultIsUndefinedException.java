package com.implemica.calculator.model;

/**
 * Custom made exception which shows that result is undefined (dividing zero by zero operation was performed).
 *
 * @author ozgreat
 */
public class ResultIsUndefinedException extends CalculationException {
  /**
   * Message of exception
   */
  private static final String MESSAGE = "Result is undefined";

  /**
   * Constructor of exception
   */
  ResultIsUndefinedException() {
    super(MESSAGE);
  }
}
