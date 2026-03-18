package by.michael.noughtsandcrosses.domain.service;

import by.michael.noughtsandcrosses.domain.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserService {

  /**
   * Регистрация нового пользователя
   *
   * @param login логин
   * @param password пароль (в открытом виде)
   * @return зарегистрированный пользователь
   */
  User register(String login, String password);

  /**
   * Авторизация пользователя
   *
   * @param login логин
   * @param password пароль
   * @return Optional с User, если авторизация успешна
   */
  Optional<User> login(String login, String password);

  /** Получить пользователя по ID */
  Optional<User> getUserById(UUID userId);
}
