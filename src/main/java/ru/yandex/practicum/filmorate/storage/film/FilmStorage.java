package ru.yandex.practicum.filmorate.storage.film;

import java.util.Collection;
import ru.yandex.practicum.filmorate.model.Film;

public interface FilmStorage {

  Film save(Film film);

  Collection<Film> findAll();

  Film findById(Long id);
}
