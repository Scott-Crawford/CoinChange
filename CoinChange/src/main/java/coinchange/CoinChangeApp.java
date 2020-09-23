package coinchange;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;

import coinchange.calculator.Calculator;
import coinchange.currency.USCurrency;
import coinchange.exceptions.CustomException;
import coinchange.util.Constants;

public class CoinChangeApp {

  public static void main(String[] args) {
    try {
      //If no mode is provided from command line, use default mode from constants
      String mode = Constants.DEFAULT_MODE;

      String filename;

      //Gather filename and mode from command line, if avaliable
      if (args.length >= 1) {
        filename = args[0];
        if (args.length >= 2) {
          mode = args[1];
        }
      //If not avaliable, ask for user to input filename and select mode
      } else {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the filename:");
        filename = scanner.nextLine();
        System.out.println("Enter the mode to use (\"greedy\" or \"dp\"):");
        System.out.println("Greedy mode (Recommended): Faster and less memory intensive. May not provide optimal solution for non-canonical currency denominations.");
        System.out.println("DP mode : Slower and more memory intensive. Uses dynamic programming to find optimal solution. \n(Warning: DP mode may exceed heap size when calculating change greater than $100,000.)");
        mode = scanner.nextLine();
        scanner.close();
      }

      File file = new File(filename);
      Scanner fileReader = new Scanner(file);
      PrintWriter writer = new PrintWriter("output.txt", "UTF-8");
      //For each line of the file, turn the doubles into coins, check against some edge cases, and pass to the calculator
      while (fileReader.hasNextLine()) {
        String data = fileReader.nextLine();
        String[] numbers = data.split(",");
        double owed = Double.parseDouble(numbers[0]);
        double paid = Double.parseDouble(numbers[1]);
        if(owed<0){
          writeToFile(writer,"Amount owed is negative\n");
          continue;
        }
        if(Math.round(100 * owed) > Integer.MAX_VALUE) {
          writeToFile(writer,"Amount owed in pennies exceeds max int\n");
          continue;
        }
        if(paid<0){
          writeToFile(writer,"Amount paid is negative\n");
          continue;
        }
        if(Math.round(100 * paid) > Integer.MAX_VALUE) {
          writeToFile(writer,"Amount paid in pennies exceeds max int\n");
          continue;
        }
        int owedCents = (int) Math.round(100 * owed);
        int paidCents = (int) Math.round(100 * paid);
        if(paid<owed){
          writeToFile(writer,"Not enough payment\n");
          continue;
        }
        Calculator<USCurrency> calculator = new Calculator<>(USCurrency.class);
        HashMap<USCurrency, Integer> map = calculator.calculateChange(owedCents, paidCents, mode);
        writeToFile(writer, map);
      }
      writer.close();
      fileReader.close();
    } catch (FileNotFoundException e) {
      System.out.println("Please provide a valid filename.");
    } catch (CustomException e) {
      System.out.println(e.getMessage());
    } catch (NumberFormatException e) {
      System.out.println("Please format your input file correctly.");
    } catch (ArrayIndexOutOfBoundsException e) {
      System.out.println("Please format your input file correctly.");
    } catch (Exception e) {
      System.out.println("An error occurred.");
      e.printStackTrace();
    }

  }

  //Method for printing the change to the output file.
  private static void writeToFile(PrintWriter writer, HashMap<USCurrency, Integer> map) {
    StringBuilder builder = new StringBuilder();
    USCurrency[] coins = USCurrency.getStaticValues();
    Arrays.sort(coins, Collections.reverseOrder(USCurrency.getStaticComparator()));
    for (USCurrency coin : coins) {
      if (map.containsKey(coin)) {
        if (builder.length() > 0) {
          builder.append(",");
        }
        //Handles plural vs singular cases. Pennies is currently the only edge case here.
        if (map.get(coin) > 1) {
          if (coin.getLabel().equals("penny")) {
            builder.append(map.get(coin) + " " + "pennies");
          } else {
            builder.append(map.get(coin) + " " + coin.getLabel() + "s");
          }
        } else {
          builder.append(map.get(coin) + " " + coin.getLabel() + "");
        }
      }
    }
    //Rather than providing an empty line, print "No change"
    if(builder.length() == 0){
      builder.append("No change");
    }
    builder.append("\n");
    String output = builder.toString();
    writer.write(output);
  }

  //Method to print more generic messages to output file
  private static void writeToFile(PrintWriter writer, String message) {
    writer.write(message);
  }

}
