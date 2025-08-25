package ru.yandex.practicum.filmorate.storage.feed;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mappers.UserFeedEventRowMapper;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.OperationType;
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
      ps.setString(3, event.getEventType().name());
      ps.setString(4, event.getOperation().name());
      ps.setLong(5, event.getTimestamp());
      return ps;
    }, keyHolder);

    if (keyHolder.getKey() != null) {
      event.setEventId(keyHolder.getKey().longValue());
    }
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
