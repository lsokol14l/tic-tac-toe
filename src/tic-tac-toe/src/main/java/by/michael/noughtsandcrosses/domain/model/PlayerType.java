package by.michael.noughtsandcrosses.domain.model;

public enum PlayerType {
  MIN(-1),
  MAX(1);

  private final int value;

  PlayerType(int i) {
    value = i;
  }

  public int getValue() {
    return value;
  }
}
