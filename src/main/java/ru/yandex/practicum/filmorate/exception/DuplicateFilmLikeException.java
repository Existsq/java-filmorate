package ru.yandex.practicum.filmorate.exception;

public class DuplicateFilmLikeException extends RuntimeException {
  public DuplicateFilmLikeException(String message) {
    super(message);
  }
}
