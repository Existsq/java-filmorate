package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

@AllArgsConstructor
@RestController
@RequestMapping("/films")
public class FilmController {
  private final FilmService filmService;

  @GetMapping
  public Collection<Film> findAll() {
    return filmService.findAll();
  }

  @GetMapping("/{id}")
  public Film findById(@PathVariable Long id) {
    return filmService.findById(id);
  }

  @PostMapping
  public Film create(@Valid @RequestBody Film film) {
    return filmService.create(film);
  }

  @PutMapping
  public Film update(@Valid @RequestBody Film film) {
    return filmService.update(film);
  }

  @PutMapping("/{id}/like/{userId}")
  public void addLike(@PathVariable Long id, @PathVariable Long userId) {
    filmService.addLike(userId, id);
  }

  @DeleteMapping("/{id}/like/{userId}")
  public void deleteLike(@PathVariable Long id, @PathVariable Long userId) {
    filmService.deleteLike(userId, id);
  }

  @DeleteMapping("/{id}")
  public void delete(@PathVariable Long id) {
    filmService.delete(id);
  }

  @GetMapping("/popular")
  public List<Film> getTopFilms(
      @RequestParam(defaultValue = "10") int count,
      @RequestParam(required = false) Integer genreId,
      @RequestParam(required = false) Integer year) {
    return filmService.getPopularByLikes(count, genreId, year);
  }

  @GetMapping("/director/{directorId}")
  public List<Film> getFilmsByDirector(
      @PathVariable Long directorId,
      @RequestParam(name = "sortBy", defaultValue = "year") String sortBy) {
    return filmService.getFilmsByDirector(directorId, sortBy);
  }

  @GetMapping("/common")
  public List<Film> getCommonFilms(@RequestParam Long userId, @RequestParam Long friendId) {
      return filmService.getCommonFilms(userId, friendId);
  }

  @GetMapping("/search")
  public Collection<Film> search(@RequestParam String query, @RequestParam Optional<String> by) {
    Set<String> fields =
        by.map(s -> Arrays.stream(s.split(",")).map(String::trim).collect(Collectors.toSet()))
            .orElse(Set.of("title", "director"));

    return filmService.search(query, fields);
  }
}
