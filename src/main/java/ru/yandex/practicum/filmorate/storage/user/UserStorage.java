package ru.yandex.practicum.filmorate.storage.user;

import java.util.Collection;
import ru.yandex.practicum.filmorate.model.User;

public interface UserStorage {

  User save(User film);

  Collection<User> findAll();

  User findById(Long id);
}
