package ru.yandex.practicum.filmorate.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.UserFeedEvent;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class UserFeedEventRowMapper implements RowMapper<UserFeedEvent> {

  @Override
  public UserFeedEvent mapRow(ResultSet rs, int rowNum) throws SQLException {
    return UserFeedEvent.builder()
            .eventId(rs.getLong("event_id"))
            .timestamp(rs.getLong("timestamp"))
            .userId(rs.getLong("user_id"))
            .eventType(rs.getString("event_type"))
            .operation(rs.getString("operation"))
            .entityId(rs.getLong("entity_id"))
            .build();
  }
}
