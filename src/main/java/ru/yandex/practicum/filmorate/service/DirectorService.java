package ru.yandex.practicum.filmorate.service;

import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;

@Service
@RequiredArgsConstructor
public class DirectorService {

  private final DirectorStorage directorStorage;

  public Director create(Director director) {
    return directorStorage.save(director);
  }

  public Collection<Director> findAll() {
    return directorStorage.findAll();
  }

  public Director update(Director director) {
    return directorStorage.update(director);
  }

  public void deleteById(Long id) {
    directorStorage.deleteById(id);
  }

  public Director findById(Long id) {
    return directorStorage
        .findDirectorById(id)
        .orElseThrow(() -> new NotFoundException("Режиссер с id " + id + " не найден"));
  }
}
