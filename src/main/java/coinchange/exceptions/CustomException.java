package coinchange.exceptions;

//Custom exception class for throwing and printing messages
public class CustomException extends Exception{
  public CustomException(String message) {
    super(message);
  }
}
