package com.implemica.calculator.model;

/**
 * Custom made exception which shows that something is out of range.
 *
 * @author ozgreat
 */
public class OverflowException extends Exception {
  /**
   * Message of exception
   */
  private static final String MESSAGE = "Overflow";

  /**
   * Constructor of exception
   */
  OverflowException() {
    super(MESSAGE);
  }
}
