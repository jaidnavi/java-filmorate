package ru.yandex.practicum.filmorate.storage;

public interface FilmLikeStorage {
    void add(Long filmId, Long userId);

    void delete(Long filmId, Long userId);

}
