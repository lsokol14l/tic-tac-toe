package by.michael.noughtsandcrosses.domain.algorithm;

import by.michael.noughtsandcrosses.domain.model.CellType;
import by.michael.noughtsandcrosses.domain.model.GameField;
import by.michael.noughtsandcrosses.domain.model.PlayerType;

import static by.michael.noughtsandcrosses.domain.model.GameField.FIELD_HEIGHT;
import static by.michael.noughtsandcrosses.domain.model.GameField.FIELD_WIDTH;

/**
 * The MiniMax algorithm adapted for my task.
 *
 * @author Michael
 */
public class MinimaxEvaluator {
  private MinimaxEvaluator() {}

  public static int evaluate(GameField gameField, PlayerType playerType) {
    if (gameField.hasWinner(PlayerType.MAX)) return 1;
    if (gameField.hasWinner(PlayerType.MIN)) return -1;
    if (gameField.isFull()) return 0;

    int evaluation;
    int[][] field = gameField.getField();
    // CellType.CROSS
    if (playerType == PlayerType.MAX) {
      evaluation = Integer.MIN_VALUE;

      for (int y = 0; y < FIELD_HEIGHT; y++) {
        for (int x = 0; x < FIELD_WIDTH; x++) {
          if (field[y][x] == CellType.VOID.getValue()) {
            field[y][x] = CellType.CROSS.getValue();
            int currentEvaluation = MinimaxEvaluator.evaluate(gameField, PlayerType.MIN);
            field[y][x] = CellType.VOID.getValue();

            if (currentEvaluation > evaluation) {
              evaluation = currentEvaluation;
            }
          }
        }
      }
    } else {
      // CellType.NOUGHT
      evaluation = Integer.MAX_VALUE;

      for (int y = 0; y < FIELD_HEIGHT; y++) {
        for (int x = 0; x < FIELD_WIDTH; x++) {
          if (field[y][x] == CellType.VOID.getValue()) {
            field[y][x] = CellType.NOUGHT.getValue();
            int currentEvaluation = MinimaxEvaluator.evaluate(gameField, PlayerType.MAX);
            field[y][x] = CellType.VOID.getValue();
            if (currentEvaluation < evaluation) {
              evaluation = currentEvaluation;
            }
          }
        }
      }
    }

    return evaluation;
  }
}
