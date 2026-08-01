package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Set;

public interface GenreRefStorage {
    void replaceByFilmId(Long filmId, Set<Genre> genres);

    void deleteByFilmId(Long filmId);

    void addGenresToFilm(Set<Genre> genres, Long filmId);
}
