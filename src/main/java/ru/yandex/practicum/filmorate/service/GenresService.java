package ru.yandex.practicum.filmorate.service;

import java.util.Collection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

@Slf4j
@Service
public class GenresService {

  private final GenreStorage genreDbStorage;

  public GenresService(GenreStorage genreDbStorage) {
    this.genreDbStorage = genreDbStorage;
  }

  public Collection<Genre> findAll() {
    return genreDbStorage.findAll();
  }

  public Genre findGenreById(Long id) {
    return genreDbStorage
        .findGenreById(id)
        .orElseThrow(() -> new NotFoundException("Указанный жанр не найден"));
  }
}
