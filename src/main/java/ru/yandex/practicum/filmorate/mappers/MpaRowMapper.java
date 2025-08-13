package ru.yandex.practicum.filmorate.mappers;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.MPA;

public class MpaRowMapper implements RowMapper<MPA>, Serializable {

  @Override
  public MPA mapRow(ResultSet rs, int rowNum) throws SQLException {
    return new MPA(rs.getLong("id"), rs.getString("name"));
  }
}
