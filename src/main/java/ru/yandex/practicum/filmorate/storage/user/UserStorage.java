package ru.yandex.practicum.filmorate.storage.user;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;

public interface UserStorage {

  User save(User user);

  Collection<User> findAll();

  Optional<User> findUserById(Long id);

  void addFriendRequest(Long userId, Long friendId);

  void deleteFriendship(Long userId, Long friendId);

  Set<Long> getConfirmedFriends(Long userId);

  Map<Long, FriendshipStatus> getFriendRequests(Long userId);

  void delete(Long userId);
}
