package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;

public record Director(long id, @NotBlank(message = "Имя режиссера не может быть пустым") String name) {}
