package com.implemica.calculator.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

@Getter
@Setter
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
  private BigDecimal memory;


  /**
   * Operation, that user will be use
   */
  private String operation;

  /**
   * Setting of precision
   */
  public static final MathContext mc32 = new MathContext(32);

  public static final MathContext mc16 = new MathContext(16);

  private static final BigDecimal max = new BigDecimal("9999999999999999E+8192");

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

    unaryOperations.put("1/", x -> BigDecimal.ONE.divide(x, mc32));//⅟𝑥
    unaryOperations.put("sqr", x -> x.pow(2));//𝑥²
    unaryOperations.put("negate", BigDecimal::negate);//±
    unaryOperations.put("√", CalculatorModel::sqrt);
  }


  /**
   * Make calculations for binary operations
   *
   * @return string with result of calculation
   */
  public String getBinaryOperationResult() throws ArithmeticException {
    if (rightOperand.equals(BigDecimal.ZERO) && operation.equals("÷")) {
      if(leftOperand.equals(BigDecimal.ZERO)){
        throw new ArithmeticException("Result is undefined");
      }
      throw new ArithmeticException("Cannot divide by zero");
    }


    if (operation == null) {
      return "0";
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
  public String getUnaryOperationResult(String op, String number) throws ArithmeticException {
    if(new BigDecimal(number).equals(BigDecimal.ZERO) && op.equals("1/")){
      throw new ArithmeticException("Cannot divide by zero");
    }
    BigDecimal res = unaryOperations.get(op).apply(new BigDecimal(number));

    return getRounded32IfItsPossible(res).toString();
  }

  /**
   * Make calculations for binary operations
   *
   * @return string with result of calculation
   */
  public String getPercentOperation() {
    BigDecimal res = BigDecimal.ZERO;

    if (operation != null) {
      if (operation.equals("+") || operation.equals("-")) {
        res = leftOperand.multiply(rightOperand.divide(BigDecimal.valueOf(100), mc32));
      } else if (operation.equals("×") || operation.equals("÷")) {
        res = rightOperand.divide(BigDecimal.valueOf(100), mc32);
      }
    }


    rightOperand = res;
    return getRounded32IfItsPossible(res).toString();
  }

  public void clearMemory() {
    memory = null;
  }


  public void memoryAdd(BigDecimal num) {
    if (memory != null) {
      memory = getRounded32IfItsPossible(memory.add(num));
    } else {
      memory = num;
    }
  }

  public void memorySub(BigDecimal num) {
    if (memory != null) {
      memory = getRounded32IfItsPossible(memory.subtract(num));
    } else {
      memory = num.negate();
    }
  }

  private static BigDecimal getRounded32IfItsPossible(BigDecimal res) throws ArithmeticException {
    res = res.round(mc32);
    BigDecimal resStrip = getRounded(res, mc32, mc32.getPrecision());
    if (resStrip != null) {
      return resStrip;
    } else {
      return res;
    }
  }

  public static BigDecimal getRounded16IfItsPossible(BigDecimal res) {
    MathContext mc = mc16;
    if (res.compareTo(BigDecimal.ONE) < 0 && res.compareTo(BigDecimal.valueOf(-3)) > 0) {
      mc = new MathContext(17);
    }
    res = res.round(mc);


    BigDecimal resStrip = getRounded(res, mc, mc.getPrecision());

    if (resStrip != null) {
      return resStrip;
    } else {
      return res;
    }
  }

  private static BigDecimal getRounded(BigDecimal res, MathContext mc, int precision) {
    if (res.compareTo(max) >= 0) {
      throw new ArithmeticException("Overflow");
    }
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