package ru.yandex.practicum.filmorate.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Director;

public class DirectorRowMapper implements RowMapper<Director> {

  @Override
  public Director mapRow(ResultSet rs, int rowNum) throws SQLException {
    return new Director(rs.getLong("id"), rs.getString("name"));
  }
}
