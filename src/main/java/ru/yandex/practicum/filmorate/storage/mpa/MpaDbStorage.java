package ru.yandex.practicum.filmorate.storage.mpa;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mappers.MpaRowMapper;
import ru.yandex.practicum.filmorate.model.MPA;

@Repository
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {

  private final JdbcTemplate jdbcTemplate;

  @Override
  public Optional<MPA> findMpaById(Long id) {
    List<MPA> results =
        jdbcTemplate.query(
            "SELECT * FROM mpa_ratings WHERE id = ? ORDER BY id ASC", new MpaRowMapper(), id);
    return results.stream().findFirst();
  }

  @Override
  public Collection<MPA> findAll() {
    return jdbcTemplate.query(
        """
            SELECT * FROM mpa_ratings
            ORDER BY id ASC
            """,
        new MpaRowMapper());
  }
}
