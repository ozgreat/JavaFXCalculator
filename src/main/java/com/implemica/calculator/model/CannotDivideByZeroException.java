package com.implemica.calculator.model;

/**
 * Custom made exception which shows that dividing by zero operation was performed
 *
 * @author ozgreat
 */
public class CannotDivideByZeroException extends Exception {
  /**
   * Message of exception
   */
  private static final String MESSAGE = "Cannot divide by zero";

  /**
   * Constructor of exception
   */
  CannotDivideByZeroException(){
    super(MESSAGE);
  }
}
