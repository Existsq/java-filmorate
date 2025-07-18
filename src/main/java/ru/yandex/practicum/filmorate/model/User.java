package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {
  private Long id;

  @NotBlank(message = "Email обязателен")
  @Email(message = "Неверный формат почты")
  private String email;

  @NotBlank(message = "Логин не может быть пустым")
  @Pattern(regexp = "^\\S+$", message = "Логин не может содержать пробелы")
  private String login;

  private String name;

  @Past(message = "День рождения не может быть в будущем")
  private LocalDate birthday;


  public enum FriendshipStatus {
    APPROVED,
    AWAITING
  }
}
