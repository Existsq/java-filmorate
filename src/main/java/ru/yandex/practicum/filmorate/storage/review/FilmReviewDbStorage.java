package ru.yandex.practicum.filmorate.storage.review;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mappers.FilmReviewRowMapper;
import ru.yandex.practicum.filmorate.model.FilmReview;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;
import java.util.Collection;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class FilmReviewDbStorage implements FilmReviewStorage {
    private final NamedParameterJdbcTemplate namedJdbc;
    private final FilmReviewRowMapper filmReviewRowMapper;
    private final FilmDbStorage filmDbStorage;
    private final UserDbStorage userDbStorage;

    @Override
    public Optional<FilmReview> getById(long id) {
        log.info("Ищем отзыв - " + id);
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);

        String sqlQuery = "SELECT fr.reviewId, fr.content, fr.is_positive, fr.film_id, fr.user_id, IFNULL(ratings.diff, 0) AS useful " +
                "FROM film_reviews fr " +
                "LEFT JOIN (" +
                    "SELECT frr.film_review_id, SUM(CASE WHEN frr.is_useful THEN 1 ELSE 0 END) - SUM(CASE WHEN NOT frr.is_useful THEN 1 ELSE 0 END) AS diff " +
                    "FROM film_review_ratings frr " +
                    "GROUP BY frr.film_review_id" +
                ") AS ratings ON fr.reviewId = ratings.film_review_id " +
                "WHERE fr.reviewId = :id";

        return namedJdbc.query(sqlQuery, params, filmReviewRowMapper).stream().findFirst();
    }

    @Override
    public Collection<FilmReview> findAll(Long filmId, int limit) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("limit", limit);

        String sqlQuery = "SELECT fr.reviewId, fr.content, fr.is_positive, fr.film_id, fr.user_id, IFNULL(ratings.diff, 0) AS useful " +
                "FROM film_reviews fr " +
                "LEFT JOIN (" +
                "SELECT frr.film_review_id, SUM(CASE WHEN frr.is_useful THEN 1 ELSE 0 END) - SUM(CASE WHEN NOT frr.is_useful THEN 1 ELSE 0 END) AS diff " +
                "FROM film_review_ratings frr " +
                "GROUP BY frr.film_review_id" +
                ") AS ratings ON fr.reviewId = ratings.film_review_id " +
                "LIMIT :limit";

        if (filmId != null) {
            if (filmDbStorage.findFilmById(filmId).isEmpty()) {
                throw new NotFoundException("Фильм с id = " + filmId + " не найден");
            }
            params.addValue("filmId", filmId);
            sqlQuery = "SELECT fr.reviewId, fr.content, fr.is_positive, fr.film_id, fr.user_id, IFNULL(ratings.diff, 0) AS useful " +
                    "FROM film_reviews fr " +
                    "LEFT JOIN (" +
                    "SELECT frr.film_review_id, SUM(CASE WHEN frr.is_useful THEN 1 ELSE 0 END) - SUM(CASE WHEN NOT frr.is_useful THEN 1 ELSE 0 END) AS diff " +
                    "FROM film_review_ratings frr " +
                    "GROUP BY frr.film_review_id" +
                    ") AS ratings ON fr.reviewId = ratings.film_review_id " +
                    "WHERE fr.film_id = :filmId " +
                    "LIMIT :limit";
        }
        log.info("Возразаем список отзывов");
        return namedJdbc.query(sqlQuery, params, filmReviewRowMapper);
    }

    @Override
    public FilmReview create(FilmReview filmReview) {
        if (userDbStorage.findUserById(filmReview.getUserId()).isEmpty()) {
            throw new NotFoundException("Пользователь с id = " + filmReview.getUserId() + " не найден");
        }

        if (filmDbStorage.findFilmById(filmReview.getFilmId()).isEmpty()) {
            throw new NotFoundException("Фильм с id = " + filmReview.getFilmId() + " не найден");
        }

        log.info("Начало создания отзыва - " + filmReview);
        final String sqlQuery = "INSERT INTO film_reviews(content, is_positive, film_id, user_id) " +
                "VALUES (:content, :isPositive, :filmId, :userId)";
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("content", filmReview.getContent())
                .addValue("isPositive", filmReview.getIsPositive())
                .addValue("filmId", filmReview.getFilmId())
                .addValue("userId", filmReview.getUserId());
        namedJdbc.update(sqlQuery, params, keyHolder, new String[]{"reviewId"});

        Long id = keyHolder.getKeyAs(Long.class);

        if (id == null) {
            throw new InternalServerException("Не удалось сохранить данные");
        }

        filmReview.setReviewId(id);
        log.info("Создания отзыва - завершено");
        return filmReview;
    }

    @Override
    public FilmReview update(FilmReview filmReview) {
        log.info("Начало обновления отзыва - " + filmReview);
        if (getById(filmReview.getReviewId()).isEmpty()) {
            throw new NotFoundException("Отзыв с id = " + filmReview.getReviewId() + " не найден");
        }

        if (userDbStorage.findUserById(filmReview.getUserId()).isEmpty()) {
            throw new NotFoundException("Пользователь с id = " + filmReview.getUserId() + " не найден");
        }

        if (filmDbStorage.findFilmById(filmReview.getFilmId()).isEmpty()) {
            throw new NotFoundException("Фильм с id = " + filmReview.getFilmId() + " не найден");
        }

        final String sqlQuery = "UPDATE film_reviews " +
                "SET content = :content, is_positive = :isPositive, film_id = :filmId, user_id = :userId " +
                "WHERE reviewId = :id";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", filmReview.getReviewId())
                .addValue("content", filmReview.getContent())
                .addValue("isPositive", filmReview.getIsPositive())
                .addValue("filmId", filmReview.getFilmId())
                .addValue("userId", filmReview.getUserId());

        int rowsUpdated = namedJdbc.update(sqlQuery, params);

        if (rowsUpdated == 0) {
            throw new InternalServerException("Не удалось обновить данные");
        }
        log.info("завершилось обновления отзыва - " + filmReview);
        return filmReview;
    }

    @Override
    public void delete(long id) {
        if (getById(id).isEmpty()) {
            throw new NotFoundException("Отзыв с id = " + id + " не найден");
        }

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);

        String sqlQuery = "DELETE FROM film_reviews where reviewId = :id";
        log.info("Удаляем отзыва");
        namedJdbc.update(sqlQuery, params);
    }

    @Override
    public void addLikeOrDislike(long id, long userId, boolean like) {
        if (getById(id).isEmpty()) {
            throw new NotFoundException("Отзыв с id = " + id + " не найден");
        }

        if (userDbStorage.findUserById(userId).isEmpty()) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);
        params.addValue("userId", userId);

        String sqlQueryDelete = "DELETE FROM film_review_ratings " +
                "WHERE film_review_id = :id AND user_id = :userId";
        namedJdbc.update(sqlQueryDelete, params);

        log.info("Добавляем лайк/дизлайк для отзыва - " + like);
        params.addValue("like", like);

        String sqlQuery = "INSERT INTO film_review_ratings (film_review_id, user_id, is_useful) " +
                "VALUES (:id, :userId, :like)";
        namedJdbc.update(sqlQuery, params);
    }

    @Override
    public void deleteLikeOrDislike(long id, long userId, boolean like) {
        if (getById(id).isEmpty()) {
            throw new NotFoundException("Отзыв с id = " + id + " не найден");
        }

        if (userDbStorage.findUserById(userId).isEmpty()) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);
        params.addValue("userId", userId);
        params.addValue("like", like);

        log.info("Удаляем лайк/дизлайк для отзыва - " + like);
        String sqlQuery = "DELETE FROM film_review_ratings " +
                "WHERE film_review_id = :id AND user_id = :userId AND is_useful = :like";
        namedJdbc.update(sqlQuery, params);
    }
}
