package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Set;

public interface GenreRefStorage {
    void replaceByFilmId(Long filmId, Set<Genre> genres);

    Set<Genre> findByFilmId(Long filmId);

    void deleteByFilmId(Long filmId);

    void addGenre(Long filmId, Long genreId);
}
