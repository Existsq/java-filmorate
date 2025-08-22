package ru.yandex.practicum.filmorate.storage.film;

import java.util.*;

import ru.yandex.practicum.filmorate.model.Film;

public interface FilmStorage {

  Film save(Film film);

  Film update(Film film);

  Optional<Film> findFilmById(Long id);

  Collection<Film> findAll();

  void deleteById(Long id);

  void addLike(Long filmId, Long userId);

  void removeLike(Long filmId, Long userId);

  List<Film> findTopFilms(int count);

  List<Film> findByDirector(Long directorId, String sortBy);

  void delete(Long filmId);

  List<Film> getCommonFilms(Long userId, Long friendId);

  Set<Long> getLikedFilmIds(Long userId);

  List<Film> getRecommendedFilms(Set<Long> similarUserIds, Long userId);

  Map<Long, Set<Long>> getAllUserLikes();
}
