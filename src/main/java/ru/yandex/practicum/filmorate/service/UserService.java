package ru.yandex.practicum.filmorate.service;

import java.util.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

@Service
public class UserService {

  private final UserStorage userStorage;

  public UserService(@Qualifier("UserDbStorage") UserStorage userStorage) {
    this.userStorage = userStorage;
  }

  public Collection<User> findAll() {
    return userStorage.findAll();
  }

  public User findById(Long id) {
    return userStorage
        .findUserById(id)
        .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));
  }

  public User create(User user) {
    return userStorage.save(user);
  }

  public User update(User user) {
    if (!userStorage.findUserById(user.getId()).isPresent()) {
      throw new NotFoundException("Пользователь с id " + user.getId() + " не найден");
    }
    return userStorage.save(user);
  }

  public void addFriend(Long userId, Long friendId) {
    validateUsers(userId, friendId);
    userStorage.addFriendRequest(userId, friendId); // теперь это сразу добавление в друзья
  }

  public void deleteFriend(Long userId, Long friendId) {
    validateUsers(userId, friendId);
    userStorage.deleteFriendship(userId, friendId);
  }

  public Set<Long> getFriends(Long userId) {
    validateUserExists(userId);
    return userStorage.getConfirmedFriends(userId);
  }

  public Set<Long> getCommonFriends(Long userId, Long otherUserId) {
    validateUsers(userId, otherUserId);
    Set<Long> friendsOfUser = userStorage.getConfirmedFriends(userId);
    Set<Long> friendsOfOther = userStorage.getConfirmedFriends(otherUserId);

    Set<Long> commonFriends = new HashSet<>(friendsOfUser);
    commonFriends.retainAll(friendsOfOther);
    return commonFriends;
  }

  private void validateUsers(Long userId, Long friendId) {
    if (userId.equals(friendId)) {
      throw new IllegalArgumentException("Нельзя добавить в друзья самого себя");
    }
    validateUserExists(userId);
    validateUserExists(friendId);
  }

  public void validateUserExists(Long userId) {
    if (!userStorage.findUserById(userId).isPresent()) {
      throw new NotFoundException("Пользователь с id " + userId + " не найден");
    }
  }
}
