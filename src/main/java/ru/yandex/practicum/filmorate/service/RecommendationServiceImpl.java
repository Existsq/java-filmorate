package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Override
    public Set<Long> getLikedFilmIds(Long userId) {
        return filmStorage.getLikedFilmIds(userId);
    }

    @Override
    public Set<Long> findUsersWithSimilarTastes(Long userId) {
        // Получаем все лайки текущего пользователя
        Set<Long> userLikes = getLikedFilmIds(userId);

        if (userLikes.isEmpty()) {
            return Collections.emptySet();
        }

        // Находим пользователей, которые лайкнули хотя бы один общий фильм
        return userStorage.findAll().stream()
                .filter(u -> !u.getId().equals(userId))
                .collect(Collectors.toMap(
                        User::getId,
                        otherUser -> {
                            Set<Long> otherLikes = getLikedFilmIds(otherUser.getId());
                            Set<Long> intersection = new HashSet<>(otherLikes);
                            intersection.retainAll(userLikes);
                            return (long) intersection.size();
                        }))
                .entrySet().stream()
                .filter(e -> e.getValue() > 0)
                .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
                .limit(10)
                .map(Map.Entry::getKey)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}