package ru.yandex.practicum.filmorate.storage.user;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;

@Slf4j
@Component
@Qualifier("InMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {

  private final Map<Long, User> users = new HashMap<>();
  private final Map<Long, Map<Long, FriendshipStatus>> friendships = new HashMap<>();
  private final AtomicLong idGenerator = new AtomicLong(0);

  @Override
  public User save(User user) {
    if (user.getId() == null) {
      user.setId(idGenerator.incrementAndGet());
      log.info("Создан новый пользователь: {}", user);
    } else {
      log.info("Обновлён пользователь: {}", user);
    }
    users.put(user.getId(), user);
    return user;
  }

  @Override
  public Collection<User> findAll() {
    return users.values();
  }

  @Override
  public Optional<User> findUserById(Long id) {
    return Optional.ofNullable(users.get(id));
  }

  @Override
  public void addFriendRequest(Long userId, Long friendId) {
    validateUsersExist(userId, friendId);

    Map<Long, FriendshipStatus> userFriends =
        friendships.computeIfAbsent(userId, k -> new HashMap<>());
    if (userFriends.containsKey(friendId)) {
      throw new RuntimeException("Этот пользователь уже в списке друзей");
    }

    userFriends.put(friendId, FriendshipStatus.CONFIRMED);
    log.info("Пользователь {} добавил {} в друзья (односторонне)", userId, friendId);
  }

  @Override
  public void deleteFriendship(Long userId, Long friendId) {
    Map<Long, FriendshipStatus> userFriends = friendships.get(userId);
    if (userFriends != null) {
      userFriends.remove(friendId);
      if (userFriends.isEmpty()) {
        friendships.remove(userId);
      }
    }
    log.info("Пользователь {} удалил {} из друзей", userId, friendId);
  }

  @Override
  public Set<Long> getConfirmedFriends(Long userId) {
    Map<Long, FriendshipStatus> userFriends = friendships.get(userId);
    if (userFriends == null) {
      return Collections.emptySet();
    }
    Set<Long> confirmed = new HashSet<>();
    for (var entry : userFriends.entrySet()) {
      if (entry.getValue() == FriendshipStatus.CONFIRMED) {
        confirmed.add(entry.getKey());
      }
    }
    return confirmed;
  }

  @Override
  public Map<Long, FriendshipStatus> getFriendRequests(Long userId) {
    Map<Long, FriendshipStatus> userFriends = friendships.get(userId);
    if (userFriends == null) {
      return Collections.emptyMap();
    }
    return Collections.unmodifiableMap(userFriends);
  }

  private void validateUsersExist(Long userId, Long friendId) {
    if (!users.containsKey(userId) || !users.containsKey(friendId)) {
      throw new RuntimeException("Один или оба пользователя не существуют");
    }
  }
}
