package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

@RestController
@RequestMapping("/users")
public class UserController {

  private final Map<Long, User> users = new HashMap<>();

  private final Logger log = LoggerFactory.getLogger(UserController.class);

  @GetMapping
  public Collection<User> findAll() {
    return users.values();
  }

  @PostMapping
  public User create(@Valid @RequestBody User user) {
    user.setId(getNextId());

    if (user.getName() == null) {
      user.setName(user.getLogin());
    }

    users.put(user.getId(), user);
    log.info("Создан новый пользователь {}", user);

    return user;
  }

  @PutMapping
  public User update(@Valid @RequestBody User user) {
    if (user.getId() == null) {
      log.warn("Ошибка валидации: поле id не может быть пустым");
      throw new ValidationException("Поле id не может быть пустым");
    }

    User oldUser = users.get(user.getId());

    if (oldUser == null) {
      log.warn("Пользователь с id={} не найден", user.getId());
      throw new NoSuchElementException("Пользователь с id=" + user.getId() + " не найден");
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

  private Long getNextId() {
    long currentMaxId = users.values().stream().mapToLong(User::getId).max().orElse(0);
    return ++currentMaxId;
  }
}
