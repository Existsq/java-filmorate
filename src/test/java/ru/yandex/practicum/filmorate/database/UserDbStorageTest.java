package ru.yandex.practicum.filmorate.database;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

@JdbcTest
@AutoConfigureTestDatabase
@Import(UserDbStorage.class)
class UserDbStorageTest {

  @Autowired private UserDbStorage userStorage;

  @Test
  void testFindUserById() {
    Optional<User> user = userStorage.findUserById(1L);

    assertThat(user).isPresent();
    assertThat(user.get().getId()).isEqualTo(1L);
    assertThat(user.get().getEmail()).isEqualTo("user1@mail.com");
  }

  @Test
  void testSaveNewUser() {
    User newUser =
        User.builder()
            .email("new@mail.com")
            .login("newuser")
            .name("New User")
            .birthday(LocalDate.of(2000, 1, 1))
            .build();

    User saved = userStorage.save(newUser);

    assertThat(saved.getId()).isNotNull();
    assertThat(userStorage.findUserById(saved.getId())).isPresent();
  }

  @Test
  void testAddFriendRequestAndGetFriends() {
    userStorage.addFriendRequest(1L, 2L);

    Set<Long> friends = userStorage.getConfirmedFriends(1L);
    assertThat(friends).containsExactly(2L);

    var requests = userStorage.getFriendRequests(1L);
    assertThat(requests.get(2L)).isEqualTo(FriendshipStatus.CONFIRMED);
  }

  @Test
  void testDeleteFriendship() {
    userStorage.addFriendRequest(1L, 2L);
    userStorage.deleteFriendship(1L, 2L);

    Set<Long> friends = userStorage.getConfirmedFriends(1L);
    assertThat(friends).isEmpty();
  }
}
