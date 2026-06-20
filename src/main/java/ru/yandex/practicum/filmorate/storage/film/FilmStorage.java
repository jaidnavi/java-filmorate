package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {
    Film create(Film film);

    Film update(Film film);

    Collection<Film> findAll();

    public Optional<Film> get(Long filmId);

    Film addLike(Long userId, Long filmId);

    Film deleteLike(Long userId, Long filmId);

    Collection<Film> findPopular(Integer count);
}