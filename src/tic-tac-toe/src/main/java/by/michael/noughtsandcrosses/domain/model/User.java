package by.michael.noughtsandcrosses.domain.model;

import java.util.UUID;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class User {
  private UUID id;
  private String login;
  private String passwordHash;
}
