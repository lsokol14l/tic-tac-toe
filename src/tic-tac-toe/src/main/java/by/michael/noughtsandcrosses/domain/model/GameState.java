package by.michael.noughtsandcrosses.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum GameState {
  /** Ожидание подключения второго игрока */
  WAITING_FOR_PLAYERS(-101),

  /** Игра идёт, игроки делают ходы */
  PLAYING(-100),

  /** Ничья */
  DRAW(0),

  /** Один из игроков победил (UUID победителя хранится в Game.winnerId) */
  PLAYER_WIN(1);

  private final int value;
}
