package ru.yandex.practicum.filmorate.storage.dao;

public interface FilmLikeDao {
    void addLike(Long filmId, Long userId);

    void deleteLike(Long filmId, Long userId);
}
