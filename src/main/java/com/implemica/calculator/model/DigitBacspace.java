package com.implemica.calculator.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DigitBacspace {
  public static BigDecimal deleteLastDigit(BigDecimal number) {
    if (number.precision() == 1 && number.scale() == 1) {
      return BigDecimal.ZERO;
    }

    BigDecimal res;
    int scale;

    if (number.scale() == 0) {
      res = number.divide(BigDecimal.TEN, RoundingMode.DOWN);
      scale = 0;
    } else {
      res = number;
      scale = res.scale() - 1;
    }

    return res.setScale(scale, RoundingMode.DOWN);
  }
}
