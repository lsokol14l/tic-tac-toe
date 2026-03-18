package by.michael.noughtsandcrosses.datasource.mapper;

import by.michael.noughtsandcrosses.datasource.model.FieldEntity;
import by.michael.noughtsandcrosses.datasource.model.GameEntity;
import by.michael.noughtsandcrosses.datasource.model.UserEntity;
import by.michael.noughtsandcrosses.domain.model.*;

public class DomainDatasourceMapper {

  // ===== USER MAPPING =====

  public static User toDomain(UserEntity userEntity) {
    return User.builder()
        .id(userEntity.getId())
        .login(userEntity.getLogin())
        .passwordHash(userEntity.getPasswordHash())
        .build();
  }

  public static UserEntity toEntity(User user) {
    return UserEntity.builder()
        .id(user.getId())
        .login(user.getLogin())
        .passwordHash(user.getPasswordHash())
        .build();
  }

  // ===== GAME MAPPING =====

  public static GameEntity toEntity(Game game) {
    return GameEntity.builder()
            .uuid(game.getUuid())
            .gameField(new FieldEntity(game.getGameField().getField()))
            .status(game.getStatus())
            .currentPlayer(game.getCurrentPlayer())
            .gameMode(game.getGameMode())
            .player1Id(game.getPlayer1Id())
            .player2Id(game.getPlayer2Id())
            .player1Symbol(game.getPlayer1Symbol())
            .player2Symbol(game.getPlayer2Symbol())
            .currentTurnPlayerId(game.getCurrentTurnPlayerId())
            .winnerId(game.getWinnerId())
            .build();
  }

  public static Game toDomain(GameEntity gameEntity) {
    return Game.builder()
            .gameField(new GameField(gameEntity.getGameField().getField()))
            .uuid(gameEntity.getUuid())
            .status(gameEntity.getStatus())
            .currentPlayer(gameEntity.getCurrentPlayer())
            .gameMode(gameEntity.getGameMode())
            .player1Id(gameEntity.getPlayer1Id())
            .player2Id(gameEntity.getPlayer2Id())
            .player1Symbol(gameEntity.getPlayer1Symbol())
            .player2Symbol(gameEntity.getPlayer2Symbol())
            .currentTurnPlayerId(gameEntity.getCurrentTurnPlayerId())
            .winnerId(gameEntity.getWinnerId())
            .build();
  }
}
