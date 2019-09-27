package com.implemica.calculator.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

import static java.lang.Integer.parseInt;

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
   * Setting of precision for inner methods
   */
  public static final MathContext mc10K = new MathContext(10000);

  /**
   * Setting of precision for outer methods
   */
  public static final MathContext mc16 = new MathContext(16);

  /**
   * Maximum E degree
   */
  private static final int MAX = 10000;

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
    binaryOperations.put("÷", (left, right) -> left.divide(right, mc10K));

    unaryOperations.put("1/", x -> BigDecimal.ONE.divide(x, mc10K));//⅟𝑥
    unaryOperations.put("sqr", x -> x.pow(2));//𝑥²
    unaryOperations.put("negate", BigDecimal::negate);//±
    unaryOperations.put("√", x -> x.sqrt(mc10K));
  }


  /**
   * Make calculations for binary operations
   *
   * @return string with result of calculation
   */
  public String getBinaryOperationResult() throws ArithmeticException {
    if (rightOperand.equals(BigDecimal.ZERO) && operation.equals("÷")) {
      if (leftOperand.equals(BigDecimal.ZERO)) {
        throw new ArithmeticException("Result is undefined");
      }
      throw new ArithmeticException("Cannot divide by zero");
    }

    BigDecimal res = binaryOperations.get(operation).apply(leftOperand, rightOperand);

    return getRounded10KIfItsPossible(res).toString();
  }

  /**
   * Make calculations for unary operations
   *
   * @param op     Current unary operation
   * @param number number, that we calc
   * @return string with result of calculation
   */
  public String getUnaryOperationResult(String op, String number) throws ArithmeticException {
    BigDecimal num = new BigDecimal(number);
    if (num.equals(BigDecimal.ZERO) && op.equals("1/")) {
      throw new ArithmeticException("Cannot divide by zero");
    } else if (num.compareTo(BigDecimal.ZERO) < 0 && op.equals("√")) {
      throw new ArithmeticException("Invalid input");
    }


    BigDecimal res = unaryOperations.get(op).apply(num);

    return getRounded10KIfItsPossible(res).toString();
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
        res = leftOperand.multiply(rightOperand.divide(BigDecimal.valueOf(100), mc10K));
      } else if (operation.equals("×") || operation.equals("÷")) {
        res = rightOperand.divide(BigDecimal.valueOf(100), mc10K);
      }
    }


    rightOperand = res;
    return getRounded10KIfItsPossible(res).toString();
  }

  /**
   * Setting memory null value
   */
  public void clearMemory() {
    memory = null;
  }

  /**
   * Add number to memory. Set memory as num.
   *
   * @param num number that we add
   */
  public void memoryAdd(BigDecimal num) {
    if (memory != null) {
      memory = getRounded10KIfItsPossible(memory.add(num));
    } else {
      memory = num;
    }
  }

  /**
   * Subtract number to memory. Set memory as num.
   *
   * @param num number that we subtract
   */
  public void memorySub(BigDecimal num) {
    if (memory != null) {
      memory = getRounded10KIfItsPossible(memory.subtract(num));
    } else {
      memory = num.negate();
    }
  }

  private static BigDecimal getRounded10KIfItsPossible(BigDecimal res) throws ArithmeticException {
    checkOverflow(res);
    res = res.round(mc10K);
    BigDecimal resStrip = getRounded(res, mc10K, mc10K.getPrecision());
    if (resStrip != null) {
      return resStrip;
    } else {
      return res;
    }
  }

  /**
   * Get rounded to mc16 number from given
   *
   * @param res given number
   * @return rounded number
   */
  public static BigDecimal getRounded16IfItsPossible(BigDecimal res) {
    MathContext mc = mc16;
    if (res.compareTo(BigDecimal.ONE) < 0 && res.compareTo(BigDecimal.valueOf(-3)) > 0) {
      mc = new MathContext(mc16.getPrecision()+1);
    }
    res = res.round(mc);


    BigDecimal resStrip = getRounded(res, mc, mc.getPrecision());

    if (resStrip != null) {
      return resStrip;
    } else {
      return res;
    }
  }

  private static BigDecimal getRounded(BigDecimal res, MathContext mc, int precision) throws ArithmeticException {
    if (res.toString().contains(".")) {
      res = res.round(mc);
      checkOverflow(res);
      BigDecimal resStrip = res.stripTrailingZeros();
      if (resStrip.toPlainString().length() <= precision && resStrip.toString().contains("E")) {
        resStrip = new BigDecimal(resStrip.toPlainString());
      } else if (resStrip.toPlainString().length() > precision + 1) {
        resStrip = getRounded(resStrip, new MathContext(mc.getPrecision() - 1), precision);
      }
      return resStrip;
    }
    checkOverflow(res);
    return null;
  }


  private static void checkOverflow(BigDecimal res) throws ArithmeticException {
    if (res.toEngineeringString().contains("E") && !res.toEngineeringString().endsWith("E")) {
      DecimalFormat df = new DecimalFormat("0.################E0####");
      String[] strArr = df.format(res).split("E");
      if (MAX <= Math.abs(parseInt(strArr[1]))) {
        throw new ArithmeticException("Overflow");
      }
    }
  }
}