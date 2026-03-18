package by.michael.noughtsandcrosses.domain.model;

/** Режим игры */
public enum GameMode {
  /** Игра с AI (ботом) */
  AI,

  /** Онлайн игра с другим игроком */
  MULTIPLAYER,

  /** Локальная игра на одном устройстве (два игрока по очереди) */
  LOCAL
}
