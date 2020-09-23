package coinchange.exceptions;

//Custom exception class for throwing and printing messages, with seperate catch block
public class CustomException extends Exception{
  public CustomException(String message) {
    super(message);
  }
}
