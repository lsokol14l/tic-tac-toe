package by.michael.noughtsandcrosses.di;

import by.michael.noughtsandcrosses.datasource.repository.GameRepository;
import by.michael.noughtsandcrosses.datasource.repository.UserRepository;
import by.michael.noughtsandcrosses.domain.service.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SpringConfiguration {

  @Bean
  public GameService gameService(GameRepository gameRepository) {
    return new GameServiceImpl(gameRepository);
  }

  @Bean
  public UserService userService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    return new UserServiceImpl(userRepository, passwordEncoder);
  }

  @Bean
  public AuthService authService(UserService userService) {
    return new AuthServiceImpl(userService);
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
