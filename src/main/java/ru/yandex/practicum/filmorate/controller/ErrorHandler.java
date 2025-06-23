package ru.yandex.practicum.filmorate.controller;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;

@Slf4j
@RestControllerAdvice(assignableTypes = {FilmController.class, UserController.class})
public class ErrorHandler {

  @ResponseStatus(BAD_REQUEST)
  @ExceptionHandler(ValidationException.class)
  public ErrorResponse handleValidationError(final ValidationException e) {
    log.warn("Возникла ошибка валидации данных: {}", e.getMessage());
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
    log.warn("Возникла ошибка при десериализации тела запроса: {}", message);

    return new ErrorResponse(message);
  }

  @ResponseStatus(NOT_FOUND)
  @ExceptionHandler(NotFoundException.class)
  public ErrorResponse handleNotFound(final NotFoundException e) {
    log.error("Возникла ошибка отсутствия данных: {}", e.getMessage());
    return new ErrorResponse(e.getMessage());
  }

  @ResponseStatus(INTERNAL_SERVER_ERROR)
  @ExceptionHandler(Throwable.class)
  public ErrorResponse handleException(final Throwable e) {
    log.error("Возникла ошибка: {}", e.getMessage());
    return new ErrorResponse("Произошла непредвиденная ошибка!");
  }
}
