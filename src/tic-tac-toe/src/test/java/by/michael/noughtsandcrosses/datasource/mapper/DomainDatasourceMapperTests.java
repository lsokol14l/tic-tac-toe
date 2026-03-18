package by.michael.noughtsandcrosses.datasource.mapper;

import by.michael.noughtsandcrosses.datasource.model.FieldEntity;
import by.michael.noughtsandcrosses.datasource.model.GameEntity;
import by.michael.noughtsandcrosses.domain.model.*;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DomainDatasourceMapperTests {

  @Test
  void testToEntity_mapsAllFields() {
    UUID gameId = UUID.randomUUID();
    UUID player1 = UUID.randomUUID();
    UUID player2 = UUID.randomUUID();
    UUID winner = UUID.randomUUID();

    Game game =
        Game.builder()
            .uuid(gameId)
            .gameField(new GameField())
            .status(GameState.PLAYER_WIN)
            .currentPlayer(PlayerType.MAX)
            .gameMode(GameMode.MULTIPLAYER)
            .player1Id(player1)
            .player2Id(player2)
            .player1Symbol(PlayerType.MAX)
            .player2Symbol(PlayerType.MIN)
            .currentTurnPlayerId(player1)
            .winnerId(winner)
            .build();

    GameEntity entity = DomainDatasourceMapper.toEntity(game);

    assertEquals(gameId, entity.getUuid());
    assertEquals(GameState.PLAYER_WIN, entity.getStatus());
    assertEquals(GameMode.MULTIPLAYER, entity.getGameMode());
    assertEquals(player1, entity.getPlayer1Id());
    assertEquals(player2, entity.getPlayer2Id());
    assertEquals(PlayerType.MAX, entity.getPlayer1Symbol());
    assertEquals(PlayerType.MIN, entity.getPlayer2Symbol());
    assertEquals(player1, entity.getCurrentTurnPlayerId());
    assertEquals(winner, entity.getWinnerId());
  }

  @Test
  void testToDomain_mapsAllFields() {
    UUID gameId = UUID.randomUUID();
    UUID player1 = UUID.randomUUID();

    GameEntity entity =
        GameEntity.builder()
            .uuid(gameId)
            .gameField(new FieldEntity(new int[3][3]))
            .status(GameState.PLAYING)
            .currentPlayer(PlayerType.MAX)
            .gameMode(GameMode.AI)
            .player1Id(player1)
            .player1Symbol(PlayerType.MAX)
            .player2Symbol(PlayerType.MIN)
            .currentTurnPlayerId(player1)
            .build();

    Game game = DomainDatasourceMapper.toDomain(entity);

    assertEquals(gameId, game.getUuid());
    assertEquals(GameState.PLAYING, game.getStatus());
    assertEquals(GameMode.AI, game.getGameMode());
    assertEquals(player1, game.getPlayer1Id());
    assertEquals(player1, game.getCurrentTurnPlayerId());
  }

  @Test
  void testToEntity_thenToDomain_roundTrip() {
    UUID player1 = UUID.randomUUID();
    Game original = new Game(player1, GameMode.LOCAL);

    GameEntity entity = DomainDatasourceMapper.toEntity(original);
    Game restored = DomainDatasourceMapper.toDomain(entity);

    assertEquals(original.getUuid(), restored.getUuid());
    assertEquals(original.getStatus(), restored.getStatus());
    assertEquals(original.getGameMode(), restored.getGameMode());
    assertEquals(original.getPlayer1Id(), restored.getPlayer1Id());
  }
}
