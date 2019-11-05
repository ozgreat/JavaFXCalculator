package com.implemica.calculator.model;

/**
 * Custom made exception which shows that input is invalid (root operation for negative number was performed).
 *
 * @author ozgreat
 */
public class NegativeRootException extends Exception {
  /**
   * Message of exception
   */
  private static final String MESSAGE = "Invalid input";

  /**
   * Constructor of exception
   */
  NegativeRootException() {
    super(MESSAGE);
  }
}
