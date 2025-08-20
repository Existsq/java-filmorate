package ru.yandex.practicum.filmorate.storage.user;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.DuplicateFriendshipException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

  private final JdbcTemplate jdbcTemplate;

  @Override
  public User save(User user) {
    if (user.getId() == null) {
      String sql = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";
      PreparedStatementCreator psc =
          connection -> {
            PreparedStatement ps =
                connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, Date.valueOf(user.getBirthday()));
            return ps;
          };
      KeyHolder keyHolder = new GeneratedKeyHolder();
      jdbcTemplate.update(psc, keyHolder);
      user.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
    } else {
      String sql = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?";
      jdbcTemplate.update(
          sql,
          user.getEmail(),
          user.getLogin(),
          user.getName(),
          Date.valueOf(user.getBirthday()),
          user.getId());
    }
    return user;
  }

  @Override
  public Collection<User> findAll() {
    String sql = "SELECT * FROM users";
    return jdbcTemplate.query(sql, new UserRowMapper());
  }

  @Override
  public Optional<User> findUserById(Long id) {
    String sql = "SELECT * FROM users WHERE id = ?";
    List<User> users = jdbcTemplate.query(sql, new UserRowMapper(), id);
    return users.stream().findFirst();
  }

  @Override
  public void addFriendRequest(Long userId, Long friendId) {
    validateUsersExist(userId, friendId);

    String checkSql = "SELECT COUNT(*) FROM friendships WHERE user_id = ? AND friend_id = ?";
    Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, userId, friendId);
    if (count != null && count > 0) {
      throw new DuplicateFriendshipException("Этот пользователь уже в списке друзей");
    }

    String sql =
        "INSERT INTO friendships (user_id, friend_id, friendship_status) VALUES (?, ?, 'CONFIRMED')";
    jdbcTemplate.update(sql, userId, friendId);
    log.info("Пользователь {} добавил {} в друзья (односторонне)", userId, friendId);
  }

  @Override
  public void deleteFriendship(Long userId, Long friendId) {
    String sql = "DELETE FROM friendships WHERE user_id = ? AND friend_id = ?";
    jdbcTemplate.update(sql, userId, friendId);
    log.info("Пользователь {} удалил {} из друзей", userId, friendId);
  }

  @Override
  public Set<Long> getConfirmedFriends(Long userId) {
    String sql =
        "SELECT friend_id FROM friendships WHERE user_id = ? AND friendship_status = 'CONFIRMED'";
    List<Long> friendIds = jdbcTemplate.queryForList(sql, Long.class, userId);
    return new HashSet<>(friendIds);
  }

  @Override
  public Map<Long, FriendshipStatus> getFriendRequests(Long userId) {
    String sql = "SELECT friend_id, friendship_status FROM friendships WHERE user_id = ?";
    return jdbcTemplate.query(
        sql,
        rs -> {
          Map<Long, FriendshipStatus> map = new HashMap<>();
          while (rs.next()) {
            map.put(
                rs.getLong("friend_id"),
                FriendshipStatus.valueOf(rs.getString("friendship_status")));
          }
          return map;
        },
        userId);
  }

  @Override
  public void delete(Long userId) {
    String sql = "DELETE FROM users WHERE id = ?";
    jdbcTemplate.update(sql, userId);
    log.info("Пользователь {} удален", userId);
  }

  private void validateUsersExist(Long userId, Long friendId) {
    if (!userExists(userId) || !userExists(friendId)) {
      throw new NotFoundException("Один или оба пользователя не существуют");
    }
  }

  private boolean userExists(Long userId) {
    String sql = "SELECT COUNT(*) FROM users WHERE id = ?";
    Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId);
    return count != null && count > 0;
  }
}
