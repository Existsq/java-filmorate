package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Film {
  private static final LocalDate CHECK_DATE = LocalDate.of(1895, 12, 28);

  private Long id;

  @NotBlank(message = "Название фильма не может быть пустым")
  private String name;

  @Size(max = 200, message = "Описание не может быть длинее 200 символов")
  private String description;

  private LocalDate releaseDate;

  @Positive(message = "Длительность фильма не может быть отрицательной")
  private int duration;

  @NotNull(message = "MPA рейтинг обязателен")
  private MPA mpa;

  private List<Genre> genres;

  @AssertTrue(message = "Фильм не может быть раньше 28.12.1895")
  @JsonIgnore
  public boolean isValidReleaseDate() {
    return releaseDate == null || !releaseDate.isBefore(CHECK_DATE);
  }
}
