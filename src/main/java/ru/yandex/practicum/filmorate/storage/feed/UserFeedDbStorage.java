package ru.yandex.practicum.filmorate.storage.feed;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mappers.UserFeedEventRowMapper;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.OperationType;
import ru.yandex.practicum.filmorate.model.UserFeedEvent;

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
  public void addEvent(Long userId, Long entityId, EventType eventType, OperationType operation) {
    String sql = "INSERT INTO user_feeds (user_id, entity_id, event_type, operation, timestamp) " +
            "VALUES (?, ?, ?, ?, ?)";

    jdbcTemplate.update(
            sql,
            userId,
            entityId,
            eventType.name(),
            operation.name(),
            System.currentTimeMillis()
    );
  }
}
