package by.michael.noughtsandcrosses.datasource.exception;

public class GameNotFoundException extends IllegalArgumentException {
  public GameNotFoundException(String gameId) {
    super("Game with id " + gameId + " not found");
  }
}
