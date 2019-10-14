package com.implemica.calculator.model.util;

import com.implemica.calculator.model.CalculatorModel;
import org.assertj.core.internal.bytebuddy.dynamic.scaffold.subclass.SubclassDynamicTypeBuilder;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static com.implemica.calculator.model.util.Operation.*;

public class ModelTestUtil {
  protected final static BigDecimal MAX = new BigDecimal("1E10000");
  protected final static BigDecimal MIN = new BigDecimal("1E-10000");
  protected final static BigDecimal MAX_POS = MAX.subtract(MIN);
  protected final static BigDecimal MIN_POS = MIN.add(MIN);
  protected final static BigDecimal MAX_POS_NEGATE = MAX_POS.negate();
  protected final static BigDecimal MIN_POS_NEGATE = MIN_POS.negate();
  protected final static BigDecimal MAX_POS_HALF = MAX_POS.divide(BigDecimal.valueOf(2), CalculatorModel.mc10K);
  protected final static BigDecimal MAX_POS_HALF_NEGATE = MAX_POS_HALF.negate();
  private static final List<String> opList = Arrays.asList("+", "-", "/", "*", "1/x", "POW", "√", "±", "=", "%+", "%*");
  private static final Map<String, Operation> operations = new HashMap<>();

  static {
    operations.put("+", ADD);
    operations.put("-", SUBTRACT);
    operations.put("*", MULTIPLY);
    operations.put("/", DIVIDE);
    operations.put("POW", POW);
    operations.put("±", NEGATE);
    operations.put("√", SQRT);
    operations.put("%+", PERCENT_ADD_SUBTRACT);
    operations.put("%*", PERCENT_MUL_DIVIDE);
    operations.put("1/x", REVERSE);
    operations.put("=", null);
  }

  protected CalculatorModel calc;

  protected BigDecimal clicker(String pattern) {
    List<Operation> operationList = new ArrayList<>();
    List<BigDecimal> operands = new ArrayList<>();
    pattern = patternBuilder(pattern);
    BigDecimal x = BigDecimal.ZERO;
    String[] debug = pattern.split(" ");
    for (String s : pattern.split(" ")) {
      if(s.isBlank()){
        continue;
      }
      if (operations.containsKey(s)) {
        operationList.add(operations.get(s));
        x = calc.doCalculate(operations.get(s));
      } else {
        operands.add(new BigDecimal(s));
        x = calc.doCalculate(new BigDecimal(s));
      }
    }
    System.out.println(x);
    calc = new CalculatorModel();
    return x;
  }

  private String patternBuilder(String pattern) {
    StringBuilder patternBuilder = new StringBuilder(pattern);
    for (int i = pattern.length(); i >= 0; --i) {
      String patternBuf = pattern.substring(0, i);
      opList.stream().filter(patternBuf::endsWith).max(Comparator.comparing(String::length)).
          ifPresent((String x) -> {
            int index = patternBuf.lastIndexOf(x);
            patternBuilder.insert(index, " ");
            patternBuilder.insert(index + x.length() + 1, " ");
          });
    }

    return patternBuilder.toString();
  }

  protected void checkOperations(String pattern, String res){
    BigDecimal x = clicker(pattern);
    assertEquals(0, x.compareTo(new BigDecimal(res)));
  }


  protected void universalCheck(String left, String right, Operation op, String expected) {
    if (op.getType() == OperationType.BINARY) {
      assertEquals(0, calc.doCalculate(op, new BigDecimal(left), new BigDecimal(right)).compareTo(new BigDecimal(expected)));
    } else if (op.getType() == OperationType.UNARY) {
      assertEquals(0, calc.doCalculate(op, new BigDecimal(left)).compareTo(new BigDecimal(expected)));
    } else if (op.getType() == OperationType.PERCENT) {
      if (op == Operation.PERCENT_ADD_SUBTRACT) {
        assertEquals(0, calc.doCalculate(op, new BigDecimal(left), new BigDecimal(right)).compareTo(new BigDecimal(expected)));
      } else if (op == Operation.PERCENT_MUL_DIVIDE) {
        assertEquals(0, calc.doCalculate(op, new BigDecimal(left), new BigDecimal(right)).compareTo(new BigDecimal(expected)));
      }
    } else if (op.getType() == OperationType.MEMORY) {
      if (op == Operation.MEMORY_CLEAR) {
        calc.setMemory(new BigDecimal(left));
        calc.clearMemory();

        assertNull(calc.getMemory());
      } else if (op == Operation.MEMORY_SAVE) {
        calc.setMemory(new BigDecimal(left));

        assertEquals(0, calc.getMemory().compareTo(new BigDecimal(expected)));
      } else if (op == Operation.MEMORY_ADD && left == null) {
        calc.memoryAdd(new BigDecimal(right));

        assertEquals(0, calc.getMemory().compareTo(new BigDecimal(expected)));
      } else if (op == Operation.MEMORY_SUB && left == null) {
        calc.memorySub(new BigDecimal(right));

        assertEquals(0, calc.getMemory().compareTo(new BigDecimal(expected)));
      } else if (op == Operation.MEMORY_ADD) {
        calc.setMemory(new BigDecimal(left));
        calc.memoryAdd(new BigDecimal(right));

        assertEquals(0, calc.getMemory().compareTo(new BigDecimal(expected)));
      } else if (op == Operation.MEMORY_SUB) {
        calc.setMemory(new BigDecimal(left));
        calc.memorySub(new BigDecimal(right));

        assertEquals(0, calc.getMemory().compareTo(new BigDecimal(expected)));
      }
      calc.clearMemory();
    }
  }

  protected void universalThrowCheck(String left, String right, Operation op, Errors error) {
    if (op.getType() == OperationType.BINARY) {
      try {
        calc.doCalculate(op, new BigDecimal(left), new BigDecimal(right));
        fail();
      } catch (ArithmeticException e) {
        assertEquals(error.getMsg(), e.getMessage());
      }
    } else if (op.getType() == OperationType.UNARY) {
      try {
        calc.doCalculate(op, new BigDecimal(left));
      } catch (ArithmeticException e) {
        assertEquals(error.getMsg(), e.getMessage());
      }
    } else if (op.getType() == OperationType.PERCENT) {
      if (op == Operation.PERCENT_ADD_SUBTRACT) {
        try {
          calc.doCalculate(op, new BigDecimal(left), new BigDecimal(right));
          fail();
        } catch (ArithmeticException e) {
          assertEquals(error.getMsg(), e.getMessage());
        }
      } else if (op == Operation.PERCENT_MUL_DIVIDE) {
        try {
          calc.doCalculate(op, new BigDecimal(left), new BigDecimal(right));
          fail();
        } catch (ArithmeticException e) {
          assertEquals(error.getMsg(), e.getMessage());
        }
      }
    }
  }

}
