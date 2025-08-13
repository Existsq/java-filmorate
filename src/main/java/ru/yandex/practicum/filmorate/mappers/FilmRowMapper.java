package ru.yandex.practicum.filmorate.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MPA;

public class FilmRowMapper implements RowMapper<Film> {

  @Override
  public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
    return Film.builder()
        .id(rs.getLong("id"))
        .name(rs.getString("name"))
        .description(rs.getString("description"))
        .releaseDate(rs.getDate("release_date").toLocalDate())
        .duration(rs.getInt("duration"))
        .mpa(new MPA(rs.getLong("mpa_id"), rs.getString("mpa_name")))
        .build();
  }
}
