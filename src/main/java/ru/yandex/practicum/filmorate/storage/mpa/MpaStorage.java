package ru.yandex.practicum.filmorate.storage.mpa;

import java.util.Collection;
import java.util.Optional;
import ru.yandex.practicum.filmorate.model.MPA;

public interface MpaStorage {

  Optional<MPA> findMpaById(Long id);

  Collection<MPA> findAll();
}
