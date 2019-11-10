package com.implemica.demo;

import com.implemica.calculator.model.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class MainModelDemo {
  private static final Map<CalculatorExceptionType, String> exceptionMessages = new HashMap<>();

  static {
    exceptionMessages.put(CalculatorExceptionType.CANNOT_DIVIDE_BY_ZERO, "Cannot divide by zero");
    exceptionMessages.put(CalculatorExceptionType.DIVIDING_ZERO_BY_ZERO, "Result is undefined");
    exceptionMessages.put(CalculatorExceptionType.NEGATIVE_ROOT, "Invalid input");
    exceptionMessages.put(CalculatorExceptionType.OVERFLOW, "Overflow");
  }

  public static void main(String[] args) {
    CalculatorModel calc = new CalculatorModel();
    BigDecimal a = BigDecimal.valueOf(7);
    BigDecimal b = BigDecimal.valueOf(3);
    BigDecimal c = BigDecimal.valueOf(2);
    BigDecimal d = BigDecimal.ONE;
    BigDecimal e = BigDecimal.valueOf(100);

    BigDecimal x;
    try {
      x = calc.calculate(ArithmeticOperation.ADD, a, b);
      x = calc.calculate(ArithmeticOperation.DIVIDE, x, c);
      x = calc.calculate(ArithmeticOperation.SUBTRACT, x, d);
      x = calc.calculate(ArithmeticOperation.SQRT, x);
      x = calc.calculate(ArithmeticOperation.ADD, x, e);
    } catch (CalculatorException ex) {
      String message;
      message = exceptionMessages.getOrDefault(ex.getType(), "Unexpected");
      System.err.println("Answer: " + message);
      x = null;
    }

    if (x != null) {
      System.out.println("Answer: " + x.toPlainString());
    }

  }
}
