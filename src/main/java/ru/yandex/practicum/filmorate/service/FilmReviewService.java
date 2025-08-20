package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.FilmReview;
import ru.yandex.practicum.filmorate.storage.review.FilmReviewStorage;
import java.util.Collection;

@Slf4j
@Service
public class FilmReviewService {
  private final FilmReviewStorage filmReviewStorage;

  public FilmReviewService(FilmReviewStorage filmReviewStorage) {
    this.filmReviewStorage = filmReviewStorage;
  }

  public Collection<FilmReview> findAll(Long filmId, int count) {
    return filmReviewStorage.findAll(filmId, count);
  }

  public FilmReview findById(Long id) {
    return filmReviewStorage
        .getById(id).orElseThrow(() -> new NotFoundException("Отзыв с id " + id + " не найден"));
  }

  public FilmReview create(FilmReview filmReview) {
    return filmReviewStorage.create(filmReview);
  }

  public FilmReview update(FilmReview filmReview) {
    return filmReviewStorage.update(filmReview);
  }

  public void delete(long id) {
    filmReviewStorage.delete(id);
  }

  public void addLikeOrDislike(long id, long userId, boolean like) {
    filmReviewStorage.addLikeOrDislike(id, userId, like);
  }

  public void deleteLikeOrDislike(long id, long userId, boolean like) {
    filmReviewStorage.deleteLikeOrDislike(id, userId, like);
  }
}
