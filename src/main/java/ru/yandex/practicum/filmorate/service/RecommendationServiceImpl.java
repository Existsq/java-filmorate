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
        log.debug("Поиск пользователей с похожими вкусами для пользователя {}", userId);

        // все фильмы, которые лайкнул пользователь
        Set<Long> userLikes = getLikedFilmIds(userId);

        if (userLikes.isEmpty()) {
            log.debug("У пользователя {} нет лайков", userId);
            return Collections.emptySet();
        }

        // для каждого пользователя - количество общих лайков
        return userStorage.findAll().stream()
                .filter(u -> !u.getId().equals(userId))
                .collect(Collectors.toMap(
                        User::getId,
                        otherUser -> {
                            Set<Long> otherLikes = getLikedFilmIds(otherUser.getId()); // лайки другого пользователя
                            Set<Long> intersection = new HashSet<>(otherLikes); // пересечение лайков
                            intersection.retainAll(userLikes); // остаются только общие
                            return (long) intersection.size();
                        }))
                .entrySet().stream()
                .filter(e -> e.getValue() > 0) // остаются только те, у кого есть общие лайки
                .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
                .limit(10)
                .map(Map.Entry::getKey)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}