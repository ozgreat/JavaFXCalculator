package com.implemica.calculator.model;

/**
 * Custom made exception which shows that something in calculation was wrong
 *
 * @author ozgreat
 *
 */
public class CalculationException extends Exception {
  /**
   * Constructor of exception
   *
   * @param msg message of exception
   */
  CalculationException(String msg) {
    super(msg);
  }
}
