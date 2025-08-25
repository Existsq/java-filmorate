package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserFeedEvent {
  private long eventId;
  private long timestamp;
  private long userId;

  @JsonProperty("eventType")
  private EventType eventType;

  @JsonProperty("operation")
  private OperationType operation;

  private long entityId;
}
