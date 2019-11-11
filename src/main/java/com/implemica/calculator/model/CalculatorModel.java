package com.implemica.calculator.model;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

import static com.implemica.calculator.model.ArithmeticOperation.*;

/**
 * Model of calculator, calculate {@link BigDecimal} with operations like sqrt, divide, multiply, etc.
 *
 * @author ozgreat
 * @see BigDecimal
 * @see ArithmeticOperation
 * @see CalculatorState
 */
public class CalculatorModel {

  /**
   * Maximum scale of number
   *
   * @see BigDecimal
   */
  private static final int DIVIDE_SCALE = 10000;

  /**
   * {@link Map} of binary operations
   */
  private final static Map<ArithmeticOperation, BinaryOperator<BigDecimal>> binaryOperations = new HashMap<>();

  /**
   * {@link Map} of unary operations
   */
  private final static Map<ArithmeticOperation, UnaryOperator<BigDecimal>> unaryOperations = new HashMap<>();

  /**
   * First impossible {@link BigDecimal} value, that bigger than one
   */
  private static final BigDecimal MAX_DECIMAL = new BigDecimal("1E10000");

  /**
   * First impossible {@link BigDecimal} value, that less that minus one
   */
  private static final BigDecimal MIN_DECIMAL = new BigDecimal("-1E10000");

  /**
   * Minimum possible positive {@link BigDecimal} value in calculator
   */
  private static final BigDecimal MIN_POSITIVE = new BigDecimal("1E-9999");

  /**
   * {@link MathContext} for sqrt operation
   */
  private static final MathContext SQRT_CONTEXT = new MathContext(10000);


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
   * {@link ArithmeticOperation}, that user will be use
   */
  private ArithmeticOperation operation;

  /**
   * {@link ArithmeticOperation}, that user before current
   */
  private ArithmeticOperation prevOperation;

  /**
   * Current state of calculator
   */
  private CalculatorState calculatorState = CalculatorState.LEFT;

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


  public void setLeftOperand(BigDecimal leftOperand) {
    this.leftOperand = leftOperand;
  }


  public void setRightOperand(BigDecimal rightOperand) {
    this.rightOperand = rightOperand;
  }

  public BigDecimal getMemory() {
    return memory;
  }

  public ArithmeticOperation getOperation() {
    return operation;
  }

  public void setOperation(ArithmeticOperation operation) {
    this.operation = operation;
  }

  public CalculatorState getCalculatorState() {
    return calculatorState;
  }

  public void setCalculatorState(CalculatorState calculatorState) {
    this.calculatorState = calculatorState;
  }


  /**
   * Make calculations for binary operations
   *
   * @return string with result of calculation
   */
  private BigDecimal getBinaryOperationResult() throws CalculatorException {
    if (rightOperand.compareTo(BigDecimal.ZERO) == 0 && operation == DIVIDE) {
      if (leftOperand.compareTo(BigDecimal.ZERO) == 0) {
        throw new CalculatorException(CalculatorExceptionType.DIVIDING_ZERO_BY_ZERO);
      }
      throw new CalculatorException(CalculatorExceptionType.CANNOT_DIVIDE_BY_ZERO);
    }

    if (leftOperand.compareTo(BigDecimal.ZERO) == 0 && operation == DIVIDE) {
      return BigDecimal.ZERO;
    }

    BigDecimal res = binaryOperations.get(operation).apply(leftOperand, rightOperand);

    if (res.compareTo(leftOperand) == 0 && operation == DIVIDE && rightOperand.compareTo(BigDecimal.ONE) != 0
        && leftOperand.compareTo(MIN_POSITIVE) == 0) {
      throw new CalculatorException(CalculatorExceptionType.OVERFLOW);
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
  private BigDecimal getUnaryOperationResult(ArithmeticOperation op, BigDecimal number) throws CalculatorException {
    if (number.equals(BigDecimal.ZERO) && op == REVERSE) {
      throw new CalculatorException(CalculatorExceptionType.CANNOT_DIVIDE_BY_ZERO);
    }

    if (number.compareTo(BigDecimal.ZERO) < 0 && op == SQRT) {
      throw new CalculatorException(CalculatorExceptionType.NEGATIVE_ROOT);
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
  private BigDecimal getPercentOperation(ArithmeticOperation operation) throws CalculatorException {
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
  public void memoryAdd(BigDecimal num) throws CalculatorException {
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
  public void memorySub(BigDecimal num) throws CalculatorException {
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
  public static void checkOverflow(BigDecimal res) throws CalculatorException {
    if (res.compareTo(MAX_DECIMAL) >= 0 || res.compareTo(MIN_DECIMAL) <= 0) {
      throw new CalculatorException(CalculatorExceptionType.OVERFLOW);
    }

    if (checkMinNumbers(res)) {
      throw new CalculatorException(CalculatorExceptionType.OVERFLOW);
    }
  }

  private static boolean checkMinNumbers(BigDecimal num) {
    return (num.abs().compareTo(MIN_POSITIVE) < 0 && num.abs().compareTo(BigDecimal.ZERO) > 0);
  }

  /**
   * Calculate operations
   *
   * @param operation     ArithmeticOperation, that we do
   * @param firstOperand  first operand of calculation
   * @param secondOperand second operand of calculation
   * @return result of calculation, that written at left or right operand field
   */
  public BigDecimal calculate(ArithmeticOperation operation, BigDecimal firstOperand, BigDecimal secondOperand)
      throws CalculatorException {
    BigDecimal result;
    if (operation.getType() == ArithmeticOperationType.BINARY) {
      leftOperand = firstOperand;
      rightOperand = secondOperand;
      this.operation = operation;
      calculatorState = CalculatorState.AFTER;
      leftOperand = getBinaryOperationResult();
      result = leftOperand;
    } else if (operation.getType() == ArithmeticOperationType.UNARY) {
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

        result = leftOperand;
      } else if (calculatorState == CalculatorState.RIGHT) {
        rightOperand = getUnaryOperationResult(operation, firstOperand);
        result = rightOperand;
      } else {
        if (firstOperand == null) {
          firstOperand = leftOperand;
        }
        rightOperand = getUnaryOperationResult(operation, firstOperand);
        result = rightOperand;
      }
    } else if (operation.getType() == ArithmeticOperationType.PERCENT) {
      leftOperand = firstOperand;
      rightOperand = secondOperand;
      rightOperand = getPercentOperation(operation);
      result = rightOperand;
    } else {
      throw new IllegalStateException();
    }
    return result;
  }

  /**
   * Calculate operations, if one of operand is null(already written at field,
   * will be written at future or operation is unary)
   *
   * @param operation    ArithmeticOperation, that we do
   * @param firstOperand first operand of calculation
   * @return result of calculation, that written at left or right operand field or firstOperand
   */
  public BigDecimal calculate(ArithmeticOperation operation, BigDecimal firstOperand) throws CalculatorException {
    BigDecimal result;
    if (operation == null) {
      if (leftOperand != null && this.operation != null) {
        if (calculatorState == CalculatorState.TRANSIENT && this.operation == DIVIDE && rightOperand != null && !firstOperand.equals(rightOperand)) {
          rightOperand = leftOperand;
        } else if (calculatorState != CalculatorState.AFTER) {
          rightOperand = firstOperand;
        }

        if (this.operation.getType() == ArithmeticOperationType.BINARY) {
          leftOperand = getBinaryOperationResult();
          calculatorState = CalculatorState.AFTER;
          result = leftOperand;
        } else if (this.operation.getType() == ArithmeticOperationType.PERCENT) {
          rightOperand = firstOperand;
          rightOperand = getPercentOperation(this.operation);
          this.operation = prevOperation;
          return rightOperand;
        } else {
          result = leftOperand;
        }
      } else if (leftOperand == null) {
        leftOperand = firstOperand;
        calculatorState = CalculatorState.AFTER;
        result = firstOperand;
      } else if (rightOperand == null) {
        rightOperand = firstOperand;
        calculatorState = CalculatorState.RIGHT;
        result = rightOperand;
      } else {
        calculatorState = CalculatorState.AFTER;
        result = firstOperand;
      }
    } else if (operation.getType() == ArithmeticOperationType.BINARY) {
      if (calculatorState == CalculatorState.LEFT) {
        leftOperand = firstOperand;
        calculatorState = CalculatorState.TRANSIENT;
        this.operation = operation;

        if (rightOperand == null) {
          rightOperand = leftOperand;
        }

        result = leftOperand;
      } else if (calculatorState == CalculatorState.RIGHT) {
        rightOperand = firstOperand;
        leftOperand = calculate(this.operation, leftOperand, firstOperand);
        this.operation = operation;
        calculatorState = CalculatorState.TRANSIENT;
        result = leftOperand;
      } else if (calculatorState == CalculatorState.AFTER) {
        calculatorState = CalculatorState.TRANSIENT;

        if (leftOperand == null) {
          leftOperand = firstOperand;
        }

        this.operation = operation;
        result = leftOperand;
      } else if (calculatorState == CalculatorState.TRANSIENT) {
        this.operation = operation;
        result = firstOperand;
      } else {
        throw new IllegalStateException();
      }
    } else if (operation.getType() == ArithmeticOperationType.UNARY) {
      result = calculate(operation, firstOperand, null);
    } else if (operation.getType() == ArithmeticOperationType.PERCENT) {
      if (firstOperand == null) {
        prevOperation = this.operation;
        this.operation = operation;
        return leftOperand;
      }
      if (leftOperand != null) {
        rightOperand = firstOperand;
        if (calculatorState == CalculatorState.TRANSIENT) {
          calculatorState = CalculatorState.RIGHT;
        }

        result = calculate(operation, leftOperand, rightOperand);
      } else {
        result = BigDecimal.ZERO;
      }
    } else {
      throw new IllegalStateException();
    }
    return result;
  }

  /**
   * Calculate if we already make operation and we want to add new operand and get result
   *
   * @param firstOperand operand that we add to calculation
   * @return result of calculation
   */
  public BigDecimal calculate(BigDecimal firstOperand) throws CalculatorException {
    return calculate(null, firstOperand);
  }

  /**
   * Use if want to set operation and calculate if we need it because of current {@link CalculatorState}
   *
   * @param operation {@link ArithmeticOperation} that we set
   * @return result of calculation
   */
  public BigDecimal calculate(ArithmeticOperation operation) throws CalculatorException {
    return calculate(operation, null);
  }

  /**
   * Calculating of memory operations
   *
   * @param operation    {@link MemoryOperation} that have to be calculated
   * @param firstOperand {@link BigDecimal} which we have to perform the operation
   * @return result of calculation
   */
  public BigDecimal calculateMemory(MemoryOperation operation, BigDecimal firstOperand) throws CalculatorException {
    if (firstOperand == null) {
      if (calculatorState == CalculatorState.RIGHT) {
        firstOperand = rightOperand;
      } else {
        firstOperand = leftOperand;
      }
    }

    if (operation == MemoryOperation.MEMORY_ADD) {
      memoryAdd(firstOperand);
    } else if (operation == MemoryOperation.MEMORY_SUB) {
      memorySub(firstOperand);
    } else if (operation == MemoryOperation.MEMORY_CLEAR) {
      clearMemory();
    } else {
      memory = firstOperand;
    }
    return memory;
  }

  /**
   * {@code calculateMemory} method, when operand is null, because we don't need them(MC or MR) or we set that earlier
   *
   * @param operation {@link MemoryOperation} that have to be calculated
   * @return result of calculation
   */
  public BigDecimal calculateMemory(MemoryOperation operation) throws CalculatorException {
    return calculateMemory(operation, null);
  }
}