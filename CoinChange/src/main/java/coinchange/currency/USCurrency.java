package coinchange.currency;

import java.util.Comparator;

public class USCurrency extends Currency {
  //Included the average denominations that most cash registers would hold. Denominations can be added or removed.
  //Selecting specific denominations based on what is currently contained inside the register would require additional methods or a register class.
  //Went with this method instead of an Enum, as Enums do not work with generics as I needed.
  public static final USCurrency TWENTY_DOLLAR = new USCurrency(2000, "twenty dollar bill");
  public static final USCurrency TEN_DOLLAR = new USCurrency(1000, "ten dollar bill");
  public static final USCurrency FIVE_DOLLAR = new USCurrency(500, "five dollar bill");
  public static final USCurrency ONE_DOLLAR = new USCurrency(100, "one dollar bill");
  public static final USCurrency QUARTER = new USCurrency(25, "quarter");
  public static final USCurrency DIME = new USCurrency(10, "dime");
  public static final USCurrency NICKEL = new USCurrency(5, "nickel");
  public static final USCurrency PENNY = new USCurrency(1, "penny");

  private static final int NUM_COINS = 8;

  private final int amount;
  private final String label;
  
  //These are static to be consistent across the entire class
  private static int lowestCoin;
  private static USCurrency[] values;
  private static int valuesCount = 0;

  public USCurrency() {
    amount = 0;
    label = "";
  }

  private USCurrency(int amount, String label) {
    this.amount = amount;
    this.label = label;
    if (amount < getLowestCoin() || getLowestCoin() == 0) {
      setLowestCoin(amount);
    }
    //As each of the denominations is added, add them to the values array
    if (valuesCount == 0) {
      values = new USCurrency[NUM_COINS];
    }
    values[valuesCount] = this;
    valuesCount++;
  }

  @Override
  public int getAmount() {
    return amount;
  }

  @Override
  public String getLabel() {
    return label;
  }

  private static void setLowestCoin(int amount) {
    lowestCoin = amount;
  }

  //Static and non-static versions of these methods are added to be able to access static variables, 
  //while also providing the methods of the parent class
  public static int getStaticLowestCoin() {
    return lowestCoin;
  }

  @Override
  public int getLowestCoin() {
    return getStaticLowestCoin();
  }

  private static Comparator<USCurrency> usCurrencyComparator = new Comparator<USCurrency>() {
      public int compare(USCurrency o1, USCurrency o2) {
          return o1.getAmount() - o2.getAmount();
      }
  };

  public static Comparator<USCurrency> getStaticComparator() {
    return usCurrencyComparator;
  }

  @Override
  public Comparator<USCurrency> getComparator() {
    return getStaticComparator();
  }

  public static USCurrency[] getStaticValues() {
    return values;
  }

  @SuppressWarnings("unchecked")
  @Override
  public USCurrency[] getValues() {
    return getStaticValues();
  }

  @Override
  public int compareTo(Currency o) {
    return this.getAmount() - o.getAmount();
  }

}