package com.implemica.calculator.model.util;

public enum CalcState {
  /**
   * CalcState when user is typing left operand
   */
  LEFT,
  /**
   * CalcState when user has just typed left operand and will type right operand
   */
  TRANSIENT,
  /**
   * CalcState when user is typing right operand
   */
  RIGHT,
  /**
   * CalcState after operation
   */
  AFTER
}
