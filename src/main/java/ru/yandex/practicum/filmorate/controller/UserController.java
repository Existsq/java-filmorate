package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

@AllArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {
  private final UserService userService;
  private final FilmService filmService;

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
    Set<Long> friendIds = userService.getFriends(id);
    return friendIds.stream().map(userService::findById).collect(Collectors.toSet());
  }

  @GetMapping("/{id}/friends/common/{otherId}")
  public Set<User> getCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
    Set<Long> commonFriendIds = userService.getCommonFriends(id, otherId);
    return commonFriendIds.stream().map(userService::findById).collect(Collectors.toSet());
  }

  @GetMapping("/{id}/recommendations")
  public List<Film> getRecommendations(@PathVariable Long id) {
    return filmService.getRecommendations(id);
  }
}
