package com.implemica.calculator.model.util;

import lombok.Getter;

public enum Errors {
  OVERFLOW("Overflow"),
  INVALID_INPUT("Invalid input"),
  CANNOT_DIVIDE_BY_ZERO("Cannot divide by zero"),
  RESULT_IS_UNDEFINED("Result is undefined");

  @Getter
  private String msg;

  Errors(String msg) {
    this.msg = msg;
  }
}
