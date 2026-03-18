package by.michael.noughtsandcrosses.web.controller;

import by.michael.noughtsandcrosses.domain.model.User;
import by.michael.noughtsandcrosses.domain.service.UserService;
import by.michael.noughtsandcrosses.web.model.UserDto;

import java.util.Optional;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {
  private UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/me")
  public ResponseEntity<UserDto> getMe(@AuthenticationPrincipal UUID userId) {
    return userService
        .getUserById(userId)
        .map(user -> ResponseEntity.ok(new UserDto(user.getId(), user.getLogin())))
        .orElse(ResponseEntity.notFound().build());
  }
}
