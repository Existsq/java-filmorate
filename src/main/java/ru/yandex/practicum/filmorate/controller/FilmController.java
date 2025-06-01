package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

@RestController
@RequestMapping("/films")
public class FilmController {

  private final Map<Long, Film> films = new HashMap<>();

  private final Logger log = LoggerFactory.getLogger(FilmController.class);

  private static final LocalDate CHECK_DATE = LocalDate.of(1895, 12, 28);

  @GetMapping
  public Collection<Film> findAll() {
    return films.values();
  }

  @PostMapping
  public Film create(@Valid @RequestBody Film film) {
    film.setId(getNextId());
    films.put(film.getId(), film);
    log.info("Создан новый фильм {}", film);

    return film;
  }

  @PutMapping
  public Film update(@Valid @RequestBody Film film) {
    if (film.getId() == null) {
      log.warn("Поле id не может быть пустым");
      throw new ValidationException("Поле id не может быть пустым");
    }

    Film oldFilm = films.get(film.getId());

    if (oldFilm == null) {
      log.warn("Фильм с id={} не найден", film.getId());
      throw new NoSuchElementException("Фильм с id " + film.getId() + " не найден");
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

  private Long getNextId() {
    long currentMaxId = films.values().stream().mapToLong(Film::getId).max().orElse(0);
    return ++currentMaxId;
  }
}
