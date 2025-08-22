package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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

  public void addLikeEvent(Long userId, Long filmId, String operation) {
    userFeedStorage.addLikeEvent(userId, filmId, operation);
  }

  public void addReviewEvent(Long userId, Long reviewId, String operation) {
    userFeedStorage.addReviewEvent(userId, reviewId, operation);
  }

  public void addFriendEvent(Long userId, Long friendId, String operation) {
    userFeedStorage.addFriendEvent(userId, friendId, operation);
  }
}
