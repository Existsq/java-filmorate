package ru.yandex.practicum.filmorate.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.FilmReview;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FilmReviewRowMapper implements RowMapper<FilmReview> {

  @Override
  public FilmReview mapRow(ResultSet rs, int rowNum) throws SQLException {
    return FilmReview.builder()
        .id(rs.getLong("id"))
        .content(rs.getString("content"))
        .is_positive(rs.getBoolean("is_positive"))
        .user_id(rs.getLong("user_id"))
        .film_id(rs.getLong("film_id"))
        .usefull(rs.getInt("usefull"))
        .build();
  }
}
