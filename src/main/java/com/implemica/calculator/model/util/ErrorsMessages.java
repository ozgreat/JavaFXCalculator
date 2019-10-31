package com.implemica.calculator.model.util;

import lombok.Getter;

/**
 * Enum of messages of expected errors
 */
public enum ErrorsMessages {
  /**
   * Message of overflow error
   */
  OVERFLOW("Overflow"),
  /**
   * Message of invalid input error
   */
  INVALID_INPUT("Invalid input"),
  /**
   *  Message of cannot divide by zero error
   */
  CANNOT_DIVIDE_BY_ZERO("Cannot divide by zero"),
  /**
   * Message of result is undefined error
   */
  RESULT_IS_UNDEFINED("Result is undefined");

  @Getter
  private String msg;

  ErrorsMessages(String msg) {
    this.msg = msg;
  }

  public static boolean isMessageMatch(String msg) {
    return OVERFLOW.msg.equals(msg) || CANNOT_DIVIDE_BY_ZERO.msg.equals(msg) || RESULT_IS_UNDEFINED.msg.equals(msg) ||
        INVALID_INPUT.msg.equals(msg);
  }
}
