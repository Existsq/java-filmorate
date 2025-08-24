package ru.yandex.practicum.filmorate.storage.film;

import java.sql.*;
import java.sql.Date;
import java.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

@Slf4j
@Repository
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

  private final JdbcTemplate jdbcTemplate;

  @Override
  public Film save(Film film) {
    KeyHolder keyHolder = new GeneratedKeyHolder();

    jdbcTemplate.update(
        connection -> {
          PreparedStatement ps =
              connection.prepareStatement(
                  "INSERT INTO films (name, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?)",
                  Statement.RETURN_GENERATED_KEYS);
          ps.setString(1, film.getName());
          ps.setString(2, film.getDescription());
          ps.setDate(3, Date.valueOf(film.getReleaseDate()));
          ps.setInt(4, film.getDuration());
          ps.setLong(5, film.getMpa().id());
          return ps;
        },
        keyHolder);

    Long id = Objects.requireNonNull(keyHolder.getKey()).longValue();
    film.setId(id);
    updateFilmGenres(film);
    updateFilmDirectors(film);

    return film;
  }

  @Override
  public Film update(Film film) {
    int updated =
        jdbcTemplate.update(
            "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? WHERE id = ?",
            film.getName(),
            film.getDescription(),
            Date.valueOf(film.getReleaseDate()),
            film.getDuration(),
            film.getMpa().id(),
            film.getId());

    if (updated == 0) {
      throw new NotFoundException("Фильм с id=" + film.getId() + " не найден");
    }

    updateFilmGenres(film);
    updateFilmDirectors(film);

    return findFilmById(film.getId())
        .orElseThrow(() -> new RuntimeException("Не удалось обновить фильм"));
  }

  @Override
  public Optional<Film> findFilmById(Long id) {
    String sql =
        """
            SELECT f.*, m.name AS mpa_name
            FROM films f
            JOIN mpa_ratings m ON f.mpa_id = m.id
            WHERE f.id = ?
            """;

    Film film = jdbcTemplate.query(sql, new FilmRowMapper(), id).stream().findFirst().orElse(null);

    if (film != null) {
      film.setGenres(getGenresForFilm(film.getId()));
      film.setDirectors(getDirectorsForFilm(film.getId()));
    }

    return Optional.ofNullable(film);
  }

  @Override
  public Collection<Film> findAll() {
    String sql =
        """
        SELECT f.*, m.name AS mpa_name
        FROM films f
        JOIN mpa_ratings m ON f.mpa_id = m.id
        ORDER BY f.id ASC
        """;

    List<Film> films = jdbcTemplate.query(sql, new FilmRowMapper());

    for (Film film : films) {
      film.setGenres(getGenresForFilm(film.getId()));
      film.setDirectors(getDirectorsForFilm(film.getId()));
    }

    return films;
  }

  @Override
  public void addLike(Long filmId, Long userId) {
    log.info("Пользователь {} поставил лайк фильму {}", userId, filmId);
    jdbcTemplate.update("INSERT INTO likes (user_id, film_id) VALUES (?, ?)", userId, filmId);
  }

  @Override
  public void removeLike(Long filmId, Long userId) {
    jdbcTemplate.update("DELETE FROM likes WHERE user_id = ? AND film_id = ?", userId, filmId);
  }

  @Override
  public List<Film> findTopFilms(int count, Integer genreId, Integer year) {
    StringBuilder sqlBuilder = new StringBuilder("""
        SELECT f.*, m.name AS mpa_name
        FROM films f
        JOIN mpa_ratings m ON f.mpa_id = m.id
        LEFT JOIN likes l ON f.id = l.film_id
    """);

    // Добавляем JOIN для фильтрации по жанру если нужно
    if (genreId != null) {
      sqlBuilder.append("""
            JOIN film_genres fg ON f.id = fg.film_id AND fg.genre_id = ?
        """);
    }

    sqlBuilder.append("""
        WHERE 1=1
    """);

    // Добавляем фильтрацию по году если нужно
    if (year != null) {
      sqlBuilder.append("""
            AND EXTRACT(YEAR FROM f.release_date) = ?
        """);
    }

    sqlBuilder.append("""
        GROUP BY f.id, m.name
        ORDER BY COUNT(DISTINCT l.user_id) DESC, f.id DESC
        LIMIT ?
    """);

    String sql = sqlBuilder.toString();

    // Подготавливаем параметры
    List<Object> params = new ArrayList<>();
    int paramIndex = 1;

    if (genreId != null) {
      params.add(genreId);
    }
    if (year != null) {
      params.add(year);
    }
    params.add(count);

    List<Film> films = jdbcTemplate.query(sql, new FilmRowMapper(), params.toArray());

    for (Film film : films) {
      film.setGenres(getGenresForFilm(film.getId()));
      film.setDirectors(getDirectorsForFilm(film.getId()));
    }

    return films;
  }

  @Override
  public void delete(Long filmId) {
    String sql = "DELETE FROM films WHERE id = ?";
    jdbcTemplate.update(sql, filmId);
    log.info("Фильм {} удален", filmId);
  }

  private void updateFilmGenres(Film film) {
    // Сначала очищаем старые связи
    jdbcTemplate.update("DELETE FROM film_genres WHERE film_id = ?", film.getId());

    if (film.getGenres() != null && !film.getGenres().isEmpty()) {
      // Убираем дубликаты жанров
      Set<Long> genreIds = new HashSet<>();
      List<Long> uniqueGenreIds =
          film.getGenres().stream().map(Genre::id).filter(genreIds::add).toList();

      String sql = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";

      jdbcTemplate.batchUpdate(
          sql,
          new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
              ps.setLong(1, film.getId());
              ps.setLong(2, uniqueGenreIds.get(i));
            }

            @Override
            public int getBatchSize() {
              return uniqueGenreIds.size();
            }
          });
    }
  }

  private void updateFilmDirectors(Film film) {
    jdbcTemplate.update("DELETE FROM film_directors WHERE film_id = ?", film.getId());

    if (film.getDirectors() != null && !film.getDirectors().isEmpty()) {
      Set<Long> directorIdsSet = new HashSet<>();
      List<Long> uniqueDirectorIds =
          film.getDirectors().stream().map(Director::id).filter(directorIdsSet::add).toList();

      String sql = "INSERT INTO film_directors (film_id, director_id) VALUES (?, ?)";
      jdbcTemplate.batchUpdate(
          sql,
          new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
              ps.setLong(1, film.getId());
              ps.setLong(2, uniqueDirectorIds.get(i));
            }

            @Override
            public int getBatchSize() {
              return uniqueDirectorIds.size();
            }
          });
    }
  }

  @Override
  public List<Film> findByDirector(Long directorId, String sortBy) {
    String orderBy;
    switch (sortBy.toLowerCase()) {
      case "likes" -> orderBy = "COUNT(DISTINCT l.user_id) DESC";
      case "year" -> orderBy = "f.release_date";
      default -> throw new IllegalArgumentException("Invalid sortBy: " + sortBy);
    }

    String sql =
        """
        SELECT f.*, m.name AS mpa_name
        FROM films f
        JOIN mpa_ratings m ON f.mpa_id = m.id
        JOIN film_directors fd ON f.id = fd.film_id
        LEFT JOIN likes l ON f.id = l.film_id
        WHERE fd.director_id = ?
        GROUP BY f.id, m.name
        ORDER BY
        """
            + orderBy;

    List<Film> films = jdbcTemplate.query(sql, new FilmRowMapper(), directorId);

    for (Film film : films) {
      film.setGenres(getGenresForFilm(film.getId()));
      film.setDirectors(getDirectorsForFilm(film.getId()));
    }

    return films;
  }

  private List<Genre> getGenresForFilm(Long filmId) {
    String sql =
        """
            SELECT g.*
            FROM film_genres fg
            JOIN genres g ON fg.genre_id = g.id
            WHERE fg.film_id = ?
            ORDER BY g.id ASC
            """;
    return jdbcTemplate.query(sql, new GenreRowMapper(), filmId);
  }

  private List<Director> getDirectorsForFilm(Long filmId) {
    String sql =
        """
        SELECT d.id, d.name
        FROM directors d
        JOIN film_directors fd ON d.id = fd.director_id
        WHERE fd.film_id = ?
        ORDER BY d.id ASC
        """;
    return jdbcTemplate.query(
        sql, (rs, rowNum) -> new Director(rs.getLong("id"), rs.getString("name")), filmId);
  }

  @Override
  public List<Film> getCommonFilms(Long userId, Long friendId) {
    log.debug("Поиск общих фильмов для пользователей {} и {}", userId, friendId);
    String sql = """
        SELECT f.*, m.name AS mpa_name, lc.like_count
        FROM films f
        JOIN mpa_ratings m ON f.mpa_id = m.id
        JOIN (
            SELECT film_id
            FROM likes
            WHERE user_id IN (?, ?)
            GROUP BY film_id
            HAVING COUNT(DISTINCT user_id) = 2
        ) cf ON f.id = cf.film_id
        JOIN (
            SELECT film_id, COUNT(*) as like_count
            FROM likes
            GROUP BY film_id
        ) lc ON f.id = lc.film_id
        ORDER BY lc.like_count DESC
        """;
    List<Film> films = jdbcTemplate.query(sql,new FilmRowMapper(), userId, friendId);

    for (Film film : films) {
        film.setGenres(getGenresForFilm(film.getId()));
    }

    log.debug("Найдено {} общих фильмов", films.size());
    return films;
  }

  public Set<Long> getLikedFilmIds(Long userId) {
      String sql = "SELECT film_id FROM likes WHERE user_id = ?";
      List<Long> filmIds = jdbcTemplate.queryForList(sql, Long.class, userId);
      return new HashSet<>(filmIds);
  }

  @Override
  public  Map<Long, Set<Long>> getAllUserLikes() {
      String sql = "SELECT user_id, film_id FROM likes";

      return jdbcTemplate.query(sql, (ResultSet rs) -> {
          Map<Long, Set<Long>> userLikesMap = new HashMap<>();
          while (rs.next()) {
              Long userId = rs.getLong("user_id");
              Long filmId = rs.getLong("film_id");
              userLikesMap.computeIfAbsent(userId, k -> new HashSet<>()).add(filmId);
          }
          return userLikesMap;
      });
  }

  @Override
  public List<Film> getRecommendedFilms(Set<Long> similarUserIds, Long userId) {
      if (similarUserIds.isEmpty()) {
          return Collections.emptyList();
      }

      String placeholders = String.join(",",
              Collections.nCopies(similarUserIds.size(), "?"));

      String sql = String.format("""
          SELECT f.*, m.name AS mpa_name,
                 COUNT(l.user_id) AS similarity_score
          FROM films f
          JOIN mpa_ratings m ON f.mpa_id = m.id
          LEFT JOIN likes l ON f.id = l.film_id AND l.user_id IN (%s)
          WHERE f.id IN (SELECT film_id FROM likes WHERE user_id IN (%s))
          AND f.id NOT IN (SELECT film_id FROM likes WHERE user_id = ?)
          GROUP BY f.id, m.name
          ORDER BY similarity_score DESC, f.id DESC
          LIMIT 20
          """, placeholders, placeholders);
       // параметры для запроса
      List<Object> params = new ArrayList<>();
      params.addAll(similarUserIds); // Для LEFT JOIN
      params.addAll(similarUserIds); // Для WHERE IN
      params.add(userId);            // Для NOT IN

      List<Film> films = jdbcTemplate.query(sql, new FilmRowMapper(), params.toArray());

      for (Film film : films) {
          film.setGenres(getGenresForFilm(film.getId()));
      }

      log.debug("Найдено {} рекомендованных фильмов", films.size());
      return films;
  }

  public Collection<Film> search(String query, Set<String> byFields) {
    List<Object> params = new ArrayList<>();
    String lowerQuery = "%" + query.toLowerCase() + "%";

    StringBuilder sql = new StringBuilder("""
        SELECT DISTINCT f.id, f.name, f.description, f.release_date, f.duration, f.mpa_id, m.name AS mpa_name,
               COUNT(DISTINCT l.user_id) AS like_count
        FROM films f
        JOIN mpa_ratings m ON f.mpa_id = m.id
        LEFT JOIN likes l ON f.id = l.film_id
    """);

    if (byFields != null && byFields.contains("director")) {
      sql.append(" LEFT JOIN film_directors fd ON f.id = fd.film_id");
      sql.append(" LEFT JOIN directors d ON fd.director_id = d.id");
    }

    List<String> conditions = new ArrayList<>();
    if (byFields == null || byFields.isEmpty() || byFields.contains("title")) {
      conditions.add("LOWER(f.name) LIKE ?");
      params.add(lowerQuery);
    }
    if (byFields != null && byFields.contains("director")) {
      conditions.add("LOWER(d.name) LIKE ?");
      params.add(lowerQuery);
    }

    sql.append(" WHERE ").append(String.join(" OR ", conditions));
    sql.append(" GROUP BY f.id, m.name");
    sql.append(" ORDER BY like_count DESC, f.id ASC");

    List<Film> films = jdbcTemplate.query(sql.toString(), new FilmRowMapper(), params.toArray());

    for (Film film : films) {
      film.setGenres(getGenresForFilm(film.getId()));
      film.setDirectors(getDirectorsForFilm(film.getId()));
    }

    return films;
  }
}