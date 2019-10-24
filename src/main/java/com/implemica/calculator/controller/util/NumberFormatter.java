package com.implemica.calculator.controller.util;

import com.implemica.calculator.model.CalculatorModel;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;

public class NumberFormatter {

  private static String EXPONENT_SEP;
  private static String POSITIVE_EXPONENT_SEP = "E+";
  private static String DEFAULT_EXPONENT_SEP = "E";
  private static char DECIMAL_SEP = '.';
  private static char GROUP_SEP = ',';

  private static DecimalFormat formatter = new DecimalFormat();
  private static DecimalFormatSymbols symbols = new DecimalFormatSymbols();

  private static final BigDecimal MIN_PLAIN = BigDecimal.valueOf(0.001);

  private static final int MAX_SYMBOLS = 16;

  private static final String FIFTEEN_DIEZ = "###############";
  private static final String GROUP_PATTERN = "###" + GROUP_SEP + "###" + DECIMAL_SEP;

  static {
    symbols.setGroupingSeparator(GROUP_SEP);
    symbols.setDecimalSeparator(DECIMAL_SEP);
    formatter.setDecimalFormatSymbols(symbols);
    formatter.setParseBigDecimal(true);
  }

  public static String format(BigDecimal number) {
    return format(number, true);
  }


  public static String format(BigDecimal number, boolean isGroup) {
    BigDecimal numberInWork;

    if (number.scale() == 0) {
      numberInWork = number;
    } else {
      numberInWork = number.stripTrailingZeros();
    }

    int trailingZerosAmount = number.scale() - numberInWork.scale();

    setExponentSep(numberInWork.abs().compareTo(BigDecimal.ONE) >= 0);

    String pattern;

    if (numberInWork.abs().compareTo(MIN_PLAIN) < 0 && numberInWork.scale() > MAX_SYMBOLS) {
      pattern = "0" + DECIMAL_SEP + FIFTEEN_DIEZ + DEFAULT_EXPONENT_SEP + "0";
    } else {
      int scale = numberInWork.scale();
      int precision = numberInWork.precision();
      int intPartSize = precision - scale;

      numberInWork = numberInWork.setScale(MAX_SYMBOLS - intPartSize, BigDecimal.ROUND_HALF_UP);
      CalculatorModel.checkOverflow(numberInWork);

      scale = numberInWork.scale();
      precision = numberInWork.precision();
      intPartSize = precision - scale;

      if (intPartSize > MAX_SYMBOLS) {
        pattern = "0" + DECIMAL_SEP;

        if (scale > 0 && scale < MAX_SYMBOLS) {
          pattern += "0".repeat(scale);
        } else {
          pattern += FIFTEEN_DIEZ;
        }

        pattern += DEFAULT_EXPONENT_SEP + "0";
      } else {
        if(intPartSize < 0){
          intPartSize = 0;
        }
        pattern = GROUP_PATTERN + "#".repeat(MAX_SYMBOLS - intPartSize);

        /*StringBuilder patternBuilder = new StringBuilder(pattern);

        for (int i = 0; i < MAX_SYMBOLS - intPartSize; i++) {
          patternBuilder.appendString("#");
        }

        pattern = patternBuilder.toString();*/
      }
    }

    formatter.applyPattern(pattern);
    formatter.setGroupingUsed(isGroup);

    return secondaryFormat(formatter.format(numberInWork), trailingZerosAmount, numberInWork.precision() - numberInWork.scale());
  }

  private static void setExponentSep(boolean isPositive) {
    if (isPositive) {
      EXPONENT_SEP = POSITIVE_EXPONENT_SEP;
    } else {
      EXPONENT_SEP = DEFAULT_EXPONENT_SEP;
    }
    symbols.setExponentSeparator(EXPONENT_SEP);
    formatter.setDecimalFormatSymbols(symbols);
  }

  public static BigDecimal parse(String str) throws ParseException {
    setExponentSep(str.contains(POSITIVE_EXPONENT_SEP));

    return (BigDecimal) formatter.parse(str);
  }

  private static String secondaryFormat(String number, int zeros, int intPartSize) {
    if (number.matches("-?\\d" + DEFAULT_EXPONENT_SEP + "\\+?-?\\d+")) {
      number = number.replace(DEFAULT_EXPONENT_SEP, DECIMAL_SEP + DEFAULT_EXPONENT_SEP);
    }

    if (number.endsWith(String.valueOf(DECIMAL_SEP))) {
      number = number.substring(0, number.length() - 1);
    }

    if (zeros != 0) {
      if(zeros > (MAX_SYMBOLS - intPartSize)){
        zeros = MAX_SYMBOLS - intPartSize;
      }

      if (!number.contains(String.valueOf(DECIMAL_SEP))) {
        number += DECIMAL_SEP;
      }

      if(zeros<=(MAX_SYMBOLS - intPartSize)) {
        number += "0".repeat(zeros);
      }
    }

    return number;
  }

  public static String appendString(String number) {
    return number.replace(String.valueOf(GROUP_SEP), "");
  }


  public static boolean isTooBigToInput(String number) {
    number = appendString(number);
    boolean isFirstZero = number.startsWith("0");
    int coef = isFirstZero ? 1 : 0;

    return MAX_SYMBOLS + coef < number.replace("-", "").replace(String.valueOf(DECIMAL_SEP), "").length();
  }
}
