package ru.yandex.practicum.filmorate.mappers;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Genre;

public class GenreRowMapper implements RowMapper<Genre>, Serializable {

  @Override
  public Genre mapRow(ResultSet rs, int rowNum) throws SQLException {
    return new Genre(rs.getLong("id"), rs.getString("name"));
  }
}
