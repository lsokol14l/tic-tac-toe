package by.michael.noughtsandcrosses.web.config;

import by.michael.noughtsandcrosses.domain.service.AuthService;
import by.michael.noughtsandcrosses.web.filter.AuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private final AuthService authService;

  public SecurityConfig(AuthService authService) {
    this.authService = authService;
  }

  /**
   * Bean для настройки SecurityFilterChain
   *
   * <p>Конфигурация: - Разрешён доступ без авторизации к /auth/register и /auth/login - Для всех
   * остальных endpoint'ов требуется авторизация - Используется AuthFilter для валидации Basic Auth
   */
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
        .formLogin(AbstractHttpConfigurer::disable)
        .httpBasic(AbstractHttpConfigurer::disable)
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(
            auth ->
                auth
                    // Публичные endpoint'ы - авторизация НЕ требуется
                    .requestMatchers("/", "/profile", "/favicon.ico")
                    .permitAll()
                    .requestMatchers("/js/**")
                    .permitAll() // Все JS файлы
                    .requestMatchers("/auth/**")
                    .permitAll()

                    // Все остальные endpoint'ы требуют авторизации
                    .anyRequest()
                    .authenticated())

        // Добавляем AuthFilter для проверки Basic Auth
        .addFilterBefore(new AuthFilter(authService), UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }
}
