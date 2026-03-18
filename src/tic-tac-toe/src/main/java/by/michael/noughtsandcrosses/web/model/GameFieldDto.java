package by.michael.noughtsandcrosses.web.model;

import static by.michael.noughtsandcrosses.datasource.model.FieldEntity.FIELD_HEIGHT;
import static by.michael.noughtsandcrosses.datasource.model.FieldEntity.FIELD_WIDTH;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class GameFieldDto {
  public static final int BOARD_SIZE = 3;

  @NotNull(message = "Game field cannot be null")
  @Size(min = BOARD_SIZE, max = BOARD_SIZE, message = "Game field must be 3x3")
  private int[][] gameField;

  public GameFieldDto(int[][] gameField) {
    this.gameField = new int[FIELD_HEIGHT][FIELD_WIDTH];
    for (int i = 0; i < FIELD_HEIGHT; i++) {
      System.arraycopy(gameField[i], 0, this.gameField[i], 0, FIELD_WIDTH);
    }
  }
}
