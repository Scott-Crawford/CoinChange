package coinchange.calculator;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import org.junit.Test;

import coinchange.currency.USCurrency;
import coinchange.exceptions.CustomException;

public class CalculatorTest {

  @Test
  public void calculateGreedy_99() throws Exception {
    Calculator<USCurrency> calculator = new Calculator<>(USCurrency.class);
    HashMap<USCurrency, Integer> map = calculator.calculateChange(1, 100, "greedy");
    assertTrue(map.get(USCurrency.QUARTER) == 3);
    assertTrue(map.get(USCurrency.DIME) == 2);
    assertTrue(map.get(USCurrency.PENNY) == 4);
  }

  @Test
  public void calculateDP_99() throws Exception {
    Calculator<USCurrency> calculator = new Calculator<>(USCurrency.class);
    HashMap<USCurrency, Integer> map = calculator.calculateChange(1, 100, "dp");
    assertTrue(map.get(USCurrency.QUARTER) == 3);
    assertTrue(map.get(USCurrency.DIME) == 2);
    assertTrue(map.get(USCurrency.PENNY) == 4);
  }

  @Test
  public void calculateRandom_Owed6_Paid12() throws Exception {
    Calculator<USCurrency> calculator = new Calculator<>(USCurrency.class);
    HashMap<USCurrency, Integer> map = calculator.calculateChange(6, 12, "greedy");
    assertTrue(map.get(USCurrency.PENNY) == 6 || (map.get(USCurrency.NICKEL) == 1 && map.get(USCurrency.PENNY) == 1));
  }

  @Test(expected = CustomException.class)
  public void calculate_badMode() throws Exception {
    Calculator<USCurrency> calculator = new Calculator<>(USCurrency.class);
    calculator.calculateChange(1, 100, "rawr");
  }

}
