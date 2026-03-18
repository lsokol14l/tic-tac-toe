package by.michael.noughtsandcrosses.web.mapper;

import by.michael.noughtsandcrosses.domain.model.Game;
import by.michael.noughtsandcrosses.domain.model.GameField;
import by.michael.noughtsandcrosses.web.model.GameDto;
import by.michael.noughtsandcrosses.web.model.GameFieldDto;

public class DomainWebMapper {

  public static Game toDomain(GameDto gameDto) {
    return Game.builder()
        .gameField(new GameField(gameDto.getGameFieldDto().getGameField()))
        .uuid(gameDto.getId())
        .status(gameDto.getStatus())
        .currentPlayer(gameDto.getPlayer1Symbol() != null ? gameDto.getPlayer1Symbol() : null)
        .player1Id(gameDto.getPlayer1Id())
        .player2Id(gameDto.getPlayer2Id())
        .player1Symbol(gameDto.getPlayer1Symbol())
        .player2Symbol(gameDto.getPlayer2Symbol())
        .currentTurnPlayerId(gameDto.getCurrentTurnPlayerId())
        .winnerId(gameDto.getWinnerId())
        .build();
  }

  public static GameDto toWeb(Game game) {
    return GameDto.builder()
        .gameFieldDto(new GameFieldDto(game.getGameField().getField()))
        .id(game.getUuid())
        .status(game.getStatus())
        .gameMode(game.getGameMode())
        .player1Id(game.getPlayer1Id())
        .player2Id(game.getPlayer2Id())
        .player1Symbol(game.getPlayer1Symbol())
        .player2Symbol(game.getPlayer2Symbol())
        .currentTurnPlayerId(game.getCurrentTurnPlayerId())
        .winnerId(game.getWinnerId())
        .statusDescription(game.getStatusDescription())
        .build();
  }
}
