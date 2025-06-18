package ru.yandex.practicum.filmorate.storage.user;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

  private final Map<Long, User> users = new HashMap<>();

  @Override
  public User create(User user) {
    user.setId(getNextId());

    if (user.getName() == null) {
      user.setName(user.getLogin());
    }

    users.put(user.getId(), user);
    log.info("Создан новый пользователь {}", user);

    return user;
  }

  @Override
  public User update(User user) {
    if (user.getId() == null) {
      log.warn("Ошибка валидации: поле id не может быть пустым");
      throw new ValidationException("Поле id не может быть пустым");
    }

    User oldUser = users.get(user.getId());

    if (oldUser == null) {
      log.warn("Пользователь с id={} не найден", user.getId());
      throw new NotFoundException("Пользователь с id=" + user.getId() + " не найден");
    }

    if (!Objects.equals(user.getName(), oldUser.getName())) {
      String oldName = oldUser.getName();

      oldUser.setName(user.getName());

      log.info("Установлено новое имя для пользователя {} -> {}", oldName, user.getName());
    }

    if (!Objects.equals(user.getLogin(), oldUser.getLogin())) {
      String oldLogin = oldUser.getLogin();

      oldUser.setLogin(user.getLogin());

      log.info("Установлен новый логин для пользователя {} -> {}", oldLogin, user.getLogin());
    }

    if (!Objects.equals(user.getEmail(), oldUser.getEmail())) {
      String oldEmail = oldUser.getEmail();

      oldUser.setEmail(user.getEmail());

      log.info("Установлен новый email для пользователя {} -> {}", oldEmail, user.getEmail());
    }

    if (!Objects.equals(user.getBirthday(), oldUser.getBirthday())) {
      LocalDate oldBirthday = oldUser.getBirthday();

      oldUser.setBirthday(user.getBirthday());

      log.info(
          "Установлена новая дата рождения для пользователя {} -> {}",
          oldBirthday,
          user.getBirthday());
    }

    return oldUser;
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
