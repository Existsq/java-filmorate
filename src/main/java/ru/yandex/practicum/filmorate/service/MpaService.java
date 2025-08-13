package ru.yandex.practicum.filmorate.service;

import java.util.Collection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

@Slf4j
@Service
public class MpaService {

  private final MpaStorage mpaStorage;

  public MpaService(MpaStorage mpaStorage) {
    this.mpaStorage = mpaStorage;
  }

  public Collection<MPA> findAll() {
    return mpaStorage.findAll();
  }

  public MPA findMpaById(Long id) {
    return mpaStorage
        .findMpaById(id)
        .orElseThrow(() -> new NotFoundException("Указанный рейтинг не найден"));
  }
}
