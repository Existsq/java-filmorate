package ru.yandex.practicum.filmorate.storage.feed;

import ru.yandex.practicum.filmorate.model.UserFeedEvent;

import java.util.List;

public interface UserFeedStorage {
  List<UserFeedEvent> getUserFeed(Long userId);

  void addEvent(UserFeedEvent event);

  void addLikeEvent(Long userId, Long filmId, String operation);

  void addReviewEvent(Long userId, Long reviewId, String operation);

  void addFriendEvent(Long userId, Long friendId, String operation);
}
