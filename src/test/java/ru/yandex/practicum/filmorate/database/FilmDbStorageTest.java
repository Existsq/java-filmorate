package ru.yandex.practicum.filmorate.database;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;

@JdbcTest
@Import({FilmDbStorage.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmDbStorageTest {

  private final FilmDbStorage filmStorage;
  private Film testFilm;

  @BeforeEach
  void setUp() {
    testFilm =
        new Film(
            null,
            "Test Film",
            "Description",
            LocalDate.of(2020, 1, 1),
            120,
            new MPA(1L, "G"),
            List.of(new Genre(1L, "Comedy")));
  }

  @Test
  void testSaveFilm() {
    Film saved = filmStorage.save(testFilm);
    assertThat(saved.getId()).isNotNull();
    assertThat(saved.getName()).isEqualTo(testFilm.getName());
    assertThat(saved.getGenres()).hasSize(1);
  }

  @Test
  void testFindFilmById() {
    Film saved = filmStorage.save(testFilm);
    Optional<Film> found = filmStorage.findFilmById(saved.getId());
    assertThat(found).isPresent();
    assertThat(found.get().getName()).isEqualTo(testFilm.getName());
  }

  @Test
  void testUpdateFilm() {
    Film saved = filmStorage.save(testFilm);
    saved.setName("Updated Name");
    Film updated = filmStorage.update(saved);
    assertThat(updated.getName()).isEqualTo("Updated Name");
  }

  @Test
  void testDeleteFilm() {
    Film saved = filmStorage.save(testFilm);
    filmStorage.deleteById(saved.getId());
    Optional<Film> found = filmStorage.findFilmById(saved.getId());
    assertThat(found).isEmpty();
  }

  @Test
  void testAddAndRemoveLike() {
    Film saved = filmStorage.save(testFilm);
    filmStorage.addLike(saved.getId(), 1L);
    List<Film> topFilms = filmStorage.findTopFilms(1);
    assertThat(topFilms).hasSize(1);
    assertThat(topFilms.getFirst().getId()).isEqualTo(saved.getId());

    filmStorage.removeLike(saved.getId(), 1L);
    topFilms = filmStorage.findTopFilms(1);
    assertThat(topFilms).hasSize(1);
  }

  @Test
  void testFindTopFilmsOrder() {
    Film film1 = filmStorage.save(testFilm);
    Film film2 =
        filmStorage.save(
            new Film(
                null,
                "Film 2",
                "Desc",
                LocalDate.of(2021, 1, 1),
                100,
                new MPA(1L, "G"),
                List.of()));
    filmStorage.addLike(film2.getId(), 1L);

    List<Film> topFilms = filmStorage.findTopFilms(2);
    assertThat(topFilms.get(0).getId()).isEqualTo(film2.getId());
    assertThat(topFilms.get(1).getId()).isEqualTo(film1.getId());
  }

  @Test
  void testUpdateNonExistingFilmThrows() {
    testFilm.setId(999L);
    assertThrows(RuntimeException.class, () -> filmStorage.update(testFilm));
  }
}
