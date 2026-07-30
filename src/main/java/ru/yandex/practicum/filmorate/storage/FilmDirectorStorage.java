package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Set;

public interface FilmDirectorStorage {

    void replaceByFilmId(Long filmId, Set<Director> directors);

    Set<Director> findByFilmId(Long filmId);

    void deleteByFilmId(Long filmId);

    void addDirector(Long filmId, Long directorId);
}
