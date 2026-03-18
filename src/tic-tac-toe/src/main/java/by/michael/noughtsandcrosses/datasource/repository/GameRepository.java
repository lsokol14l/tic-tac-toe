package by.michael.noughtsandcrosses.datasource.repository;

import by.michael.noughtsandcrosses.datasource.model.GameEntity;
import by.michael.noughtsandcrosses.domain.model.Game;
import by.michael.noughtsandcrosses.domain.model.GameMode;
import by.michael.noughtsandcrosses.domain.model.GameState;
import java.util.List;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends CrudRepository<GameEntity, UUID> {
  List<GameEntity> findByStatusAndGameMode(GameState status, GameMode gameMode);

  List<GameEntity> findByPlayer1IdOrPlayer2Id(UUID player1Id, UUID player2Id);
}
