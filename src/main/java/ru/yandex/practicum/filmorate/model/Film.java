package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;

/** Film. */
@Data
@Builder
public class Film {
  Long id;

  @NotBlank(message = "Название фильма не может быть пустым")
  @NotEmpty(message = "Название фильма не может быть пустым")
  String name;

  @Size(max = 200, message = "Описание не может быть длинее 200 символов")
  String description;

  LocalDate releaseDate;

  @Positive(message = "Длительность фильма не может быть отрицательной")
  int duration;

  @AssertTrue(message = "Фильм не может быть раньше 28.12.1895")
  public boolean isValidReleaseDate() {
    return releaseDate == null || !releaseDate.isBefore(LocalDate.of(1895, 12, 28));
  }
}
