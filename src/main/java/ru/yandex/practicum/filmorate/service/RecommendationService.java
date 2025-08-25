package ru.yandex.practicum.filmorate.service;

import java.util.Set;

public interface RecommendationService {

    Set<Long> getLikedFilmIds(Long userId);

    Set<Long> findUsersWithSimilarTastes(Long userId);
}