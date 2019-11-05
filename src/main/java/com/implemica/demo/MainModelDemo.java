package com.implemica.demo;

import com.implemica.calculator.model.*;

import java.math.BigDecimal;

public class MainModelDemo {
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
    } catch (CannotDivideByZeroException | DivideZeroByZeroException | NegativeRootException | OverflowException ex) {
      System.err.println("Answer: " + ex.getMessage());
      x = null;
    }

    if (x != null) {
      System.out.println("Answer: " + x.toPlainString());
    }

  }
}
