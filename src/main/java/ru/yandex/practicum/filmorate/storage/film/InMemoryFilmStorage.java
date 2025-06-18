package ru.yandex.practicum.filmorate.storage.film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

  private final Map<Long, Film> films = new HashMap<>();

  @Override
  public Film create(Film film) {
    film.setId(getNextId());
    films.put(film.getId(), film);

    log.info("Создан новый фильм {}", film);

    return film;
  }

  @Override
  public Film update(Film film) {
    if (film.getId() == null) {
      log.warn("Поле id не может быть пустым");
      throw new ValidationException("Поле id не может быть пустым");
    }

    Film oldFilm = films.get(film.getId());

    if (oldFilm == null) {
      log.warn("Фильм с id={} не найден", film.getId());
      throw new NotFoundException("Фильм с id " + film.getId() + " не найден");
    }

    if (!Objects.equals(film.getName(), oldFilm.getName())) {
      String oldName = oldFilm.getName();

      oldFilm.setName(film.getName());

      log.info("Установлено новое имя для фильма {} -> {}", oldName, film.getName());
    }

    if (!Objects.equals(film.getDescription(), oldFilm.getDescription())) {
      String oldDescription = oldFilm.getDescription();

      oldFilm.setDescription(film.getDescription());

      log.info(
          "Установлено новое описание для фильма {} -> {}", oldDescription, film.getDescription());
    }

    if (!Objects.equals(film.getReleaseDate(), oldFilm.getReleaseDate())) {
      LocalDate oldReleaseDate = oldFilm.getReleaseDate();

      oldFilm.setReleaseDate(film.getReleaseDate());

      log.info(
          "Установлена новая дата выпуска фильма {} -> {}", oldReleaseDate, film.getReleaseDate());
    }

    if (!Objects.equals(film.getDuration(), oldFilm.getDuration())) {
      int oldDuration = oldFilm.getDuration();

      oldFilm.setDuration(film.getDuration());

      log.info("Установлена новая длительность фильма {} -> {}", oldDuration, film.getDuration());
    }

    return oldFilm;
  }

  @Override
  public Collection<Film> findAll() {
    return films.values();
  }

  @Override
  public Film findById(Long id) {
    return films.get(id);
  }

  private Long getNextId() {
    long currentMaxId = films.values().stream().mapToLong(Film::getId).max().orElse(0);
    return ++currentMaxId;
  }
}
