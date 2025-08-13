package ru.yandex.practicum.filmorate.storage.genre;

import java.util.Collection;
import java.util.Optional;
import ru.yandex.practicum.filmorate.model.Genre;

public interface GenreStorage {

  Optional<Genre> findGenreById(Long id);

  Collection<Genre> findAll();
}
