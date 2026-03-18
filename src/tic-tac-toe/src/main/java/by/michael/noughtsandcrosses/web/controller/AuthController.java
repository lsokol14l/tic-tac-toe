package by.michael.noughtsandcrosses.web.controller;

import by.michael.noughtsandcrosses.domain.service.AuthService;
import by.michael.noughtsandcrosses.web.model.SignUpRequest;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  /**
   * Регистрация нового пользователя
   *
   * <p>POST /auth/register Body: { "login": "user123", "password": "password123" }
   *
   * <p>Response: 200 OK - { "success": true } 400 Bad Request - { "success": false }
   */
  @PostMapping("/register")
  public ResponseEntity<Map<String, Boolean>> register(@Valid @RequestBody SignUpRequest request) {
    boolean success = authService.register(request);

    if (success) {
      return ResponseEntity.ok(Map.of("success", true));
    } else {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("success", false));
    }
  }

  /**
   * Авторизация пользователя
   *
   * <p>POST /auth/login Header: Authorization: Basic base64(login:password)
   *
   * <p>Response: 200 OK - { "userId": "123e4567-e89b-12d3-a456-426614174000" } 401 Unauthorized -
   * пустое тело
   */
  @PostMapping("/login")
  public ResponseEntity<Map<String, String>> login(
      @RequestHeader("Authorization") String authorizationHeader) {

    return authService
        .authorize(authorizationHeader)
        .map(userId -> ResponseEntity.ok(Map.of("userId", userId.toString())))
        .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
  }
}
