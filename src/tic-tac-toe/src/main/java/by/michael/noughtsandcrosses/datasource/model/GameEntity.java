package by.michael.noughtsandcrosses.datasource.model;

import by.michael.noughtsandcrosses.domain.model.GameMode;
import by.michael.noughtsandcrosses.domain.model.GameState;
import by.michael.noughtsandcrosses.domain.model.PlayerType;
import jakarta.persistence.*;
import java.util.Objects;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "games")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameEntity {
  @Embedded private FieldEntity gameField;

  @Id
  @Column(name = "game_id", nullable = false)
  private UUID uuid;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private GameState status;

  @Enumerated(EnumType.STRING)
  @Column(name = "player", nullable = false)
  private PlayerType currentPlayer;

  // ===== Мультиплеер поля =====
  @Enumerated(EnumType.STRING)
  @Column(name = "game_mode", nullable = false)
  private GameMode gameMode;

  @Column(name = "player1_id")
  private UUID player1Id;

  @Column(name = "player2_id")
  private UUID player2Id;

  @Enumerated(EnumType.STRING)
  @Column(name = "player1_symbol")
  private PlayerType player1Symbol;

  @Enumerated(EnumType.STRING)
  @Column(name = "player2_symbol")
  private PlayerType player2Symbol;

  @Column(name = "current_turn_player_id")
  private UUID currentTurnPlayerId;

  @Column(name = "winner_id")
  private UUID winnerId;

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    GameEntity that = (GameEntity) o;
    return Objects.equals(uuid, that.uuid);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(uuid);
  }
}
