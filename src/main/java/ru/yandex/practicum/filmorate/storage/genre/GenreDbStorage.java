package ru.yandex.practicum.filmorate.storage.genre;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Genre;

@Repository
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {

  private final JdbcTemplate jdbcTemplate;

  @Override
  public Optional<Genre> findGenreById(Long id) {
    List<Genre> results =
        jdbcTemplate.query(
            """
                SELECT * FROM genres WHERE id = ?
                ORDER BY id ASC
                """,
            new GenreRowMapper(), id);
    return results.stream().findFirst();
  }

  @Override
  public Collection<Genre> findAll() {
    return jdbcTemplate.query(
        """
            SELECT * FROM genres
            ORDER BY id ASC
            """,
        new GenreRowMapper());
  }
}
