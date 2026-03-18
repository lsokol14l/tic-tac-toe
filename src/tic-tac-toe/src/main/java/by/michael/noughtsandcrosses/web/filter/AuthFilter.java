package by.michael.noughtsandcrosses.web.filter;

import by.michael.noughtsandcrosses.domain.service.AuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

/**
 * Фильтр для проверки Basic Authentication
 *
 * <p>Валидирует логин и пароль через AuthService. Если валидация прошла успешно - выполняет запрос.
 * Если провалена - возвращает 401 Unauthorized.
 */
public class AuthFilter extends GenericFilterBean {

  private final AuthService authService;

  public AuthFilter(AuthService authService) {
    this.authService = authService;
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    HttpServletRequest httpRequest = (HttpServletRequest) request;
    HttpServletResponse httpResponse = (HttpServletResponse) response;

    String requestPath = httpRequest.getRequestURI();

    // Пропускаем публичные endpoint'ы (они настроены в SecurityConfig как permitAll)
    if (isPublicEndpoint(requestPath)) {
      chain.doFilter(request, response);
      return;
    }

    // Получаем Authorization заголовок
    String authorizationHeader = httpRequest.getHeader("Authorization");

    // Валидируем логин и пароль через AuthService
    var userIdOpt = authService.authorize(authorizationHeader);

    if (userIdOpt.isPresent()) {
      UUID userId = userIdOpt.get();

      UsernamePasswordAuthenticationToken authentication =
          new UsernamePasswordAuthenticationToken(
              userId,
              null, // credentials (пароль не храним)
              Collections.emptyList());

      SecurityContextHolder.getContext().setAuthentication(authentication);

      chain.doFilter(request, response);

      SecurityContextHolder.clearContext();
    } else {
      // Валидация провалена — возвращаем 401 Unauthorized
      httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      httpResponse.setContentType("application/json");
      httpResponse.getWriter().write("{\"error\": \"Unauthorized\"}");
    }
  }

  /**
   * Проверяет, является ли endpoint публичным (не требует авторизации) Синхронизировано с
   * SecurityConfig.permitAll()
   */
  private boolean isPublicEndpoint(String requestPath) {
    return requestPath.equals("/")
        || requestPath.startsWith("/auth/")
        || requestPath.equals("/favicon.ico")
        || requestPath.startsWith("/js/")
        || requestPath.equals("/profile");
  }
}
