package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FilmReview {
    private long id;
    @NotBlank(message = "Комментарий не может быть пустым")
    private String content;
    private boolean isPositive;
    private long filmId;
    private long userId;
    private int usefull;
}
