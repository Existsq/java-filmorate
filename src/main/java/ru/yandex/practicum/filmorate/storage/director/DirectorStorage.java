package ru.yandex.practicum.filmorate.storage.director;

import java.util.Collection;
import java.util.Optional;
import ru.yandex.practicum.filmorate.model.Director;

public interface DirectorStorage {

  Director save(Director director);

  Director update(Director director);

  Collection<Director> findAll();

  Optional<Director> findDirectorById(Long id);

  void deleteById(Long id);
}
