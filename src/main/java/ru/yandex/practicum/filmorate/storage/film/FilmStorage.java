package ru.yandex.practicum.filmorate.storage.film;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import ru.yandex.practicum.filmorate.model.Film;

public interface FilmStorage {

  Film save(Film film);

  Film update(Film film);

  Optional<Film> findFilmById(Long id);

  Collection<Film> findAll();

  void deleteById(Long id);

  void addLike(Long filmId, Long userId);

  void removeLike(Long filmId, Long userId);

  List<Film> findTopFilms(int count, Integer genreId, Integer year);

  List<Film> findByDirector(Long directorId, String sortBy);

  void delete(Long filmId);
}
