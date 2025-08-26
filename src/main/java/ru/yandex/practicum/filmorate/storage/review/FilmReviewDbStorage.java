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
        log.info("Ищем отзыв - {}", id);

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);
        String sqlQuery =  """
            SELECT fr.id, fr.content, fr.is_positive, fr.film_id, fr.user_id, IFNULL(ratings.diff, 0) AS useful
            FROM film_reviews fr
            LEFT JOIN (
                SELECT frr.film_review_id,
                       SUM(CASE WHEN frr.is_useful THEN 1 ELSE 0 END)
                       - SUM(CASE WHEN NOT frr.is_useful THEN 1 ELSE 0 END) AS diff
                FROM film_review_ratings frr
                GROUP BY frr.film_review_id
            ) AS ratings ON fr.id = ratings.film_review_id
            WHERE fr.id = :id
        """;
        return namedJdbc.query(sqlQuery, params, filmReviewRowMapper).stream().findFirst();
    }

    @Override
    public Collection<FilmReview> findAll(Long filmId, int limit) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("limit", limit);

        String sqlQuery = """
            SELECT fr.id, fr.content, fr.is_positive, fr.film_id, fr.user_id, IFNULL(ratings.diff, 0) AS useful
            FROM film_reviews fr
            LEFT JOIN (
                SELECT frr.film_review_id,
                    SUM(CASE WHEN frr.is_useful THEN 1 ELSE 0 END)
                    - SUM(CASE WHEN NOT frr.is_useful THEN 1 ELSE 0 END) AS diff
                FROM film_review_ratings frr
                GROUP BY frr.film_review_id
            ) AS ratings ON fr.id = ratings.film_review_id
        """;

        if (filmId != null) {
            if (filmDbStorage.findFilmById(filmId).isEmpty()) {
                throw new NotFoundException("Фильм с id = " + filmId + " не найден");
            }
            params.addValue("filmId", filmId);
            sqlQuery += """
                WHERE fr.film_id = :filmId
            """;
        }

        sqlQuery += """
            ORDER BY useful DESC LIMIT :limit
        """;

        log.info("Возразаем список отзывов");
        return namedJdbc.query(sqlQuery, params, filmReviewRowMapper);
    }

    @Override
    public FilmReview create(FilmReview filmReview) {
        validateUserAndFilm(filmReview.getUserId(), filmReview.getFilmId());

        log.info("Начало создания отзыва - {}", filmReview);

        final String sqlQuery = """
           INSERT INTO film_reviews(content, is_positive, film_id, user_id)
           VALUES (:content, :isPositive, :filmId, :userId)
        """;

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("content", filmReview.getContent())
                .addValue("isPositive", filmReview.getIsPositive())
                .addValue("filmId", filmReview.getFilmId())
                .addValue("userId", filmReview.getUserId());
        namedJdbc.update(sqlQuery, params, keyHolder, new String[]{"id"});

        Long id = keyHolder.getKeyAs(Long.class);

        if (id == null) {
            throw new InternalServerException("Не удалось сохранить данные");
        }

        filmReview.setId(id);
        log.info("Создания отзыва - завершено {}", filmReview);
        return filmReview;
    }

    @Override
    public FilmReview update(FilmReview filmReview) {
        log.info("Начало обновления отзыва - {}", filmReview);
        validateUserAndFilm(filmReview.getUserId(), filmReview.getFilmId());

        FilmReview filmReviewDb = getById(filmReview.getId())
                .orElseThrow(() -> new NotFoundException(
                        "Отзыв с id = " + filmReview.getId() + " не найден"
                ));

        filmReview.setFilmId(filmReviewDb.getFilmId());
        filmReview.setUserId(filmReviewDb.getUserId());

        final String sqlQuery = """
           UPDATE film_reviews
           SET content = :content, is_positive = :isPositive
           WHERE id = :id
        """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", filmReview.getId())
                .addValue("content", filmReview.getContent())
                .addValue("isPositive", filmReview.getIsPositive());

        int rowsUpdated = namedJdbc.update(sqlQuery, params);

        if (rowsUpdated == 0) {
            throw new InternalServerException("Не удалось обновить данные");
        }
        log.info("завершилось обновления отзыва - {}", filmReview);
        return filmReview;
    }

    @Override
    public void delete(long id) {
        validateReviewExist(id);

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);

        final String sqlQuery = """
            DELETE FROM film_reviews
            WHERE id = :id
        """;

        log.info("Удаляем отзыва");
        namedJdbc.update(sqlQuery, params);
    }

    @Override
    public void addLikeOrDislike(long id, long userId, boolean like) {
        validateReviewAndUser(id, userId);
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);
        params.addValue("userId", userId);

        final String sqlQueryDelete = """
            DELETE FROM film_review_ratings
            WHERE film_review_id = :id AND user_id = :userId
        """;

        namedJdbc.update(sqlQueryDelete, params);

        log.info("Добавляем лайк/дизлайк для отзыва - {}", like);
        params.addValue("like", like);

        final String sqlQuery = """
            INSERT INTO film_review_ratings (film_review_id, user_id, is_useful)
            VALUES (:id, :userId, :like)
        """;
        namedJdbc.update(sqlQuery, params);
    }

    @Override
    public void deleteLikeOrDislike(long id, long userId, boolean like) {
        validateReviewAndUser(id, userId);
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);
        params.addValue("userId", userId);
        params.addValue("like", like);

        log.info("Удаляем лайк/дизлайк для отзыва - {}", like);

        final String sqlQuery = """
            DELETE FROM film_review_ratings
            WHERE film_review_id = :id
            AND user_id = :userId
            AND is_useful = :like
        """;
        namedJdbc.update(sqlQuery, params);
    }

    private void validateReviewExist(long id) {
        if (getById(id).isEmpty()) {
            throw new NotFoundException("Отзыв с id = " + id + " не найден");
        }
    }

    private void validateUserExist(long id) {
        if (userDbStorage.findUserById(id).isEmpty()) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
    }

    private void validateFilmExist(long id) {
        if (filmDbStorage.findFilmById(id).isEmpty()) {
            throw new NotFoundException("Фильм с id = " + id + " не найден");
        }
    }

    private void validateReviewAndUser(long reviewId, long userId) {
        validateReviewExist(reviewId);
        validateUserExist(userId);
    }

    private void validateUserAndFilm(long userId, long filmId) {
        validateUserExist(userId);
        validateFilmExist(filmId);
    }
}
