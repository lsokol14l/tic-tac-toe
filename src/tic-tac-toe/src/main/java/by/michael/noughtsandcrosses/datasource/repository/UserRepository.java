package by.michael.noughtsandcrosses.datasource.repository;

import by.michael.noughtsandcrosses.datasource.model.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends CrudRepository<UserEntity, UUID> {

  Optional<UserEntity> findByLogin(String login);

  boolean existsByLogin(String login);
}
