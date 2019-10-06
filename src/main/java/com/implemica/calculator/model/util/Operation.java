package com.implemica.calculator.model.util;

import lombok.Getter;

public enum Operation {
  SQRT(OperationType.UNARY , "√"),
  POW(OperationType.UNARY, "sqr"),
  REVERSE(OperationType.UNARY, "1/"),
  NEGATE(OperationType.UNARY, "negate"),
  ADD(OperationType.BINARY, "+"),
  SUBTRACT(OperationType.BINARY, "-"),
  MULTIPLY(OperationType.BINARY, "×"),
  DIVIDE(OperationType.BINARY, "÷"),
  PERCENT_ADD_SUBTRACT(OperationType.PERCENT),
  PERCENT_MUL_DIVIDE(OperationType.PERCENT),
  MEMORY_ADD(OperationType.MEMORY),
  MEMORY_SUB(OperationType.MEMORY),
  MEMORY_SAVE(OperationType.MEMORY),
  MEMORY_CLEAR(OperationType.MEMORY);

  @Getter
  final private OperationType type;

  @Getter
  final private String symbol;

  Operation(OperationType type) {
    this.type = type;
    symbol = null;
  }

  Operation(OperationType type, String symbol) {
    this.type = type;
    this.symbol = symbol;
  }
}
