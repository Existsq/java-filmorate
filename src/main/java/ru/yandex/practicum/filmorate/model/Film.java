package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Set;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
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

  private Set<Genre> genres;

  private MPA mpa;

  @AssertTrue(message = "Фильм не может быть раньше 28.12.1895")
  @JsonIgnore
  public boolean isValidReleaseDate() {
    return releaseDate == null || !releaseDate.isBefore(CHECK_DATE);
  }

  public enum Genre {
    COMEDY,
    DRAMA,
    THRILLER,
    DOCUMENTARY,
    CARTOON,
    ACTION;
  }

  public enum MPA {
    G,
    PG,
    PG13,
    R,
    NC17;

    @Override
    public String toString() {
      if (name().length() == 4) {
        return this.name().substring(0, 2) + "-" + this.name().substring(2);
      }
      return this.name();
    }
  }
}
