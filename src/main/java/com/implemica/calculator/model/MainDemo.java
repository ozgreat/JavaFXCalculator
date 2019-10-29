package com.implemica.calculator.model;

import com.implemica.calculator.model.CalculatorModel;
import com.implemica.calculator.model.util.Operation;

import java.math.BigDecimal;

public class MainDemo {
  public static void main(String[] args) {
    CalculatorModel calc = new CalculatorModel();

    BigDecimal x = calc.doCalculate(Operation.ADD, BigDecimal.valueOf(7), BigDecimal.valueOf(3));
    x = calc.doCalculate(Operation.DIVIDE, x, BigDecimal.valueOf(2));
    x = calc.doCalculate(Operation.SUBTRACT, x, BigDecimal.ONE);
    x = calc.doCalculate(Operation.SQRT, x);
    System.out.println("Answer: " + calc.doCalculate(Operation.ADD, x, BigDecimal.valueOf(100)));
  }
}
