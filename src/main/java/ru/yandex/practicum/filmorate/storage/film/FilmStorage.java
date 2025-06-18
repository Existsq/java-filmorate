package ru.yandex.practicum.filmorate.storage.film;

import java.util.Collection;
import ru.yandex.practicum.filmorate.model.Film;

public interface FilmStorage {

  Film create(Film film);

  Film update(Film film);

  Collection<Film> findAll();

  Film findById(Long id);
}
