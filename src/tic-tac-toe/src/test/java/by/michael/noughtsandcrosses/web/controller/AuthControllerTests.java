package by.michael.noughtsandcrosses.web.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import by.michael.noughtsandcrosses.domain.service.AuthService;
import by.michael.noughtsandcrosses.web.model.SignUpRequest;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

class AuthControllerTests {

  private AuthService authService;
  private AuthController authController;

  @BeforeEach
  void setUp() {
    authService = Mockito.mock(AuthService.class);
    authController = new AuthController(authService);
  }

  @Test
  void testRegister_success_returns200() {
    when(authService.register(any())).thenReturn(true);

    SignUpRequest request = new SignUpRequest();
    request.setLogin("newuser@mail.ru");
    request.setPassword("password123");

    ResponseEntity<Map<String, Boolean>> response = authController.register(request);
    assertEquals(200, response.getStatusCode().value());
    assertTrue((response.getBody()).containsKey("success"));
    assertEquals(true, (response.getBody()).get("success"));
  }

  @Test
  void testRegister_shortPassword_returns400() {
    SignUpRequest request = new SignUpRequest();
    request.setLogin("user@mail.ru");
    request.setPassword("123"); // меньше 6 символов

    ResponseEntity<?> response = authController.register(request);
    assertEquals(400, response.getStatusCode().value());
  }

  @Test
  void testRegister_blankLogin_returns400() {
    SignUpRequest request = new SignUpRequest();
    request.setLogin("");
    request.setPassword("password123");

    ResponseEntity<?> response = authController.register(request);
    assertEquals(400, response.getStatusCode().value());
  }

  @Test
  void testLogin_validCredentials_returns200WithUserId() {
    UUID userId = UUID.randomUUID();
    when(authService.authorize(any())).thenReturn(Optional.of(userId));

    // Предполагается, что login и password передаются как параметры
    String header = "Basic " + Base64.getEncoder().encodeToString("user:pass".getBytes());
    ResponseEntity<Map<String, String>> response = authController.login(header);
    assertEquals(200, response.getStatusCode().value());
    assertEquals(userId.toString(), (response.getBody()).get("userId"));
  }

  @Test
  void testLogin_invalidCredentials_returns401() {
    when(authService.authorize(any())).thenReturn(Optional.empty());

    String header = "Basic " + Base64.getEncoder().encodeToString("user:wrong".getBytes());
    ResponseEntity<?> response = authController.login(header);
    assertEquals(401, response.getStatusCode().value());
  }
}
