package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Film {
  private static final LocalDate CHECK_DATE = LocalDate.of(1895, 12, 28);

  Long id;

  @NotBlank(message = "Название фильма не может быть пустым")
  String name;

  @Size(max = 200, message = "Описание не может быть длинее 200 символов")
  String description;

  LocalDate releaseDate;

  @Positive(message = "Длительность фильма не может быть отрицательной")
  int duration;

  @AssertTrue(message = "Фильм не может быть раньше 28.12.1895")
  @JsonIgnore
  public boolean isValidReleaseDate() {
    return releaseDate == null || !releaseDate.isBefore(CHECK_DATE);
  }
}
