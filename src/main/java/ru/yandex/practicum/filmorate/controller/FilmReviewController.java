package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.FilmReview;
import ru.yandex.practicum.filmorate.service.FilmReviewService;
import java.util.Collection;

@AllArgsConstructor
@RestController
@RequestMapping("/reviews")
public class FilmReviewController {
  private final FilmReviewService filmReviewService;

  @GetMapping
  public Collection<FilmReview> findAll(
          @RequestParam(required = false) Long filmId,
          @RequestParam(defaultValue = "10") int count
  ) {
    return filmReviewService.findAll(filmId, count);
  }

  @GetMapping("/{id}")
  public FilmReview findById(@PathVariable long id) {
    return filmReviewService.findById(id);
  }

  @PostMapping
  public FilmReview create(@Valid @RequestBody FilmReview filmReview) {
    return filmReviewService.create(filmReview);
  }

  @PutMapping
  public FilmReview update(@Valid @RequestBody FilmReview filmReview) {
    return filmReviewService.update(filmReview);
  }

  @DeleteMapping("/{id}")
  public void delete(@PathVariable long id) {
    filmReviewService.delete(id);
  }

  @PutMapping("/{id}/like/{userId}")
  public void addLike(@PathVariable long id, @PathVariable long userId) {
    filmReviewService.addLikeOrDislike(id, userId, true);
  }

  @PutMapping("/{id}/dislike/{userId}")
  public void addDislike(@PathVariable long id, @PathVariable long userId) {
    filmReviewService.addLikeOrDislike(id, userId, false);
  }

  @DeleteMapping("/{id}/like/{userId}")
  public void deleteLike(@PathVariable long id, @PathVariable long userId) {
    filmReviewService.deleteLikeOrDislike(id, userId, true);
  }

  @DeleteMapping("/{id}/dislike/{userId}")
  public void deleteDislike(@PathVariable long id, @PathVariable long userId) {
    filmReviewService.deleteLikeOrDislike(id, userId, false);
  }
}
