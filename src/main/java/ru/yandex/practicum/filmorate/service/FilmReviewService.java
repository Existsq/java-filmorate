package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.FilmReview;
import ru.yandex.practicum.filmorate.model.OperationType;
import ru.yandex.practicum.filmorate.storage.review.FilmReviewStorage;
import java.util.Collection;

@Slf4j
@Service
public class FilmReviewService {
  private final FilmReviewStorage filmReviewStorage;

  private final UserFeedService userFeedService;

  public FilmReviewService(FilmReviewStorage filmReviewStorage, UserFeedService userFeedService) {
    this.filmReviewStorage = filmReviewStorage;
    this.userFeedService = userFeedService;
  }

  public Collection<FilmReview> findAll(Long filmId, int count) {
    return filmReviewStorage.findAll(filmId, count);
  }

  public FilmReview findById(Long id) {
    return filmReviewStorage
        .getById(id).orElseThrow(() -> new NotFoundException("Отзыв с id " + id + " не найден"));
  }

  public FilmReview create(FilmReview filmReview) {
    FilmReview created = filmReviewStorage.create(filmReview);
    userFeedService.addReviewEvent(created.getUserId(), created.getId(), OperationType.ADD);
    return created;
  }

  public FilmReview update(FilmReview filmReview) {
    FilmReview updated = filmReviewStorage.update(filmReview);
    userFeedService.addReviewEvent(updated.getUserId(), updated.getId(), OperationType.UPDATE);
    return updated;
  }

  public void delete(long id) {
    FilmReview review = findById(id);
    userFeedService.addReviewEvent(review.getUserId(), id, OperationType.REMOVE);
    filmReviewStorage.delete(id);
  }

  public void addLikeOrDislike(long id, long userId, boolean like) {
    filmReviewStorage.addLikeOrDislike(id, userId, like);
  }

  public void deleteLikeOrDislike(long id, long userId, boolean like) {
    filmReviewStorage.deleteLikeOrDislike(id, userId, like);
  }
}
