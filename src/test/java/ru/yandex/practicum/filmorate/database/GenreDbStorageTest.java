package ru.yandex.practicum.filmorate.database;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Import({GenreDbStorage.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class GenreDbStorageTest {

  private final GenreDbStorage genreStorage;

  @BeforeEach
  void setUp() {
    genreStorage.findAll().forEach(g -> { });
  }

  @Test
  void testFindAllGenres() {
    Collection<Genre> genres = genreStorage.findAll();
    assertThat(genres).isNotEmpty();
    long previousId = 0;
    for (Genre genre : genres) {
      assertThat(genre.id()).isGreaterThan(previousId);
      previousId = genre.id();
    }
  }

  @Test
  void testFindGenreByIdExists() {
    Optional<Genre> genreOptional = genreStorage.findGenreById(1L);
    assertThat(genreOptional).isPresent();
    assertThat(genreOptional.get().id()).isEqualTo(1L);
  }

  @Test
  void testFindGenreByIdNotExists() {
    Optional<Genre> genreOptional = genreStorage.findGenreById(999L);
    assertThat(genreOptional).isEmpty();
  }
}
