package ru.yandex.practicum.filmorate.storage.user;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

  private final Map<Long, User> users = new HashMap<>();

  @Override
  public User save(User user) {
    if (user.getId() == null || !users.containsKey(user.getId())) {
      user.setId(getNextId());
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
  public User findById(Long id) {
    return users.get(id);
  }

  private Long getNextId() {
    long currentMaxId = users.values().stream().mapToLong(User::getId).max().orElse(0);
    return ++currentMaxId;
  }
}
