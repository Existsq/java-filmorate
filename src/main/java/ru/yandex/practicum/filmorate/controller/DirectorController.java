package ru.yandex.practicum.filmorate.controller;

import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

@RestController
@RequestMapping("/directors")
@RequiredArgsConstructor
public class DirectorController {

  private final DirectorService directorService;

  @PostMapping
  public Director create(@RequestBody Director director) {
    return directorService.create(director);
  }

  @GetMapping
  public Collection<Director> findAll() {
    return directorService.findAll();
  }

  @GetMapping("/{id}")
  public Director findById(@PathVariable Long id) {
    return directorService.findById(id);
  }

  @PutMapping
  public Director update(@RequestBody Director director) {
    return directorService.update(director);
  }

  @DeleteMapping("/{id}")
  public void delete(@PathVariable Long id) {
    directorService.deleteById(id);
  }
}
