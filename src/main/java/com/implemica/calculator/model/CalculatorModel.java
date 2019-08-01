package com.implemica.calculator.model;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

public class CalculatorModel {
  private BigDecimal leftOperand;

  private BigDecimal rightOperand;

  private BigDecimal memory;

  private String operation;

  private static final MathContext mc = new MathContext(16);

  private static final BigDecimal SQRT_DIG = new BigDecimal(150); //todo: magical number
  private static final BigDecimal SQRT_PRE = new BigDecimal(10).pow(SQRT_DIG.intValue());

  private final static Map<String, BinaryOperator<BigDecimal>> binaryOperations = new HashMap<>();

  private final static Map<String, UnaryOperator<BigDecimal>> unaryOperations = new HashMap<>();

  private final static Map<String, BinaryOperator<BigDecimal>> percentOperations = new HashMap<>();

  static {
    binaryOperations.put("+", BigDecimal::add);
    binaryOperations.put("-", BigDecimal::subtract);
    binaryOperations.put("×", BigDecimal::multiply);
    binaryOperations.put("÷", (left, right) -> left.divide(right, mc));

    unaryOperations.put("1/x", x -> BigDecimal.ONE.divide(x, mc));
    unaryOperations.put("pow", x -> x.pow(2));
    unaryOperations.put("±", BigDecimal::negate);
    unaryOperations.put("√", CalculatorModel::sqrt);

    percentOperations.put("+", (left, right) -> left.add(left.multiply(right).divide(BigDecimal.valueOf(100), mc)));
    percentOperations.put("-", (left, right) -> left.subtract(left.multiply(right).divide(BigDecimal.valueOf(100), mc)));
    percentOperations.put("×", (left, right) -> left.multiply(left.multiply(right).divide(BigDecimal.valueOf(100), mc)));
    percentOperations.put("÷", (left, right) -> left.divide(left.multiply(right).divide(BigDecimal.valueOf(100), mc), mc));
  }

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

  public String getBinaryOperationResult() {
    if (rightOperand.equals(BigDecimal.ZERO) && operation.equals("÷")) {
      return ""; // todo: replace with throw
    }

    BigDecimal res = binaryOperations.get(operation).apply(leftOperand, rightOperand);

    return getRoundedIfItsPossible(res).toString();
  }

  public String getUnaryOperationResult(String op, String number) {
    BigDecimal res = unaryOperations.get(op).apply(new BigDecimal(number));

    return getRoundedIfItsPossible(res).toString();
  }

  public String getPercentOperation() {
    BigDecimal res = percentOperations.get(operation).apply(leftOperand, rightOperand);

    return getRoundedIfItsPossible(res).toPlainString();
  }

  private static BigDecimal getRoundedIfItsPossible(BigDecimal res) {
    res = res.round(mc);
    BigDecimal integerValue = BigDecimal.valueOf(res.intValue());
    if (res.compareTo(integerValue) == 0) {
      return BigDecimal.valueOf(res.intValueExact());
    } else {
      return res;
    }
  }

  private static BigDecimal sqrt(BigDecimal c) {
    if (c.equals(BigDecimal.ZERO)) {
      return BigDecimal.ZERO;
    } else if (c.equals(BigDecimal.ONE)) {
      return BigDecimal.ONE;
    }
    BigDecimal res = sqrt(c, BigDecimal.ONE, BigDecimal.ONE.divide(SQRT_PRE, mc));

    return getRoundedIfItsPossible(res);
  }

  private static BigDecimal sqrt(BigDecimal c, BigDecimal xn, BigDecimal precision) {
    BigDecimal fx = xn.pow(2).add(c.negate());
    BigDecimal fpx = xn.multiply(new BigDecimal(2));
    BigDecimal xn1 = fx.divide(fpx, 2 * SQRT_DIG.intValue(), RoundingMode.HALF_DOWN);
    xn1 = xn.add(xn1.negate());

    BigDecimal currentSquare = xn1.pow(2);

    BigDecimal currentPrecision = currentSquare.subtract(c);
    currentPrecision = currentPrecision.abs();
    if (currentPrecision.compareTo(precision) <= -1) {
      return xn1;
    }

    return sqrt(c, xn1, precision);
  }

}