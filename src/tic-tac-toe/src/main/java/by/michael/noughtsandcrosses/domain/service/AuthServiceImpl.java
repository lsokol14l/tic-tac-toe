package by.michael.noughtsandcrosses.domain.service;

import by.michael.noughtsandcrosses.domain.model.User;
import by.michael.noughtsandcrosses.web.model.SignUpRequest;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

public class AuthServiceImpl implements AuthService {

  private final UserService userService;

  public AuthServiceImpl(UserService userService) {
    this.userService = userService;
  }

  @Override
  public boolean register(SignUpRequest request) {
    try {
      userService.register(request.getLogin(), request.getPassword());
      return true;
    } catch (IllegalArgumentException e) {
      // Пользователь с таким логином уже существует
      return false;
    }
  }

  @Override
  public Optional<UUID> authorize(String authorizationHeader) {
    if (authorizationHeader == null || !authorizationHeader.startsWith("Basic ")) {
      return Optional.empty();
    }

    // Извлекаем base64 часть: "Basic dGVzdDoxMjM=" -> "dGVzdDoxMjM="
    String base64Credentials = authorizationHeader.substring("Basic ".length()).trim();

    try {
      // Декодируем base64: "dGVzdDoxMjM=" -> "test:123"
      byte[] decodedBytes = Base64.getDecoder().decode(base64Credentials);
      String credentials = new String(decodedBytes, StandardCharsets.UTF_8);

      // Парсим "login:password"
      String[] parts = credentials.split(":", 2);

      if (parts.length != 2) {
        return Optional.empty();
      }

      String login = parts[0];
      String password = parts[1];

      // Авторизуемся через UserService
      Optional<User> userOpt = userService.login(login, password);

      return userOpt.map(User::getId);

    } catch (IllegalArgumentException e) {
      // Невалидный base64
      return Optional.empty();
    }
  }
}
