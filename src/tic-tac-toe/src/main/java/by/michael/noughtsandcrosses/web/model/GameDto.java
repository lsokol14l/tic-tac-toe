package by.michael.noughtsandcrosses.web.model;

import by.michael.noughtsandcrosses.domain.model.GameMode;
import by.michael.noughtsandcrosses.domain.model.GameState;
import by.michael.noughtsandcrosses.domain.model.PlayerType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.Objects;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class GameDto {
  @Valid
  @NotNull(message = "Game field cannot be null")
  private GameFieldDto gameFieldDto;

  @NotNull(message = "Game ID cannot be null")
  private UUID id;

  @NotNull(message = "Game status cannot be null")
  private GameState status;

  // ===== Мультиплеер поля =====
  private GameMode gameMode;

  /** UUID игрока 1 */
  private UUID player1Id;

  /** UUID игрока 2 */
  private UUID player2Id;

  /** Символ игрока 1 (MAX/MIN = X/O) */
  private PlayerType player1Symbol;

  /** Символ игрока 2 */
  private PlayerType player2Symbol;

  /** Чей сейчас ход */
  private UUID currentTurnPlayerId;

  /** UUID победителя */
  private UUID winnerId;

  /** Описание статуса (для фронтенда) */
  private String statusDescription;

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    GameDto gameDto = (GameDto) o;
    return Objects.equals(id, gameDto.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }
}
