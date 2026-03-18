package by.michael.noughtsandcrosses.domain.model;

public enum CellType {
  VOID(0),
  CROSS(1),
  NOUGHT(-1);

  private final int value;

  CellType(int i) {
    value = i;
  }

  public int getValue() {
    return value;
  }
}
