package com.implemica.calculator.model;

import com.implemica.calculator.model.util.CalcState;
import com.implemica.calculator.model.util.Errors;
import com.implemica.calculator.model.util.Operation;
import com.implemica.calculator.model.util.OperationType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

import static com.implemica.calculator.model.util.Operation.*;

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

  private Operation prevOperation;

  /**
   * Current state of calculator
   */
  private CalcState calcState = CalcState.LEFT;

  /**
   * Setting of precision for outer methods
   */
  public static final MathContext mc16 = new MathContext(16);

  /**
   * Maximum E degree
   */
  private static final int DIVIDE_SCALE = 10000;

  /**
   * Map of binary operations
   */
  private final static Map<Operation, BinaryOperator<BigDecimal>> binaryOperations = new HashMap<>();

  /**
   * Map of unary operations
   */
  private final static Map<Operation, UnaryOperator<BigDecimal>> unaryOperations = new HashMap<>();

  private static final BigDecimal MAX_DECIMAL = new BigDecimal("1E10000");

  private static final BigDecimal MIN_DECIMAL = new BigDecimal("-1E10000");

  private static final BigDecimal MIN_POSITIVE = new BigDecimal("1E-10000");

  private static final BigDecimal MIN_NEGATIVE = new BigDecimal("-1E-10000");

  private static final int MAX_SCALE = 9999;

  private static final MathContext SQRT_CONTEXT = new MathContext(10000);

  static {
    binaryOperations.put(ADD, BigDecimal::add);
    binaryOperations.put(SUBTRACT, BigDecimal::subtract);
    binaryOperations.put(MULTIPLY, BigDecimal::multiply);
    binaryOperations.put(DIVIDE, (left, right) -> left.divide(right, DIVIDE_SCALE, BigDecimal.ROUND_HALF_UP));

    unaryOperations.put(REVERSE, x -> BigDecimal.ONE.divide(x, DIVIDE_SCALE, BigDecimal.ROUND_HALF_UP));//⅟𝑥
    unaryOperations.put(POW, x -> x.pow(2));//𝑥²
    unaryOperations.put(NEGATE, BigDecimal::negate);//±
    unaryOperations.put(SQRT, x -> x.sqrt(SQRT_CONTEXT));
  }


  /**
   * Make calculations for binary operations
   *
   * @return string with result of calculation
   */
  private BigDecimal getBinaryOperationResult() throws ArithmeticException {
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
  private BigDecimal getUnaryOperationResult(Operation op, BigDecimal number) throws ArithmeticException {
    if (number.equals(BigDecimal.ZERO) && op == REVERSE) {
      throw new ArithmeticException(Errors.CANNOT_DIVIDE_BY_ZERO.getMsg());
    } else if (number.compareTo(BigDecimal.ZERO) < 0 && op == SQRT) {
      throw new ArithmeticException(Errors.INVALID_INPUT.getMsg());
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
  private BigDecimal getPercentOperation(Operation operation) throws ArithmeticException {
    BigDecimal res = BigDecimal.ZERO;

    if (operation != null) {
      if ((operation == PERCENT_ADD_SUBTRACT) && leftOperand.compareTo(BigDecimal.ZERO) != 0) {
        res = leftOperand.multiply(rightOperand.divide(BigDecimal.valueOf(100), DIVIDE_SCALE, BigDecimal.ROUND_HALF_UP));
      } else if (operation == PERCENT_MUL_DIVIDE) {
        res = rightOperand.divide(BigDecimal.valueOf(100), DIVIDE_SCALE, BigDecimal.ROUND_HALF_UP);
      }
    }


    rightOperand = res;
    checkOverflow(res);
    return res.stripTrailingZeros();
  }

  public BigDecimal getMemory() {
    return memory;
  }

  public BigDecimal recallMemory() {
    if (calcState == CalcState.LEFT) {
      leftOperand = memory;
    } else if (calcState == CalcState.RIGHT) {
      rightOperand = memory;
    }
    return getMemory().stripTrailingZeros();
  }

  public void memorySave(BigDecimal num) {
    if (calcState == CalcState.TRANSIENT) {
      memory = leftOperand;
    } else if (calcState == CalcState.AFTER) {
      if (leftOperand == null) {
        memory = BigDecimal.ZERO;
      } else {
        memory = leftOperand;
      }
    } else if (calcState == CalcState.RIGHT || calcState == CalcState.LEFT) {
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
  public void memoryAdd(BigDecimal num) {
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
  public void memorySub(BigDecimal num) {
    if (memory != null) {
      checkOverflow(num);
      memory = memory.subtract(num);
    } else {
      num = num.negate();
      memory = num;
    }
  }

  public static void checkOverflow(BigDecimal res) throws ArithmeticException {
    if (res.compareTo(MAX_DECIMAL) >= 0 || res.compareTo(MIN_DECIMAL) <= 0) {
      throw new ArithmeticException(Errors.OVERFLOW.getMsg());
    }

    if (checkMinNumbers(res)) {
      throw new ArithmeticException(Errors.OVERFLOW.getMsg());
    }
  }

  private static boolean checkMinNumbers(BigDecimal num) {
    return (num.compareTo(MIN_POSITIVE) <= 0 && num.compareTo(BigDecimal.ZERO) > 0) || (num.compareTo(MIN_NEGATIVE) >= 0 && num.compareTo(BigDecimal.ZERO) < 0);
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
  public BigDecimal doCalculate(Operation operation, BigDecimal firstOperand, BigDecimal secondOperand) throws ArithmeticException {
    if (operation.getType() == OperationType.BINARY) {
      leftOperand = firstOperand;
      rightOperand = secondOperand;
      this.operation = operation;
      calcState = CalcState.AFTER;
      leftOperand = getBinaryOperationResult();
      return leftOperand;
    } else if (operation.getType() == OperationType.UNARY) {
      if (firstOperand == null) {
        if (calcState == CalcState.AFTER || calcState == CalcState.LEFT) {
          firstOperand = leftOperand;
        } else {
          firstOperand = rightOperand;
        }
      }
      if (calcState == CalcState.AFTER || calcState == CalcState.LEFT) {
        leftOperand = getUnaryOperationResult(operation, firstOperand);
        if (operation != NEGATE) {
          calcState = CalcState.TRANSIENT;
        }
        return leftOperand;
      } else if (calcState == CalcState.RIGHT) {
        rightOperand = getUnaryOperationResult(operation, firstOperand);
        return rightOperand;
      } else if (calcState == CalcState.TRANSIENT) {
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
  public BigDecimal doCalculate(Operation operation, BigDecimal firstOperand) {
    if (operation == null) {
      if (leftOperand != null && this.operation != null) {
        if (calcState == CalcState.TRANSIENT && this.operation == DIVIDE && rightOperand != null && !firstOperand.equals(rightOperand)) {
          rightOperand = leftOperand;
        } else if (calcState != CalcState.AFTER) {
          rightOperand = firstOperand;
        }

        if (this.operation.getType() == OperationType.BINARY) {
          leftOperand = getBinaryOperationResult();
          calcState = CalcState.AFTER;
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
        calcState = CalcState.AFTER;
        return leftOperand;
      } else if (rightOperand == null) {
        rightOperand = firstOperand;
        calcState = CalcState.RIGHT;
        return rightOperand;
      } else {
        calcState = CalcState.AFTER;
        return firstOperand;
      }
    } else if (operation.getType() == OperationType.BINARY) {
      if (calcState == CalcState.LEFT) {
        leftOperand = firstOperand;
        calcState = CalcState.TRANSIENT;
        this.operation = operation;

        if (rightOperand == null) {
          rightOperand = leftOperand;
        }

        return leftOperand;
      } else if (calcState == CalcState.RIGHT) {
        rightOperand = firstOperand;
        leftOperand = doCalculate(this.operation, leftOperand, firstOperand);
        this.operation = operation;
        calcState = CalcState.TRANSIENT;
        return leftOperand;
      } else if (calcState == CalcState.AFTER) {
        calcState = CalcState.TRANSIENT;

        if (leftOperand == null) {
          leftOperand = firstOperand;
        }

        this.operation = operation;

        return firstOperand;
      } else if (calcState == CalcState.TRANSIENT) {
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
        if (calcState == CalcState.TRANSIENT) {
          calcState = CalcState.RIGHT;
        } else if (calcState == CalcState.LEFT) {
          calcState = CalcState.TRANSIENT;
          leftOperand = rightOperand;
        } else if (calcState == CalcState.RIGHT) {
          calcState = CalcState.AFTER;
        }

        return doCalculate(operation, leftOperand, rightOperand);
      } else {
        return BigDecimal.ZERO;
      }
    } else if (operation.getType() == OperationType.MEMORY) {
      if (firstOperand == null) {
        if (calcState == CalcState.RIGHT) {
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
        setMemory(firstOperand);
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
  public BigDecimal doCalculate(BigDecimal firstOperand) {
    return doCalculate(null, firstOperand);
  }

  public BigDecimal doCalculate(Operation operation) {
    return doCalculate(operation, null);
  }
}