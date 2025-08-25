package ru.yandex.practicum.filmorate.storage.feed;

import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.OperationType;
import ru.yandex.practicum.filmorate.model.UserFeedEvent;

import java.util.List;

public interface UserFeedStorage {
  List<UserFeedEvent> getUserFeed(Long userId);

  void addEvent(UserFeedEvent event);

  void addEvent(Long userId, Long entityId, EventType eventType, OperationType operation);
}
