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
        .reviewId(rs.getLong("reviewId"))
        .content(rs.getString("content"))
        .isPositive(rs.getBoolean("is_positive"))
        .userId(rs.getLong("user_id"))
        .filmId(rs.getLong("film_id"))
        .useful(rs.getInt("useful"))
        .build();
  }
}
