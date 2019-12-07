package com.ozgreat.calculator.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Util class that, gives you the opportunity to delete parts of number
 */
public class DigitBackspace {
  /**
   * Delete last digit in number
   *
   * @param number number, where we should delete last symbol
   * @return number without last symbol
   */
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
