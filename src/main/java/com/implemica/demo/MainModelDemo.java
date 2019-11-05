package com.implemica.demo;

import com.implemica.calculator.model.CalculationException;
import com.implemica.calculator.model.CalculatorModel;
import com.implemica.calculator.model.Operation;

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
      x = calc.doCalculate(Operation.ADD, a, b);
      x = calc.doCalculate(Operation.DIVIDE, x, c);
      x = calc.doCalculate(Operation.SUBTRACT, x, d);
      x = calc.doCalculate(Operation.SQRT, x);
      x = calc.doCalculate(Operation.ADD, x, e);
    } catch (CalculationException ex) {
      System.err.println("Answer: " + ex.getMessage());
      x = null;
    }

    if (x != null) {
      System.out.println("Answer: " + x.toPlainString());
    }

  }
}
