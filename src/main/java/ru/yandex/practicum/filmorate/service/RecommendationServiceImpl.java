package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {

    private final FilmStorage filmStorage;

    @Override
    public Set<Long> getLikedFilmIds(Long userId) {
        return filmStorage.getLikedFilmIds(userId);
    }

    @Override
    public Set<Long> findUsersWithSimilarTastes(Long userId) {

        Set<Long> userLikedFilms = getLikedFilmIds(userId);
        if (userLikedFilms.isEmpty()) {
            return Collections.emptySet();
        }
        // все лайки всех пользователей
        Map<Long, Set<Long>> allUserLikes = filmStorage.getAllUserLikes();

        allUserLikes.remove(userId);
        // сходство для каждого пользователя
        return allUserLikes.entrySet().stream()
                .map(entry -> {
                    Long otherUserId = entry.getKey();
                    Set<Long> otherLikes = entry.getValue();

                    // пересечение
                    Set<Long> commonFilms = new HashSet<>(otherLikes);
                    commonFilms.retainAll(userLikedFilms);

                    return new AbstractMap.SimpleEntry<>(otherUserId, (long) commonFilms.size());
                })
                .filter(entry -> entry.getValue() > 0)
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .limit(10)
                .map(Map.Entry::getKey)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}