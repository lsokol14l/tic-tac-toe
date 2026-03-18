package by.michael.noughtsandcrosses.datasource.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity {

  @Id
  @Column(name = "user_id", nullable = false)
  private UUID id;

  @Column(name = "login", nullable = false, unique = true, length = 50)
  private String login;

  @Column(name = "password_hash", nullable = false)
  private String passwordHash;
}
