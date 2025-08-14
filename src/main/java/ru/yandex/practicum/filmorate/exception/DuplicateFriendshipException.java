package ru.yandex.practicum.filmorate.exception;

public class DuplicateFriendshipException extends RuntimeException {
  public DuplicateFriendshipException(String message) {
    super(message);
  }
}
