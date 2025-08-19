package ru.yandex.practicum.filmorate.storage.director;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mappers.DirectorRowMapper;
import ru.yandex.practicum.filmorate.model.Director;

@Slf4j
@Repository
@RequiredArgsConstructor
public class DirectorDbStorage implements DirectorStorage {

  private final JdbcTemplate jdbcTemplate;

  @Override
  public Director save(Director director) {
    String sql = "INSERT INTO directors (name) VALUES (?)";
    KeyHolder keyHolder = new GeneratedKeyHolder();

    jdbcTemplate.update(
        connection -> {
          PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
          ps.setString(1, director.name());
          return ps;
        },
        keyHolder);

    long id = Objects.requireNonNull(keyHolder.getKey()).longValue();

    log.info("Создан режиссер {} с id={}", director.name(), id);
    return new Director(id, director.name());
  }

  @Override
  public Director update(Director director) {
    String sql = "UPDATE directors SET name = ? WHERE id = ?";
    int updated = jdbcTemplate.update(sql, director.name(), director.id());

    if (updated == 0) {
      throw new NotFoundException("Режиссер с id=" + director.id() + " не найден");
    }

    log.info("Обновлен режиссер {} с id={}", director.name(), director.id());
    return director;
  }

  @Override
  public Collection<Director> findAll() {
    String sql =
        """
        SELECT d.id, d.name
        FROM directors d
        ORDER BY d.id ASC
        """;

    Collection<Director> directors = jdbcTemplate.query(sql, new DirectorRowMapper());
    log.info("Найдено {} режиссеров", directors.size());
    return directors;
  }

  @Override
  public Optional<Director> findDirectorById(Long id) {
    String sql =
        """
            SELECT d.id, d.name
            FROM directors d
            WHERE d.id = ?
            """;

    Director director =
        jdbcTemplate.query(sql, new DirectorRowMapper(), id).stream().findFirst().orElse(null);

    if (director != null) {
      log.info("Найден режиссер {} с id={}", director.name(), id);
    } else {
      log.info("Режиссер с id={} не найден", id);
    }

    return Optional.ofNullable(director);
  }

  @Override
  public void deleteById(Long id) {
    jdbcTemplate.update("DELETE FROM film_directors WHERE director_id = ?", id);
    jdbcTemplate.update("DELETE FROM directors WHERE id = ?", id);
    log.info("Режиссер с id={} удален", id);
  }
}
