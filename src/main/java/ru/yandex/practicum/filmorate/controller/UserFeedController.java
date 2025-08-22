package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.UserFeedEvent;
import ru.yandex.practicum.filmorate.service.UserFeedService;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserFeedController {

  private final UserFeedService userFeedService;

  @GetMapping("/{id}/feed")
  public List<UserFeedEvent> getUserFeed(@PathVariable Long id) {
    return userFeedService.getUserFeed(id);
  }
}
