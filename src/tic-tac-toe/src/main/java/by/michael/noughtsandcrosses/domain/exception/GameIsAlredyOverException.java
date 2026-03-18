package by.michael.noughtsandcrosses.domain.exception;

public class GameIsAlredyOverException extends RuntimeException {
  public GameIsAlredyOverException(String message) {
    super(message);
  }
}
