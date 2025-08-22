package ru.yandex.practicum.filmorate.service;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

@Slf4j
@Service
public class FilmService {

  private final FilmStorage filmStorage;

  private final UserService userService;

  private final RecommendationService recommendationService;

  public FilmService(FilmStorage filmStorage, UserService userService,
                     RecommendationService recommendationService) {
    this.filmStorage = filmStorage;
    this.userService = userService;
    this.recommendationService = recommendationService;
  }

  public Film create(Film film) {
    Film created = filmStorage.save(film);
    log.info("Создан фильм: {}", created);
    return created;
  }

  public Film update(Film film) {
    if (film.getId() == null) {
      throw new ValidationException("Поле id не может быть пустым");
    }
    if (filmStorage.findFilmById(film.getId()).isEmpty()) {
      throw new NotFoundException("Фильм с id " + film.getId() + " не найден");
    }

    Film updated = filmStorage.update(film);
    log.info("Обновлён фильм: {}", updated);
    return updated;
  }

  public Collection<Film> findAll() {
    return filmStorage.findAll();
  }

  public Film findById(Long id) {
    return filmStorage
        .findFilmById(id)
        .orElseThrow(() -> new NotFoundException("Фильм с id " + id + " не найден"));
  }

  public void addLike(Long userId, Long filmId) {
    userService.validateUserExists(userId);
    findById(filmId);
    filmStorage.addLike(filmId, userId);
    log.info("Пользователь {} поставил лайк фильму {}", userId, filmId);
  }

  public void deleteLike(Long userId, Long filmId) {
    userService.validateUserExists(userId);
    findById(filmId);
    filmStorage.removeLike(filmId, userId);
    log.info("Пользователь {} удалил лайк с фильма {}", userId, filmId);
  }

  public List<Film> getPopularByLikes(int count) {
    List<Film> topFilms = filmStorage.findTopFilms(count);
    log.debug("Топ {} популярных фильмов: {}", count, topFilms);
    return topFilms;
  }

  public List<Film> getFilmsByDirector(Long directorId, String sortBy) {
    return filmStorage.findByDirector(directorId, sortBy);
  }

  public void delete(Long filmId) {
    filmStorage.delete(filmId);
  }

  public List<Film> getCommonFilms(Long userId, Long friendId) {
      userService.validateUserExists(userId);
      userService.validateUserExists(friendId);
      log.info("Поиск общих фильмов пользователей {} и {}", userId, friendId);
      return filmStorage.getCommonFilms(userId, friendId);
  }
  
  public List<Film> getRecommendations(Long userId) {
      log.info("Формирование рекомендаций для пользователя {}", userId);
      userService.validateUserExists(userId);

      Set<Long> similarUserIds = recommendationService.findUsersWithSimilarTastes(userId);

      if (similarUserIds.isEmpty()) {
          log.info("Для пользователя {} не найдено пользователей с похожими вкусами", userId);
          return List.of();
      }

      log.info("Для пользователя {} найдено {} похожих пользователей: {}",
              userId, similarUserIds.size(), similarUserIds);

      List<Film> recommendations = filmStorage.getRecommendedFilms(similarUserIds, userId);

      log.info("Для пользователя {} сгенерировано {} рекомендаций",
              userId, recommendations.size());

      return recommendations;
  }
}
