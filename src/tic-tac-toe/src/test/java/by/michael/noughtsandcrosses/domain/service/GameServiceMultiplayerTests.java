package by.michael.noughtsandcrosses.domain.service;

import by.michael.noughtsandcrosses.datasource.mapper.DomainDatasourceMapper;
import by.michael.noughtsandcrosses.datasource.model.GameEntity;
import by.michael.noughtsandcrosses.datasource.repository.GameRepository;
import by.michael.noughtsandcrosses.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameServiceMultiplayerTests {

  @Mock private GameRepository gameRepository;

  private GameService gameService;

  @BeforeEach
  void setUp() {
    gameService = new GameServiceImpl(gameRepository);
  }

  // ─── createNewGame ───────────────────────────────────────────────────────

  @Test
  void testCreateMultiplayerGame_statusIsWaiting() {
    UUID userId = UUID.randomUUID();

    Game game = gameService.createNewGame(GameMode.MULTIPLAYER, userId);

    assertEquals(GameState.WAITING_FOR_PLAYERS, game.getStatus());
    assertEquals(userId, game.getPlayer1Id());
    assertNull(game.getPlayer2Id());
    verify(gameRepository).save(any(GameEntity.class));
  }

  // ─── joinGame ────────────────────────────────────────────────────────────

  @Test
  void testJoinGame_setsPlayer2AndChangesStatusToPlaying() {
    UUID player1 = UUID.randomUUID();
    UUID player2 = UUID.randomUUID();

    Game waiting = new Game(player1, GameMode.MULTIPLAYER);
    GameEntity entity = DomainDatasourceMapper.toEntity(waiting);
    when(gameRepository.findById(waiting.getUuid())).thenReturn(Optional.of(entity));

    Game joined = gameService.joinGame(waiting.getUuid(), player2);

    assertEquals(player2, joined.getPlayer2Id());
    assertEquals(GameState.PLAYING, joined.getStatus());
    assertEquals(player1, joined.getCurrentTurnPlayerId());
    verify(gameRepository).save(any(GameEntity.class));
  }

  @Test
  void testJoinGame_gameNotFound_throwsException() {
    UUID unknown = UUID.randomUUID();
    when(gameRepository.findById(unknown)).thenReturn(Optional.empty());

    assertThrows(Exception.class, () -> gameService.joinGame(unknown, UUID.randomUUID()));
  }

  // ─── getAvailableGames ───────────────────────────────────────────────────

  @Test
  void testGetAvailableGames_returnsOnlyWaitingMultiplayerGames() {
    UUID user1 = UUID.randomUUID();
    UUID user2 = UUID.randomUUID();

    GameEntity waiting1 = DomainDatasourceMapper.toEntity(new Game(user1, GameMode.MULTIPLAYER));
    GameEntity waiting2 = DomainDatasourceMapper.toEntity(new Game(user2, GameMode.MULTIPLAYER));

    when(gameRepository.findByStatusAndGameMode(
            GameState.WAITING_FOR_PLAYERS, GameMode.MULTIPLAYER))
        .thenReturn(List.of(waiting1, waiting2));

    List<Game> available = gameService.getAvailableGames();

    assertEquals(2, available.size());
    assertTrue(available.stream().allMatch(g -> g.getStatus() == GameState.WAITING_FOR_PLAYERS));
    assertTrue(available.stream().allMatch(g -> g.getGameMode() == GameMode.MULTIPLAYER));
  }

  @Test
  void testGetAvailableGames_returnsEmptyWhenNoneWaiting() {
    when(gameRepository.findByStatusAndGameMode(
            GameState.WAITING_FOR_PLAYERS, GameMode.MULTIPLAYER))
        .thenReturn(List.of());

    assertTrue(gameService.getAvailableGames().isEmpty());
  }

  // ─── getMyGames ──────────────────────────────────────────────────────────

  @Test
  void testGetMyGames_returnsGamesForPlayer() {
    UUID player1 = UUID.randomUUID();
    UUID player2 = UUID.randomUUID();

    Game aiGame = new Game();
    aiGame.setPlayer1Id(player1);
    Game mpGame = new Game(player1, GameMode.MULTIPLAYER);

    when(gameRepository.findByPlayer1IdOrPlayer2Id(player1, player1))
        .thenReturn(
            List.of(
                DomainDatasourceMapper.toEntity(aiGame), DomainDatasourceMapper.toEntity(mpGame)));

    List<Game> myGames = gameService.getMyGames(player1);

    assertEquals(2, myGames.size());
    // проверяем что чужие игры не попали
    assertFalse(
        myGames.stream()
            .anyMatch(g -> !player1.equals(g.getPlayer1Id()) && !player1.equals(g.getPlayer2Id())));
  }

  // ─── getGame ─────────────────────────────────────────────────────────────

  @Test
  void testGetGame_returnsCorrectGame() {
    UUID userId = UUID.randomUUID();
    Game expected = new Game(userId, GameMode.MULTIPLAYER);
    when(gameRepository.findById(expected.getUuid()))
        .thenReturn(Optional.of(DomainDatasourceMapper.toEntity(expected)));

    Game found = gameService.getGame(expected.getUuid());

    assertEquals(expected.getUuid(), found.getUuid());
    assertEquals(userId, found.getPlayer1Id());
  }

  @Test
  void testGetGame_notFound_throwsException() {
    UUID unknown = UUID.randomUUID();
    when(gameRepository.findById(unknown)).thenReturn(Optional.empty());

    assertThrows(Exception.class, () -> gameService.getGame(unknown));
  }

  // ─── processMove security ────────────────────────────────────────────────

  @Test
  void testProcessMove_throwsWhenNotYourTurn() {
    UUID player1 = UUID.randomUUID();
    UUID player2 = UUID.randomUUID();

    // Создаём игру в состоянии PLAYING, ход player1
    Game playing = new Game(player1, GameMode.MULTIPLAYER);
    playing.joinPlayer2(player2); // переводит в PLAYING, currentTurn = player1

    GameEntity entity = DomainDatasourceMapper.toEntity(playing);
    when(gameRepository.findById(playing.getUuid())).thenReturn(Optional.of(entity));

    // player2 пытается сходить
    Game userGame =
        Game.builder()
            .uuid(playing.getUuid())
            .gameField(playing.getGameField())
            .status(GameState.PLAYING)
            .build();
    userGame.getGameField().getField()[0][0] = 1;

    assertThrows(
        IllegalArgumentException.class,
        () -> gameService.processMove(playing.getUuid(), userGame, player2));
  }

  @Test
  void testProcessMove_throwsWhenNotParticipant() {
    UUID player1 = UUID.randomUUID();
    UUID stranger = UUID.randomUUID();

    Game game = new Game();
    game.setPlayer1Id(player1);
    game.setCurrentTurnPlayerId(player1);

    when(gameRepository.findById(game.getUuid()))
        .thenReturn(Optional.of(DomainDatasourceMapper.toEntity(game)));

    Game userGame = copyGame(game);
    userGame.makeMove(0, 0);

    assertThrows(
        IllegalArgumentException.class,
        () -> gameService.processMove(game.getUuid(), userGame, stranger));
  }

  // ─── helpers ─────────────────────────────────────────────────────────────

  private Game copyGame(Game game) {
    Game copy = new Game();
    int[][] original = game.getGameField().getField();
    for (int y = 0; y < GameField.FIELD_HEIGHT; y++)
      for (int x = 0; x < GameField.FIELD_WIDTH; x++)
        if (original[y][x] != CellType.VOID.getValue())
          copy.getGameField().getField()[y][x] = original[y][x];
    return copy;
  }
}
