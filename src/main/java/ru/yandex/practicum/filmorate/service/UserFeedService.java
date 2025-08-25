package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.OperationType;
import ru.yandex.practicum.filmorate.model.UserFeedEvent;
import ru.yandex.practicum.filmorate.storage.feed.UserFeedStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserFeedService {

  private final UserFeedStorage userFeedStorage;

  public List<UserFeedEvent> getUserFeed(Long userId) {
    return userFeedStorage.getUserFeed(userId);
  }

  public void addLikeEvent(Long userId, Long filmId, OperationType operation) {
    this.addEvent(userId, filmId, EventType.LIKE, operation);
  }

  public void addReviewEvent(Long userId, Long reviewId, OperationType operation) {
    this.addEvent(userId, reviewId, EventType.REVIEW, operation);
  }

  public void addFriendEvent(Long userId, Long friendId, OperationType operation) {
    this.addEvent(userId, friendId, EventType.FRIEND, operation);
  }

  public void addEvent(Long userId, Long entityId, EventType eventType, OperationType operation) {
    userFeedStorage.addEvent(userId, entityId, eventType, operation);
  }
}
