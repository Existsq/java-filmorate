package ru.yandex.practicum.filmorate.storage.user;

import java.util.Collection;
import ru.yandex.practicum.filmorate.model.User;

public interface UserStorage {

  User create(User user);

  User update(User user);

  Collection<User> findAll();

  User findById(Long id);
}
