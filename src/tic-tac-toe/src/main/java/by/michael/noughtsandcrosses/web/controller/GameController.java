package by.michael.noughtsandcrosses.web.controller;

import by.michael.noughtsandcrosses.domain.model.Game;
import by.michael.noughtsandcrosses.domain.model.GameMode;
import by.michael.noughtsandcrosses.domain.service.GameService;
import by.michael.noughtsandcrosses.domain.service.UserService;
import by.michael.noughtsandcrosses.web.mapper.DomainWebMapper;
import by.michael.noughtsandcrosses.web.model.GameDto;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/game")
public class GameController {
  private final GameService gameService;

  public GameController(GameService gameService) {
    this.gameService = gameService;
  }

  /**
   * Создать новую игру с выбором режима
   *
   * @param mode режим игры: "ai", "multiplayer", "local"
   * @param userId UUID авторизованного пользователя
   */
  @PostMapping("/new/{mode}")
  public ResponseEntity<GameDto> createGameWithMode(
      @PathVariable String mode, @AuthenticationPrincipal UUID userId) {

    GameMode gameMode;
    try {
      gameMode = GameMode.valueOf(mode.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException(
          "Invalid game mode: " + mode + ". Use: ai, multiplayer, or local");
    }

    Game newGame = gameService.createNewGame(gameMode, userId);
    return ResponseEntity.ok(DomainWebMapper.toWeb(newGame));
  }

  @GetMapping("/{uuid}")
  public ResponseEntity<GameDto> getGame(@PathVariable UUID uuid) {
    return ResponseEntity.ok(DomainWebMapper.toWeb(gameService.getGame(uuid)));
  }

  @PostMapping("/{uuid}")
  public ResponseEntity<GameDto> makeMove(
      @PathVariable UUID uuid,
      @Valid @RequestBody GameDto gameDto,
      @AuthenticationPrincipal UUID userId) {
    if (!gameDto.getId().equals(uuid))
      throw new IllegalArgumentException("Game id and url id are not the same!");

    Game userGame = DomainWebMapper.toDomain(gameDto);
    Game updatedGame = gameService.processMove(uuid, userGame, userId);

    return ResponseEntity.ok(DomainWebMapper.toWeb(updatedGame));
  }

  @GetMapping("/available")
  public ResponseEntity<List<GameDto>> getAvailableGames() {
    List<GameDto> games =
        gameService.getAvailableGames().stream().map(DomainWebMapper::toWeb).toList();
    return ResponseEntity.ok(games);
  }

  @GetMapping("/my")
  public ResponseEntity<List<GameDto>> getMyGames(@AuthenticationPrincipal UUID uuid) {
    List<GameDto> games =
        gameService.getMyGames(uuid).stream().map(DomainWebMapper::toWeb).toList();
    return ResponseEntity.ok(games);
  }

  @PostMapping("/{uuid}/join")
  public ResponseEntity<GameDto> joinGame(
      @PathVariable UUID uuid, @AuthenticationPrincipal UUID userId) {
    Game game = gameService.joinGame(uuid, userId);
    return ResponseEntity.ok(DomainWebMapper.toWeb(game));
  }
}
