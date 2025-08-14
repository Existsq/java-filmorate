package ru.yandex.practicum.filmorate.database;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;

@JdbcTest
@Import({MpaDbStorage.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class MpaDbStorageTest {

  private final MpaDbStorage mpaStorage;

  @Test
  void testFindMpaByIdExisting() {
    Optional<MPA> mpa = mpaStorage.findMpaById(1L);
    assertThat(mpa).isPresent();
    assertThat(mpa.get().id()).isEqualTo(1L);
    assertThat(mpa.get().name()).isEqualTo("G");
  }

  @Test
  void testFindMpaByIdNonExisting() {
    Optional<MPA> mpa = mpaStorage.findMpaById(999L);
    assertThat(mpa).isEmpty();
  }

  @Test
  void testFindAllMpa() {
    Collection<MPA> allMpa = mpaStorage.findAll();
    assertThat(allMpa).isNotEmpty();
    assertThat(allMpa).hasSizeGreaterThanOrEqualTo(5);
  }
}
