package com.implemica.calculator.model;

import lombok.Getter;

/**
 * Enum of possible operations
 */
public enum Operation {
  /**
   * Square root operation
   */
  SQRT(OperationType.UNARY),
  /**
   * Square operation
   */
  POW(OperationType.UNARY),
  /**
   * Reverse operation
   */
  REVERSE(OperationType.UNARY),
  /**
   * Negate operation
   */
  NEGATE(OperationType.UNARY),
  /**
   * Add operation
   */
  ADD(OperationType.BINARY),
  /**
   * Subtract operation
   */
  SUBTRACT(OperationType.BINARY),
  /**
   * Multiply operation
   */
  MULTIPLY(OperationType.BINARY),
  /**
   * Divide operation
   */
  DIVIDE(OperationType.BINARY),
  /**
   * Percent add or subtract operation
   */
  PERCENT_ADD_SUBTRACT(OperationType.PERCENT),
  /**
   * Percent multiply or divide operation
   */
  PERCENT_MUL_DIVIDE(OperationType.PERCENT),
  /**
   * Memory add operation
   */
  MEMORY_ADD(OperationType.MEMORY),
  /**
   * Memory subtract operation
   */
  MEMORY_SUB(OperationType.MEMORY),
  /**
   * Memory save operation
   */
  MEMORY_SAVE(OperationType.MEMORY),
  /**
   * Memory clear operation
   */
  MEMORY_CLEAR(OperationType.MEMORY);

  /**
   * Type of operation
   */
  @Getter
  final private OperationType type;

  Operation(OperationType type) {
    this.type = type;
  }
}
