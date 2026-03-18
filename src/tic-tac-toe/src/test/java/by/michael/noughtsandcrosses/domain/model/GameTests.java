package by.michael.noughtsandcrosses.domain.model;

import by.michael.noughtsandcrosses.domain.exception.GameIsAlredyOverException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GameTests {

  @Test
  void testNewAIGame_initialState() {
    UUID userId = UUID.randomUUID();
    Game game = new Game();
    game.setPlayer1Id(userId);
    game.setCurrentTurnPlayerId(userId);

    assertEquals(GameState.PLAYING, game.getStatus());
    assertEquals(PlayerType.MAX, game.getCurrentPlayer());
    assertEquals(GameMode.AI, game.getGameMode());
    assertEquals(PlayerType.MAX, game.getPlayer1Symbol());
    assertEquals(PlayerType.MIN, game.getPlayer2Symbol());
    assertEquals(userId, game.getCurrentTurnPlayerId());
    assertNull(game.getWinnerId());
  }

  @Test
  void testNewMultiplayerGame_statusIsWaitingForPlayers() {
    UUID userId = UUID.randomUUID();
    Game game = new Game(userId, GameMode.MULTIPLAYER);

    assertEquals(GameState.WAITING_FOR_PLAYERS, game.getStatus());
    assertEquals(userId, game.getPlayer1Id());
    assertNull(game.getPlayer2Id());
  }

  @Test
  void testNewLocalGame_statusIsPlaying() {
    UUID userId = UUID.randomUUID();
    Game game = new Game(userId, GameMode.LOCAL);

    assertEquals(GameState.PLAYING, game.getStatus());
  }

  @Test
  void testJoinPlayer2_setsPlayer2AndStartsGame() {
    UUID player1 = UUID.randomUUID();
    UUID player2 = UUID.randomUUID();
    Game game = new Game(player1, GameMode.MULTIPLAYER);

    game.joinPlayer2(player2);

    assertEquals(player2, game.getPlayer2Id());
    assertEquals(GameState.PLAYING, game.getStatus());
    assertEquals(player1, game.getCurrentTurnPlayerId());
  }

  @Test
  void testMakeMove_switchesCurrentPlayer() {
    Game game = new Game();
    assertEquals(PlayerType.MAX, game.getCurrentPlayer());

    game.makeMove(0, 0);

    assertEquals(PlayerType.MIN, game.getCurrentPlayer());
  }

  @Test
  void testMakeMove_multiplayer_switchesTurnPlayerId() {
    UUID player1 = UUID.randomUUID();
    UUID player2 = UUID.randomUUID();
    Game game = new Game(player1, GameMode.MULTIPLAYER);
    game.joinPlayer2(player2);

    assertEquals(player1, game.getCurrentTurnPlayerId());

    game.makeMove(0, 0);

    assertEquals(player2, game.getCurrentTurnPlayerId());

    game.makeMove(1, 1);

    assertEquals(player1, game.getCurrentTurnPlayerId());
  }

  @Test
  void testMakeMove_xWinsFirstRow_statusIsPlayerWin() {
    UUID player1 = UUID.randomUUID();
    Game game = new Game();
    game.setPlayer1Id(player1);
    game.setCurrentTurnPlayerId(player1);

    // X: (0,0), O: (1,0), X: (0,1), O: (1,1), X: (0,2) → X wins top row
    game.makeMove(0, 0); // X
    game.makeMove(1, 0); // O
    game.makeMove(0, 1); // X
    game.makeMove(1, 1); // O
    game.makeMove(0, 2); // X wins

    assertEquals(GameState.PLAYER_WIN, game.getStatus());
    assertEquals(player1, game.getWinnerId());
  }

  @Test
  void testMakeMove_draw_statusIsDraw() {
    Game game = new Game();

    // Ничья: X O X / X X O / O X O
    game.makeMove(0, 0); // X
    game.makeMove(1, 0); // O
    game.makeMove(2, 0); // X
    game.makeMove(0, 1); // O
    game.makeMove(2, 1); // X
    game.makeMove(0, 2); // O
    game.makeMove(1, 1); // X
    game.makeMove(2, 2); // O
    game.makeMove(1, 2); // X

    assertEquals(GameState.DRAW, game.getStatus());
  }

  @Test
  void testMakeMove_throwsWhenGameAlreadyOver() {
    Game game = new Game();
    game.makeMove(0, 0);
    game.makeMove(1, 0);
    game.makeMove(0, 1);
    game.makeMove(1, 1);
    game.makeMove(0, 2); // X wins

    assertThrows(GameIsAlredyOverException.class, () -> game.makeMove(2, 2));
  }

  @Test
  void testIsPlayerInGame_returnsCorrect() {
    UUID player1 = UUID.randomUUID();
    UUID player2 = UUID.randomUUID();
    UUID stranger = UUID.randomUUID();
    Game game = new Game(player1, GameMode.MULTIPLAYER);
    game.joinPlayer2(player2);

    assertTrue(game.isPlayerInGame(player1));
    assertTrue(game.isPlayerInGame(player2));
    assertFalse(game.isPlayerInGame(stranger));
  }

  @Test
  void testGetPlayerSymbol_returnsCorrectSymbol() {
    UUID player1 = UUID.randomUUID();
    UUID player2 = UUID.randomUUID();
    Game game = new Game(player1, GameMode.MULTIPLAYER);
    game.joinPlayer2(player2);

    assertEquals(PlayerType.MAX, game.getPlayerSymbol(player1));
    assertEquals(PlayerType.MIN, game.getPlayerSymbol(player2));
  }

  @Test
  void testIsPlayerTurn_returnsCorrect() {
    UUID player1 = UUID.randomUUID();
    UUID player2 = UUID.randomUUID();
    Game game = new Game(player1, GameMode.MULTIPLAYER);
    game.joinPlayer2(player2);

    assertTrue(game.isPlayerTurn(player1));
    assertFalse(game.isPlayerTurn(player2));
  }

  @Test
  void testIsGameOver_falseWhenPlaying() {
    Game game = new Game();
    assertFalse(game.isGameOver());
  }

  @Test
  void testIsGameOver_trueAfterWin() {
    Game game = new Game();
    game.makeMove(0, 0);
    game.makeMove(1, 0);
    game.makeMove(0, 1);
    game.makeMove(1, 1);
    game.makeMove(0, 2);

    assertTrue(game.isGameOver());
  }
}
