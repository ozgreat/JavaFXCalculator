package com.implemica.calculator.model;

/**
 * Enum that contains possible status in calculator
 */
public enum CalculatorState {
  /**
   * CalculatorState when user is typing left operand
   */
  LEFT,
  /**
   * CalculatorState when user has just typed left operand and will type right operand
   */
  TRANSIENT,
  /**
   * CalculatorState when user is typing right operand
   */
  RIGHT,
  /**
   * CalculatorState after operation
   */
  AFTER
}
