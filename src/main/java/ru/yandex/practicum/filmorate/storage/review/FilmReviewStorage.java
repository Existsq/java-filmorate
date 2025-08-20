package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.FilmReview;
import java.util.Collection;
import java.util.Optional;

public interface FilmReviewStorage {
    Optional<FilmReview> getById(long id);

    Collection<FilmReview> findAll(Long filmId, int limit);

    FilmReview create(FilmReview filmReview);

    FilmReview update(FilmReview filmReview);

    void delete(long id);

    void addLikeOrDislike(long id, long userId, boolean like);

    void deleteLikeOrDislike(long id, long userId, boolean like);
}
