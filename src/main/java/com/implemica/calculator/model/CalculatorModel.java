package com.implemica.calculator.model;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

public class CalculatorModel {
  /**
   * Left operand of binary and percent operations
   */
  private BigDecimal leftOperand;

  /**
   * Right operand of binary and percent operations
   */
  private BigDecimal rightOperand;

  /**
   * Memory cell in calculator
   */
  private List<BigDecimal> memory;

  /**
   * Operation, that user will be use
   */
  private String operation;

  /**
   * Setting of precision
   */
  public static final MathContext mc32 = new MathContext(32);

  public static final MathContext mc16 = new MathContext(16);

  private static final BigDecimal SQRT_DIG = new BigDecimal(150); //todo: magical number
  private static final BigDecimal SQRT_PRE = new BigDecimal(10).pow(SQRT_DIG.intValue());

  /**
   * Map of binary operations
   */
  private final static Map<String, BinaryOperator<BigDecimal>> binaryOperations = new HashMap<>();

  /**
   * Map of unary operations
   */
  private final static Map<String, UnaryOperator<BigDecimal>> unaryOperations = new HashMap<>();


  static {
    binaryOperations.put("+", BigDecimal::add);
    binaryOperations.put("-", BigDecimal::subtract);
    binaryOperations.put("×", BigDecimal::multiply);
    binaryOperations.put("÷", (left, right) -> left.divide(right, mc32));

    unaryOperations.put("⅟\uD835\uDC65", x -> BigDecimal.ONE.divide(x, mc32));//⅟𝑥
    unaryOperations.put("\uD835\uDC65²", x -> x.pow(2));//𝑥²
    unaryOperations.put("±", BigDecimal::negate);
    unaryOperations.put("√", CalculatorModel::sqrt);
  }

  public CalculatorModel() {
    memory = new LinkedList<>();
    operation = "+";
    leftOperand = BigDecimal.ZERO;
    rightOperand = BigDecimal.ZERO;
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

  public List<BigDecimal> getMemory() {
    return memory;
  }

  /**
   * Make calculations for binary operations
   *
   * @return string with result of calculation
   */
  public String getBinaryOperationResult() {
    if (rightOperand.equals(BigDecimal.ZERO) && operation.equals("÷")) {
      throw new ArithmeticException(); // todo: add message and test
    }

    BigDecimal res = binaryOperations.get(operation).apply(leftOperand, rightOperand);

    return getRounded32IfItsPossible(res).toString();
  }

  /**
   * Make calculations for unary operations
   *
   * @param op     Current unary operation
   * @param number number, that we calc
   * @return string with result of calculation
   */
  public String getUnaryOperationResult(String op, String number) {
    BigDecimal res = unaryOperations.get(op).apply(new BigDecimal(number));

    return getRounded32IfItsPossible(res).toString();
  }

  /**
   * Make calculations for binary operations
   *
   * @return string with result of calculation
   */
  public String getPercentOperation() {
    BigDecimal res = leftOperand.multiply(rightOperand.divide(BigDecimal.valueOf(100), mc32));
    rightOperand = res;
    return getRounded32IfItsPossible(res).toString();
  }

  public void clearMemory() {
    memory.clear();
  }

  public BigDecimal recallMemory() {
    return memory.get(memory.size() - 1);
  }

  public void memoryAdd(BigDecimal num) {
    if (memory.size() > 0) {
      memory.set(memory.size() - 1, memory.get(memory.size() - 1).add(num));
    } else {
      memory.add(num);
    }
  }

  public void memorySub(BigDecimal num) {
    if (memory.size() > 0) {
      memory.set(memory.size() - 1, memory.get(memory.size() - 1).subtract(num));
    } else {
      memory.add(num.negate());
    }
  }

  private static BigDecimal getRounded32IfItsPossible(BigDecimal res) {
    res = res.round(mc32);
    BigDecimal resStrip = getRounded(res, mc32, mc32.getPrecision());
    if (resStrip != null) {
      return resStrip;
    } else {
      return res;
    }
  }

  public static BigDecimal getRounded16IfItsPossible(BigDecimal res) {
    res = res.round(mc16);
    BigDecimal resStrip = getRounded(res, mc16, mc16.getPrecision());
    if (resStrip != null) {
      return resStrip;
    } else {
      return res;
    }
  }

  private static BigDecimal getRounded(BigDecimal res, MathContext mc, int precision) {
    if (res.toString().contains(".")) {
      res = res.round(mc);
      BigDecimal resStrip = res.stripTrailingZeros();
      if (resStrip.toPlainString().length() <= precision && resStrip.toString().contains("E")) {
        resStrip = new BigDecimal(resStrip.toPlainString());
      } else if (resStrip.toPlainString().length() > precision + 1) {
        resStrip = getRounded(resStrip, new MathContext(mc.getPrecision() - 1), precision);
      }
      return resStrip;
    }
    return null;
  }


  private static BigDecimal sqrt(BigDecimal c) {
    if (c.equals(BigDecimal.ZERO)) {
      return BigDecimal.ZERO;
    } else if (c.equals(BigDecimal.ONE)) {
      return BigDecimal.ONE;
    }
    BigDecimal res = sqrt(c, BigDecimal.ONE, BigDecimal.ONE.divide(SQRT_PRE, mc32));

    return getRounded32IfItsPossible(res);
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