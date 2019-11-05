package com.implemica.calculator.model;

/**
 * Enum of possible operations
 */
public enum ArithmeticOperation {
  /**
   * Square root operation
   */
  SQRT(ArithmeticOperationType.UNARY),
  /**
   * Square operation
   */
  POW(ArithmeticOperationType.UNARY),
  /**
   * Reverse operation
   */
  REVERSE(ArithmeticOperationType.UNARY),
  /**
   * Negate operation
   */
  NEGATE(ArithmeticOperationType.UNARY),
  /**
   * Add operation
   */
  ADD(ArithmeticOperationType.BINARY),
  /**
   * Subtract operation
   */
  SUBTRACT(ArithmeticOperationType.BINARY),
  /**
   * Multiply operation
   */
  MULTIPLY(ArithmeticOperationType.BINARY),
  /**
   * Divide operation
   */
  DIVIDE(ArithmeticOperationType.BINARY),
  /**
   * Percent add or subtract operation
   */
  PERCENT_ADD_SUBTRACT(ArithmeticOperationType.PERCENT),
  /**
   * Percent multiply or divide operation
   */
  PERCENT_MUL_DIVIDE(ArithmeticOperationType.PERCENT);

  /**
   * Type of operation
   */
  final private ArithmeticOperationType type;

  ArithmeticOperation(ArithmeticOperationType type) {
    this.type = type;
  }

  public ArithmeticOperationType getType() {
    return type;
  }}
