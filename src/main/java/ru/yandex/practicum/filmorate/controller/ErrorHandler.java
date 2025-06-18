package ru.yandex.practicum.filmorate.controller;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.util.Objects;
import java.util.Optional;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;

@RestControllerAdvice(assignableTypes = {FilmController.class, UserController.class})
public class ErrorHandler {

  @ResponseStatus(BAD_REQUEST)
  @ExceptionHandler(ValidationException.class)
  public ErrorResponse handleValidationError(final ValidationException e) {
    return new ErrorResponse(e.getMessage());
  }

  @ResponseStatus(BAD_REQUEST)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ErrorResponse handleValidationErrors(MethodArgumentNotValidException e) {
    String message =
        e.getBindingResult().getFieldErrors().stream()
            .map(DefaultMessageSourceResolvable::getDefaultMessage)
            .filter(Objects::nonNull)
            .findFirst()
            .orElse("Ошибка валидации");

    return new ErrorResponse(message);
  }

  @ResponseStatus(NOT_FOUND)
  @ExceptionHandler(NotFoundException.class)
  public ErrorResponse handleNotFound(final NotFoundException e) {
    return new ErrorResponse(e.getMessage());
  }

  @ResponseStatus(INTERNAL_SERVER_ERROR)
  @ExceptionHandler(Throwable.class)
  public ErrorResponse handleException(final Throwable e) {
    return new ErrorResponse("Произошла непредвиденная ошибка!");
  }
}
