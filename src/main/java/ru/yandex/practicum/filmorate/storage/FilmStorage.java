package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {
    Film create(Film film);

    Film update(Film film);

    Collection<Film> findAll();

    Collection<Film> getByDirector(Long directorId, String sortBy);

    Optional<Film> get(Long filmId);

    Collection<Film> findPopular(int count);
}