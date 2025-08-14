package ru.yandex.practicum.filmorate.controller;

import java.util.Collection;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenresService;

@RestController
public class GenresController {

  private final GenresService genresService;

  public GenresController(GenresService genresService) {
    this.genresService = genresService;
  }

  @GetMapping("/genres")
  public Collection<Genre> findAll() {
    return genresService.findAll();
  }

  @GetMapping("/genres/{id}")
  public Genre findGenreById(@PathVariable Long id) {
    return genresService.findGenreById(id);
  }
}
