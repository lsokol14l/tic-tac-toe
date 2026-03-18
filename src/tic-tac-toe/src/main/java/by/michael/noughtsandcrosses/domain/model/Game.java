package by.michael.noughtsandcrosses.domain.model;

import by.michael.noughtsandcrosses.domain.exception.GameIsAlredyOverException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class Game {
  private GameField gameField;
  private UUID uuid;
  private GameState status;
  private PlayerType currentPlayer;

  // ===== Мультиплеер поля =====
  private GameMode gameMode;

  /** UUID игрока 1 (создатель игры) */
  private UUID player1Id;

  /** UUID игрока 2 (второй игрок) */
  private UUID player2Id;

  /** Символ (X/O), которым играет игрок 1 */
  private PlayerType player1Symbol;

  /** Символ (X/O), которым играет игрок 2 */
  private PlayerType player2Symbol;

  /** UUID игрока, который должен сделать ход сейчас */
  private UUID currentTurnPlayerId;

  /** UUID победителя (если игра закончена победой) */
  private UUID winnerId;

  /** Конструктор для одиночной игры против AI */
  public Game() {
    this.gameField = new GameField();
    this.uuid = UUID.randomUUID();
    this.status = GameState.PLAYING;
    this.currentPlayer = PlayerType.MAX;
    this.gameMode = GameMode.AI;
    this.player1Id = null; // Будет установлен позже в сервисе
    this.player2Id = null; // Бот не имеет UUID
    this.player1Symbol = PlayerType.MAX; // Игрок играет за X
    this.player2Symbol = PlayerType.MIN; // Бот играет за O
    this.currentTurnPlayerId = null; // Будет установлен после setPlayer1Id
    this.winnerId = null;
  }

  /**
   * Конструктор для мультиплеерной или локальной игры
   *
   * @param player1Id UUID создателя игры
   * @param gameMode режим игры
   */
  public Game(UUID player1Id, GameMode gameMode) {
    this.gameField = new GameField();
    this.uuid = UUID.randomUUID();
    this.gameMode = gameMode;
    this.currentPlayer = PlayerType.MAX;
    this.player1Id = player1Id;
    this.player2Id = null;
    this.player1Symbol = PlayerType.MAX;
    this.player2Symbol = PlayerType.MIN;
    this.currentTurnPlayerId = player1Id;
    this.winnerId = null;

    // Для мультиплеера ждем второго игрока, для локального сразу играем
    if (gameMode == GameMode.MULTIPLAYER) {
      this.status = GameState.WAITING_FOR_PLAYERS;
    } else {
      this.status = GameState.PLAYING;
    }
  }

  /** Проверяет, играет ли игрок против AI */
  public boolean isAIGame() {
    return gameMode == GameMode.AI;
  }

  /** Проверяет, мультиплеер ли это */
  public boolean isMultiplayer() {
    return gameMode == GameMode.MULTIPLAYER;
  }

  /** Проверяет, локальная ли игра */
  public boolean isLocalGame() {
    return gameMode == GameMode.LOCAL;
  }

  /**
   * Второй игрок присоединяется к игре
   *
   * @param player2Id UUID второго игрока
   */
  public void joinPlayer2(UUID player2Id) {
    if (this.player2Id != null) {
      throw new IllegalStateException("Игра уже заполнена 2 игроками");
    }

    if (this.player1Id.equals(player2Id)) {
      throw new IllegalArgumentException("Игрок не может играть против себя!");
    }

    this.player2Id = player2Id;
    this.status = GameState.PLAYING;
  }

  /** Проверяет, является ли данный игрок участником этой игры */
  public boolean isPlayerInGame(UUID playerId) {
    return playerId.equals(player1Id) || playerId.equals(player2Id);
  }

  /** Проверяет, чей сейчас ход */
  public boolean isPlayerTurn(UUID playerId) {
    return playerId.equals(currentTurnPlayerId);
  }

  /**
   * Получить символ (X/O), которым играет данный игрок
   *
   * @param playerId UUID игрока
   * @return PlayerType.MAX (X) или PlayerType.MIN (O)
   */
  public PlayerType getPlayerSymbol(UUID playerId) {
    if (playerId.equals(player1Id)) {
      return player1Symbol;
    } else if (playerId.equals(player2Id)) {
      return player2Symbol;
    }
    throw new IllegalArgumentException("Player " + playerId + " is not in this game");
  }

  /**
   * Получить UUID игрока по его символу
   *
   * @param symbol PlayerType.MAX (X) или PlayerType.MIN (O)
   * @return UUID игрока
   */
  public UUID getPlayerIdBySymbol(PlayerType symbol) {
    if (symbol.equals(player1Symbol)) {
      return player1Id;
    } else if (symbol.equals(player2Symbol)) {
      return player2Id;
    }
    return null;
  }

  public void makeMove(int x, int y) {
    if (isGameOver()) {
      throw new GameIsAlredyOverException("Game is over!");
    }

    gameField.makeMove(x, y, currentPlayer);
    updateGameState();
    switchPlayer();
  }

  private void updateGameState() {
    // Проверяем победителя
    PlayerType winnerSymbol = null;

    if (gameField.hasWinner(PlayerType.MIN)) {
      winnerSymbol = PlayerType.MIN;
    } else if (gameField.hasWinner(PlayerType.MAX)) {
      winnerSymbol = PlayerType.MAX;
    }

    // Если есть победитель
    if (winnerSymbol != null) {
      status = GameState.PLAYER_WIN;
      winnerId = getPlayerIdBySymbol(winnerSymbol);
    }
    // Если доска заполнена и нет победителя - ничья
    else if (gameField.isFull()) {
      status = GameState.DRAW;
      winnerId = null;
    }
    // Иначе игра продолжается
    else {
      status = GameState.PLAYING;
    }
  }

  /** Проверяет, может ли игра продолжаться */
  public boolean canContinue() {
    return status == GameState.PLAYING || status == GameState.WAITING_FOR_PLAYERS;
  }

  /** Получить читаемое описание текущего состояния */
  public String getStatusDescription() {
    return switch (status) {
      case WAITING_FOR_PLAYERS -> "Ожидание второго игрока";
      case PLAYING -> "Игра идёт";
      case DRAW -> "Ничья";
      case PLAYER_WIN -> {
        if (winnerId != null) {
          yield "Победил игрок " + winnerId;
        }
        yield "Игра окончена победой";
      }
      default -> "Неизвестное состояние";
    };
  }

  private void switchPlayer() {
    if (status == GameState.PLAYING) {
      currentPlayer = (currentPlayer == PlayerType.MIN) ? PlayerType.MAX : PlayerType.MIN;

      // Переключаем ход между игроками
      if ((isLocalGame() || isMultiplayer()) && player1Id != null && player2Id != null) {
        currentTurnPlayerId = currentTurnPlayerId.equals(player1Id) ? player2Id : player1Id;
      }
    }
  }

  public int[] getNextMove() {
    if (isGameOver()) {
      throw new GameIsAlredyOverException("Game is over!");
    }
    return gameField.getBestMove(currentPlayer);
  }

  public boolean isGameOver() {
    return status != GameState.PLAYING && status != GameState.WAITING_FOR_PLAYERS;
  }

  /** Проверяет, ожидает ли игра второго игрока */
  public boolean isWaitingForPlayers() {
    return status == GameState.WAITING_FOR_PLAYERS;
  }
}
