package by.michael.noughtsandcrosses.domain.service;

import by.michael.noughtsandcrosses.datasource.exception.GameNotFoundException;
import by.michael.noughtsandcrosses.datasource.mapper.DomainDatasourceMapper;
import by.michael.noughtsandcrosses.datasource.model.GameEntity;
import by.michael.noughtsandcrosses.datasource.repository.GameRepository;
import by.michael.noughtsandcrosses.domain.exception.GameIsAlredyOverException;
import by.michael.noughtsandcrosses.domain.model.*;
import org.springframework.validation.DataBinder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class GameServiceImpl implements GameService {
  private final GameRepository gameRepository;

  public GameServiceImpl(GameRepository gameRepository) {
    this.gameRepository = gameRepository;
  }

  @Override
  public int[] getNextMove(Game currentGame) {
    return currentGame.getNextMove();
  }

  @Override
  public boolean isActionValid(Game currentGame, Game newGame) {
    int[][] currentField = currentGame.getGameField().getField();
    int[][] newField = newGame.getGameField().getField();

    int changesCount = 0;

    // Проверяем, что изменена только одна клетка и предыдущие ходы не изменены
    for (int y = 0; y < GameField.FIELD_HEIGHT; y++) {
      for (int x = 0; x < GameField.FIELD_WIDTH; x++) {
        if (currentField[y][x] != newField[y][x]) {
          // Если клетка была не пустой
          if (currentField[y][x] != CellType.VOID.getValue()) {
            return false;
          }
          changesCount++;
        }
      }
    }

    // Должна быть изменена ровно одна клетка
    return changesCount == 1;
  }

  @Override
  public boolean isGameOver(Game currentGame) {
    return currentGame.isGameOver();
  }

  @Override
  public Game createNewGame(GameMode mode, UUID userId) {
    Game game;

    if (mode == GameMode.AI) {
      // Игра против Бота
      game = new Game();
      game.setPlayer1Id(userId);
      game.setCurrentTurnPlayerId(userId);
    } else {
      // Мультиплеер или локальная игра
      game = new Game(userId, mode);
    }

    gameRepository.save(DomainDatasourceMapper.toEntity(game));
    return game;
  }

  @Override
  public Game processMove(UUID gameId, Game userGame, UUID userId) {
    Optional<GameEntity> currentGameEntity = gameRepository.findById(gameId);
    if (currentGameEntity.isEmpty())
      throw new IllegalArgumentException("Game not found: " + gameId);
    Game currentGame = DomainDatasourceMapper.toDomain(currentGameEntity.get());

    if (isGameOver(currentGame)) {
      throw new GameIsAlredyOverException("Game is already over");
    }

    // Проверяем, что игрок участвует в этой игре
    if (currentGame.getPlayer1Id() != null && !currentGame.isPlayerInGame(userId)) {
      throw new IllegalArgumentException("You are not a participant of this game");
    }

    // Проверяем, что сейчас ход именно этого игрока
    if (currentGame.getCurrentTurnPlayerId() != null && !currentGame.isPlayerTurn(userId)) {
      throw new IllegalArgumentException("It is not your turn");
    }

    if (!isActionValid(currentGame, userGame)) {
      throw new IllegalArgumentException(
          "Invalid move: previous moves were changed or multiple cells modified");
    }

    applyUserMove(currentGame, userGame);

    if (isGameOver(currentGame)) {
      updateGame(currentGame);
      return currentGame;
    }

    // Для режима с ботом: делаем ход бота
    if (currentGame.isAIGame()) {
      int[] botMove = getNextMove(currentGame);
      currentGame.makeMove(botMove[0], botMove[1]);

      // ВАЖНО: После хода бота возвращаем currentTurnPlayerId игроку
      currentGame.setCurrentTurnPlayerId(currentGame.getPlayer1Id());
    }

    updateGame(currentGame);
    return currentGame;
  }

  private void applyUserMove(Game currentGame, Game userGame) {
    int[][] currentField = currentGame.getGameField().getField();
    int[][] newField = userGame.getGameField().getField();

    for (int y = 0; y < GameField.FIELD_HEIGHT; y++) {
      for (int x = 0; x < GameField.FIELD_WIDTH; x++) {
        if (currentField[y][x] != newField[y][x]) {
          currentGame.makeMove(x, y);
          return;
        }
      }
    }
  }

  private void updateGame(Game game) {
    gameRepository.save(DomainDatasourceMapper.toEntity(game));
  }

  @Override
  public List<Game> getAvailableGames() {
    return gameRepository
        .findByStatusAndGameMode(GameState.WAITING_FOR_PLAYERS, GameMode.MULTIPLAYER)
        .stream()
        .map(DomainDatasourceMapper::toDomain)
        .toList();
  }

  @Override
  public Game joinGame(UUID gameId, UUID userId) {
    GameEntity byId =
        gameRepository
            .findById(gameId)
            .orElseThrow(() -> new GameNotFoundException(gameId.toString()));

    Game game = DomainDatasourceMapper.toDomain(byId);

    game.joinPlayer2(userId);

    gameRepository.save(DomainDatasourceMapper.toEntity(game));

    return game;
  }

  @Override
  public Game getGame(UUID gameId) {
    GameEntity byId =
        gameRepository
            .findById(gameId)
            .orElseThrow(() -> new GameNotFoundException(gameId.toString()));

    return DomainDatasourceMapper.toDomain(byId);
  }

  @Override
  public List<Game> getMyGames(UUID userId) {
    return gameRepository.findByPlayer1IdOrPlayer2Id(userId, userId).stream()
        .map(DomainDatasourceMapper::toDomain)
        .toList();
  }
}
