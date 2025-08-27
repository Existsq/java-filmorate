package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.OperationType;
import ru.yandex.practicum.filmorate.model.UserFeedEvent;
import ru.yandex.practicum.filmorate.storage.feed.UserFeedStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserFeedService {

  private final UserFeedStorage userFeedStorage;

  private final UserStorage userStorage;

  public List<UserFeedEvent> getUserFeed(Long userId) {
    if (userStorage.findUserById(userId).isEmpty()) {
      throw new NotFoundException("Пользователь с id " + userId + " не найден");
    }
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
