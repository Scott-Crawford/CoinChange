package coinchange.calculator;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import coinchange.currency.Currency;
import coinchange.exceptions.CustomException;
import coinchange.util.Constants;

//Calculator that can be used for any currency
public class Calculator<T extends Currency> {

  //Cache to be used by both greedy mode and dp mode
  private HashMap<Integer, HashMap<T, Integer>> mapMapCache = new HashMap<>();

  //Caches for dp mode
  private int[] dpCache = new int[0];
  private int[] numCache = new int[0];

  private Random rand = new Random();

  //Instance of Currency subclass to be able to access non static methods
  Currency currency;

  //Takes in the currency child class in order to create the currency instance
  public Calculator(Class<T> clazz) {
    try {
      T t = clazz.getDeclaredConstructor().newInstance();
      if (t instanceof Currency) {
        currency = t;
      }
    } catch (Exception e) {
      System.out.println("An error occurred.");
      e.printStackTrace();
    }
  }

  //Checks the special condition, then which mode was selected. Additional conditions can be added.
  public HashMap<T, Integer> calculateChange(int owed, int paid, String mode) throws Exception{
    if (checkSpecialCondition(owed)) {
      return randomChange(paid - owed);
    } else if (mode.equals("dp")) {
      return dpChange(paid - owed);
    } else if (mode.equals("greedy")) {
      return greedyChange(paid - owed);
    } else {
      throw new CustomException("Please provide either dp or greedy as the mode.");
    }
  }

  //Checks the special condition. Stored as a constant, can be easily changed.
  private boolean checkSpecialCondition(int owed) {
    return owed % Constants.SPECIAL_CONDITION == 0;
  }

  //Provides random amounts for each denomination, adding up to the change amount.
  @SuppressWarnings("unchecked")
  private HashMap<T, Integer> randomChange(int change) {
    HashMap<T, Integer> map = new HashMap<>();

    int coinDemonsCount = currency.getValues().length;
    T[] coinDenomArr = (T[])new Currency[coinDemonsCount];
    Pair<T> pair = cleanArr(coinDenomArr, change, coinDemonsCount);
    coinDenomArr = pair.arr;
    coinDemonsCount = pair.val;

    int changeRemainder = change;

    //Selects a denomination smaller than the remainder and subtracts until the remainder is 0 or smaller than lowest denomination
    while (changeRemainder > 0 && changeRemainder >= currency.getLowestCoin()) {
      int val = rand.nextInt(coinDemonsCount);
      if (coinDenomArr[val].getAmount() <= changeRemainder) {
        changeRemainder -= coinDenomArr[val].getAmount();
        if (map.containsKey(coinDenomArr[val])) {
          map.put(coinDenomArr[val], map.get(coinDenomArr[val]) + 1);
        } else {
          map.put(coinDenomArr[val], 1);
        }
      }
    }

    return map;
  }

  //Uses the dynamic programming method of solving subproblems.
  //Always provides optimal solution, but takes longer and more memory.
  @SuppressWarnings("unchecked")
  private HashMap<T, Integer> dpChange(int change) {

    if (change < dpCache.length && change > 0) {
      return mapMapCache.get(dpCache[change]);
    }

    int coinDemonsCount = currency.getValues().length;
    T[] coinDenomArr = (T[])new Currency[coinDemonsCount];
    Pair<T> pair = cleanArr(coinDenomArr, change, coinDemonsCount);
    coinDenomArr = pair.arr;
    coinDemonsCount = pair.val;

    int[] dp = new int[change + 1];
    int[] num = new int[change + 1];
    for (int i = 0; i < dpCache.length; i++) {
      dp[i] = dpCache[i];
      num[i] = numCache[i];
    }

    for (int i = dpCache.length - 1; i <= change; i++) {
      for (int j = 0; j < coinDemonsCount; j++) {
        if (coinDenomArr[j].getAmount() <= i) {
          //checks if new amount is bigger/same and that fewer coins are used
          if (dp[i] <= dp[i - coinDenomArr[j].getAmount()] + coinDenomArr[j].getAmount()
                  && (num[i] == 0 || (num[i] > num[i - coinDenomArr[j].getAmount()] + 1))) {
            if (!mapMapCache.containsKey(dp[i - coinDenomArr[j].getAmount()])) {
              mapMapCache.put(dp[i - coinDenomArr[j].getAmount()], new HashMap<T, Integer>());
            }
            //Deep copy of hashmap
            HashMap<T, Integer> copy = new HashMap<>();
            for (Map.Entry<T, Integer> entry : mapMapCache.get(dp[i - coinDenomArr[j].getAmount()])
                    .entrySet()) {
              copy.put(entry.getKey(), entry.getValue());
            }
            if (copy.containsKey(coinDenomArr[j])) {
              copy.put(coinDenomArr[j], copy.get(coinDenomArr[j]) + 1);
            } else {
              copy.put(coinDenomArr[j], 1);
            }
            mapMapCache.put(i, copy);
            dp[i] = dp[i - coinDenomArr[j].getAmount()] + coinDenomArr[j].getAmount();
            num[i] = num[i - coinDenomArr[j].getAmount()] + 1;
          } else {
            if (!mapMapCache.containsKey(i)) {
              mapMapCache.put(i, new HashMap<T, Integer>());
            }
          }
        }
      }
    }

    dpCache = dp;
    numCache = num;

    return mapMapCache.get(dp[change]);
  }

  //Uses greedy method. Works for most currencies, but there exist edge cases where it won't provide the optimal solution.
  //A currency with the coins {4,3,1} will return a 4 and two 1s with greedy method, while the optimal solution is two 3s.
  @SuppressWarnings("unchecked")
  private HashMap<T, Integer> greedyChange(int change) {
    if (mapMapCache.containsKey(change)) {
      return mapMapCache.get(change);
    }

    HashMap<T, Integer> map = new HashMap<>();
    int coinDemonsCount = currency.getValues().length;
    T[] coinDenomArr = (T[])new Currency[coinDemonsCount];
    Pair<T> pair = cleanArr(coinDenomArr, change, coinDemonsCount);
    coinDenomArr = pair.arr;
    coinDemonsCount = pair.val;

    int changeTemp = change;

    for (int i = 0; i < coinDemonsCount; i++) {
      int quotient = changeTemp / coinDenomArr[i].getAmount();
      int remainder = changeTemp % coinDenomArr[i].getAmount();
      if (quotient > 0) {
        map.put(coinDenomArr[i], quotient);
      }
      changeTemp = remainder;
    }

    mapMapCache.put(change, map);

    return map;
  }

  //Method for sorting the coin denominations greatest to smallest, 
  //and removing from the array any coins greater than the change amount
  //returns the clean array and the exact number of coins in it.
  private Pair<T> cleanArr(T[] arr, int change, int count) {
    T[] coinDenomArr = currency.getValues();

    Arrays.sort(coinDenomArr,Collections.reverseOrder());

    int x = 0;
    for (int i = 0; i < count; i++) {
      if (coinDenomArr[i].getAmount() <= change) {
        arr[x] = (T) coinDenomArr[i];
        x++;
      }
    }
    count = x;
    return new Pair<>(arr, count);
  }
}

//Pair class used exclusively in cleanArr method
class Pair<T> {

  public final T[] arr;
  public final int val;

  public Pair(T[] arr, int val) {
    this.arr = arr;
    this.val = val;
  }
}