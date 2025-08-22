package ru.yandex.practicum.filmorate.storage.feed;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mappers.UserFeedEventRowMapper;
import ru.yandex.practicum.filmorate.model.UserFeedEvent;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserFeedDbStorage implements UserFeedStorage {

  private final JdbcTemplate jdbcTemplate;
  private final UserFeedEventRowMapper rowMapper;

  @Override
  public List<UserFeedEvent> getUserFeed(Long userId) {
    String sql = "SELECT * FROM user_feeds WHERE user_id = ? ORDER BY timestamp ASC";
    return jdbcTemplate.query(sql, rowMapper, userId);
  }

  @Override
  public void addEvent(UserFeedEvent event) {
    String sql = "INSERT INTO user_feeds (user_id, entity_id, event_type, operation, timestamp) " +
            "VALUES (?, ?, ?, ?, ?)";

    KeyHolder keyHolder = new GeneratedKeyHolder();

    jdbcTemplate.update(connection -> {
      PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
      ps.setLong(1, event.getUserId());
      ps.setLong(2, event.getEntityId());
      ps.setString(3, event.getEventType());
      ps.setString(4, event.getOperation());
      ps.setLong(5, event.getTimestamp());
      return ps;
    }, keyHolder);

    if (keyHolder.getKey() != null) {
      event.setEventId(keyHolder.getKey().longValue());
    }
  }

  @Override
  public void addLikeEvent(Long userId, Long filmId, String operation) {
    UserFeedEvent event = UserFeedEvent.builder()
            .userId(userId)
            .entityId(filmId)
            .eventType("LIKE")
            .operation(operation)
            .timestamp(System.currentTimeMillis())
            .build();
    addEvent(event);
  }

  @Override
  public void addReviewEvent(Long userId, Long reviewId, String operation) {
    UserFeedEvent event = UserFeedEvent.builder()
            .userId(userId)
            .entityId(reviewId)
            .eventType("REVIEW")
            .operation(operation)
            .timestamp(System.currentTimeMillis())
            .build();
    addEvent(event);
  }

  @Override
  public void addFriendEvent(Long userId, Long friendId, String operation) {
    UserFeedEvent event = UserFeedEvent.builder()
            .userId(userId)
            .entityId(friendId)
            .eventType("FRIEND")
            .operation(operation)
            .timestamp(System.currentTimeMillis())
            .build();
    addEvent(event);
  }
}
