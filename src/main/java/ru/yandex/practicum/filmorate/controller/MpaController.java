package ru.yandex.practicum.filmorate.controller;

import java.util.Collection;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.service.MpaService;

@RestController
public class MpaController {

  private final MpaService mpaService;

  public MpaController(MpaService mpaService) {
    this.mpaService = mpaService;
  }

  @GetMapping("/mpa")
  public Collection<MPA> findAll() {
    return mpaService.findAll();
  }

  @GetMapping("/mpa/{id}")
  public MPA findMpaById(@PathVariable Long id) {
    return mpaService.findMpaById(id);
  }
}
