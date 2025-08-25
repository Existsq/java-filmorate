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

  public static class UserFeedEventBuilder {
    private EventType eventType;
    private OperationType operation;

    public UserFeedEventBuilder eventType(EventType eventType) {
      this.eventType = eventType;
      return this;
    }

    public UserFeedEventBuilder eventType(String eventType) {
      this.eventType = EventType.valueOf(eventType);
      return this;
    }

    public UserFeedEventBuilder operation(OperationType operation) {
      this.operation = operation;
      return this;
    }

    public UserFeedEventBuilder operation(String operation) {
      this.operation = OperationType.valueOf(operation);
      return this;
    }
  }
}
