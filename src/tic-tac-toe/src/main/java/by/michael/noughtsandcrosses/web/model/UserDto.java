package by.michael.noughtsandcrosses.web.model;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class UserDto {
  private UUID userId;
  private String login;
}
