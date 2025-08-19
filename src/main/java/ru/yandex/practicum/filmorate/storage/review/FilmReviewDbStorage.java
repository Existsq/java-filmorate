package ru.yandex.practicum.filmorate.storage.review;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
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
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);

        String sqlQuery = "SELECT fr.id, fr.content, fr.is_positive, fr.film_id, fr.user_id, IFNULL(ratings.diff, 0) AS usefull " +
                "FROM film_reviews fr " +
                "LEFT JOIN (" +
                    "SELECT frr.film_review_id, SUM(CASE WHEN frr.is_positive THEN 1 ELSE 0 END) - SUM(CASE WHEN NOT frr.is_positive THEN 1 ELSE 0 END) AS diff " +
                    "FROM film_review_ratings frr " +
                    "GROUP BY frr.film_review_id" +
                ") AS ratings ON fr.id = ratings.film_review_id " +
                "WHERE fr.id = :id";

        return namedJdbc.query(sqlQuery, params, filmReviewRowMapper).stream().findFirst();
    }

    @Override
    public Collection<FilmReview> findAll(Long filmId, int limit) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("limit", limit);

        String sqlQuery = "SELECT fr.id, fr.content, fr.is_positive, fr.film_id, fr.user_id, IFNULL(ratings.diff, 0) AS usefull " +
                "FROM film_reviews fr " +
                "LEFT JOIN (" +
                "SELECT frr.film_review_id, SUM(CASE WHEN frr.is_positive THEN 1 ELSE 0 END) - SUM(CASE WHEN NOT frr.is_positive THEN 1 ELSE 0 END) AS diff " +
                "FROM film_review_ratings frr " +
                "GROUP BY frr.film_review_id" +
                ") AS ratings ON fr.id = ratings.film_review_id " +
                "limit :limit";

        if (filmId != null) {
            if (filmDbStorage.findFilmById(filmId).isEmpty()) {
                throw new NotFoundException("Фильм с id = " + filmId + " не найден");
            }
            params.addValue("film_id", filmId);
            sqlQuery = "SELECT fr.id, fr.content, fr.is_positive, fr.film_id, fr.user_id, IFNULL(ratings.diff, 0) AS usefull " +
                    "FROM film_reviews fr " +
                    "LEFT JOIN (" +
                    "SELECT frr.film_review_id, SUM(CASE WHEN frr.is_positive THEN 1 ELSE 0 END) - SUM(CASE WHEN NOT frr.is_positive THEN 1 ELSE 0 END) AS diff " +
                    "FROM film_review_ratings frr " +
                    "GROUP BY frr.film_review_id" +
                    ") AS ratings ON fr.id = ratings.film_review_id " +
                    "WHERE fr.film_id = :film_id " +
                    "limit :limit";
        }

        return namedJdbc.query(sqlQuery, filmReviewRowMapper);
    }

    @Override
    public FilmReview create(FilmReview filmReview) {
        return null;
    }

    @Override
    public FilmReview update(FilmReview filmReview) {
        return null;
    }

    @Override
    public void delete(long id) {
        if (getById(id).isEmpty()) {
            throw new NotFoundException("Отзыв с id = " + id + " не найден");
        }

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);

        String sqlQuery = "DELETE FROM film_reviews where id = :id";
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
        params.addValue("like", like);

        String sqlQuery = "INSERT INTO film_review_ratings (film_review_id, user_id, is_usefull) " +
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

        String sqlQuery = "DELETE FROM film_review_ratings " +
                "WHERE film_review_id = :id AND user_id = :userId AND is_usefull = :like";
        namedJdbc.update(sqlQuery, params);
    }
}
