package com.implemica.calculator.controller.util;

import com.implemica.calculator.model.CalculatorModel;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;

/**
 * Util class, that format {@link BigDecimal} using with some custom fixes
 * to {@link String}, that will be showing at calculators display. Also parse them back to BigDecimal
 *
 * @author ozgreat
 * @see DecimalFormat
 * @see BigDecimal
 * @see String
 */
public class NumberFormatter {
  /**
   * Exponent separator for cases when number with scientific annotation is bigger
   * than one, cause {@link DecimalFormat} can add "-" to number if number less than one,
   * but can't add "+" in different case
   *
   * @see DecimalFormat
   */
  private static String POSITIVE_EXPONENT_SEP = "E+";

  /**
   * Default exponent separator
   */
  private static String DEFAULT_EXPONENT_SEP = "E";

  /**
   * Separator, that separate integer and decimal part of number
   */
  private static char DECIMAL_SEP = '.';

  /**
   * Separator, that separate group of number in integer part of number
   */
  private static char GROUP_SEP = ',';

  /**
   * {@link DecimalFormat} object, that we use to format and parse numbers
   *
   * @see DecimalFormat
   */
  private static DecimalFormat formatter = new DecimalFormat();

  /**
   * Symbols of decimal formatter: separators etc.
   *
   * @see DecimalFormatSymbols
   */
  private static DecimalFormatSymbols symbols = new DecimalFormatSymbols();

  /**
   * If number is less than that number and have with too much scale will be formatted like number with scientific
   * annotation
   */
  private static final BigDecimal MIN_PLAIN = BigDecimal.valueOf(0.001);

  /**
   * Amount of maximum symbol on display in common case
   */
  private static final int MAX_SYMBOLS = 16;

  /**
   * Pattern of decimal part in common case
   */
  private static final String FIFTEEN_DIEZ = "###############";

  /**
   * Pattern of Integer part in common case
   */
  private static final String GROUP_PATTERN = "###" + GROUP_SEP + "###" + DECIMAL_SEP;

  static {
    symbols.setGroupingSeparator(GROUP_SEP);
    symbols.setDecimalSeparator(DECIMAL_SEP);
    formatter.setDecimalFormatSymbols(symbols);
    formatter.setParseBigDecimal(true);
  }


  /**
   * Format {@link BigDecimal} to {@link String}
   *
   * @param number Number that we have to format
   * @return Result of formatting
   *
   * @see BigDecimal
   * @see DecimalFormat
   * @see DecimalFormatSymbols
   */
  public static String format(BigDecimal number) {
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
        if (intPartSize < 0) {
          intPartSize = 0;
        }
        pattern = GROUP_PATTERN + "#".repeat(MAX_SYMBOLS - intPartSize);
      }
    }

    formatter.applyPattern(pattern);
    formatter.setGroupingUsed(true);

    String res = formatter.format(numberInWork);

    if (res.contains(DEFAULT_EXPONENT_SEP) && !res.contains(String.valueOf(DECIMAL_SEP))) {
      res = res.replace(DEFAULT_EXPONENT_SEP, DECIMAL_SEP + DEFAULT_EXPONENT_SEP);
    }

    if (res.endsWith(String.valueOf(DECIMAL_SEP))) {
      res = res.substring(0, res.length() - 1);
    }

    int intPartSize = numberInWork.precision() - numberInWork.scale();
    if (trailingZerosAmount != 0) {
      if (trailingZerosAmount > (MAX_SYMBOLS - intPartSize)) {
        trailingZerosAmount = MAX_SYMBOLS - intPartSize;
      }

      if (!res.contains(String.valueOf(DECIMAL_SEP))) {
        res += DECIMAL_SEP;
      }

      if (trailingZerosAmount <= (MAX_SYMBOLS - intPartSize)) {
        res += "0".repeat(trailingZerosAmount);
      }
    }

    return res;
  }

  /**
   * Setting up exponent separator according to the status of his power
   *
   * @param isPositive status of power of number
   */
  private static void setExponentSep(boolean isPositive) {
    String EXPONENT_SEP;
    if (isPositive) {
      EXPONENT_SEP = POSITIVE_EXPONENT_SEP;
    } else {
      EXPONENT_SEP = DEFAULT_EXPONENT_SEP;
    }
    symbols.setExponentSeparator(EXPONENT_SEP);
    formatter.setDecimalFormatSymbols(symbols);
  }

  /**
   * Get {@link BigDecimal} from {@link String} using {@link DecimalFormat}
   *
   * @param str number that we have to parse in {@link BigDecimal}
   * @return {@link BigDecimal} object that parsed from str
   * @throws ParseException if pattern in {@link DecimalFormat} object is wrong
   *
   * @see DecimalFormat
   * @see ParseException
   */
  public static BigDecimal parse(String str) throws ParseException {
    setExponentSep(str.contains(POSITIVE_EXPONENT_SEP));

    return (BigDecimal) formatter.parse(str);
  }

  /**
   * Remove group separator from number in {@link String}
   *
   * @param number number in string
   * @return number without group separator
   */
  public static String removeGroupSeparator(String number) {
    return number.replace(String.valueOf(GROUP_SEP), "");
  }

  /**
   * Returns true if number's length is less that MAX_SYMBOLS plus coefficient, that depends on first number
   *
   * @param number number in work
   * @return status of them length
   */
  public static boolean isTooBigToInput(String number) {
    number = removeGroupSeparator(number);
    boolean isFirstZero = number.startsWith("0") || number.startsWith("-0");
    int coef = isFirstZero ? 1 : 0;

    return MAX_SYMBOLS + coef < number.replace("-", "").replace(String.valueOf(DECIMAL_SEP), "").length();
  }
}
