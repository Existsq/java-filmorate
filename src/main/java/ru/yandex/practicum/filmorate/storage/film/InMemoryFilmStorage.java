package ru.yandex.practicum.filmorate.storage.film;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

@Qualifier("InMemoryFilmStorage")
@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

  private final Map<Long, Film> films = new HashMap<>();

  private final Map<Long, Set<Long>> likes = new HashMap<>();

  @Override
  public Film save(Film film) {
    if (film.getId() == null) {
      film.setId(getNextId());
      log.info("Создан новый фильм: {}", film);
    } else {
      log.info("Обновлён фильм: {}", film);
    }
    films.put(film.getId(), film);
    return film;
  }

  @Override
  public Film update(Film film) {
    if (!films.containsKey(film.getId())) {
      throw new IllegalArgumentException("Фильм с id=" + film.getId() + " не найден");
    }
    films.put(film.getId(), film);
    log.info("Обновлён фильм: {}", film);
    return film;
  }

  @Override
  public Collection<Film> findAll() {
    return films.values();
  }

  @Override
  public void deleteById(Long id) {
    films.remove(id);
    likes.remove(id);
    log.info("Удалён фильм с id={}", id);
  }

  @Override
  public void addLike(Long filmId, Long userId) {
    if (!films.containsKey(filmId)) {
      log.warn("Попытка поставить лайк несуществующему фильму id={}", filmId);
      return;
    }
    likes.computeIfAbsent(filmId, k -> new HashSet<>()).add(userId);
    log.info("Пользователь {} поставил лайк фильму {}", userId, filmId);
  }

  @Override
  public void removeLike(Long filmId, Long userId) {
    Set<Long> filmLikes = likes.get(filmId);
    if (filmLikes != null) {
      filmLikes.remove(userId);
      if (filmLikes.isEmpty()) {
        likes.remove(filmId);
      }
      log.info("Пользователь {} удалил лайк с фильма {}", userId, filmId);
    }
  }

  @Override
  public List<Film> findTopFilms(int count) {
    return films.values().stream()
        .sorted(
            (f1, f2) -> {
              int likesCount1 = likes.getOrDefault(f1.getId(), Set.of()).size();
              int likesCount2 = likes.getOrDefault(f2.getId(), Set.of()).size();
              return Integer.compare(likesCount2, likesCount1);
            })
        .limit(count)
        .toList();
  }

  @Override
  public Optional<Film> findFilmById(Long id) {
    return Optional.ofNullable(films.get(id));
  }

  private Long getNextId() {
    return films.keySet().stream().mapToLong(Long::longValue).max().orElse(0) + 1;
  }
}
