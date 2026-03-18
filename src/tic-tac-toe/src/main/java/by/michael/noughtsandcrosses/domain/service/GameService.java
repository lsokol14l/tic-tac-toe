package by.michael.noughtsandcrosses.domain.service;

import by.michael.noughtsandcrosses.domain.model.Game;
import by.michael.noughtsandcrosses.domain.model.GameMode;
import java.util.List;
import java.util.UUID;

public interface GameService {
  /**
   * Метод получения следующего хода текущей игры алгоритмом «Минимакс»
   *
   * @param currentGame текущая игра
   * @return следующий ход в формате [x, y]
   */
  int[] getNextMove(Game currentGame);

  /**
   * Метод валидации игрового поля текущей игры (проверка, что не изменены предыдущие ходы)
   *
   * @param currentGame текущая игра из хранилища
   * @param newGame игра с новым ходом от пользователя
   * @return true если валидация прошла успешно
   */
  boolean isActionValid(Game currentGame, Game newGame);

  /**
   * Метод проверки окончания игры
   *
   * @param currentGame текущая игра
   * @return true если игра окончена
   */
  boolean isGameOver(Game currentGame);

  /**
   * Создать новую игру с выбранным режимом
   *
   * @param mode режим игры (AI, MULTIPLAYER, LOCAL)
   * @param userId UUID создателя игры
   * @return созданная игра
   */
  Game createNewGame(GameMode mode, UUID userId);

  /**
   * Метод обработки хода пользователя и компьютера
   *
   * @param gameId UUID игры
   * @param userGame игра с ходом пользователя
   * @param userId UUID игрока, который делает ход
   * @return обновленная игра после хода пользователя и компьютера
   */
  Game processMove(UUID gameId, Game userGame, UUID userId);

  /**
   * Получить список всех доступным мультиплеерных игр, ожидающих 2 игрока
   *
   * @return список игр со статусом WAITING_FOR_PLAYERS
   */
  List<Game> getAvailableGames();

  /**
   * @param gameId id игры, к которой подключается игрок
   * @param userId id игрока, который хочет подключиться
   * @return игра с двумя игроками
   */
  Game joinGame(UUID gameId, UUID userId);

  /**
   * Получить текущее состояние игры по ID
   *
   * @param gameId UUID игры
   * @return текущее состояние игры
   */
  Game getGame(UUID gameId);

  /**
   * @param userId id текущего игрока
   * @return список игр в которых есть id игрока
   */
  List<Game> getMyGames(UUID userId);
}
