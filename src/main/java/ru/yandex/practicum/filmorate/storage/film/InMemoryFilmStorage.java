package ru.yandex.practicum.filmorate.storage.film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

  private final Map<Long, Film> films = new HashMap<>();

  @Override
  public Film save(Film film) {
    if (film.getId() == null || !films.containsKey(film.getId())) {
      film.setId(getNextId());
      log.info("Создан новый фильм: {}", film);
    } else {
      log.info("Обновлён фильм: {}", film);
    }

    films.put(film.getId(), film);
    return film;
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
