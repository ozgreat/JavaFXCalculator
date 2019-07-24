package com.implemica.calculator.model;

import java.math.BigDecimal;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CalculatorModel {
  private BigDecimal leftOperand;

  private BigDecimal rightOperand;

  private BigDecimal memory;

  private String operation;

  private final static Map<String, BinaryOperator<BigDecimal>> operations = new HashMap<>(Stream.of(
      new AbstractMap.SimpleEntry<String, BinaryOperator<BigDecimal>>("+", BigDecimal::add),
      new AbstractMap.SimpleEntry<String, BinaryOperator<BigDecimal>>("-", BigDecimal::subtract),
      new AbstractMap.SimpleEntry<String, BinaryOperator<BigDecimal>>("×", BigDecimal::multiply),
      new AbstractMap.SimpleEntry<String, BinaryOperator<BigDecimal>>("÷", BigDecimal::divide)
  ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

  public BigDecimal getLeftOperand() {
    return leftOperand;
  }

  public void setLeftOperand(BigDecimal leftOperand) {
    this.leftOperand = leftOperand;
  }

  public BigDecimal getRightOperand() {
    return rightOperand;
  }

  public void setRightOperand(BigDecimal rightOperand) {
    this.rightOperand = rightOperand;
  }

  public String getOperation() {
    return operation;
  }

  public void setOperation(String operation) {
    this.operation = operation;
  }

  public String getResult() {
    if (rightOperand.equals(BigDecimal.ZERO) && operation.equals("÷")) {
      return ""; // todo: replace with throw
    }

    return operations.get(operation).apply(leftOperand, rightOperand).toString();
  }

}
