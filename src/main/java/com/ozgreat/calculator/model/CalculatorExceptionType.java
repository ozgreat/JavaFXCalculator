package com.ozgreat.calculator.model;

/**
 * Possible types of exception for {@link CalculatorException}
 */
public enum CalculatorExceptionType {
  /**
   * Exception which shows that dividing by zero operation was performed
   */
  CANNOT_DIVIDE_BY_ZERO,
  /**
   * Exception which shows that result is undefined (dividing zero by zero operation was performed).
   */
  DIVIDING_ZERO_BY_ZERO,
  /**
   * Exception which shows that input is invalid (root operation for negative number was performed).
   */
  NEGATIVE_ROOT,
  /**
   * Exception which shows that result of calculation is out of range
   */
  OVERFLOW
}
