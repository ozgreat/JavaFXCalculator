package com.implemica.calculator.model;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

import static com.implemica.calculator.model.Operation.*;

/**
 * Model of calculator, calculate {@link BigDecimal} with operations like sqrt, divide, multiply, etc.
 *
 * @author ozgreat
 * @see BigDecimal
 * @see Operation
 * @see CalculatorState
 */
public class CalculatorModel {
  /**
   * Left operand of binary and percent operations
   */
  private BigDecimal leftOperand;

  /**
   * Right operand of binary and percent operations
   */
  private BigDecimal rightOperand;

  public void setLeftOperand(BigDecimal leftOperand) {
    this.leftOperand = leftOperand;
  }


  public void setRightOperand(BigDecimal rightOperand) {
    this.rightOperand = rightOperand;
  }

  public BigDecimal getMemory() {
    return memory;
  }

  public Operation getOperation() {
    return operation;
  }

  public void setOperation(Operation operation) {
    this.operation = operation;
  }

  public CalculatorState getCalculatorState() {
    return calculatorState;
  }

  public void setCalculatorState(CalculatorState calculatorState) {
    this.calculatorState = calculatorState;
  }

  /**
   * Memory cell in calculator
   */
  private BigDecimal memory;

  /**
   * {@link Operation}, that user will be use
   */
  private Operation operation;

  /**
   * {@link Operation}, that user before current
   */
  private Operation prevOperation;

  /**
   * Current state of calculator
   */
  private CalculatorState calculatorState = CalculatorState.LEFT;

  /**
   * Maximum scale of number
   *
   * @see BigDecimal
   */
  private static final int DIVIDE_SCALE = 10000;

  /**
   * {@link Map} of binary operations
   */
  private final static Map<Operation, BinaryOperator<BigDecimal>> binaryOperations = new HashMap<>();

  /**
   * {@link Map} of unary operations
   */
  private final static Map<Operation, UnaryOperator<BigDecimal>> unaryOperations = new HashMap<>();

  /**
   * First {@link BigDecimal} value, that bigger than one and can't be calculating
   */
  private static final BigDecimal MAX_DECIMAL = new BigDecimal("1E10000");

  private static final BigDecimal MIN_DECIMAL = new BigDecimal("-1E10000");

  private static final BigDecimal MIN_POSITIVE = new BigDecimal("1E-9999");

  private static final BigDecimal MIN_NEGATIVE = new BigDecimal("-1E-9999");

  private static final MathContext SQRT_CONTEXT = new MathContext(10000);

  static {
    binaryOperations.put(ADD, BigDecimal::add);
    binaryOperations.put(SUBTRACT, BigDecimal::subtract);
    binaryOperations.put(MULTIPLY, BigDecimal::multiply);
    binaryOperations.put(DIVIDE, (left, right) -> left.divide(right, DIVIDE_SCALE, RoundingMode.HALF_UP));

    unaryOperations.put(REVERSE, x -> BigDecimal.ONE.divide(x, DIVIDE_SCALE, RoundingMode.HALF_UP));//⅟𝑥
    unaryOperations.put(POW, x -> x.pow(2));//𝑥²
    unaryOperations.put(NEGATE, BigDecimal::negate);//±
    unaryOperations.put(SQRT, x -> x.sqrt(SQRT_CONTEXT));
  }


  /**
   * Make calculations for binary operations
   *
   * @return string with result of calculation
   */
  private BigDecimal getBinaryOperationResult() throws CalculationException {
    if (rightOperand.compareTo(BigDecimal.ZERO) == 0 && operation == DIVIDE) {
      if (leftOperand.compareTo(BigDecimal.ZERO) == 0) {
        throw new ResultIsUndefinedException();
      }
      throw new CannotDivideByZeroException();
    }

    if (leftOperand.compareTo(BigDecimal.ZERO) == 0 && operation == DIVIDE) {
      return BigDecimal.ZERO;
    }

    BigDecimal res = binaryOperations.get(operation).apply(leftOperand, rightOperand);

    if (res.compareTo(leftOperand) == 0 && operation == DIVIDE && rightOperand.compareTo(BigDecimal.ONE) != 0
        && leftOperand.compareTo(MIN_POSITIVE) == 0) {
      throw new OverflowException();
    }

    checkOverflow(res);
    res = res.stripTrailingZeros();
    return res;
  }

  /**
   * Make calculations for unary operations
   *
   * @param op     Current unary operation
   * @param number number, that we calc
   * @return string with result of calculation
   */
  private BigDecimal getUnaryOperationResult(Operation op, BigDecimal number) throws CalculationException {
    if (number.equals(BigDecimal.ZERO) && op == REVERSE) {
      throw new CannotDivideByZeroException();
    } else if (number.compareTo(BigDecimal.ZERO) < 0 && op == SQRT) {
      throw new InvalidInputException();
    }


    BigDecimal res = unaryOperations.get(op).apply(number);

    checkOverflow(res);
    return res.stripTrailingZeros();
  }

  /**
   * Make calculations for binary operations
   *
   * @return string with result of calculation
   */
  private BigDecimal getPercentOperation(Operation operation) throws OverflowException {
    BigDecimal res = BigDecimal.ZERO;

    if (operation != null) {
      if ((operation == PERCENT_ADD_SUBTRACT) && leftOperand.compareTo(BigDecimal.ZERO) != 0) {
        res = leftOperand.multiply(rightOperand.divide(BigDecimal.valueOf(100), DIVIDE_SCALE, RoundingMode.HALF_UP));
      } else if (operation == PERCENT_MUL_DIVIDE) {
        res = rightOperand.divide(BigDecimal.valueOf(100), DIVIDE_SCALE, RoundingMode.HALF_UP);
      }
    }


    rightOperand = res;
    checkOverflow(res);
    return res.stripTrailingZeros();
  }

  /**
   * Get memory and setup them as operand if have to
   *
   * @return memory value
   */
  public BigDecimal recallMemory() {
    if (calculatorState == CalculatorState.LEFT) {
      leftOperand = memory;
    } else if (calculatorState == CalculatorState.RIGHT) {
      rightOperand = memory;
    }
    return getMemory().stripTrailingZeros();
  }

  /**
   * Save number to memory
   *
   * @param num num to save
   */
  public void memorySave(BigDecimal num) {
    if (calculatorState == CalculatorState.TRANSIENT) {
      memory = leftOperand;
    } else if (calculatorState == CalculatorState.AFTER) {
      if (leftOperand == null) {
        memory = BigDecimal.ZERO;
      } else {
        memory = leftOperand;
      }
    } else if (calculatorState == CalculatorState.RIGHT || calculatorState == CalculatorState.LEFT) {
      memory = num;
    }
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
  public void memoryAdd(BigDecimal num) throws OverflowException {
    if (memory != null) {
      checkOverflow(num);
      memory = memory.add(num);
    } else {
      memory = num;
    }
  }

  /**
   * Subtract number to memory. Set memory as num.
   *
   * @param num number that we subtract
   */
  public void memorySub(BigDecimal num) throws OverflowException {
    if (memory != null) {
      checkOverflow(num);
      memory = memory.subtract(num);
    } else {
      num = num.negate();
      memory = num;
    }
  }

  /**
   * Check, that number don't too big or too small
   *
   * @param res number to check
   * @throws ArithmeticException if res is too big or too small
   */
  public static void checkOverflow(BigDecimal res) throws OverflowException {
    if (res.compareTo(MAX_DECIMAL) >= 0 || res.compareTo(MIN_DECIMAL) <= 0) {
      throw new OverflowException();
    }

    if (checkMinNumbers(res)) {
      throw new OverflowException();
    }
  }

  private static boolean checkMinNumbers(BigDecimal num) {
    return (num.compareTo(MIN_POSITIVE) < 0 && num.compareTo(BigDecimal.ZERO) > 0)
        || (num.compareTo(MIN_NEGATIVE) > 0 && num.compareTo(BigDecimal.ZERO) < 0);
  }

  /**
   * Calculate operations
   *
   * @param operation     Operation, that we do
   * @param firstOperand  first operand of calculation
   * @param secondOperand second operand of calculation
   * @return result of calculation, that written at left or right operand field
   * @throws ArithmeticException If Overflow, Invalid input or Dividing by Zero
   */
  public BigDecimal doCalculate(Operation operation, BigDecimal firstOperand, BigDecimal secondOperand) throws CalculationException {
    if (operation.getType() == OperationType.BINARY) {
      leftOperand = firstOperand;
      rightOperand = secondOperand;
      this.operation = operation;
      calculatorState = CalculatorState.AFTER;
      leftOperand = getBinaryOperationResult();
      return leftOperand;
    } else if (operation.getType() == OperationType.UNARY) {
      if (firstOperand == null) {
        if (calculatorState == CalculatorState.AFTER || calculatorState == CalculatorState.LEFT) {
          if (leftOperand == null) {
            leftOperand = BigDecimal.ZERO;
          }
          firstOperand = leftOperand;
        } else {
          firstOperand = rightOperand;
        }
      }
      if (calculatorState == CalculatorState.AFTER || calculatorState == CalculatorState.LEFT) {
        leftOperand = getUnaryOperationResult(operation, firstOperand);
        /*if (operation != NEGATE) {
          calculatorState = CalculatorState.TRANSIENT;
        }*/
        return leftOperand;
      } else if (calculatorState == CalculatorState.RIGHT) {
        rightOperand = getUnaryOperationResult(operation, firstOperand);
        return rightOperand;
      } else if (calculatorState == CalculatorState.TRANSIENT) {
        if (firstOperand == null) {
          firstOperand = leftOperand;
        }
        rightOperand = getUnaryOperationResult(operation, firstOperand);
        return rightOperand;
      }
    } else if (operation.getType() == OperationType.PERCENT) {
      leftOperand = firstOperand;
      rightOperand = secondOperand;
      rightOperand = getPercentOperation(operation);
      return rightOperand;
    }
    return null;
  }

  /**
   * Calculate operations, if one of operand is null(already written at field,
   * will be written at future or operation is unary)
   *
   * @param operation    Operation, that we do
   * @param firstOperand first operand of calculation
   * @return result of calculation, that written at left or right operand field or firstOperand
   */
  public BigDecimal doCalculate(Operation operation, BigDecimal firstOperand) throws CalculationException {
    if (operation == null) {
      if (leftOperand != null && this.operation != null) {
        if (calculatorState == CalculatorState.TRANSIENT && this.operation == DIVIDE && rightOperand != null && !firstOperand.equals(rightOperand)) {
          rightOperand = leftOperand;
        } else if (calculatorState != CalculatorState.AFTER) {
          rightOperand = firstOperand;
        }

        if (this.operation.getType() == OperationType.BINARY) {
          leftOperand = getBinaryOperationResult();
          calculatorState = CalculatorState.AFTER;
          return leftOperand;
        } else if (this.operation.getType() == OperationType.PERCENT) {
          rightOperand = firstOperand;
          rightOperand = getPercentOperation(this.operation);
          this.operation = prevOperation;
          return rightOperand;
        }

        return leftOperand;
      } else if (leftOperand == null) {
        leftOperand = firstOperand;
        calculatorState = CalculatorState.AFTER;
        return leftOperand;
      } else if (rightOperand == null) {
        rightOperand = firstOperand;
        calculatorState = CalculatorState.RIGHT;
        return rightOperand;
      } else {
        calculatorState = CalculatorState.AFTER;
        return firstOperand;
      }
    } else if (operation.getType() == OperationType.BINARY) {
      if (calculatorState == CalculatorState.LEFT) {
        leftOperand = firstOperand;
        calculatorState = CalculatorState.TRANSIENT;
        this.operation = operation;

        if (rightOperand == null) {
          rightOperand = leftOperand;
        }

        return leftOperand;
      } else if (calculatorState == CalculatorState.RIGHT) {
        rightOperand = firstOperand;
        leftOperand = doCalculate(this.operation, leftOperand, firstOperand);
        this.operation = operation;
        calculatorState = CalculatorState.TRANSIENT;
        return leftOperand;
      } else if (calculatorState == CalculatorState.AFTER) {
        calculatorState = CalculatorState.TRANSIENT;

        if (leftOperand == null) {
          leftOperand = firstOperand;
        }

        this.operation = operation;

        return firstOperand;
      } else if (calculatorState == CalculatorState.TRANSIENT) {
        this.operation = operation;

        return firstOperand;
      }
    } else if (operation.getType() == OperationType.UNARY) {
      if (firstOperand == null) {
        return doCalculate(operation, null, null);
      }
      return doCalculate(operation, firstOperand, null);
    } else if (operation.getType() == OperationType.PERCENT) {
      if (firstOperand == null) {
        prevOperation = this.operation;
        this.operation = operation;

        return leftOperand;
      }
      if (leftOperand != null) {
        rightOperand = firstOperand;
        if (calculatorState == CalculatorState.TRANSIENT) {
          calculatorState = CalculatorState.RIGHT;
        } /*else if (calculatorState == CalculatorState.LEFT) {
          calculatorState = CalculatorState.TRANSIENT;
          leftOperand = rightOperand;
        }*/

        return doCalculate(operation, leftOperand, rightOperand);
      } else {
        return BigDecimal.ZERO;
      }
    } else if (operation.getType() == OperationType.MEMORY) {
      if (firstOperand == null) {
        if (calculatorState == CalculatorState.RIGHT) {
          firstOperand = rightOperand;
        } else {
          firstOperand = leftOperand;
        }
      }

      if (operation == MEMORY_ADD) {
        memoryAdd(firstOperand);
      } else if (operation == MEMORY_SUB) {
        memorySub(firstOperand);
      } else if (operation == MEMORY_CLEAR) {
        clearMemory();
      } else {
        memory = firstOperand;
      }
      return memory;
    }
    return null;
  }

  /**
   * Calculate if we already make operation and we want to add new operand and get result
   *
   * @param firstOperand operand that we add to calculation
   * @return result of calculation
   */
  public BigDecimal doCalculate(BigDecimal firstOperand) throws CalculationException {
    return doCalculate(null, firstOperand);
  }

  /**
   * Use if want to set operation and calculate if we need it because of current {@link CalculatorState}
   *
   * @param operation {@link Operation} that we set
   * @return result of calculation
   */
  public BigDecimal doCalculate(Operation operation) throws CalculationException {
    return doCalculate(operation, null);
  }
}