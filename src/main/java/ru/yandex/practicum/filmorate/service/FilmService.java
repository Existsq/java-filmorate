package ru.yandex.practicum.filmorate.service;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

@Slf4j
@AllArgsConstructor
@Service
public class FilmService {
  private final Map<Long, Set<Long>> likes = new HashMap<>();
  private final FilmStorage filmStorage;
  private final UserService userService;

  public Film create(Film film) {
    Film created = filmStorage.save(film);
    log.info("Создан фильм: {}", created);
    return created;
  }

  public Film update(Film film) {
    if (film.getId() == null) {
      log.warn("Поле id не может быть пустым");
      throw new ValidationException("Поле id не может быть пустым");
    }

    Film existing = filmStorage.findById(film.getId());
    if (existing == null) {
      log.warn("Фильм с id={} не найден", film.getId());
      throw new NotFoundException("Фильм с id " + film.getId() + " не найден");
    }

    applyUpdates(existing, film);

    Film updated = filmStorage.save(existing);
    log.info("Обновлён фильм: {}", updated);
    return updated;
  }

  private void applyUpdates(Film target, Film source) {
    if (!Objects.equals(target.getName(), source.getName())) {
      log.info("Имя изменено: {} -> {}", target.getName(), source.getName());
      target.setName(source.getName());
    }

    if (!Objects.equals(target.getDescription(), source.getDescription())) {
      log.info("Описание изменено: {} -> {}", target.getDescription(), source.getDescription());
      target.setDescription(source.getDescription());
    }

    if (!Objects.equals(target.getReleaseDate(), source.getReleaseDate())) {
      log.info("Дата релиза изменена: {} -> {}", target.getReleaseDate(), source.getReleaseDate());
      target.setReleaseDate(source.getReleaseDate());
    }

    if (!Objects.equals(target.getDuration(), source.getDuration())) {
      log.info("Длительность изменена: {} -> {}", target.getDuration(), source.getDuration());
      target.setDuration(source.getDuration());
    }
  }

  public Collection<Film> findAll() {
    return filmStorage.findAll();
  }

  public Film findById(Long id) {
    return filmStorage.findById(id);
  }

  public void addLike(Long userId, Long filmId) {
    userService.validateUserExists(userId);
    validateFilmExists(filmId);

    likes.computeIfAbsent(filmId, k -> new HashSet<>()).add(userId);
    log.info("Пользователь {} поставил лайк фильму {}", userId, filmId);
  }

  public void deleteLike(Long userId, Long filmId) {
    userService.validateUserExists(userId);
    validateFilmExists(filmId);

    Set<Long> filmLikes = likes.get(filmId);

    if (filmLikes != null) {
      filmLikes.remove(userId);
      if (filmLikes.isEmpty()) {
        likes.remove(filmId);
      }
      log.info("Пользователь {} удалил лайк с фильма {}", userId, filmId);
    }
  }

  public List<Long> getPopularByLikes(int count) {
    List<Long> result =
        likes.entrySet().stream()
            .sorted(Comparator.comparingInt(e -> e.getValue().size()))
            .limit(count)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList())
            .reversed();

    log.debug("Топ {} популярных фильмов по лайкам: {}", count, result);
    return result;
  }

  private void validateFilmExists(Long filmId) {
    if (this.findById(filmId) == null) {
      log.warn("Попытка доступа к несуществующему фильму id={}", filmId);
      throw new NotFoundException("Фильм с id " + filmId + " не найден");
    }
  }
}
