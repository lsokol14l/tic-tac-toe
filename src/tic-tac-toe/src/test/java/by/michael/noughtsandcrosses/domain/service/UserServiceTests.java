package by.michael.noughtsandcrosses.domain.service;

import by.michael.noughtsandcrosses.datasource.mapper.DomainDatasourceMapper;
import by.michael.noughtsandcrosses.datasource.model.UserEntity;
import by.michael.noughtsandcrosses.datasource.repository.UserRepository;
import by.michael.noughtsandcrosses.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTests {

  @Mock private UserRepository userRepository;

  private UserService userService;
  private PasswordEncoder passwordEncoder;

  @BeforeEach
  void setUp() {
    passwordEncoder = new BCryptPasswordEncoder();
    userService = new UserServiceImpl(userRepository, passwordEncoder);
  }

  @Test
  void testRegister_success() {
    when(userRepository.existsByLogin("alice")).thenReturn(false);
    when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

    User user = userService.register("alice", "password123");

    assertNotNull(user);
    assertEquals("alice", user.getLogin());
    assertNotNull(user.getId());
    verify(userRepository).save(any());
  }

  @Test
  void testRegister_duplicateLogin_throwsException() {
    when(userRepository.existsByLogin("alice")).thenReturn(true);

    assertThrows(
        IllegalArgumentException.class, () -> userService.register("alice", "password123"));

    verify(userRepository, never()).save(any());
  }

  @Test
  void testLogin_correctPassword_returnsUser() {
    UUID userId = UUID.randomUUID();
    String hash = passwordEncoder.encode("secret");
    UserEntity entity = UserEntity.builder().id(userId).login("bob").passwordHash(hash).build();

    when(userRepository.findByLogin("bob")).thenReturn(Optional.of(entity));

    Optional<User> result = userService.login("bob", "secret");

    assertTrue(result.isPresent());
    assertEquals("bob", result.get().getLogin());
  }

  @Test
  void testLogin_wrongPassword_returnsEmpty() {
    UUID userId = UUID.randomUUID();
    String hash = passwordEncoder.encode("correctPassword");
    UserEntity entity = UserEntity.builder().id(userId).login("bob").passwordHash(hash).build();

    when(userRepository.findByLogin("bob")).thenReturn(Optional.of(entity));

    Optional<User> result = userService.login("bob", "wrongPassword");

    assertTrue(result.isEmpty());
  }

  @Test
  void testLogin_unknownUser_returnsEmpty() {
    when(userRepository.findByLogin(anyString())).thenReturn(Optional.empty());

    Optional<User> result = userService.login("unknown", "password");

    assertTrue(result.isEmpty());
  }

  @Test
  void testGetUserById_found() {
    UUID userId = UUID.randomUUID();
    UserEntity entity = UserEntity.builder().id(userId).login("carol").passwordHash("hash").build();

    when(userRepository.findById(userId)).thenReturn(Optional.of(entity));

    Optional<User> result = userService.getUserById(userId);

    assertTrue(result.isPresent());
    assertEquals("carol", result.get().getLogin());
    assertEquals(userId, result.get().getId());
  }

  @Test
  void testGetUserById_notFound_returnsEmpty() {
    when(userRepository.findById(any())).thenReturn(Optional.empty());

    Optional<User> result = userService.getUserById(UUID.randomUUID());

    assertTrue(result.isEmpty());
  }
}
