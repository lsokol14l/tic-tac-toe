package by.michael.noughtsandcrosses.domain.model;

import by.michael.noughtsandcrosses.domain.algorithm.MinimaxEvaluator;

public class GameField {
  public static final int FIELD_WIDTH = 3;
  public static final int FIELD_HEIGHT = 3;
  private final int[][] field;

  public GameField() {
    field = new int[FIELD_HEIGHT][FIELD_WIDTH];
  }

  public GameField(int[][] field) {
    this.field = new int[FIELD_HEIGHT][FIELD_WIDTH];
    for (int i = 0; i < FIELD_HEIGHT; i++) {
      System.arraycopy(field[i], 0, this.field[i], 0, FIELD_WIDTH);
    }
  }

  public void makeMove(int x, int y, PlayerType player) {
    if (!isValidMove(x, y)) {
      throw new IllegalArgumentException("Invalid move: (" + x + ", " + y + ")");
    }

    CellType cellType = (player == PlayerType.MAX) ? CellType.CROSS : CellType.NOUGHT;
    field[y][x] = cellType.getValue();
  }

  public boolean isValidMove(int x, int y) {
    return x >= 0
        && x < FIELD_WIDTH
        && y >= 0
        && y < FIELD_HEIGHT
        && field[y][x] == CellType.VOID.getValue();
  }

  public boolean hasWinner(PlayerType player) {
    CellType type = (player == PlayerType.MAX) ? CellType.CROSS : CellType.NOUGHT;
    int value = type.getValue();

    // Проверка строк и столбцов
    for (int i = 0; i < 3; i++) {
      if (checkLine(field[i][0], field[i][1], field[i][2], value)
          || checkLine(field[0][i], field[1][i], field[2][i], value)) {
        return true;
      }
    }

    // Проверка диагоналей
    return checkLine(field[0][0], field[1][1], field[2][2], value)
        || checkLine(field[0][2], field[1][1], field[2][0], value);
  }

  private boolean checkLine(int a, int b, int c, int value) {
    return a == value && b == value && c == value;
  }

  public boolean isFull() {
    for (int[] row : field) {
      for (int cell : row) {
        if (cell == CellType.VOID.getValue()) {
          return false;
        }
      }
    }
    return true;
  }

  public int[] getBestMove(PlayerType player) {
    int bestScore = (player == PlayerType.MAX) ? Integer.MIN_VALUE : Integer.MAX_VALUE;
    int[] bestMove = null;

    for (int y = 0; y < FIELD_HEIGHT; y++) {
      for (int x = 0; x < FIELD_WIDTH; x++) {
        if (isValidMove(x, y)) {
          makeMove(x, y, player);
          int score =
              MinimaxEvaluator.evaluate(
                  this, player == PlayerType.MAX ? PlayerType.MIN : PlayerType.MAX);
          field[y][x] = CellType.VOID.getValue();

          if ((player == PlayerType.MAX && score > bestScore)
              || (player == PlayerType.MIN && score < bestScore)) {
            bestScore = score;
            bestMove = new int[] {x, y};
          }
        }
      }
    }

    return bestMove != null ? bestMove : new int[] {-1, -1};
  }

  public int[][] getField() {
    return field;
  }

  public GameField copy() {
    int[][] newField = new int[FIELD_HEIGHT][FIELD_WIDTH];
    for (int i = 0; i < FIELD_HEIGHT; i++) {
      System.arraycopy(field[i], 0, newField[i], 0, FIELD_WIDTH);
    }
    return new GameField(newField);
  }
}
