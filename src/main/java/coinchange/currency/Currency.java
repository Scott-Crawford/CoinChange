package coinchange.currency;

import java.util.Comparator;

//Parent class for all currencies, to allow the calculator to accept a generic Currency type
//If a client with a different form of currency is added in the future, a child class like USCurrency will need to be created.
public class Currency implements Comparable<Currency>{
  
  public int getAmount() {
    return 0;
  }

  public String getLabel() {
    return "";
  }

  //Needed for random method, in the case that the currency passed in doesn't contain a penny.
  public int getLowestCoin() {
    return 0;
  }

  public Comparator<? extends Currency> getComparator() {
    return null;
  }

  public <T extends Currency> T[] getValues() {
    return null;
  }

  public Currency() {
  }

  @Override
  public int compareTo(Currency o) {
    return 0;
  }
}
