package by.michael.noughtsandcrosses.web.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequest {

  @NotBlank(message = "Логин не может быть пустым")
  @Size(min = 3, max = 50, message = "Логин должен содержаться от 3 до 50 символов")
  private String login;

  @NotBlank(message = "Пароль не может быть пустым")
  @Size(min = 6, message = "Пароль должен состоять хотя бы из 6 символов")
  private String password;
}
