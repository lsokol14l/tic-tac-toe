package by.michael.noughtsandcrosses.domain.service;

import by.michael.noughtsandcrosses.datasource.mapper.DomainDatasourceMapper;
import by.michael.noughtsandcrosses.datasource.model.FieldEntity;
import by.michael.noughtsandcrosses.datasource.model.GameEntity;
import by.michael.noughtsandcrosses.datasource.repository.GameRepository;
import by.michael.noughtsandcrosses.domain.exception.GameIsAlredyOverException;
import by.michael.noughtsandcrosses.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameServiceTests {

  @Mock private GameRepository gameRepository;

  private GameService gameService;

  @BeforeEach
  void setUp() {
    gameService = new GameServiceImpl(gameRepository);
  }

  @Test
  void testCreateNewGame() {
    UUID testUserId = UUID.randomUUID();

    Game game = gameService.createNewGame(GameMode.AI, testUserId);

    assertNotNull(game);
    assertNotNull(game.getUuid());
    assertEquals(GameState.PLAYING, game.getStatus());
    assertEquals(PlayerType.MAX, game.getCurrentPlayer());
    assertEquals(testUserId, game.getPlayer1Id());
    assertEquals(GameMode.AI, game.getGameMode());
    verify(gameRepository).save(any(GameEntity.class));
  }

  @Test
  void testIsActionValidWithValidMove() {
    Game currentGame = new Game();
    currentGame.makeMove(0, 0);

    Game newGame = copyGame(currentGame);
    newGame.makeMove(1, 1);

    assertTrue(gameService.isActionValid(currentGame, newGame));
  }

  @Test
  void testIsActionValidWithInvalidMove_ModifiedPreviousMove() {
    Game currentGame = new Game();
    currentGame.makeMove(0, 0);

    Game newGame = new Game();
    newGame.makeMove(1, 1); // не содержит предыдущий ход

    assertFalse(gameService.isActionValid(currentGame, newGame));
  }

  @Test
  void testIsActionValidWithInvalidMove_MultipleCellsChanged() {
    Game currentGame = new Game();
    currentGame.makeMove(0, 0);

    Game newGame = copyGame(currentGame);
    newGame.makeMove(1, 1);
    newGame.makeMove(2, 2); // два новых хода

    assertFalse(gameService.isActionValid(currentGame, newGame));
  }

  @Test
  void testIsGameOver() {
    Game game = new Game();
    assertFalse(gameService.isGameOver(game));

    int[][] fieldArray = game.getGameField().getField();
    fieldArray[0][0] = CellType.CROSS.getValue();
    fieldArray[0][1] = CellType.CROSS.getValue();
    fieldArray[0][2] = CellType.CROSS.getValue();
    game.makeMove(1, 1);

    assertTrue(game.isGameOver());
  }

  @Test
  void testProcessMove_UserMoveOnly() {
    UUID testUserId = UUID.randomUUID();
    Game game = new Game();
    game.setPlayer1Id(testUserId);
    game.setCurrentTurnPlayerId(testUserId);
    UUID gameId = game.getUuid();

    GameEntity entity = DomainDatasourceMapper.toEntity(game);
    when(gameRepository.findById(gameId)).thenReturn(Optional.of(entity));

    Game userGame = copyGame(game);
    userGame.makeMove(1, 1);

    Game updatedGame = gameService.processMove(gameId, userGame, testUserId);

    assertNotNull(updatedGame);
    int movesCount = countMoves(updatedGame.getGameField());
    assertEquals(2, movesCount); // ход игрока + ход бота
    verify(gameRepository, atLeastOnce()).save(any(GameEntity.class));
  }

  @Test
  void testProcessMove_ThrowsExceptionOnInvalidMove() {
    UUID testUserId = UUID.randomUUID();
    Game game = new Game();
    game.setPlayer1Id(testUserId);
    game.setCurrentTurnPlayerId(testUserId);
    UUID gameId = game.getUuid();

    GameEntity entity = DomainDatasourceMapper.toEntity(game);
    when(gameRepository.findById(gameId)).thenReturn(Optional.of(entity));

    Game userGame = new Game(); // совершенно новая игра без предыдущих ходов

    assertThrows(
        IllegalArgumentException.class,
        () -> gameService.processMove(gameId, userGame, testUserId));
  }

  @Test
  void testProcessMove_ThrowsWhenGameNotFound() {
    UUID gameId = UUID.randomUUID();
    when(gameRepository.findById(gameId)).thenReturn(Optional.empty());

    assertThrows(
        Exception.class, () -> gameService.processMove(gameId, new Game(), UUID.randomUUID()));
  }

  @Test
  void testGetNextMove() {
    Game game = new Game();

    int[] nextMove = gameService.getNextMove(game);

    assertNotNull(nextMove);
    assertEquals(2, nextMove.length);
    assertTrue(nextMove[0] >= 0 && nextMove[0] < 3);
    assertTrue(nextMove[1] >= 0 && nextMove[1] < 3);
  }

  // ─── helpers ────────────────────────────────────────────────────────────

  private Game copyGame(Game game) {
    Game copy = new Game();
    int[][] original = game.getGameField().getField();
    for (int y = 0; y < GameField.FIELD_HEIGHT; y++)
      for (int x = 0; x < GameField.FIELD_WIDTH; x++)
        if (original[y][x] != CellType.VOID.getValue())
          copy.getGameField().getField()[y][x] = original[y][x];
    return copy;
  }

  private int countMoves(GameField field) {
    int count = 0;
    int[][] f = field.getField();
    for (int y = 0; y < GameField.FIELD_HEIGHT; y++)
      for (int x = 0; x < GameField.FIELD_WIDTH; x++)
        if (f[y][x] != CellType.VOID.getValue()) count++;
    return count;
  }
}
