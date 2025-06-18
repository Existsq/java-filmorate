package ru.yandex.practicum.filmorate.service;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

@Slf4j
@Service
public class UserService {
  private final Map<Long, Set<Long>> friends = new HashMap<>();
  private final UserStorage userStorage;

  @Autowired
  public UserService(final UserStorage userStorage) {
    this.userStorage = userStorage;
  }

  public User create(User user) {
    User created = userStorage.create(user);
    log.info("Создан пользователь: {}", created);
    return created;
  }

  public User update(User user) {
    User updated = userStorage.update(user);
    log.info("Обновлён пользователь: {}", updated);
    return updated;
  }

  public Collection<User> findAll() {
    return userStorage.findAll();
  }

  public User findById(Long id) {
    return userStorage.findById(id);
  }

  public void addFriend(Long userId, Long friendId) {
    this.validateUsers(userId, friendId);

    friends.computeIfAbsent(userId, k -> new HashSet<>()).add(friendId);
    friends.computeIfAbsent(friendId, k -> new HashSet<>()).add(userId);

    log.info("Пользователь {} добавил в друзья пользователя {}", userId, friendId);
  }

  public void deleteFriend(Long userId, Long friendId) {
    this.validateUsers(userId, friendId);

    Set<Long> userFriends = friends.get(userId);
    if (userFriends != null) {
      userFriends.remove(friendId);
      if (userFriends.isEmpty()) {
        friends.remove(userId);
      }
    }

    Set<Long> friendFriends = friends.get(friendId);
    if (friendFriends != null) {
      friendFriends.remove(userId);
      if (friendFriends.isEmpty()) {
        friends.remove(friendId);
      }
    }

    log.info("Пользователь {} удалил из друзей пользователя {}", userId, friendId);
  }

  public Set<Long> getFriends(Long id) {
    if (userStorage.findById(id) == null) {
      log.warn("Попытка получить друзей для несуществующего пользователя с id={}", id);
      throw new NotFoundException("Указан несуществующий id");
    }
    log.debug("Получен список друзей пользователя {}", id);
    return friends.getOrDefault(id, Collections.emptySet());
  }

  public Set<Long> getCommonFriends(Long userId1, Long userId2) {
    this.validateUsers(userId1, userId2);

    Set<Long> firstUsrFriends = friends.get(userId1);
    Set<Long> secondUsrFriends = friends.get(userId2);

    if (firstUsrFriends == null || secondUsrFriends == null) {
      log.debug("У одного из пользователей ({} или {}) нет друзей", userId1, userId2);
      return Collections.emptySet();
    }

    Set<Long> commonFriends = new HashSet<>(firstUsrFriends);
    commonFriends.retainAll(secondUsrFriends);
    log.info("Общие друзья между пользователями {} и {}: {}", userId1, userId2, commonFriends);
    return commonFriends;
  }

  public void validateUserExists(Long userId) {
    if (userStorage.findById(userId) == null) {
      log.warn("Пользователь с id={} не найден", userId);
      throw new NotFoundException("Пользователь с id " + userId + " не найден");
    }
  }

  private void validateUsers(Long userId1, Long userId2) {
    validateUserExists(userId1);
    validateUserExists(userId2);
  }
}
