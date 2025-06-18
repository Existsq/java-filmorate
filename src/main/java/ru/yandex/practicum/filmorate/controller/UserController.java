package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {
  private final UserService userService;

  @Autowired
  public UserController(final UserService userService) {
    this.userService = userService;
  }

  @GetMapping
  public Collection<User> findAll() {
    return userService.findAll();
  }

  @GetMapping("/{id}")
  public User findById(@PathVariable Long id) {
    return userService.findById(id);
  }

  @PostMapping
  public User create(@Valid @RequestBody User user) {
    return userService.create(user);
  }

  @PutMapping
  public User update(@Valid @RequestBody User user) {
    return userService.update(user);
  }

  @PutMapping("/{id}/friends/{friendId}")
  public void addFriend(@PathVariable Long id, @PathVariable Long friendId) {
    userService.addFriend(id, friendId);
  }

  @DeleteMapping("/{id}/friends/{friendId}")
  public void deleteFriend(@PathVariable Long id, @PathVariable Long friendId) {
    userService.deleteFriend(id, friendId);
  }

  @GetMapping("/{id}/friends")
  public Set<User> getFriends(@PathVariable Long id) {
    Set<Long> userFriends = userService.getFriends(id);

    return userFriends.stream().map(userService::findById).collect(Collectors.toSet());
  }

  @GetMapping("/{id}/friends/common/{otherId}")
  public Set<User> getCommonFriends(
      @PathVariable("id") Long userId1, @PathVariable("otherId") Long userId2) {
    Set<Long> userFriends = userService.getCommonFriends(userId1, userId2);

    return userFriends.stream().map(userService::findById).collect(Collectors.toSet());
  }
}
