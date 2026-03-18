package by.michael.noughtsandcrosses.domain.service;

import by.michael.noughtsandcrosses.datasource.mapper.DomainDatasourceMapper;
import by.michael.noughtsandcrosses.datasource.model.UserEntity;
import by.michael.noughtsandcrosses.datasource.repository.UserRepository;
import by.michael.noughtsandcrosses.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public User register(String login, String password) {
    if (userRepository.existsByLogin(login)) {
      throw new IllegalArgumentException("User with login '" + login + "' already exists");
    }

    User newUser = new User();
    newUser.setId(UUID.randomUUID());
    newUser.setLogin(login);
    newUser.setPasswordHash(passwordEncoder.encode(password));
    UserEntity save = userRepository.save(DomainDatasourceMapper.toEntity(newUser));
    return DomainDatasourceMapper.toDomain(save);
  }

  @Override
  public Optional<User> login(String login, String password) {
    Optional<UserEntity> userOpt = userRepository.findByLogin(login);

    if (userOpt.isEmpty()) {
      return Optional.empty();
    }

    UserEntity user = userOpt.get();

    if (passwordEncoder.matches(password, user.getPasswordHash())) {
      return Optional.of(DomainDatasourceMapper.toDomain(user));
    }

    return Optional.empty();
  }

  @Override
  public Optional<User> getUserById(UUID userId) {
    Optional<UserEntity> byId = userRepository.findById(userId);
    return byId.map(DomainDatasourceMapper::toDomain);
  }
}
