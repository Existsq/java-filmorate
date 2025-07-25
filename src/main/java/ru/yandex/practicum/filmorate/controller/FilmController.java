package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import java.util.Collection;
import java.util.List;
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

  @GetMapping("/popular")
  public List<Film> getTop10(@RequestParam(defaultValue = "10") int count) {
    List<Long> topFilms = filmService.getPopularByLikes(count);

    return topFilms.stream().map(filmService::findById).collect(Collectors.toList());
  }
}
