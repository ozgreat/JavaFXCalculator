package com.implemica.calculator.model;

import com.implemica.calculator.model.util.CalcState;
import com.implemica.calculator.model.util.Errors;
import com.implemica.calculator.model.util.Operation;
import com.implemica.calculator.model.util.OperationType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

import static com.implemica.calculator.model.util.Operation.*;
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
  private Operation operation;

  /**
   * Current state of calculator
   */
  private CalcState calcState = CalcState.LEFT;

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
  private final static Map<Operation, BinaryOperator<BigDecimal>> binaryOperations = new HashMap<>();

  /**
   * Map of unary operations
   */
  private final static Map<Operation, UnaryOperator<BigDecimal>> unaryOperations = new HashMap<>();

  private static final BigDecimal MAX_DECIMAL = new BigDecimal("1E10000");

  static {
    binaryOperations.put(ADD, BigDecimal::add);
    binaryOperations.put(SUBTRACT, BigDecimal::subtract);
    binaryOperations.put(MULTIPLY, BigDecimal::multiply);
    binaryOperations.put(DIVIDE, (left, right) -> left.divide(right, mc10K));

    unaryOperations.put(REVERSE, x -> BigDecimal.ONE.divide(x, mc10K));//⅟𝑥
    unaryOperations.put(POW, x -> x.pow(2));//𝑥²
    unaryOperations.put(NEGATE, BigDecimal::negate);//±
    unaryOperations.put(SQRT, x -> x.sqrt(mc10K));
  }


  /**
   * Make calculations for binary operations
   *
   * @return string with result of calculation
   */
  public BigDecimal getBinaryOperationResult() throws ArithmeticException {
    if (rightOperand.compareTo(BigDecimal.ZERO) == 0 && operation == DIVIDE) {
      if (leftOperand.compareTo(BigDecimal.ZERO) == 0) {
        throw new ArithmeticException(Errors.RESULT_IS_UNDEFINED.getMsg());
      }
      throw new ArithmeticException(Errors.CANNOT_DIVIDE_BY_ZERO.getMsg());
    }

    if (leftOperand.compareTo(BigDecimal.ZERO) == 0 && operation == DIVIDE) {
      return BigDecimal.ZERO;
    }

    BigDecimal res = binaryOperations.get(operation).apply(leftOperand, rightOperand);

    return getRounded10KIfItsPossible(res);
  }

  /**
   * Make calculations for unary operations
   *
   * @param op     Current unary operation
   * @param number number, that we calc
   * @return string with result of calculation
   */
  public BigDecimal getUnaryOperationResult(Operation op, String number) throws ArithmeticException {
    BigDecimal num = new BigDecimal(number);
    if (num.equals(BigDecimal.ZERO) && op == REVERSE) {
      throw new ArithmeticException(Errors.CANNOT_DIVIDE_BY_ZERO.getMsg());
    } else if (num.compareTo(BigDecimal.ZERO) < 0 && op == SQRT) {
      throw new ArithmeticException(Errors.INVALID_INPUT.getMsg());
    }


    BigDecimal res = unaryOperations.get(op).apply(num);

    return getRounded10KIfItsPossible(res);
  }

  /**
   * Make calculations for binary operations
   *
   * @return string with result of calculation
   */
  public BigDecimal getPercentOperation() {
    BigDecimal res = BigDecimal.ZERO;

    if (operation != null) {
      if ((operation == ADD || operation == SUBTRACT) && leftOperand.compareTo(BigDecimal.ZERO) != 0) {
        res = leftOperand.multiply(rightOperand.divide(BigDecimal.valueOf(100), mc10K));
      } else if (operation == MULTIPLY || operation == DIVIDE) {
        res = rightOperand.divide(BigDecimal.valueOf(100), mc10K);
      }
    }


    rightOperand = res;
    return getRounded10KIfItsPossible(res);
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
      mc = new MathContext(mc16.getPrecision() + 1);
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
      if (resStrip.toPlainString().replace(".", "").length() <= precision && resStrip.toString().contains("E")) {
        resStrip = new BigDecimal(resStrip.toPlainString());
      } else if (resStrip.toString().replace(".", "").replace("-", "").length() > precision + 1) {
        resStrip = getRounded(resStrip, new MathContext(mc.getPrecision() - 1), precision);
      }
      return resStrip;
    }
    checkOverflow(res);
    return null;
  }


  private static void checkOverflow(BigDecimal res) throws ArithmeticException {
    if (res.compareTo(MAX_DECIMAL) >= 0) {
      throw new ArithmeticException(Errors.OVERFLOW.getMsg());
    }
    if (res.toEngineeringString().contains("E") && !res.toEngineeringString().endsWith("E")) {
      DecimalFormat df = new DecimalFormat("0.################E0####");
      String[] strArr = df.format(res).split("E");
      if (MAX == Math.abs(parseInt(strArr[1])) && new BigDecimal(strArr[0]).abs().compareTo(BigDecimal.ONE) <= 0) {
        throw new ArithmeticException(Errors.OVERFLOW.getMsg());
      }
      if (MAX < Math.abs(parseInt(strArr[1]))) {
        throw new ArithmeticException(Errors.OVERFLOW.getMsg());
      }
    }
  }

  public BigDecimal op(Operation op, BigDecimal left, BigDecimal right) {
    if (op.getType() == OperationType.BINARY) {
      leftOperand = left;
      rightOperand = right;
      operation = op;
      BigDecimal res = getBinaryOperationResult();
    }
    return null;
  }
}