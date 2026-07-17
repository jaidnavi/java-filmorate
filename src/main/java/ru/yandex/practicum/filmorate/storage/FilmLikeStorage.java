package ru.yandex.practicum.filmorate.storage;

import java.util.Set;

public interface FilmLikeStorage {
    void add(Long filmId, Long userId);

    void delete(Long filmId, Long userId);

    Set<Long> findUserByFilmId(Long filmId);

}
