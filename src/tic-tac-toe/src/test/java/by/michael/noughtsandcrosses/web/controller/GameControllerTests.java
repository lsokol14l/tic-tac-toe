package by.michael.noughtsandcrosses.web.controller;

import by.michael.noughtsandcrosses.datasource.exception.GameNotFoundException;
import by.michael.noughtsandcrosses.domain.model.*;
import by.michael.noughtsandcrosses.domain.service.GameService;
import by.michael.noughtsandcrosses.web.mapper.DomainWebMapper;
import by.michael.noughtsandcrosses.web.model.GameDto;
import by.michael.noughtsandcrosses.web.model.GameFieldDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

class GameControllerTests {

  private GameService gameService;
  private GameController gameController;
  private UUID testUserId;
  private Game testGame;
  private GameDto testGameDto;

  @BeforeEach
  void setUp() {
    gameService = Mockito.mock(GameService.class);
    gameController = new GameController(gameService);

    testUserId = UUID.randomUUID();
    testGame =
        Game.builder()
            .uuid(UUID.randomUUID())
            .gameField(new GameField())
            .status(GameState.PLAYING)
            .currentPlayer(PlayerType.MAX)
            .gameMode(GameMode.AI)
            .player1Id(testUserId)
            .player1Symbol(PlayerType.MAX)
            .player2Symbol(PlayerType.MIN)
            .currentTurnPlayerId(testUserId)
            .build();

    testGameDto = DomainWebMapper.toWeb(testGame);
  }

  @Test
  void testCreateGame_ai_returns200() {
    when(gameService.createNewGame(GameMode.AI, testUserId)).thenReturn(testGame);

    ResponseEntity<GameDto> response = gameController.createGameWithMode("ai", testUserId);
    assertEquals(200, response.getStatusCode().value());
    assertEquals(GameState.PLAYING, response.getBody().getStatus());
    assertEquals(GameMode.AI, response.getBody().getGameMode());
  }

  @Test
  void testCreateGame_invalidMode_returns400() {
    Exception ex =
        assertThrows(
            IllegalArgumentException.class,
            () -> gameController.createGameWithMode("invalid", testUserId));
    assertTrue(ex.getMessage().contains("Invalid game mode"));
  }

  @Test
  void testCreateGame_multiplayer_returns200() {
    Game mpGame =
        Game.builder()
            .uuid(UUID.randomUUID())
            .gameField(new GameField())
            .status(GameState.WAITING_FOR_PLAYERS)
            .currentPlayer(PlayerType.MAX)
            .gameMode(GameMode.MULTIPLAYER)
            .player1Id(testUserId)
            .player1Symbol(PlayerType.MAX)
            .player2Symbol(PlayerType.MIN)
            .currentTurnPlayerId(testUserId)
            .build();

    when(gameService.createNewGame(GameMode.MULTIPLAYER, testUserId)).thenReturn(mpGame);

    ResponseEntity<GameDto> response = gameController.createGameWithMode("multiplayer", testUserId);
    assertEquals(200, response.getStatusCode().value());
    assertEquals(GameState.WAITING_FOR_PLAYERS, response.getBody().getStatus());
  }

  @Test
  void testGetGame_existing_returns200() {
    when(gameService.getGame(testGame.getUuid())).thenReturn(testGame);

    ResponseEntity<GameDto> response = gameController.getGame(testGame.getUuid());
    assertEquals(200, response.getStatusCode().value());
    assertEquals(testGame.getUuid(), response.getBody().getId());
  }

  @Test
  void testGetGame_notFound_returns400() {
    UUID unknown = UUID.randomUUID();
    when(gameService.getGame(unknown)).thenThrow(new GameNotFoundException(unknown.toString()));

    assertThrows(GameNotFoundException.class, () -> gameController.getGame(unknown));
  }

  @Test
  void testMakeMove_notYourTurn_returns400() {
    UUID gameId = testGame.getUuid();
    when(gameService.processMove(any(), any(), any()))
        .thenThrow(new IllegalArgumentException("It is not your turn"));

    GameDto dto =
        GameDto.builder()
            .id(gameId)
            .gameFieldDto(new GameFieldDto(new int[][] {{1, 0, 0}, {0, 0, 0}, {0, 0, 0}}))
            .status(GameState.PLAYING)
            .build();

    Exception ex =
        assertThrows(
            IllegalArgumentException.class, () -> gameController.makeMove(gameId, dto, testUserId));
    assertTrue(ex.getMessage().contains("not your turn"));
  }

  @Test
  void testGetAvailableGames_returns200() {
    when(gameService.getAvailableGames()).thenReturn(List.of(testGame));

    ResponseEntity<List<GameDto>> response = gameController.getAvailableGames();
    assertEquals(200, response.getStatusCode().value());
    assertEquals(1, response.getBody().size());
  }

  @Test
  void testGetMyGames_returns200() {
    when(gameService.getMyGames(testUserId)).thenReturn(List.of(testGame));

    ResponseEntity<List<GameDto>> response = gameController.getMyGames(testUserId);
    assertEquals(200, response.getStatusCode().value());
    assertEquals(1, response.getBody().size());
  }
}
