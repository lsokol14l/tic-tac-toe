package by.michael.noughtsandcrosses.domain.service;

import by.michael.noughtsandcrosses.web.model.SignUpRequest;
import java.util.Optional;
import java.util.UUID;

public interface AuthService {

  /**
   * Регистрация пользователя
   *
   * @param request данные для регистрации (логин + пароль)
   * @return true если регистрация успешна, false если логин занят
   */
  boolean register(SignUpRequest request);

  /**
   * Авторизация по Basic Auth заголовку
   *
   * @param authorizationHeader заголовок вида "Basic base64(login:password)"
   * @return UUID пользователя, если авторизация успешна
   */
  Optional<UUID> authorize(String authorizationHeader);
}
